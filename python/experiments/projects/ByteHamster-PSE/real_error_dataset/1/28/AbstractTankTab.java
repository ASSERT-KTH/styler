package edu.kit.pse.osip.simulation.view.control;

import javafx.scene.control.Tab;

/**
 * This class contains the controls for a single tank in the simulation.
 */
public abstract class AbstractTankTab extends Tab {

    public AbstractTankTab(String name) {
        super(name);
    }

    /**
     * Gets the value of outFlow.
     * @return The value of outFlow.
     */
    public final int getOutFlow () {
        throw new RuntimeException("Not implemented!");
    }

    /**
     * Gets the value of temperature.
     * @return The value of temperature.
     */
    public final int getTemperature() {
        throw new RuntimeException("Not implemented!");
    }
}
