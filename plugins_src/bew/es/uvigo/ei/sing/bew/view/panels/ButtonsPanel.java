package es.uvigo.ei.sing.bew.view.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Custom panel to add buttons to the different dialogs in the application.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ButtonsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<JButton> panelButtons;

	/**
	 * Default constructor.
	 * 
	 * @param buttonsTitle
	 *            String[] with buttons title.
	 * @param iconPaths
	 *            URL[] with icons image.
	 * @param buttonsListener
	 *            ActionListener[] with buttons listeners.
	 */
	public ButtonsPanel(String[] buttonsTitle, URL[] iconPaths,
			ActionListener[] buttonsListener, Object... helpButton) {
		super();

		// Variables need some kind of order to be constructed properly (ex: [0]
		// "OK button", "iconOkButton", "listenerOk")
		this.panelButtons = new ArrayList<JButton>();

		init(buttonsTitle, iconPaths, buttonsListener, helpButton);
	}

	/**
	 * Initializes the dialog.
	 */
	private void init(String[] buttonsTitle, URL[] iconPaths,
			ActionListener[] buttonsListener, Object... helpButton) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[buttonsTitle.length + 1];
		gridBagLayout.rowWeights = new double[] { 1.0 };
		setLayout(gridBagLayout);

		// All the input parameters have the same length
		for (int i = 0; i < buttonsTitle.length; i++) {
			JButton button = new JButton(buttonsTitle[i]);
			button.setIcon(new ImageIcon(iconPaths[i]));
			GridBagConstraints gbcButton = new GridBagConstraints();
			gbcButton.insets = new Insets(0, 0, 0, 5);
			gbcButton.gridx = i;
			gbcButton.gridy = 0;
			add(button, gbcButton);

			// Button width in the column
			gridBagLayout.columnWeights[i] = 1.0;

			button.addActionListener(buttonsListener[i]);
			this.panelButtons.add(button);
		}

		// If helpButton has something ([0]: help option, [1]: parent dialog)
		if (helpButton.length > 0) {
			// Last position
			int i = buttonsTitle.length;

			JButton button = new JButton();
			button.setIcon(new ImageIcon(getClass()
					.getResource("/img/help.png")));
			GridBagConstraints gbcButton = new GridBagConstraints();
			gbcButton.insets = new Insets(0, 0, 0, 5);
			gbcButton.gridx = i + 1;
			gbcButton.gridy = 0;
			add(button, gbcButton);

			// Button width in the column
			gridBagLayout.columnWeights[i] = 1.0;

			// Create the helpSet and HelpBroker for JavaHelp
			HelpSet helpset;
			try {
				helpset = new HelpSet(getClass().getClassLoader(), getClass()
						.getResource("/help/help_set.hs"));
				final DefaultHelpBroker dhb = new DefaultHelpBroker(helpset);
				helpset.setHomeID(helpButton[0].toString());
				dhb.enableHelpKey((Component) helpButton[1],
						helpButton[0].toString(), helpset);
				// For modal dialogs
				dhb.setActivationWindow((Window) helpButton[1]);

				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dhb.setDisplayed(true);
					}
				});
			} catch (HelpSetException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}

			this.panelButtons.add(button);
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<JButton> getPanelButtons() {
		return panelButtons;
	}

}
