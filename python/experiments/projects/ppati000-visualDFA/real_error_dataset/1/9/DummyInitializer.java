package dfa.analyses;

import dfa.framework.BlockState;
import dfa.framework.Initializer;
import dfa.framework.SimpleBlockGraph;
import soot.toolkits.graph.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick Petrovic
 *
 *         A {@code DummyTransition} performs the initialization for a {@code DummyAnalysis}.
 */
public class DummyInitializer implements Initializer<DummyElement> {

    private SimpleBlockGraph blockGraph;

    /**
     * Creates a {@code DummyInitializer} for the given {@code SimpleBlockGraph}.
     *
     * @param blockGraph
     *         the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public DummyInitializer(SimpleBlockGraph blockGraph) {
        this.blockGraph = blockGraph;
    }

    @Override
    public Map<Block, BlockState<DummyElement>> getInitialStates() {
        List<Block> blocks = blockGraph.getBlocks();

        BlockState<DummyElement> defaultState = new BlockState<>(new DummyElement(DummyElement.ValueType.TOP), new DummyElement(DummyElement.ValueType.BOTTOM));

        Map<Block, BlockState<DummyElement>> initialMap = new HashMap<>();

        for (Block b : blocks) {
            initialMap.put(b, defaultState);
        }

        return initialMap;
    }

}
