package es.uvigo.ei.sing.bew.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;

/**
 * Custom panel to put the login and password.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private CustomTextField textUser;
	private JPasswordField textPass;

	/**
	 * Create the panel.
	 */
	public LoginPanel(String message) {
		super();

		init(message);
	}

	/**
	 * Initializes the dialog.
	 */
	private void init(String message) {
		setBorder(new TitledBorder(null, I18n.get("LoginPanelLogin"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 30, 30 };
		gridBagLayout.rowHeights = new int[] { 25, 25, 15 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);
		{
			JLabel lblUser = new JLabel(I18n.get("LoginPanelUser") + ": ");
			GridBagConstraints gbcLblUser = new GridBagConstraints();
			gbcLblUser.insets = new Insets(0, 0, 5, 5);
			gbcLblUser.gridx = 0;
			gbcLblUser.gridy = 0;
			add(lblUser, gbcLblUser);

			textUser = new CustomTextField("");
			textUser.setToolTipText("Introduce BiofOmics login.");
			textUser.setPlaceholder("jsmith");
			GridBagConstraints gbcTextUser = new GridBagConstraints();
			gbcTextUser.insets = new Insets(0, 0, 5, 5);
			gbcTextUser.fill = GridBagConstraints.HORIZONTAL;
			gbcTextUser.gridx = 1;
			gbcTextUser.gridy = 0;
			add(textUser, gbcTextUser);
			textUser.setColumns(10);
		}
		{
			JLabel lblPass = new JLabel(I18n.get("LoginPanelPassword") + ": ");
			GridBagConstraints gbcLblPass = new GridBagConstraints();
			gbcLblPass.insets = new Insets(0, 0, 5, 5);
			gbcLblPass.gridx = 0;
			gbcLblPass.gridy = 1;
			add(lblPass, gbcLblPass);

			textPass = new JPasswordField();
			textPass.setToolTipText("Introduce BiofOmics password.");
			GridBagConstraints gbcTextPass = new GridBagConstraints();
			gbcTextPass.insets = new Insets(0, 0, 5, 5);
			gbcTextPass.fill = GridBagConstraints.HORIZONTAL;
			gbcTextPass.gridx = 1;
			gbcTextPass.gridy = 1;
			add(textPass, gbcTextPass);
			textPass.setColumns(10);
		}
		{
			JLabel lblInfo = new JLabel(message);
			lblInfo.setIcon(new ImageIcon(LoginPanel.class
					.getResource("/img/about.png")));
			GridBagConstraints gbcLblInfo = new GridBagConstraints();
			gbcLblInfo.gridwidth = 2;
			gbcLblInfo.insets = new Insets(5, 0, 0, 5);
			gbcLblInfo.gridx = 0;
			gbcLblInfo.gridy = 2;
			add(lblInfo, gbcLblInfo);
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getTextUser() {
		return textUser.getText();
	}

	/**
	 * 
	 * @return String with the password.
	 */
	public String getTextPassword() {
		char[] pass = textPass.getPassword();
		String toRet = "";
		for (char c : pass) {
			toRet += c;
		}

		return toRet;
	}
}
