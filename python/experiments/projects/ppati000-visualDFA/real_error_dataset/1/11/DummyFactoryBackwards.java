package dfa.analyses.testanalyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * A factory for backwards dummy analyses.
 * 
 * @author Sebastian Rauch
 *
 */
public class DummyFactoryBackwards extends DFAFactory<DummyElement> {

	@Override
	public String getName() {
		return "Dummy Analysis Backwards";
	}

	@Override
	public DFADirection getDirection() {
		return DFADirection.BACKWARD;
	}

	@Override
	public DataFlowAnalysis<DummyElement> getAnalysis(SimpleBlockGraph blockGraph) {
		return new DummyAnalysis(blockGraph);
	}

}