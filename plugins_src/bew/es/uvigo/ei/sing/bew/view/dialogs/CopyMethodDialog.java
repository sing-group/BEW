package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.SelectExpAndMethodPanel;

/**
 * Custom dialog to select a Method to copy.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class CopyMethodDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private SelectExpAndMethodPanel methodExpPanel;

	private boolean canExit;

	/**
	 * Default Constructor
	 * 
	 * @param methods
	 *            List with the possible methods to copy
	 */
	public CopyMethodDialog() {
		super();

		init();
		initButtons();
	}

	/**
	 * Method to initialize the Dialog
	 */
	public void init() {
		setTitle(I18n.get("copyMethod?"));
		setResizable(false);
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Configure JDialog dimensions
		this.setMinimumSize(new Dimension(425, 210));
		this.setMaximumSize(new Dimension(425, 210));
		this.setPreferredSize(new Dimension(425, 210));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblContentPanel = new GridBagLayout();
		gblContentPanel.columnWidths = new int[] { 200 };
		gblContentPanel.rowHeights = new int[] { 100, 100 };
		gblContentPanel.columnWeights = new double[] { 1.0 };
		gblContentPanel.rowWeights = new double[] { 1.0, 1.0 };
		contentPanel.setLayout(gblContentPanel);
		{
			JPanel infoPanel = new JPanel();
			GridBagConstraints gbcInfoPanel = new GridBagConstraints();
			gbcInfoPanel.fill = GridBagConstraints.HORIZONTAL;
			gbcInfoPanel.insets = new Insets(0, 0, 0, 5);
			gbcInfoPanel.gridx = 0;
			gbcInfoPanel.gridy = 0;
			contentPanel.add(infoPanel, gbcInfoPanel);
			infoPanel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JTextArea selOneMethod = new JTextArea();
				infoPanel.add(selOneMethod);
				selOneMethod.setBackground(UIManager.getColor("menu"));
				selOneMethod.setForeground(Color.BLACK);
				selOneMethod.setFont(new Font("Tahoma", Font.PLAIN, 13));
				selOneMethod.setEditable(false);
				selOneMethod.setWrapStyleWord(true);
				selOneMethod.setLineWrap(true);
				selOneMethod.setText(I18n.get("copyMethodText"));
			}
		}
		{
			JPanel selectionPanel = new JPanel();
			GridBagConstraints gbcSelectionPanel = new GridBagConstraints();
			gbcSelectionPanel.fill = GridBagConstraints.BOTH;
			gbcSelectionPanel.gridx = 0;
			gbcSelectionPanel.gridy = 1;
			contentPanel.add(selectionPanel, gbcSelectionPanel);
			selectionPanel.setLayout(new GridLayout(1, 0, 0, 0));

			methodExpPanel = new SelectExpAndMethodPanel(true, true);
			selectionPanel.add(methodExpPanel);
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
				if (methodExpPanel.finish()) {
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
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						"addMethod", this), BorderLayout.SOUTH);
	}

	/**
	 * Function to map the selected Method name in the comboBox with the Method
	 * Object.
	 * 
	 * @return The method associated to the selected name.
	 */
	public Method getSelectedMethod() {
		return methodExpPanel.getSelectedMethod();
	}

	/**
	 * Function to map the selected Method name in the comboBox with the Method
	 * Object.
	 * 
	 * @return The method associated to the selected name.
	 */
	public IExperiment getSelectedExp() {
		return methodExpPanel.getSelectedExp();
	}

	/**
	 * Check exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return canExit;
	}

	/**
	 * Set exit.
	 * 
	 * @param exit
	 */
	public void setExit(boolean exit) {
		this.canExit = exit;
	}
}
