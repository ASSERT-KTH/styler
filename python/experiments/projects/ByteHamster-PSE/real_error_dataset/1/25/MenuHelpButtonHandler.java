package edu.kit.pse.osip.simulation.controller;

import edu.kit.pse.osip.simulation.view.dialogs.HelpDialog;

/**
 * Handles a click on the help menu button in the simulation view.
 */
public class MenuHelpButtonHandler {
    /**
     * Handles a click on the help menu button in the simulation view.
     * @param event The ocurred event.
     */
    public final void handle (javafx.event.ActionEvent event) {
        HelpDialog d = new HelpDialog();
        d.show();
    }
}
