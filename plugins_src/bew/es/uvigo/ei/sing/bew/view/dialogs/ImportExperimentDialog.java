package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.FileToData;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Dialog to import an Experiment.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ImportExperimentDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private JTextField pathField;
	private JButton chooserButton;
	private JCheckBox loadPlotsCheck;
	private JCheckBox loadStatsCheck;

	// Variable to save the temporary Files during the load
	private List<File> temporaryFiles;
	private String[] paths;
	private boolean canExit;

	/**
	 * Default constructor (UNUSED)
	 */
	public ImportExperimentDialog() {
		super();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.canExit = false;
		temporaryFiles = new ArrayList<File>();

		initialize();
		initButtons();
	}

	/**
	 * Initializes the dialog.
	 */
	public void initialize() {
		setTitle("Import an experiment from a file");
		setModal(true);
		setLocationRelativeTo(null);
		setSize(480, 200);

		JPanel informationPane = new JPanel();
		getContentPane().add(informationPane, BorderLayout.NORTH);
		GridBagLayout gblInformationPane = new GridBagLayout();
		gblInformationPane.columnWidths = new int[] { 100, 100, 100, 100 };
		gblInformationPane.rowHeights = new int[] { 40, 40 };
		gblInformationPane.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0 };
		gblInformationPane.rowWeights = new double[] { 1.0, 0.0 };
		informationPane.setLayout(gblInformationPane);

		pathField = new JTextField();
		pathField.setBackground(Color.WHITE);
		pathField.setEditable(false);
		pathField.setText(I18n.get("selectFile"));
		GridBagConstraints gbcPathField = new GridBagConstraints();
		gbcPathField.fill = GridBagConstraints.HORIZONTAL;
		gbcPathField.gridwidth = 3;
		gbcPathField.insets = new Insets(20, 5, 5, 5);
		gbcPathField.gridx = 0;
		gbcPathField.gridy = 0;
		informationPane.add(pathField, gbcPathField);
		pathField.setColumns(10);

		chooserButton = new JButton("Import file");
		chooserButton.setIcon(new ImageIcon(ImportExperimentDialog.class
				.getResource("/img/load.png")));
		GridBagConstraints gbcChooserButton = new GridBagConstraints();
		gbcChooserButton.insets = new Insets(20, 0, 5, 5);
		gbcChooserButton.gridx = 3;
		gbcChooserButton.gridy = 0;
		informationPane.add(chooserButton, gbcChooserButton);

		chooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Change for path = ... to load more than one file
				paths[0] = FileToData.loadXMLFile(temporaryFiles);

				pathField.setText("");
				for (String path : paths) {
					if (pathField.getText().equals(""))
						pathField.setText(path);
					else
						pathField.setText(pathField.getText() + ";" + path);
				}

				// All the paths have the same type
				if (paths.length > 0 && paths[0].endsWith(".xml")) {
					loadPlotsCheck.setEnabled(true);
					loadStatsCheck.setEnabled(true);
				} else {
					loadPlotsCheck.setEnabled(false);
					loadStatsCheck.setEnabled(false);
				}
			}
		});

		loadPlotsCheck = new JCheckBox("Load plots?");
		loadPlotsCheck.setEnabled(false);
		GridBagConstraints gbcLoadPlots = new GridBagConstraints();
		gbcLoadPlots.fill = GridBagConstraints.BOTH;
		gbcLoadPlots.insets = new Insets(15, 5, 0, 5);
		gbcLoadPlots.gridx = 0;
		gbcLoadPlots.gridy = 1;
		informationPane.add(loadPlotsCheck, gbcLoadPlots);
		// For the future
		loadPlotsCheck.setVisible(false);

		loadStatsCheck = new JCheckBox("Load statistics?");
		loadStatsCheck.setEnabled(false);
		GridBagConstraints gbcLoadStatistics = new GridBagConstraints();
		gbcLoadStatistics.insets = new Insets(15, 0, 0, 5);
		gbcLoadStatistics.fill = GridBagConstraints.BOTH;
		gbcLoadStatistics.gridx = 1;
		gbcLoadStatistics.gridy = 1;
		informationPane.add(loadStatsCheck, gbcLoadStatistics);
		// For the future
		loadStatsCheck.setVisible(false);
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
				if (paths.length > 0) {
					canExit = true;
					dispose();
				} else {
					ShowDialog.showError(I18n.get("noFileTitle"),
							I18n.get("noFile"));
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
						"importExp", this), BorderLayout.SOUTH);
	}

	/**
	 * DEPRECATED.
	 * 
	 * @return
	 */
	public boolean loadPlots() {
		if (this.loadPlotsCheck.isEnabled()) {
			return this.loadPlotsCheck.isSelected();
		} else {
			return false;
		}
	}

	/**
	 * DEPRECATED
	 * 
	 * @return
	 */
	public boolean loadStatistics() {
		if (this.loadStatsCheck.isEnabled()) {
			return this.loadStatsCheck.isSelected();
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String[] getPath() {
		return this.paths;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.canExit;
	}

	/**
	 * 
	 * @return
	 */
	public List<File> getTemporaryFiles() {
		return temporaryFiles;
	}
}
