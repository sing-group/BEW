package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.ListsPanel;
import es.uvigo.ei.sing.bew.view.panels.ReportingOptionPanel;

/**
 * Custom dialog to generate a HTML report of an Experiment.
 * 
 * @author Gael P�rez Rodr�guez
 * 
 */
public class ReportingDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private JPanel contentPanel;
	private JTabbedPane methodTabbedPane;
	private ListsPanel listsPanel;
	private JTextField textFieldSaveFile;
	private JComboBox comboExperiments;
	private JCheckBox chckbxSetup;
	private JCheckBox chckbxConstant;
	private JCheckBox chckbxMethod;

	private boolean isExit;
	// Map with <Method, List<Plot>>
	private Map<Method, List<Plot>> mapMethodPlots;
	// Map with <Method, List<Statistic>>
	private Map<Method, List<Statistic>> mapMethodStatistics;
	private Map<String, Method> mapNameMethod;
	private Boolean[] experimentOptions;
	private File saveFile;
	private IExperiment selectedExperiment;

	/**
	 * Create the dialog.
	 */
	public ReportingDialog() {
		super();
		
		this.isExit = false;
		this.experimentOptions = new Boolean[3];
		this.mapNameMethod = new HashMap<String, Method>();
		this.mapMethodPlots = new HashMap<Method, List<Plot>>();
		this.mapMethodStatistics = new HashMap<Method, List<Statistic>>();

		try {
			init();
			initButtons();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the dialog.
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		setTitle("Reporting operation");
		setSize(new Dimension(800, 600));
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Content panel initiation
		getContentPane().setLayout(new BorderLayout());
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(0, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			// Create header panel for directory and Experiment selection
			JPanel headerPanel = new JPanel();
			contentPanel.add(headerPanel, BorderLayout.NORTH);
			headerPanel.setLayout(new GridLayout(2, 1, 0, 0));

			// Panel for directory selection
			JPanel savePanel = new JPanel();
			headerPanel.add(savePanel);
			savePanel.setBorder(new TitledBorder(null, "Directory selection",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gblSavePanel = new GridBagLayout();
			gblSavePanel.columnWidths = new int[] { 50, 100, 50 };
			gblSavePanel.rowHeights = new int[] { 10 };
			gblSavePanel.columnWeights = new double[] { 1.0, 1.0, 1.0 };
			gblSavePanel.rowWeights = new double[] { 0.0 };
			savePanel.setLayout(gblSavePanel);
			{
				// Directory sel label
				JLabel lblSaveFile = new JLabel(
						"Select a directory to save the report:");

				GridBagConstraints gbcLblSave = new GridBagConstraints();
				gbcLblSave.insets = new Insets(0, 0, 0, 5);
				gbcLblSave.gridx = 0;
				gbcLblSave.gridy = 0;
				savePanel.add(lblSaveFile, gbcLblSave);
			}
			{
				// Dir sel textField
				textFieldSaveFile = new JTextField();
				textFieldSaveFile.setEditable(false);
				textFieldSaveFile.setColumns(10);

				GridBagConstraints gbcTextSave = new GridBagConstraints();
				gbcTextSave.fill = GridBagConstraints.HORIZONTAL;
				gbcTextSave.insets = new Insets(0, 0, 0, 5);
				gbcTextSave.gridx = 1;
				gbcTextSave.gridy = 0;
				savePanel.add(textFieldSaveFile, gbcTextSave);
			}
			{
				// Dir sel button
				JButton btnSaveFile = new JButton("Select directory");
				btnSaveFile.setIcon(new ImageIcon(ReportingDialog.class
						.getResource("/img/selectDir.png")));
				btnSaveFile.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// Call file chooser
						CustomFileChooser fc = new CustomFileChooser();

						// Get saved path
						if (saveFile != null)
							fc.setCurrentDirectory(saveFile);

						// Set only directories
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						// We open a save dialog
						int retrieval = fc.showSaveDialog(null);

						if (retrieval == JFileChooser.APPROVE_OPTION) {
							saveFile = fc.getSelectedFile();
							textFieldSaveFile.setText(saveFile
									.getAbsolutePath());
						}
					}
				});

				GridBagConstraints gbcBtnSave = new GridBagConstraints();
				gbcBtnSave.insets = new Insets(0, 0, 5, 0);
				gbcBtnSave.gridx = 2;
				gbcBtnSave.gridy = 0;
				savePanel.add(btnSaveFile, gbcBtnSave);
			}
			{
				// Experiment selection panel
				JPanel experimentPanel = new JPanel();
				headerPanel.add(experimentPanel);

				// Layout configuration
				experimentPanel.setBorder(new TitledBorder(null,
						"Experiment selection", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				GridBagLayout gblExpPanel = new GridBagLayout();
				gblExpPanel.columnWidths = new int[] { 50, 100, 50 };
				gblExpPanel.rowHeights = new int[] { 20, 20 };
				gblExpPanel.columnWeights = new double[] { 1.0, 0.0,
						1.0 };
				gblExpPanel.rowWeights = new double[] { 0.0, 0.0 };
				experimentPanel.setLayout(gblExpPanel);
				{
					// Exp sel label
					JLabel labelExperiment = new JLabel(
							"Select a experiment to create the report:");

					GridBagConstraints gbcLabelExp = new GridBagConstraints();
					gbcLabelExp.insets = new Insets(0, 0, 0, 5);
					gbcLabelExp.gridx = 0;
					gbcLabelExp.gridy = 0;
					experimentPanel.add(labelExperiment, gbcLabelExp);
				}
				{
					// Exp sel combo
					comboExperiments = new JComboBox();
					comboExperiments
							.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxx");

					GridBagConstraints gbcComboExp = new GridBagConstraints();
					gbcComboExp.insets = new Insets(0, 0, 5, 0);
					gbcComboExp.fill = GridBagConstraints.HORIZONTAL;
					gbcComboExp.gridwidth = 2;
					gbcComboExp.gridx = 1;
					gbcComboExp.gridy = 0;
					experimentPanel.add(comboExperiments, gbcComboExp);
				}
				{
					// Exp sel checkbox
					chckbxSetup = new JCheckBox("Save Setup");
					chckbxSetup.setToolTipText("Generate the experiment setup (name, authors...) in the report.");

					GridBagConstraints gbcChckSetup = new GridBagConstraints();
					gbcChckSetup.insets = new Insets(0, 0, 0, 5);
					gbcChckSetup.gridx = 0;
					gbcChckSetup.gridy = 1;
					experimentPanel.add(chckbxSetup, gbcChckSetup);
				}
				{
					// Exp sel checkbox
					chckbxConstant = new JCheckBox("Save Constant Conditions");
					chckbxConstant.setToolTipText("Generate the experiment constants in the report.");

					GridBagConstraints gbcChckCons = new GridBagConstraints();
					gbcChckCons.insets = new Insets(0, 0, 0, 5);
					gbcChckCons.gridx = 1;
					gbcChckCons.gridy = 1;
					experimentPanel.add(chckbxConstant, gbcChckCons);
				}
				{
					// Exp sel checkbox
					chckbxMethod = new JCheckBox("Save Method statistics");
					chckbxMethod.setToolTipText("Generate the methods statistics for the selected experiment in the report.");

					GridBagConstraints gbcChckMethod = new GridBagConstraints();
					gbcChckMethod.insets = new Insets(0, 0, 0, 5);
					gbcChckMethod.gridx = 2;
					gbcChckMethod.gridy = 1;
					experimentPanel.add(chckbxMethod, gbcChckMethod);
				}
			}
		}
		{
			JPanel bottomPanel = new JPanel();
			contentPanel.add(bottomPanel, BorderLayout.CENTER);
			bottomPanel.setLayout(new GridLayout(0, 2, 0, 0));
			// Panel for Methods
			JPanel methodPanel = new JPanel();
			bottomPanel.add(methodPanel);
			{
				methodPanel.setBorder(new TitledBorder(UIManager
						.getBorder("TitledBorder.border"), "Methods selection",
						TitledBorder.LEADING, TitledBorder.TOP, null, null));
				methodPanel.setLayout(new BorderLayout(0, 0));
				{
					// TextPane for information
					JTextPane infoTextPane = new JTextPane();
					infoTextPane.setBackground(SystemColor.menu);

					// Default text
					infoTextPane
							.setText("Select the Methods to generate the reports from the first list. Then select the options you want to report for each Method.");
					infoTextPane.setEditable(false);
					methodPanel.add(infoTextPane, BorderLayout.NORTH);

					{
						// Lists panel to show the Methods
						listsPanel = new ListsPanel("Available Methods",
								"Selected Methods", false);
						listsPanel.getSelectedList().setToolTipText("Remove a method from the report.");
						listsPanel.getAvailableList().setToolTipText("Select the methods you want to include in the report.");

						ListSelectionListener availableListener = new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								// Add a tab when select a Method from the
								// available list
								if (!e.getValueIsAdjusting()) {
									JList list = (JList) e.getSource();
									Object selectedValue = list
											.getSelectedValue();
									if (selectedValue != null) {
										listsPanel
												.availableToSelected(selectedValue);
										addNewTab(mapNameMethod
												.get(selectedValue.toString()));
									}
									listsPanel.clearSelections();
								}
							}
						};

						ListSelectionListener selectedListener = new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								// Remove a tab when select a Method from
								// selected list
								if (!e.getValueIsAdjusting()) {
									JList list = (JList) e.getSource();
									Object selectedValue = list
											.getSelectedValue();
									if (selectedValue != null) {
										listsPanel
												.selectedToAvailable(selectedValue);
										deleteTab(mapNameMethod
												.get(selectedValue.toString()));
									}
									listsPanel.clearSelections();
								}
							}
						};

						// Add new listeners
						listsPanel
								.addListenerToAvailableList(availableListener);
						listsPanel.addListenerToSelectedList(selectedListener);

						methodPanel.add(listsPanel, BorderLayout.CENTER);

					}
				}
				{
					// Tabbed panel for selected Methods
					methodTabbedPane = new JTabbedPane(JTabbedPane.TOP);
					bottomPanel.add(methodTabbedPane);
					methodTabbedPane.setBorder(new TitledBorder(null,
							"Reporting options", TitledBorder.LEADING,
							TitledBorder.TOP, null, null));
				}
			}

		}

		// Adding listener for Experiments combo
		comboExperiments.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// When user select something
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object item = e.getItem();
					ClipboardItem ci = (ClipboardItem) item;
					// Save user selection
					selectedExperiment = (IExperiment) ci.getUserData();

					// Clear variables
					clearValues();

					// Add methods for selected experiment to lists
					addValuesToList();
				}
			}
		});

		// Fill combo box
		fillExperimentCombo();

		setLocationRelativeTo(null);
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
				if (finish()) {
					isExit = true;
					dispose();
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isExit = false;
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						"generateReport", this), BorderLayout.SOUTH);
	}

	/**
	 * 
	 * @param name
	 */
	private void addNewTab(Method item) {
		methodTabbedPane.addTab(item.getName(),
				(Component) new ReportingOptionPanel(item));

		// Save Experiment in map
		mapMethodPlots.put(item, new ArrayList<Plot>());
		mapMethodStatistics.put(item, new ArrayList<Statistic>());
	}

	/**
	 * 
	 * @param name
	 */
	private void deleteTab(Method item) {
		int index = methodTabbedPane.indexOfTab(item.getName());
		if (methodTabbedPane.getTabCount() > 0 && index != -1)
			methodTabbedPane.removeTabAt(index);

		// Remove Experiment in map
		mapMethodPlots.remove(item);
		mapMethodStatistics.remove(item);
	}

	/**
	 * 
	 * @return
	 */
	private boolean finish() {
		// If user select at least one experiment
		if (saveFile != null) {
			int index = methodTabbedPane.getTabCount();

			ReportingOptionPanel rop;
			String currentMethodName = "";
			Method currentMethod;
			for (int tab = 0; tab < index; tab++) {
				// Get real panel from tab
				rop = (ReportingOptionPanel) methodTabbedPane
						.getComponentAt(tab);
				// Retrieve method name
				currentMethodName = methodTabbedPane.getTitleAt(tab);
				// Retrieve method
				currentMethod = mapNameMethod.get(currentMethodName);

				// Save Method plots
				FunctionConstants.addValuesToKey(mapMethodPlots, currentMethod,
						rop.getSelectedPlots());
				// Save Method plots
				FunctionConstants.addValuesToKey(mapMethodStatistics,
						currentMethod, rop.getSelectedStatistics());
			}

			// Get options
			setExperimentOptions();

			return true;
		} else {
			ShowDialog.showError(I18n.get("reportingPathTitle"),
					I18n.get("reportingPath"));
		}
		return false;
	}

	/**
	 * Fills the Experiment combo with the available Experiments in the
	 * clipboard.
	 */
	private void fillExperimentCombo() {
		List<ClipboardItem> experimentList = Core.getInstance().getClipboard()
				.getItemsByClass(IExperiment.class);

		for (ClipboardItem item : experimentList) {
			comboExperiments.addItem(item);
		}
	}

	/**
	 * Add methods of the selected experiment to the list.
	 */
	private void addValuesToList() {
		List<Method> parentMethods = new ArrayList<Method>();
		List<String> methodNames = new ArrayList<String>();

		parentMethods = selectedExperiment.getMethods().getMetodos();
		for (Method value : parentMethods) {
			methodNames.add(value.getName());
			mapNameMethod.put(value.getName(), value);
		}

		listsPanel.addStringsToList(methodNames);
	}

	/**
	 * 
	 * @return
	 */
	public List<Object> getSelectedMethods() {
		// List with <Method>
		List<Object> toRet = new ArrayList<Object>();
		List<Object> selectedMethods = listsPanel.getAllSelectedValues();

		String name;
		// Go over selected method names and save the method associate to the
		// name
		for (Object value : selectedMethods) {
			name = value.toString();
			toRet.add(mapNameMethod.get(name));
		}

		return toRet;
	}

	/**
	 * Clear all values in the dialog.
	 */
	private void clearValues() {
		this.chckbxConstant.setSelected(false);
		this.chckbxMethod.setSelected(false);
		this.chckbxSetup.setSelected(false);

		this.isExit = false;

		this.listsPanel.deleteAllValues();

		for (Method m : mapNameMethod.values()) {
			deleteTab(m);
		}

		mapNameMethod.clear();
		mapMethodPlots.clear();
		mapMethodStatistics.clear();
	}

	/**
	 * 
	 */
	private void setExperimentOptions() {
		this.experimentOptions[0] = chckbxSetup.isSelected();
		this.experimentOptions[1] = chckbxConstant.isSelected();
		this.experimentOptions[2] = chckbxMethod.isSelected();
	}

	/**
	 * 
	 * @return
	 */
	public Boolean[] getExperimentOptions() {
		return experimentOptions;
	}

	/**
	 * Get the path and the name of the target file.
	 * 
	 * @return
	 */
	public String getPath() {
		Calendar now = Calendar.getInstance();

		// return saveFile.getAbsolutePath() + "\\" +
		// selectedExperiment.getName()
		// + "_" + now.get(Calendar.HOUR_OF_DAY) + "_"
		// + now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
		return saveFile.getAbsolutePath() + "/ExperimentReport" + "_"
				+ now.get(Calendar.HOUR_OF_DAY) + "_"
				+ now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return isExit;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Method, List<Plot>> getMapMethodPlots() {
		return mapMethodPlots;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Method, List<Statistic>> getMapMethodStatistics() {
		return mapMethodStatistics;
	}

	/**
	 * 
	 * @return
	 */
	public IExperiment getSelectedExperiment() {
		return selectedExperiment;
	}
}
