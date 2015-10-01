package es.uvigo.ei.sing.bew.constants;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import es.uvigo.ei.sing.bew.view.components.CustomDialog;

/**
 * This class shows a simply progress bar in a popup dialog.
 * 
 * @author Gael
 * 
 */
public class ShowProgressBar extends CustomDialog implements
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	// Variables
	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 * 
	 * @param task
	 *            SwingWorker<Void, Void> task to realize while the progress bar
	 *            is up.
	 */
	public ShowProgressBar(SwingWorker<Void, Void> task) {
		super();

		initialize();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		task.addPropertyChangeListener(this);
		task.execute();

		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Initialize the dialog.
	 */
	public void initialize() {
		setTitle(I18n.get("ShowProgressBarTitle"));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				ShowProgressBar.class.getResource("/img/loading.png")));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setSize(new Dimension(250, 60));
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());

		{
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			progressBar.setStringPainted(true);
			progressBar.setValue(0);
			progressBar.setString(I18n.get("ShowProgressBarWorking"));
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			getContentPane().add(progressBar, BorderLayout.CENTER);
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();

			if (progress == 100) {
				// Turn off the wait cursor
				setCursor(null);
				progressBar.setIndeterminate(false);
				progressBar.setString("Done!");
				dispose();
			}
			progressBar.setValue(progress);
		}
	}
}
