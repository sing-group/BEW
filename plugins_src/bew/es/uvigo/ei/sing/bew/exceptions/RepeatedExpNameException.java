package es.uvigo.ei.sing.bew.exceptions;

/**
 * Custom Exception.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class RepeatedExpNameException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with custom message.
	 * 
	 * @param message
	 *            String, custom message.
	 */
	public RepeatedExpNameException(String message) {
		super(message);
	}

	/**
	 * Default constructor.
	 */
	public RepeatedExpNameException() {
		super();
	}
}
