package es.uvigo.ei.sing.bew.util;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Class to limit the input number of characters in a JTextField.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private int limit;

	/**
	 * Default constructor.
	 * 
	 * @param limit
	 *            Inputo Integer limit.
	 */
	public JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	/**
	 * 
	 */
	@Override
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;
		
		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}else{
			Toolkit.getDefaultToolkit().beep();	
		}
	}
	
}