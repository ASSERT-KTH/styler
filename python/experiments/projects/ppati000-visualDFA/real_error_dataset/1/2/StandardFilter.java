package codeprocessor;

/**
 * 
 * @author Anika Nietzer 
 * 			Implementation of the Interface {@code Filter}. Used if
 *          all inherited methods should be filtered out.
 *
 */
public class StandardFilter implements Filter {

	@Override
	/**
	 * Filter for a given method, that decides if the method should be filtered
	 * out or not.
	 * 
	 * @param signature
	 *            signature of the method
	 * @return if the method should be filtered out or not
	 */
	public boolean filter(String signature) {
		return false;
	}

}
