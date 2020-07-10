package codeprocessor;

/**
 * @author Anika Nietzer 
 * 			A {@code CodeProcessor} represents a unit for compiling
 *         	and processing java-sourcecode to java-bytecode.
 */
public class CodeProcessor {

	private String className;
	private String errorMessage;
	private String packageName;
	private boolean success;

	/**
	 * Creates a {@code CodeProcessor} with the given code fragment and compiles
	 * it to java-bytecode.
	 * 
	 * @param code
	 *            the java-code fragment, that will be compiled
	 */
	public CodeProcessor(String code) {
	}

	/**
	 * Returns the name of the compiled class.
	 * 
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns an error message, in case of failure.
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns the name of the package of the compiled class.
	 * 
	 * @return the packageName.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * True if the compiling process was successful, false otherwise.
	 * 
	 * @return if the compiling process was successful
	 */
	public boolean wasSuccessful() {
		return success;
	}
}
