package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.SelectExpAndMethodPanel;

/**
 * Custom dialog for select a method from an experiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectExpAndMethodDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private SelectExpAndMethodPanel methodPanel;

	private boolean canExit;

	/**
	 * Default constructor.
	 */
	public SelectExpAndMethodDialog(boolean isOnlyIntra, boolean isNewMethod) {
		super();

		setTitle(I18n.get("selectMethodFromExp"));
		methodPanel = new SelectExpAndMethodPanel(isOnlyIntra, isNewMethod);
		canExit = false;

		initialize();
		initButtons();

		setLocationRelativeTo(null);
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		setSize(400, 150);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			contentPanel.add(methodPanel);
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "Ok", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (methodPanel.finish()) {
					canExit = true;
					dispose();
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canExit = false;
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners),
				BorderLayout.SOUTH);
	}

	/**
	 * Get selected Method.
	 * 
	 * @return
	 */
	public Method getSelectedMethod() {
		return methodPanel.getSelectedMethod();
	}

	/**
	 * Check exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return canExit;
	}
}
