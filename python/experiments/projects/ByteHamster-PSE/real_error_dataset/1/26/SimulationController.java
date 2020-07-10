package edu.kit.pse.osip.simulation.controller;

import edu.kit.pse.osip.core.OSIPConstants;
import edu.kit.pse.osip.core.SimulationConstants;
import edu.kit.pse.osip.core.io.files.ParserException;
import edu.kit.pse.osip.core.io.files.ScenarioFile;
import edu.kit.pse.osip.core.io.files.ServerSettingsWrapper;
import edu.kit.pse.osip.core.model.base.AbstractTank;
import edu.kit.pse.osip.core.model.base.MixTank;
import edu.kit.pse.osip.core.model.base.Tank;
import edu.kit.pse.osip.core.model.base.TankSelector;
import edu.kit.pse.osip.core.model.behavior.AlarmBehavior;
import edu.kit.pse.osip.core.model.behavior.FillAlarm;
import edu.kit.pse.osip.core.model.behavior.Scenario;
import edu.kit.pse.osip.core.model.behavior.TemperatureAlarm;
import edu.kit.pse.osip.core.utils.language.Translator;
import edu.kit.pse.osip.simulation.view.control.SimulationControlWindow;
import edu.kit.pse.osip.simulation.view.dialogs.AboutDialog;
import edu.kit.pse.osip.simulation.view.dialogs.HelpDialog;
import edu.kit.pse.osip.simulation.view.main.SimulationMainWindow;
import edu.kit.pse.osip.simulation.view.settings.SimulationSettingsWindow;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.kit.pse.osip.core.model.simulation.ProductionSiteSimulation;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.eclipse.milo.opcua.stack.core.UaException;

/**
 * Manages servers and controls view actions.
 * @author David Kahles
 * @version 1.0
 */
public class SimulationController extends Application {
    private final ProductionSiteSimulation productionSite = new ProductionSiteSimulation();
    private PhysicsSimulator simulator;

    private SimulationViewInterface currentSimulationView;
    private SimulationSettingsInterface settingsInterface;
    private SimulationControlInterface controlInterface;

    private final MixTankContainer mixCont = new MixTankContainer();
    private final List<TankContainer> tanks = new LinkedList<>();

    private Scenario currentScenario;

    private ServerSettingsWrapper settingsWrapper;
    private boolean overflow = false;
    private Timer stepTimer = new Timer(true);
    private boolean resetInProgress = false;

    private static final float FILL_ALARM_LOWER_THRESHOLD = 0.05f;
    private static final float FILL_ALARM_UPPER_THRESHOLD = 0.95f;
    private static final float TEMP_ALARM_LOWER_THRESHOLD = SimulationConstants.MIN_TEMPERATURE
            + 0.05f * (SimulationConstants.MAX_TEMPERATURE - SimulationConstants.MIN_TEMPERATURE);
    private static final float TEMP_ALARM_UPPER_THRESHOLD = SimulationConstants.MIN_TEMPERATURE
            + 0.95f * (SimulationConstants.MAX_TEMPERATURE - SimulationConstants.MIN_TEMPERATURE);

    /**
     * Responsible for controlling the display windows and simulating the production
     */
    public SimulationController() {
        Locale.setDefault(Locale.US);
        File settingsLocation = new File(System.getProperty("user.home") + File.separator + ".osip");
        settingsLocation.mkdirs();
        settingsWrapper = new ServerSettingsWrapper(new File(settingsLocation, "simulation.conf"));

        simulator = new PhysicsSimulator(productionSite);
    }

    private void initialize() throws UaException, ExecutionException, InterruptedException {
        for (TankSelector selector: TankSelector.valuesWithoutMix()) {
            TankContainer cont = new TankContainer();
            tanks.add(cont);
            cont.tank = productionSite.getUpperTank(selector);
            cont.selector = selector;
        }
        mixCont.tank = productionSite.getMixTank();
        setupAlarms();

        setupServer();
        startMainLoop();
        updateServerValues();
    }

    private void setupServer() throws UaException, ExecutionException, InterruptedException {
        int defaultPort = OSIPConstants.DEFAULT_PORT_MIX;
        mixCont.server = new MixTankServer(settingsWrapper.getServerPort(TankSelector.MIX, defaultPort++));
        mixCont.server.start();

        for (TankContainer cont : tanks) {
            cont.server = new TankServer(settingsWrapper.getServerPort(cont.selector, defaultPort++));
            cont.server.start();
        }
    }

    /**
     * Re-setup the servers
     * @return true if successful
     */
    private boolean reSetupServer() {
        boolean error = false;
        Translator t = Translator.getInstance();

        for (TankSelector selector: TankSelector.values()) {
            settingsWrapper.setServerPort(selector, settingsInterface.getPort(selector));
        }
        settingsWrapper.saveSettings();

        if (hasDoublePorts()) {
            currentSimulationView.showOPCUAServerError(t.getString("simulation.settings.samePort"));
            return false;
        }

        for (TankContainer cont: tanks) {
            try {
                if (cont.server != null) {
                    cont.server.stop();
                }
            } catch (InterruptedException | ExecutionException ex) {
                currentSimulationView.showOPCUAServerError(String.format(
                    t.getString("simulation.settings.stopError") + ": " + ex.getMessage(),
                    cont.selector.toString().toLowerCase()));
                error = true;
            }
            cont.server = null;
        }
        try {
            if (mixCont.server != null) {
                mixCont.server.stop();
            }
        } catch (InterruptedException | ExecutionException ex) {
            currentSimulationView.showOPCUAServerError(String.format(t.getString("simulation.settings.stopError")
                    + ": " + ex.getMessage(), TankSelector.MIX.toString().toLowerCase()));
            error = true;
        }
        mixCont.server = null;

        for (TankContainer cont: tanks) {
            try {
                int port = settingsInterface.getPort(cont.selector);
                cont.server = new TankServer(port);
                cont.server.start();
            } catch (InterruptedException | ExecutionException | UaException ex) {
                currentSimulationView.showOPCUAServerError(String.format(
                    t.getString("simulation.settings.startError") + ": "
                    + ex.getMessage(), cont.selector.toString().toLowerCase()));
                error = true;
            }
        }
        try {
            int port = settingsInterface.getPort(TankSelector.MIX);
            mixCont.server = new MixTankServer(port);
            mixCont.server.start();
        } catch (InterruptedException | ExecutionException | UaException ex) {
            currentSimulationView.showOPCUAServerError(String.format(t.getString("simulation.settings.startError")
                + ": " + ex.getMessage(), TankSelector.MIX.toString().toLowerCase()));
            error = true;
        }
        return !error;
    }

    private boolean hasDoublePorts() {
        Vector<Integer> ports = new Vector<>(TankSelector.values().length);
        for (TankSelector selector: TankSelector.values()) {
            int port = settingsInterface.getPort(selector);
            if (ports.contains(port)) {
                return true;
            } else {
                ports.add(port);
            }
        }
        return false;
    }

    private void setupAlarms() {
        for (TankContainer cont: tanks) {
            cont.overflowAlarm =
                new FillAlarm(cont.tank, FILL_ALARM_UPPER_THRESHOLD, AlarmBehavior.GREATER_THAN);
            cont.underflowAlarm =
                new FillAlarm(cont.tank, FILL_ALARM_LOWER_THRESHOLD, AlarmBehavior.SMALLER_THAN);
            cont.overheatAlarm =
                new TemperatureAlarm(cont.tank, TEMP_ALARM_UPPER_THRESHOLD, AlarmBehavior.GREATER_THAN);
            cont.undercoolAlarm =
                new TemperatureAlarm(cont.tank, TEMP_ALARM_LOWER_THRESHOLD, AlarmBehavior.SMALLER_THAN);
        }

        mixCont.overflowAlarm =
            new FillAlarm(mixCont.tank, FILL_ALARM_UPPER_THRESHOLD, AlarmBehavior.GREATER_THAN);
        mixCont.underflowAlarm =
            new FillAlarm(mixCont.tank, FILL_ALARM_LOWER_THRESHOLD, AlarmBehavior.SMALLER_THAN);
        mixCont.overheatAlarm =
            new TemperatureAlarm(mixCont.tank, TEMP_ALARM_UPPER_THRESHOLD, AlarmBehavior.GREATER_THAN);
        mixCont.undercoolAlarm =
            new TemperatureAlarm(mixCont.tank, TEMP_ALARM_LOWER_THRESHOLD, AlarmBehavior.SMALLER_THAN);
    }

    /**
     * Start loop that updates the values
     */
    private void startMainLoop() {
        if (stepTimer != null) {
            stepTimer.cancel();
        }
        stepTimer = new Timer(true);
        stepTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!overflow) {
                    simulator.tick();
                    updateServerValues();
                }
            }
        }, 0, 100);
    }

    /**
     * Update values from model inside the servers
     */
    private void updateServerValues() {
        for (TankContainer cont: tanks) {
            if (cont.server == null) {
                continue;
            }
            cont.server.setInputFlowRate(cont.tank.getInPipe().getValveThreshold());
            cont.server.setColor(cont.tank.getLiquid().getColor().getRGB());
            cont.server.setFillLevel(cont.tank.getLiquid().getAmount());
            cont.server.setOutputFlowRate(cont.tank.getOutPipe().getValveThreshold());
            cont.server.setTemperature(cont.tank.getLiquid().getTemperature());

            cont.server.setOverflowAlarm(cont.overflowAlarm.isAlarmTriggered());
            cont.server.setUnderflowAlarm(cont.underflowAlarm.isAlarmTriggered());
            cont.server.setOverheatAlarm(cont.overheatAlarm.isAlarmTriggered());
            cont.server.setUndercoolAlarm(cont.undercoolAlarm.isAlarmTriggered());

            if (cont.tank.getFillLevel() > 1 && !overflow && !resetInProgress) {
                overflow = true;
                showOverflow(cont.tank);
            }
        }

        if (mixCont.server == null) {
            return;
        }
        mixCont.server.setMotorSpeed(mixCont.tank.getMotor().getRPM());
        mixCont.server.setColor(mixCont.tank.getLiquid().getColor().getRGB());
        mixCont.server.setFillLevel(mixCont.tank.getLiquid().getAmount());
        mixCont.server.setOutputFlowRate(mixCont.tank.getOutPipe().getValveThreshold());
        mixCont.server.setTemperature(mixCont.tank.getLiquid().getTemperature());

        mixCont.server.setOverflowAlarm(mixCont.overflowAlarm.isAlarmTriggered());
        mixCont.server.setUnderflowAlarm(mixCont.underflowAlarm.isAlarmTriggered());
        mixCont.server.setOverheatAlarm(mixCont.overheatAlarm.isAlarmTriggered());
        mixCont.server.setUndercoolAlarm(mixCont.undercoolAlarm.isAlarmTriggered());

        if (mixCont.tank.getFillLevel() > 1 && !overflow && !resetInProgress) {
            overflow = true;
            showOverflow(mixCont.tank);
        }
    }

    private void showOverflow(AbstractTank tank) {
        controlInterface.setControlsDisabled(true);
        Platform.runLater(() -> currentSimulationView.showOverflow(tank));
        if (currentScenario != null) {
            currentScenario.cancelScenario();
            currentSimulationView.scenarioFinished();
        }
    }

    /**
     * Called bx JavaFx to start drawing the UI
     * @param primaryStage The stage to draw the main window on
     */
    public void start(Stage primaryStage) {
        controlInterface = new SimulationControlWindow(productionSite);

        currentSimulationView = new SimulationMainWindow(productionSite);
        currentSimulationView.start(primaryStage);
        setupView(primaryStage);

        currentSimulationView.setProgressIndicatorVisible(true);
        new Thread(() -> {
            try {
                initialize();
                Platform.runLater(() -> currentSimulationView.setProgressIndicatorVisible(false));
            } catch (UaException | InterruptedException | ExecutionException ex) {
                Platform.runLater(() -> {
                    currentSimulationView.showOPCUAServerError("Could not start OPC UA servers: " +
                        ex.getLocalizedMessage());
                    settingsInterface.show();
                });
            }
        }).start();
    }

    private void setupView(Stage primaryStage) {
        Stage help = new HelpDialog();
        Stage about = new AboutDialog();
        settingsInterface = new SimulationSettingsWindow(settingsWrapper);

        primaryStage.setOnHiding((event) -> {
            help.hide();
            about.hide();
            settingsInterface.close();
            controlInterface.close();
        });

        currentSimulationView.setSettingsButtonHandler(actionEvent -> settingsInterface.show());
        currentSimulationView.setControlButtonHandler(actionEvent -> controlInterface.show());
        currentSimulationView.setAboutButtonHandler(actionEvent -> about.show());
        currentSimulationView.setHelpButtonHandler(actionEvent -> help.show());

        currentSimulationView.setScenarioStartListener(this::startScenario);
        currentSimulationView.setScenarioStopListener(() -> {
            if (currentScenario != null) {
                currentScenario.cancelScenario();
                currentSimulationView.scenarioFinished();
                controlInterface.setControlsDisabled(false);
            }
        });

        currentSimulationView.setResetListener((event) -> {
            if (currentScenario != null) {
                currentScenario.cancelScenario();
                currentSimulationView.scenarioFinished();
                controlInterface.setControlsDisabled(false);
            }

            startMainLoop();

            resetInProgress = true;
            productionSite.reset();
            overflow = false;
            resetInProgress = false;
            controlInterface.setControlsDisabled(false);
        });

        controlInterface.setValveListener((pipe, number) -> {
            pipe.setValveThreshold(number);
            updateServerValues();
        });
        controlInterface.setTemperatureListener((tankSelector, number) -> {
            simulator.setInputTemperature(tankSelector, number);
            updateServerValues();
        });
        controlInterface.setMotorListener(rpm -> {
            productionSite.getMixTank().getMotor().setRPM(rpm);
            updateServerValues();
        });
        settingsInterface.setSettingsChangedListener(actionEvent -> {
            currentSimulationView.setProgressIndicatorVisible(true);
            new Thread(() -> {
                Platform.runLater(() -> settingsInterface.close());
                if (!reSetupServer()) {
                    Platform.runLater(() -> settingsInterface.show());
                    return;
                }
                startMainLoop();
                currentSimulationView.setProgressIndicatorVisible(false);
            }).start();
        });
    }

    private void startScenario(String file) {
        try {
            ScenarioFile scenarioFile = new ScenarioFile(file);
            currentScenario = scenarioFile.getScenario();
        } catch (ParserException | IOException ex) {
            currentSimulationView.showScenarioError(ex.getMessage());
            currentSimulationView.scenarioFinished();
            return;
        }
        currentSimulationView.scenarioStarted();
        currentScenario.setProductionSite(productionSite);
        currentScenario.setScenarioFinishedListener(() -> {
            currentSimulationView.scenarioFinished();
            controlInterface.setControlsDisabled(false);
        });
        controlInterface.setControlsDisabled(true);
        resetInProgress = true;
        productionSite.reset();
        resetInProgress = false;
        currentScenario.startScenario();
    }

    /**
     * Called when the last window is closed
     */
    public void stop() {
        stepTimer.cancel();
        System.out.println("Stopped simulation thread");
        try {
            for (TankContainer cont : tanks) {
                if (cont.server == null) {
                    continue;
                }
                cont.server.stop();
                cont.server = null;
            }
            if (mixCont.server != null) {
                mixCont.server.stop();
                mixCont.server = null;
            }
        } catch (InterruptedException | ExecutionException ex) {
            System.err.println("Couldn't stop OPC UA servers, continuing: " + ex.getMessage());
        }
        AbstractTankServer.releaseSharedResources();
    }

    /**
     * Groups all tank related attributes together.
     */
    private class TankContainer {
        private TankSelector selector;
        private Tank tank;
        private TankServer server;

        private FillAlarm overflowAlarm;
        private FillAlarm underflowAlarm;
        private TemperatureAlarm overheatAlarm;
        private TemperatureAlarm undercoolAlarm;
    }

    /**
     * Groups all mixtank related attributes together.
     */
    private class MixTankContainer {
        private MixTank tank;
        private MixTankServer server;

        private FillAlarm overflowAlarm;
        private FillAlarm underflowAlarm;
        private TemperatureAlarm overheatAlarm;
        private TemperatureAlarm undercoolAlarm;
    }
}
