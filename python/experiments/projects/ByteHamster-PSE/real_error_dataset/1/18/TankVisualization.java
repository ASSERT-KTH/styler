package edu.kit.pse.osip.monitoring.view.dashboard;

import edu.kit.pse.osip.core.model.base.Tank;

/**
 * Visualization for a normal tank.
 */
public class TankVisualization extends edu.kit.pse.osip.monitoring.view.dashboard.AbstractTankVisualization {
    /**
     * Visualization of the supply pipe.
     */
    private BarVisualization supply;
    /**
     * Creates a new visualization.
     * @param tank The tank to display
     */
    protected TankVisualization (Tank tank) {
        throw new RuntimeException("Not implemented!");
    }
}
