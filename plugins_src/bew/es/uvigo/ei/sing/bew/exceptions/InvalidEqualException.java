package es.uvigo.ei.sing.bew.exceptions;

/**
 * Custom exception.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class InvalidEqualException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with custom message.
	 * 
	 * @param message
	 *            String, custom message.
	 */
	public InvalidEqualException(String message) {
		super(message);
	}

	/**
	 * Default constructor.
	 */
	public InvalidEqualException() {
		super();
	}
}
