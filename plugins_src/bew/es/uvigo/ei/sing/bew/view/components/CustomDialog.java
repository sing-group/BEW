package es.uvigo.ei.sing.bew.view.components;

import java.awt.Toolkit;

import javax.swing.JDialog;

/**
 * Custom dialog with the application icon.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CustomDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CustomDialog() {
		super();

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				CustomDialog.class.getResource("/img/favicon.png")));
	}

	/**
	 * 
	 * @param parent
	 */
	public CustomDialog(JDialog parent) {
		super(parent);

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				CustomDialog.class.getResource("/img/favicon.png")));
	}
}
