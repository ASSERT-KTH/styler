package edu.kit.pse.osip.monitoring.view.dashboard;

import edu.kit.pse.osip.core.model.base.MixTank;

/**
 * Visualization for the mixtank.
 */
public class MixTankVisualization extends edu.kit.pse.osip.monitoring.view.dashboard.AbstractTankVisualization {
    /**
     * Visualization of the motor speed.
     */
    private GaugeVisualization motorSpeed;
    /**
     * Visualization of the mixed color.
     */
    private ColorVisualization color;
    /**
     * Creates a new visualization.
     * @param tank The tank to display
     */
    protected MixTankVisualization (MixTank tank) {
        throw new RuntimeException("Not implemented!");
    }
}
