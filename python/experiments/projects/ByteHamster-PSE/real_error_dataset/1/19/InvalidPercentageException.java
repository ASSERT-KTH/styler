package edu.kit.pse.osip.core.utils.formatting;

/**
 * This exception signifies that a percentage is invalid.
 */
public class InvalidPercentageException extends java.lang.IllegalArgumentException {
    private static final long serialVersionUID = 6133492534710708929L;

    /**
     * Creates a new InvalidPercentageException
     * @param tried The string that was tried to be parsed
     * @param reason Explains why the check failed
     */
    public InvalidPercentageException (String tried, String reason) {
        throw new RuntimeException("Not implemented!");
    }
}
