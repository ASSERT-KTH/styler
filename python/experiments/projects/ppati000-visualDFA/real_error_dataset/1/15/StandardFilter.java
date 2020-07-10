package codeprocessor;

import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer 
 * 			Implementation of the Interface {@code Filter}. Used if
 *          all inherited methods should be filtered out.
 *
 */
public class StandardFilter extends Filter {

	/**
	 * Filter for a given method, that decides if the method should be filtered
	 * out or not.
	 * 
	 * @param method
	 *            signature of the method
	 * @return if the method passes the filter or not
	 */
	public boolean filter(SootMethod method) {
	    return !method.isJavaLibraryMethod() && super.filterTaint(method);
	}

}
