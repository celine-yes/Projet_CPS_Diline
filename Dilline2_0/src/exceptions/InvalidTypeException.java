package exceptions;

/**
 * Custom exception class to indicate that an invalid or inappropriate type has been used or encountered.
 * This exception can be thrown in scenarios where type constraints are violated or unexpected types are processed.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class InvalidTypeException extends Exception {
	
	private static final long serialVersionUID = 1L;  // UID for serialization, ensures compatibility during deserialization

    /**
     * Constructs a new InvalidTypeException with a specified detail message.
     * The detail message provides more information about the error condition encountered.
     *
     * @param message the detail message, which provides more information about the exception cause.
     */
	public InvalidTypeException(String message) {
        super(message);  // Call superclass constructor to initialize the exception with the provided message
    }
	
}

