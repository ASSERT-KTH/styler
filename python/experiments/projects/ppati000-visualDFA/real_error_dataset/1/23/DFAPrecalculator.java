package controller;

import dfa.framework.DFAException;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.DFAPrecalcController;
import dfa.framework.LatticeElement;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;

/**
 * 
 * @author Anika Nietzer Unit, used by the {@code DFAExecution} to precalculate
 *         all analysis steps in a different thread.
 *
 */
public class DFAPrecalculator implements Runnable {

    private DFAExecution<? extends LatticeElement> dfaExecution;
    private DFAFactory<? extends LatticeElement> factory;
    private Worklist worklist;
    private SimpleBlockGraph simpleBlockGraph;
    private DFAPrecalcController precalcController;

    private Controller controller;

    /**
     * Creates a new {@code DFAPrecalculator} to calculate all steps at the
     * beginning of the analysis.
     * 
     * @param factory
     *            {@code DFAFactory} that will be used
     * @param worklist
     *            {@code Worklist} that will be used for the analysis
     * @param simpleBlockGraph
     *            {@code SimpleBlockGraph} on that the analysis will be
     *            performed
     * @param precalcController
     *            {@code DFAPrecalcController}
     * @param controller {@code Controller} to inform the user about an exception
     * 
     */
    public DFAPrecalculator(DFAFactory<? extends LatticeElement> factory, Worklist worklist,
            SimpleBlockGraph simpleBlockGraph, DFAPrecalcController precalcController, Controller controller) {
        if (factory == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        if (worklist == null) {
            throw new IllegalArgumentException("worklist must not be null");
        }
        if (simpleBlockGraph == null) {
            throw new IllegalArgumentException("simpleBlockGraph must not be null");
        }
        this.factory = factory;
        this.worklist = worklist;
        this.simpleBlockGraph = simpleBlockGraph;
        this.precalcController = precalcController;
        this.controller = controller;
    }

    @Override
    /**
     * Used during creation of a new thread, that performs the steps of the
     * analysis.
     */
    public void run() {
        try {
        this.dfaExecution = new DFAExecution(this.factory, this.worklist, this.simpleBlockGraph,
                this.precalcController);
        } catch(DFAException e) {
            this.controller.createExceptionBox(e.getMessage());
            this.precalcController.stopPrecalc();
        }
    }

    /**
     * Returns the dfaExecution.
     * 
     * @return created instance of {@code DFAExecution}
     */
    public DFAExecution<? extends LatticeElement> getDFAExecution() {
        if (this.dfaExecution == null) {
            throw new IllegalStateException("dfaExecution must no be null");
        }
        return this.dfaExecution;
    }

}
