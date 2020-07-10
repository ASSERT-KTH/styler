package edu.kit.pse.osip.simulation.view.main;

import edu.kit.pse.osip.core.SimulationConstants;
import edu.kit.pse.osip.core.model.base.AbstractTank;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class visualizes a temperature sensor.
 */
public class TemperatureSensorDrawer extends edu.kit.pse.osip.simulation.view.main.ObjectDrawer {

    private int rows;
    private int cols;
    private AbstractTank tank;

    /**
     * Generates a new drawer for temperature sensors		
     * @param pos The center of the drawer
     * @param tank The tank to get the attributes from
     * @param rows The number of rows in which the Tanks are ordered.
     * @param cols The number of columns in which the Tanks are ordered
     */
    public TemperatureSensorDrawer(Point2D pos, AbstractTank tank, int rows, int cols) {
        super(pos);
        this.tank = tank;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * The Drawer draws itself onto the GraphicsContext at its getPosition().
     * @param context The context that the object draws itself onto
     */
    @Override
    public final void draw(GraphicsContext context, long currentTime) {
        Canvas canvas = context.getCanvas();

        double temp = (tank.getLiquid().getTemperature() - SimulationConstants.MIN_TEMPERATURE)
                / (SimulationConstants.MAX_TEMPERATURE - SimulationConstants.MIN_TEMPERATURE);

        double totalHeight = canvas.getHeight() - 25;
        double totalWidth = canvas.getWidth();

        context.setFill(Color.WHITE);
        context.setStroke(Color.BLACK);
        context.setLineWidth(2);

        double outerTopLeftX = (getPosition().getX() - ViewConstants.TEMP_WIDTH / cols) * totalWidth;
        double outerTopLeftY = (getPosition().getY() - ViewConstants.TEMP_HEIGHT / rows) * totalHeight;
        double outerWidth = (ViewConstants.TEMP_WIDTH / cols) * totalWidth;
        double outerHeight = (ViewConstants.TEMP_HEIGHT / rows) * totalHeight;

        double innerTopLeftX = (getPosition().getX() - (ViewConstants.TEMP_WIDTH - 0.015) / cols) * totalWidth;
        double innerTopLeftY = (getPosition().getY() - (ViewConstants.TEMP_HEIGHT - 0.015) / rows) * totalHeight;
        double innerWidth = ((ViewConstants.TEMP_WIDTH - 0.03) / cols) * totalWidth;
        double innerHeight = ((ViewConstants.TEMP_HEIGHT - 0.03) / rows) * totalHeight;

        context.fillRect(outerTopLeftX, outerTopLeftY, outerWidth, outerHeight);
        context.strokeRect(outerTopLeftX, outerTopLeftY, outerWidth, outerHeight);
        context.strokeRect(innerTopLeftX, innerTopLeftY, innerWidth, innerHeight);

        double tempTopLeftX = (getPosition().getX() - (ViewConstants.TEMP_WIDTH - 0.015) / cols) * totalWidth;
        double tempTopLeftY = (getPosition().getY() - (ViewConstants.TEMP_HEIGHT - 0.015 - (1.0 - temp)
                * ViewConstants.TEMP_HEIGHT) / rows) * totalHeight;
        double tempWidth = ((ViewConstants.TEMP_WIDTH - 0.03) / cols) * totalWidth;
        double tempHeight = ((ViewConstants.TEMP_HEIGHT - 0.03 - (1.0 - temp)
                * ViewConstants.TEMP_HEIGHT) / rows) * totalHeight;

        context.setFill(Color.RED);
        context.fillRect(tempTopLeftX, tempTopLeftY, tempWidth, tempHeight);
    }
}
