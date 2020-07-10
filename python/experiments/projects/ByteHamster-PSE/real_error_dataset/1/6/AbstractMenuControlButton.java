package edu.kit.pse.osip.simulation.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Handles a click on the control menu button
 *
 * @author Niko Wilhelm
 * @version 1.0
 */
public abstract class AbstractMenuControlButton implements EventHandler<ActionEvent>{

    /**
     * Creates a new handler.
     * @param controlWindow The current control window
     */
    protected AbstractMenuControlButton(SimulationControlWindow controlWindow) {
        throw new RuntimeException("Not implemented!");
    }

    /**
     * Handles a click on the settings menu button in the monitoring view.
     * @param event The occured event.
     */
    @Override
    public abstract void handle(ActionEvent event);
}
