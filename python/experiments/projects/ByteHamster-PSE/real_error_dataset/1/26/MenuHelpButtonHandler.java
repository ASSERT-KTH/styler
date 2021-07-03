package edu.kit.pse.osip.simulation.controller;

import edu.kit.pse.osip.simulation.view.dialogs.HelpDialog;
import javafx.event.ActionEvent;

/**
 * Handles a click on the help menu button in the simulation view.
 *
 * @version 0.1
 * @author Niko Wilhelm
 */
public class MenuHelpButtonHandler extends AbstractMenuHelpButton{

    /**
     * Handles a click on the help menu button in the simulation view.
     * @param event The ocurred event.
     */
    public void handle(ActionEvent event) {
        HelpDialog d = new HelpDialog();
        d.show();
    }
}
