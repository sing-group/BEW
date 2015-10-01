package es.uvigo.ei.sing.bew.exceptions;

/**
 * Custom exception.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class TooMuchDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with custom message.
	 * 
	 * @param message
	 *            String, custom message.
	 */
	public TooMuchDataException(String message) {
		super(message);
	}

	/**
	 * Default constructor.
	 */
	public TooMuchDataException() {
		super();
	}
}
