package edu.kit.pse.osip.core.io.files;

/**
 * Exception class for exceptions in parsing
 */
public class ParserException {
    /**
     * Constructor of ParserException
     * @param msg Message
     * @param line The line in which the exception occured
     * @param character The character within the line where the error occured
     */
    public ParserException (String msg, int line, int character) {
        throw new RuntimeException("Not implemented!");
    }
}
