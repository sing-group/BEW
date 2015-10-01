package es.uvigo.ei.sing.bew.constants;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class generates pop-up dialogs in the application.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ShowDialog {

	/**
	 * Method to generate an error pop-up dialog.
	 * 
	 * @param title
	 *            Title of the dialog.
	 * @param body
	 *            Text of the dialog.
	 */
	public static void showError(String title, String body) {
		JTextArea myTArea = new JTextArea(10, 40);
		myTArea.setLineWrap(true);
		myTArea.setEditable(false);
		JScrollPane myScroller = new JScrollPane(myTArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		myTArea.setText(body);

		JOptionPane.showMessageDialog(null, myScroller, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Method to generate an error pop-up dialog.
	 * 
	 * @param parent
	 *            Parent of the dialog.
	 * @param title
	 *            Title of the dialog.
	 * @param body
	 *            Text of the dialog.
	 */
	public static void showError(Component parent, String title, String body) {
		JTextArea myTArea = new JTextArea(10, 40);
		myTArea.setLineWrap(true);
		myTArea.setEditable(false);
		JScrollPane myScroller = new JScrollPane(myTArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		myTArea.setText(body);

		JOptionPane.showMessageDialog(parent, myScroller, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Method to generate an error pop-up dialog.
	 * 
	 * @param title
	 *            Title of the dialog.
	 * @param body
	 *            Text of the dialog.
	 * @param extra
	 *            Extra text of the dialog.
	 */
	public static void showError(String title, String body, String extra) {
		JTextArea myTArea = new JTextArea(10, 40);
		myTArea.setLineWrap(true);
		myTArea.setEditable(false);
		JScrollPane myScroller = new JScrollPane(myTArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		myTArea.setText(body + "\n" + extra);

		JOptionPane.showMessageDialog(null, myScroller, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Method to generate an information pop-up dialog.
	 * 
	 * @param title
	 *            Title of the dialog.
	 * @param body
	 *            Text of the dialog.
	 */
	public static void showInfo(String title, String body) {
		JTextArea myTArea = new JTextArea(10, 40);
		myTArea.setLineWrap(true);
		myTArea.setEditable(false);
		JScrollPane myScroller = new JScrollPane(myTArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		myTArea.setText(body);

		JOptionPane.showMessageDialog(null, myScroller, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Method to generate an information pop-up dialog.
	 * 
	 * @param parent
	 *            Parent of the dialog.
	 * @param title
	 *            Title of the dialog.
	 * @param body
	 *            Text of the dialog.
	 */
	public static void showInfo(Component parent, String title, String body) {
		JTextArea myTArea = new JTextArea(10, 40);
		myTArea.setLineWrap(true);
		myTArea.setEditable(false);
		JScrollPane myScroller = new JScrollPane(myTArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		myTArea.setText(body);

		JOptionPane.showMessageDialog(parent, myScroller, title,
				JOptionPane.INFORMATION_MESSAGE);
	}
}
