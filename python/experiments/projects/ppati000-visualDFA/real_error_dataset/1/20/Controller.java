package controller;

import java.util.List;
import javax.swing.JPanel;
import gui.ProgramFrame;

/**
 * 
 * @author Anika Nietzer 
 * 			Central unit, that is responsible for the communication
 *       	between the GUI and the remaining packages of the program.
 *
 */
public class Controller {

	private ProgramFrame programFrame;

	/**
	 * Creates a new {@code Controller} and additionally an
	 * {@code AnalysisLoader} and a {@code GraphUIController}.
	 */
	public Controller() {

	}

	/**
	 * Leads to the calculation of the next block by the DFAFramework and
	 * ensures that the {@code GraphUIController} actualizes the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a next block to show or not
	 */
	public boolean nextBlock() {
		return false;
	}

	/**
	 * Leads to the calculation of the next line by the DFAFramework and ensures
	 * that the {@code GraphUIController} updates the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a next line to show or not
	 */
	public boolean nextLine() {
		return false;
	}

	/**
	 * Leads to the calculation of the previous line by the DFAFramework and
	 * ensures that the {@code GraphUIController} updates the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a previous line to show or not
	 */
	public boolean previousLine() {
		return false;
	}

	/**
	 * Leads to the calculation of the previous block by the DFAFramework and
	 * ensures that the {@code GraphUIController} updates the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return whether there was a previous block to show or not
	 */
	public boolean previousBlock() {
		return false;
	}

	/**
	 * Leads to the calculation of a given step in the analysis from the
	 * DFAFramework and ensures that the {@code GraphUIController} updates
	 * the {@code VisualGraphPanel}.
	 * 
	 * @param step
	 *            step to show in the animation
	 * @return weather the step can be shown or not
	 */
	public boolean jumpToStep(int step) {
		return false;
	}

	/**
	 * Creates a new {@code AutoplayDriver} to replay the different steps of the
	 * analysis if a delay bigger than zero is selected or jumps to the last
	 * step of the analysis if the chosen delay is zero.
	 */
	public void play() {

	}

	/**
	 * Stops the {@code AutoplayDriver}.
	 */
	public void pause() {

	}

	/**
	 * Creates a new {@code CodeProcessor} to process the input of the user and
	 * creates a {@code SimpleBlockGraph} of the chosen method. Precalculates the steps of the
	 * analysis with the {@code DFAPrecalculator}. The {@code GraphUIController}
	 * is invoked to display the CFG. The {@code ControlPanel}, the
	 * {@code StatePanel} and the {@code VisualGraphPanel} are activated and the
	 * {@code InputPanel} is deactivated.
	 */
	public void startAnalysis() {

	}

	/**
	 * Deletes the actual {@code DFAExecution} and the content of the
	 * {@code VisualGraphPanel} through the {@code GraphUIController}. The
	 * {@code ControlPanel}, the {@code StatePanel} and the
	 * {@code VisualGraphPanel} are deactivated and the {@code InputPanel}
	 * is activated.
	 */
	public void stopAnalysis() {

	}

	/**
	 * Sets the {@code ProgramFrame} to programFrame.
	 * 
	 * @param programFrame
	 *            programFrame that should be set
	 */
	public void setProgramFrame(ProgramFrame programFrame) {
		this.programFrame = programFrame;
	}

	/**
	 * Returns a list of the names of the analyses that were found during
	 * program start by the {@code AnalysisLoader}.
	 * 
	 * @return list of names of the found analyses
	 */
	public List<String> getAnalysis() {
		List<String> list = null;
		return list;
	}

	/**
	 * Returns the {@code VisualGraphPanel} created by the
	 * {@code GraphUIController).
	 * 
	 * @return the instance of {@code VisualGraphPanel}
	 */
	public JPanel getVisualGraphPanel() {
		return null;
	}
}
