package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog to update metadata.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class UpdateMetadataDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private ButtonsPanel buttonsPanel;
	private JTextArea areaLog;
	private JCheckBox chckOwCond;
	private JCheckBox chckOwValues;
	private JCheckBox chckUptMethods;

	private boolean canExit;

	/**
	 * Creates the dialog.
	 */
	public UpdateMetadataDialog() {
		super();

		this.canExit = false;

		init();
		initButtons("updateBio");

		setLocationRelativeTo(null);
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setMinimumSize(new Dimension(460, 235));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(I18n.get("DownloadMainFilesTitle"));
		{
			// ContentPane
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new TitledBorder(null, I18n
					.get("DownloadMainFilesOptions"), TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			GridBagLayout gblContentPanel = new GridBagLayout();
			gblContentPanel.columnWidths = new int[] { 20, 130 };
			gblContentPanel.rowHeights = new int[] { 40, 40, 40 };
			gblContentPanel.columnWeights = new double[] { 0.0, 1.0 };
			gblContentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
			contentPanel.setLayout(gblContentPanel);
			{
				// Second row
				chckOwCond = new JCheckBox(
						I18n.get("DownloadMainFilesConditions"));
				GridBagConstraints gbcChckOwCond = new GridBagConstraints();
				gbcChckOwCond.anchor = GridBagConstraints.WEST;
				gbcChckOwCond.insets = new Insets(0, 0, 5, 5);
				gbcChckOwCond.gridx = 0;
				gbcChckOwCond.gridy = 0;
				contentPanel.add(chckOwCond, gbcChckOwCond);

				chckOwCond.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// Set default text
						areaLog.setText(I18n.get("DownloadMainFilesText"));
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						areaLog.setText(I18n.get("DownloadMainFilesTextCond"));
					}

					@Override
					public void mouseClicked(MouseEvent e) {
					}
				});
			}
			{
				// ContentPane third row
				chckOwValues = new JCheckBox(
						I18n.get("DownloadMainFilesValues"));
				GridBagConstraints gbcChckOwValues = new GridBagConstraints();
				gbcChckOwValues.anchor = GridBagConstraints.WEST;
				gbcChckOwValues.insets = new Insets(0, 0, 5, 0);
				gbcChckOwValues.gridx = 0;
				gbcChckOwValues.gridy = 1;
				contentPanel.add(chckOwValues, gbcChckOwValues);

				chckOwValues.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// Set default text
						areaLog.setText(I18n.get("DownloadMainFilesText"));
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						areaLog.setText(I18n.get("DownloadMainFilesTextVal"));
					}

					@Override
					public void mouseClicked(MouseEvent e) {
					}
				});
			}
			{
				// Fourth row
				chckUptMethods = new JCheckBox("Overwrite Methods");
				chckUptMethods.setSelected(true);
				GridBagConstraints gbcChckUptMethods = new GridBagConstraints();
				gbcChckUptMethods.anchor = GridBagConstraints.WEST;
				gbcChckUptMethods.insets = new Insets(0, 0, 0, 5);
				gbcChckUptMethods.gridx = 0;
				gbcChckUptMethods.gridy = 2;
				contentPanel.add(chckUptMethods, gbcChckUptMethods);

				chckUptMethods.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// Set default text
						areaLog.setText(I18n.get("DownloadMainFilesText"));
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						areaLog.setText(I18n.get("DownloadMainFilesTextMet"));
					}

					@Override
					public void mouseClicked(MouseEvent e) {
					}
				});
			}
			{
				// Second column
				areaLog = new JTextArea();
				JScrollPane scrollLog = new JScrollPane(areaLog);
				areaLog.setEditable(false);
				areaLog.setWrapStyleWord(true);
				areaLog.setLineWrap(true);
				GridBagConstraints gbcTextArea = new GridBagConstraints();
				gbcTextArea.gridheight = 3;
				gbcTextArea.insets = new Insets(10, 10, 10, 10);
				gbcTextArea.fill = GridBagConstraints.BOTH;
				gbcTextArea.gridx = 1;
				gbcTextArea.gridy = 0;
				contentPanel.add(scrollLog, gbcTextArea);

				// Set default text
				areaLog.setText(I18n.get("DownloadMainFilesText"));
			}
		}
	}

	/**
	 * Initializes buttons.
	 * 
	 * @param help
	 */
	private void initButtons(String help) {
		String[] buttonNames = { I18n.get("DownloadMainFilesUpdate"),
				I18n.get("cancel") };
		URL[] iconPaths = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };

		ActionListener[] listeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canExit = true;
				dispose();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canExit = false;
				dispose();
			}
		} };

		this.buttonsPanel = new ButtonsPanel(buttonNames, iconPaths, listeners,
				help, this);
		buttonsPanel.setBackground(new Color(214, 217, 223));

		// ContentPane South
		getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConditions() {
		return this.chckOwCond.isSelected();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isValues() {
		return this.chckOwValues.isSelected();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMethods() {
		return this.chckUptMethods.isSelected();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.canExit;
	}
}
