package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.LoginPanel;

/**
 * Custom dialog to list and download an Experiment from BioFomics.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class UploadExpDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private ButtonsPanel buttonsPanel;
	private LoginPanel loginPanel;

	private JLabel lblExperiment;
	private JComboBox comboExp;

	private boolean canExit;
	private IExperiment selectedExp = null;

	/**
	 * Create the dialog.
	 */
	public UploadExpDialog() {
		super();

		init();
		initButtons("uploadExp");

		setLocationRelativeTo(null);

		fillComboExp();
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(new Dimension(550, 250));
		setTitle(I18n.get("uploadExpDialogTitle"));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		{
			// ContentPane North
			loginPanel = new LoginPanel(I18n.get("uploadLoginMessage"));
			getContentPane().add(loginPanel, BorderLayout.NORTH);
		}
		{
			// ContentPane Center
			contentPanel.setBorder(new TitledBorder(null, I18n
					.get("DownloadExpDialogExpList"), TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			GridBagLayout gblContentPanel = new GridBagLayout();
			gblContentPanel.columnWidths = new int[] { 30, 30 };
			gblContentPanel.rowHeights = new int[] { 25 };
			gblContentPanel.columnWeights = new double[] { 1.0, 1.0 };
			gblContentPanel.rowWeights = new double[] { 0.0 };
			contentPanel.setLayout(gblContentPanel);
			{
				lblExperiment = new JLabel(I18n.get("selectExperiment"));
				lblExperiment.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbcLblExperiment = new GridBagConstraints();
				gbcLblExperiment.fill = GridBagConstraints.BOTH;
				gbcLblExperiment.insets = new Insets(0, 0, 5, 5);
				gbcLblExperiment.gridx = 0;
				gbcLblExperiment.gridy = 0;
				contentPanel.add(lblExperiment, gbcLblExperiment);
			}
		}
		{
			comboExp = new JComboBox();
			GridBagConstraints gbcComboBox = new GridBagConstraints();
			gbcComboBox.fill = GridBagConstraints.BOTH;
			gbcComboBox.insets = new Insets(0, 0, 5, 5);
			gbcComboBox.gridx = 1;
			gbcComboBox.gridy = 0;
			contentPanel.add(comboExp, gbcComboBox);

			comboExp.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxx");

			comboExp.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					// Get selected Experiment
					ClipboardItem item = (ClipboardItem) comboExp
							.getSelectedItem();
					selectedExp = (IExperiment) item.getUserData();
				}
			});
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons(String help) {
		String[] buttonNames = { I18n.get("uploadExpDialogButton"),
				I18n.get("cancel") };
		URL[] iconPaths = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };

		ActionListener[] listeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Accept button
				if (finish()) {
					BewConstants.USER = loginPanel.getTextUser();
					BewConstants.PASS = loginPanel.getTextPassword();

					canExit = true;
					dispose();
				} else {
					canExit = false;
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Cancel button
				canExit = false;
				dispose();
			}
		} };

		this.buttonsPanel = new ButtonsPanel(buttonNames, iconPaths, listeners,
				help, this);

		// ContentPane South
		getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * Fills the combo with the cliboard Experiments.
	 */
	private void fillComboExp() {
		List<ClipboardItem> items;
		// Obtain all methods of all Experiments
		items = Core.getInstance().getClipboard()
				.getItemsByClass(IExperiment.class);

		if (!items.isEmpty()) {
			// Add all the methods to the ComboBox
			for (ClipboardItem item : items) {
				// We save the first experiment (selected by default)
				if (selectedExp == null)
					selectedExp = (IExperiment) item.getUserData();

				this.comboExp.addItem(item);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean finish() {
		boolean toRet = false;

		if (selectedExp != null)
			toRet = true;

		return toRet;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.canExit;
	}

	public IExperiment getSelectedExp() {
		return selectedExp;
	}
}
