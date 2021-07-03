package edu.kit.pse.osip.simulation.view.control;

/**
 * This class contains the controls for a single tank in the simulation.
 */
public abstract class AbstractTankTab extends javafx.scene.control.TabPane {
    /**
     * Gets the value of outFlow.
     * @return The value of outFlow.
     */
    public final int getOutFlow () {
        throw new RuntimeException("Not implemented!");
    }
}
