package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.toolkits.graph.Block;

/**
 * A {@code DFAExecution} precalculates a {@code DataFlowAnalysis} and provides the intermediate analysis-steps.
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code DFAExecution}
 * 
 * @author Sebastian Rauch
 */
public class DFAExecution<E extends LatticeElement> {

    private final Worklist initialWorklist;

    private final DFADirection direction;

    private final DataFlowAnalysis<E> dfa;

    private final ControlFlowGraph cfg;

    private List<AnalysisState<E>> analysisStates = new ArrayList<>();
    private List<Integer> blockSteps = new ArrayList<>();

    private int currentElementaryStep = 0;
    private int currentBlockStep = 0;

    /**
     * Creates a {@code DFAExecution} from a given {@code DFAFactory}, an initial {@code Worklist} and a
     * {@code SimpleBlockGraph}.
     * 
     * @param dfaFactory
     *        a {@code DFAFactory} from which a {@code DataFlowAnalysis} will be generated
     * @param initialWorklist
     *        an (empty) worklist to use in this {@code DFAExecution}
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis is based on
     * @param precalcController
     *        a {@code DFAPrecalcController} to control the precalculation of this {@code DFAExecution}
     * 
     * @throws IllegalArgumentException
     *         if any of {@code initialWorklist} or {@code blockGraph} is {@code null}
     * 
     * @throws NullPointerException
     *         if {@code dfaFactory} is {@code null}
     */
    public DFAExecution(DFAFactory<E> dfaFactory, Worklist initialWorklist, SimpleBlockGraph blockGraph,
            DFAPrecalcController precalcController) {
        if (initialWorklist == null) {
            throw new IllegalArgumentException("initialWorklist must not be null");
        }

        if (blockGraph == null) {
            throw new IllegalArgumentException("blockGraph must not be null");
        }

        this.initialWorklist = initialWorklist;
        this.direction = dfaFactory.getDirection();

        dfa = dfaFactory.getAnalysis(blockGraph);

        this.cfg = new ControlFlowGraph(blockGraph);

        precalc(precalcController);
    }

    private DFAExecution(DFAExecution<E> copyFrom) {
        this.initialWorklist = copyFrom.initialWorklist;
        this.direction = copyFrom.getDirection();
        this.dfa = copyFrom.dfa;
        this.cfg = copyFrom.getCFG();

        this.analysisStates = copyFrom.analysisStates;
        this.blockSteps = copyFrom.blockSteps;

        setCurrentElementaryStep(copyFrom.getCurrentElementaryStep());
        setCurrentBlockStep(copyFrom.getCurrentBlockStep());
    }

    /**
     * Returns the {@code ControlFlowGraph} that is used to execute the dataflow-analysis.
     * 
     * @return the {@code ControlFlowGraph} used to execute the dataflow-analysis
     */
    public ControlFlowGraph getCFG() {
        return cfg;
    }

    /**
     * Returns the direction of the {@code DataFlowAnalysis} that is executed.
     * 
     * @return the direction of the {@code DataFlowAnalysis} that is executed
     */
    public DFADirection getDirection() {
        return direction;
    }

    /**
     * Returns the total number of elementary-steps.
     * 
     * @return the total number of elementary-steps
     */
    public int getTotalElementarySteps() {
        return analysisStates.size();
    }

    /**
     * Returns the total number of block-steps.
     * 
     * @return the total number of block-steps
     */
    public int getTotalBlockSteps() {
        return blockSteps.size();
    }

    /**
     * Returns the current elementary-step.
     * 
     * @return the current elementary-step, always in range {@code [0, ..., getTotalElementarySteps() - 1]}
     */
    public int getCurrentElementaryStep() {
        return currentElementaryStep;
    }

    /**
     * Sets the current elementary-step. The current block-step is updated to be consistent with the current
     * elementary-step.
     * 
     * @param elementaryStep
     *        the elementary-step to set as current elementary-step
     * 
     * @throws IllegalArgumentException
     *         if {@code elementaryStep} is not in range {@code [0, ..., getTotalElementarySteps() - 1]}
     */
    public void setCurrentElementaryStep(int elementaryStep) {
        if (elementaryStep < 0 || elementaryStep >= getTotalElementarySteps()) {
            throw new IndexOutOfBoundsException("invalid elementaryStep: " + elementaryStep);
        }

        currentElementaryStep = elementaryStep;
        int idx = Collections.binarySearch(blockSteps, currentElementaryStep);

        if (idx < 0) { // currentElementaryStep has not been found
            idx = -(idx + 1) - 1; // this gives the correct index of the corresponding block-step
        }
        currentBlockStep = idx;
    }

    /**
     * Returns the current block-step.
     * 
     * @return the current block-step, always in range {@code [0, ..., getTotalBlockSteps() - 1]}
     */
    public int getCurrentBlockStep() {
        return currentBlockStep;
    }

    /**
     * Sets the current block-step. The current elementary-step is updated to be consistent with the current block-step.
     * 
     * @param blockStep
     *        the block-step to set as current block-step
     * 
     * @throws IllegalArgumentException
     *         if {@code blockStep} is not in range {@code [0, ..., getTotalBlockSteps() - 1]}
     */
    public void setCurrentBlockStep(int blockStep) {
        if (blockStep < 0 || blockStep >= getTotalBlockSteps()) {
            throw new IndexOutOfBoundsException("invalid blockStep: " + blockStep);
        }

        currentBlockStep = blockStep;
        currentElementaryStep = blockSteps.get(blockStep);
    }

    /**
     * Increases the current elementary-step (by 1) if possible.
     * 
     * @return {@code true} if there was a next elementary-step, {@code false} otherwise
     */
    public boolean nextElementaryStep() {
        int nextStep = getCurrentElementaryStep() + 1;
        if (nextStep < getTotalElementarySteps()) {
            setCurrentElementaryStep(nextStep);
            return true;
        }

        return false;
    }

    /**
     * Decreases the current elementary-step by 1 if possible.
     * 
     * @return {@code true} if there was a next elementary-step, {@code false} otherwise
     */
    public boolean previousElementaryStep() {
        int prevStep = getCurrentElementaryStep() - 1;
        if (prevStep >= 0) {
            setCurrentElementaryStep(prevStep);
            return true;
        }

        return false;
    }

    /**
     * Advances the current block-step (by 1) if possible.
     * 
     * @return {@code true} if there was a next block-step, {@code false} otherwise
     */
    public boolean nextBlockStep() {
        int nextBlockStep = getCurrentBlockStep() + 1;
        if (nextBlockStep < getTotalBlockSteps()) {
            setCurrentBlockStep(nextBlockStep);
            return true;
        }

        return false;
    }

    /**
     * Decreases the current block-step by 1 if possible.
     * 
     * @return {@code true} if there was a next block-step, {@code false} otherwise
     */
    public boolean previousBlockStep() {
        int currentBlockStep = getCurrentBlockStep() - 1;
        if (currentBlockStep >= 0) {
            setCurrentBlockStep(currentBlockStep);
            return true;
        }

        return false;
    }

    /**
     * Returns whether the current elementary-step is at {@code ElementaryBlock} that has a breakpoint.
     * 
     * @return {@code true} if the {@code ElementaryBlock} of the current elementary-step has a breakpoint,
     *         {@code false} otherwise
     */
    public boolean isAtBreakpoint() {
        AnalysisState<? extends LatticeElement> currentState = getCurrentAnalysisState();
        BasicBlock bBlock = currentState.getCurrentBasicBlock();

        int eBlockIdx = currentState.getCurrentElementaryBlockIndex();
        if (bBlock == null || eBlockIdx < 0 || eBlockIdx >= bBlock.getElementaryBlockCount()) {
            return false;
        }

        return bBlock.getElementaryBlock(eBlockIdx).hasBreakpoint();
    }

    /**
     * Returns the current {@code AnalysisState}, i. e. the state of the analysis at the current elementary-step.
     * 
     * @return the current {@code AnalysisState}
     */
    public AnalysisState<E> getCurrentAnalysisState() {
        return analysisStates.get(currentElementaryStep);
    }

    /**
     * Creates a <em>shallow copy</em> of this {@code DFAExecution}. The returned copy shares the {@code AnalysisState}s
     * with the original but can have different current block- or elementary-steps.
     */
    @Override
    public DFAExecution<E> clone() {
        return new DFAExecution<E>(this);
    }

    private void precalc(DFAPrecalcController precalcCtrl) {
        Map<Block, BlockState<E>> initialStates = dfa.getInitialStates();

        BasicBlock startBlock = getStartBlock();
        if (startBlock == null) {
            throw new DFAException("there is no start block");
        }

        AnalysisState<E> initialState = new AnalysisState<E>(initialWorklist, startBlock, -1);

        List<BasicBlock> basicBlocks = cfg.getBasicBlocks();
        for (BasicBlock bBlock : basicBlocks) {
            Block sootBlock = bBlock.getSootBlock();
            BlockState<E> state = initialStates.get(sootBlock);

            LogicalColor lColor = bBlock.equals(startBlock) ? LogicalColor.CURRENT : LogicalColor.NOT_VISITED;
            initialState.setBlockState(bBlock, state);
            initialState.setColor(bBlock, lColor);

            // set all in- and out-states to null for the elementary-blocks
            List<ElementaryBlock> elementaryBlocks = bBlock.getElementaryBlocks();
            BlockState<E> nullState = new BlockState<E>(null, null);
            for (ElementaryBlock eBlock : elementaryBlocks) {
                initialState.setBlockState(eBlock, nullState);
            }

        }

        analysisStates.add(initialState);

        // elementary step 0 is always a block step
        blockSteps.add(0);

        iterateToFixpoint(initialState, precalcCtrl);
    }

    private void iterateToFixpoint(AnalysisState<E> initialState, DFAPrecalcController precalcCtrl) {
        Set<BasicBlock> visitedBasicBlocks = new HashSet<BasicBlock>();
        visitedBasicBlocks.add(getStartBlock());

        int elementaryStep = 1;
        AnalysisState<E> prevAnalysisState = initialState;

        while (true) {
            switch (precalcCtrl.getPrecalcState()) {
            case CALCULATING:
                break; // break switch
            case COMPLETED:
                return;
            case PAUSED:
                try {
                    Thread.sleep(precalcCtrl.getWaitTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            case STOPPED:
                precalcCtrl.setResult(this, false);
                return;
            default:
                throw new IllegalStateException("unknown precalc state: " + precalcCtrl.getPrecalcState());
            }

            BasicBlock prevBasicBlock = prevAnalysisState.getCurrentBasicBlock();

            AnalysisState<E> newAnalysisState = null;
            Worklist prevWorklist = prevAnalysisState.getWorklist();
            if (prevBasicBlock == null) {
                // no basic block currently selected, this begins a new basic block step (or completes calculation)

                if (prevWorklist.isEmpty()) {
                    // we are at a fixpoint
                    precalcCtrl.setResult(this, true);
                    return;
                }

                Worklist newWorklist = prevWorklist.clone();
                BasicBlock newBasicBlock = newWorklist.poll();
                visitedBasicBlocks.add(newBasicBlock);

                newAnalysisState = newState(prevAnalysisState, newWorklist, newBasicBlock, -1);

                // join predecessors out-states
                List<BasicBlock> preds = getPredecessors(newBasicBlock);
                Set<E> predOutStates = new HashSet<E>();

                for (BasicBlock p : preds) {
                    E predOutState = prevAnalysisState.getBlockState(p).getOutState();
                    predOutStates.add(predOutState);
                }

                E outStatesJoin = dfa.join(predOutStates);

                BlockState<E> prevBlockState = prevAnalysisState.getBlockState(newBasicBlock);
                BlockState<E> newBlockState = new BlockState<E>(outStatesJoin, prevBlockState.getOutState());
                newAnalysisState.setBlockState(newBasicBlock, newBlockState);

                updateColors(prevAnalysisState, newAnalysisState, visitedBasicBlocks);

                analysisStates.add(newAnalysisState);

                // this begins a new block step
                blockSteps.add(elementaryStep++);
                prevAnalysisState = newAnalysisState;
                continue;
            }

            // prevBasicBlock is not null (so this is not a new block step)
            int eBlockIdx = prevAnalysisState.getCurrentElementaryBlockIndex();
            if (prevBasicBlock.getElementaryBlockCount() == 0) {
                // handle empty basic block [e. g. artificial end block]
                E inState = prevAnalysisState.getBlockState(prevBasicBlock).getInState();
                newAnalysisState = finishBasicBlock(prevBasicBlock, inState, prevAnalysisState, visitedBasicBlocks);
            } else if (eBlockIdx < prevBasicBlock.getElementaryBlockCount()) {
                // handle non-empty basic block
                E prevOutState;
                ElementaryBlock nextElementaryBlock;
                if (eBlockIdx < 0) {
                    // first elementary block
                    prevOutState = prevAnalysisState.getBlockState(prevBasicBlock).getInState();
                    nextElementaryBlock = getElementaryBlock(prevBasicBlock, 0);
                } else if (eBlockIdx < prevBasicBlock.getElementaryBlockCount() - 1) {
                    // some elementary block that is not the first or last in the basic block
                    ElementaryBlock prevElementaryBlock = getElementaryBlock(prevBasicBlock, eBlockIdx);
                    prevOutState = prevAnalysisState.getBlockState(prevElementaryBlock).getOutState();
                    nextElementaryBlock = getElementaryBlock(prevBasicBlock, eBlockIdx + 1);
                } else {
                    ElementaryBlock prevElementaryBlock = getElementaryBlock(prevBasicBlock, eBlockIdx);
                    prevOutState = prevAnalysisState.getBlockState(prevElementaryBlock).getOutState();
                    nextElementaryBlock = null;
                }

                if (nextElementaryBlock == null) {
                    newAnalysisState =
                            finishBasicBlock(prevBasicBlock, prevOutState, prevAnalysisState, visitedBasicBlocks);
                } else {
                    E nextOutState = dfa.transition(prevOutState, nextElementaryBlock.getUnit());
                    BlockState<E> nextBlockState = new BlockState<E>(prevOutState, nextOutState);
                    newAnalysisState = newState(prevAnalysisState, prevWorklist.clone(), prevBasicBlock, ++eBlockIdx);
                    newAnalysisState.setBlockState(nextElementaryBlock, nextBlockState);
                }
            }

            analysisStates.add(newAnalysisState);
            prevAnalysisState = newAnalysisState;
            ++elementaryStep;
        }
    }

    private BasicBlock getStartBlock() {
        switch (getDirection()) {
        case FORWARD:
            return cfg.getStartBlock();
        case BACKWARD:
            return cfg.getEndBlock();
        default:
            throw new IllegalStateException("unknown direction: " + getDirection());
        }
    }

    private List<BasicBlock> getPredecessors(BasicBlock bBlock) {
        switch (direction) {
        case FORWARD:
            return cfg.getPredecessors(bBlock);
        case BACKWARD:
            return cfg.getSuccessors(bBlock);
        default:
            throw new IllegalStateException();
        }
    }

    private List<BasicBlock> getSuccessors(BasicBlock bBlock) {
        switch (direction) {
        case FORWARD:
            return cfg.getSuccessors(bBlock);
        case BACKWARD:
            return cfg.getPredecessors(bBlock);
        default:
            throw new IllegalStateException("unknown direction: " + getDirection());
        }
    }

    private AnalysisState<E> newState(AnalysisState<E> state, Worklist newWorklist, BasicBlock currentBBlock,
            int eBlockIdx) {
        Map<AbstractBlock, BlockState<E>> stateMap = new HashMap<AbstractBlock, BlockState<E>>(state.getStateMap());
        Map<BasicBlock, LogicalColor> colorMap = new HashMap<BasicBlock, LogicalColor>(state.getColorMap());

        AnalysisState<E> newState = new AnalysisState<E>(newWorklist, currentBBlock, eBlockIdx, stateMap, colorMap);
        newState.setCurrentElementaryBlockIndex(eBlockIdx);
        return newState;
    }

    /*
     * updates the color-mapping in newState according to prevState and the worklist and current BasicBlock of newState
     */
    private void updateColors(AnalysisState<E> prevState, AnalysisState<E> newState, Set<BasicBlock> visited) {
        List<BasicBlock> basicBlocks = cfg.getBasicBlocks();
        Worklist newWorklist = newState.getWorklist();

        for (BasicBlock basicBlock : basicBlocks) {
            LogicalColor newColor;
            if (basicBlock.equals(newState.getCurrentBasicBlock())) {
                newColor = LogicalColor.CURRENT;
            } else if (newWorklist.contains(basicBlock)) {
                newColor = LogicalColor.ON_WORKLIST;
            } else {
                if (visited.contains(basicBlock)) {
                    newColor = LogicalColor.VISITED_NOT_ON_WORKLIST;
                } else {
                    newColor = LogicalColor.NOT_VISITED;
                }
            }
            newState.setColor(basicBlock, newColor);
        }
    }

    private AnalysisState<E> finishBasicBlock(BasicBlock currentBBlock, E outState, AnalysisState<E> prevAnalysisState,
            Set<BasicBlock> visited) {
        Worklist newWorklist = prevAnalysisState.getWorklist().clone();
        BlockState<E> prevBlockState = prevAnalysisState.getBlockState(currentBBlock);

        List<BasicBlock> successors = getSuccessors(currentBBlock);
        E prevOutState = prevBlockState.getOutState();

        boolean outStateChanged = !outState.equals(prevOutState);
        for (BasicBlock bSucc : successors) {
            if (outStateChanged || !visited.contains(bSucc)) {
                newWorklist.add(bSucc);
            }
        }

        AnalysisState<E> newAnalysisState = newState(prevAnalysisState, newWorklist, null, -1);
        BlockState<E> newBlockState = new BlockState<E>(prevBlockState.getInState(), outState);
        newAnalysisState.setBlockState(currentBBlock, newBlockState);

        updateColors(prevAnalysisState, newAnalysisState, visited);
        return newAnalysisState;
    }

    private ElementaryBlock getElementaryBlock(BasicBlock bBlock, int eBlockIdx) {
        List<ElementaryBlock> elementaryBlocks = bBlock.getElementaryBlocks();
        switch (direction) {
        case FORWARD:
            return elementaryBlocks.get(eBlockIdx);
        case BACKWARD:
            return elementaryBlocks.get(bBlock.getElementaryBlockCount() - 1 - eBlockIdx);
        default:
            throw new IllegalStateException("unknown direction: " + getDirection());
        }
    }

}
