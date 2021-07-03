package dfa.analyses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dfa.framework.BlockState;
import dfa.framework.Initializer;
import dfa.framework.SimpleBlockGraph;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.Type;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code ConstantFoldingTransition} performs the initialization for a {@code ConstantFoldingAnalysis}.
 */
public class ConstantFoldingInitializer implements Initializer<ConstantFoldingElement> {

    private SimpleBlockGraph blockGraph;

    /**
     * Creates a {@code ConstantFoldingInitializer} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public ConstantFoldingInitializer(SimpleBlockGraph blockGraph) {
        this.blockGraph = blockGraph;
    }

    @Override
    public Map<Block, BlockState<ConstantFoldingElement>> getInitialStates() {
        List<Block> heads = blockGraph.getHeads();
        if (heads.size() < 1) {
            throw new IllegalStateException("no entry point found");
        } else if (heads.size() > 1) {
            throw new IllegalStateException("multiply entry points found");
        }

        Chain<Local> locals = blockGraph.getBody().getLocals();

        Map<JimpleLocal, ConstantFoldingElement.Value> initialBottomMap = new TreeMap<>(
                ConstantFoldingElement.COMPARATOR);
        Map<JimpleLocal, ConstantFoldingElement.Value> initialHeadMap = new TreeMap<>(
                ConstantFoldingElement.COMPARATOR);

        ConstantFoldingElement.Value nullInt = new ConstantFoldingElement.Value(IntConstant.v(0));
        ConstantFoldingElement.Value nullLong = new ConstantFoldingElement.Value(LongConstant.v(0));

        for (Local l : locals) {
            if (!(l instanceof JimpleLocal)) {
                throw new IllegalStateException("no jimple local");
            }
            Type t = l.getType();
            if (!(t instanceof PrimType)) {
                continue;
            }
            if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                    || t instanceof IntType) {
                initialBottomMap.put((JimpleLocal) l, ConstantFoldingElement.Value.getBottom());
                initialHeadMap.put((JimpleLocal) l, nullInt);
            } else if (t instanceof LongType) {
                initialBottomMap.put((JimpleLocal) l, ConstantFoldingElement.Value.getBottom());
                initialHeadMap.put((JimpleLocal) l, nullLong);
            }
        }

        Block head = heads.get(0);
        List<Block> blocks = blockGraph.getBlocks();

        ConstantFoldingElement headIn = new ConstantFoldingElement(initialHeadMap);
        ConstantFoldingElement defaultIn = new ConstantFoldingElement(initialBottomMap);

        BlockState<ConstantFoldingElement> headState = new BlockState<ConstantFoldingElement>(headIn, defaultIn);
        BlockState<ConstantFoldingElement> defaultState = new BlockState<ConstantFoldingElement>(defaultIn, defaultIn);

        Map<Block, BlockState<ConstantFoldingElement>> initialMap = new HashMap<Block, BlockState<ConstantFoldingElement>>();

        for (Block b : blocks) {
            if (b == head) {
                initialMap.put(b, headState);
            } else {
                initialMap.put(b, defaultState);
            }
        }

        return initialMap;
    }

}
