package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.views.MethodTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.SetupPanel;

/**
 * Custom dialog for comparing intraExperiments.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class CompareExperimentsDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private SetupPanel setupPanel;
	private JTabbedPane tabbedPane;
	private boolean isExit = false;

	private Object[] methods;
	// Save method tables in this variable
	private List<MethodTable> methodTables;
	private List<List<Object>> selectedRows;

	// IntraExperiments with their row numbers
	private Map<Object, List<Object>> mapExpRows;
	// IntraExperiments with their row colors
	private Map<Object, Object> mapExpColors;

	private int numConditions;
	private List<Condition> conditionNames;
	private String methodUnits = "";
	private String methodName = "";
	private String[] interExpSetup;
	private String[] intraExpNames;

	private Set<IExperiment> intraExperiments;

	/**
	 * Default Constructor.
	 * 
	 * @param methods
	 *            IntraExperiment Methods.
	 * @param intraExperiments
	 *            IntraExperiments in the comparison.
	 * @param colors
	 *            IntraExperiment colors.
	 */
	public CompareExperimentsDialog(Object[] methods,
			Object[] intraExperiments, Object[] colors) {
		setTitle(I18n.get("compareExpsDialog"));

		this.methods = methods;
		this.mapExpRows = new HashMap<Object, List<Object>>();
		// Fill the map with the intraExperiments
		for (Object exp : intraExperiments)
			mapExpRows.put(exp, new ArrayList<Object>());

		this.mapExpColors = new HashMap<Object, Object>();
		// Fill the map with the intraExperiments and colors (same size)
		for (int i = 0; i < intraExperiments.length; i++)
			mapExpColors.put(intraExperiments[i], colors[i]);

		this.methodTables = new ArrayList<MethodTable>();
		this.selectedRows = new ArrayList<List<Object>>();
		this.conditionNames = new ArrayList<Condition>();
		this.intraExperiments = new HashSet<IExperiment>();
		this.intraExpNames = new String[methods.length];

		this.interExpSetup = new String[8];

		// Obtain information to the experiment from one method
		getMethodInformation();

		init();
		initButtons();

		setLocationRelativeTo(null);
	}

	/**
	 * Method to initialize dialog
	 */
	private void init() {
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JSplitPane splitPane = new JSplitPane();

		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		{
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			getContentPane().add(splitPane, BorderLayout.CENTER);
		}
		{
			setupPanel = new SetupPanel();
			splitPane.setLeftComponent(setupPanel);
		}
		{
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			tabbedPane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("methodView"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			splitPane.setRightComponent(tabbedPane);

			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		{
			// Add methods to the panel
			createMethodView();
		}
	}

	/**
	 * Method to initialize the buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "Ok", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (retrieveInformation()) {
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
						"compareIntra", this), BorderLayout.SOUTH);
	}

	/**
	 * Save the information in the local variables.
	 * 
	 * @return True if all the information are correct, false otherwise.
	 */
	private boolean retrieveInformation() {
		int index = 0;
		HashMap<DataSeries, Integer> dataSeries = new HashMap<DataSeries, Integer>();

		if (FunctionConstants.validateRequiredFields(this.setupPanel)) {
			// Validate the introduced name
			if (validateName()) {
				// Save InterExperiment setup
				fillExpSetup();

				// Table with the maximum number of columns
				int totalCol = 0;

				for (MethodTable table : methodTables) {
					// If a table has nothing selected, function is incorrect
					if (table.getSelectedRows().length == 0) {
						selectedRows.clear();
						ShowDialog.showError(I18n.get("noSelectionTitle"),
								I18n.get("noSelection"));

						return false;
					} else {
						// Obtain all the dataSeries unsorted (dataSerie == row)
						for (int row : table.getSelectedRows()) {
							dataSeries
									.putAll(getDataSerie(row, methods[index]));
						}
						index++;
					}
					if (totalCol < table.getColumnCount())
						totalCol = table.getColumnCount();
				}

				// Sort the dataSeries map
				SortedSet<Map.Entry<DataSeries, Integer>> sortedDS = FunctionConstants
						.entriesSortedByValues(dataSeries);

				// Obtain the selectedRows taken from each dataSerie in the
				// method
				DataSeries aux;
				IExperiment parent;
				int row = 0;
				for (Map.Entry<DataSeries, Integer> map : sortedDS) {
					// Get dataSerie
					aux = map.getKey();
					// Get parent
					parent = aux.getParent().getParent();
					// Save the parent (intraExperiment)
					intraExperiments.add(parent);
					// Insert data of the selected dataSerie
					selectedRows.add(getDSData(aux, totalCol));
					// Insert the intraExperiment of the dataSerie
					FunctionConstants.addValueToKey(mapExpRows, parent, row);
					row++;
				}
				for (IExperiment exp : intraExperiments) {
					// Get method conditions
					for (Method m : exp.getMethods().getMetodos()) {
						if (m.getName().equals(methodName))
							conditionNames.addAll(m.getArrayCondition()
									.getElements());
					}
				}
			} else {
				// Invalid name
				ShowDialog.showError(I18n.get("duplicateNameTitle"),
						I18n.get("duplicateName"));
				return false;
			}
		} else {
			ShowDialog.showError(I18n.get("requiredFieldsMissingTitle"),
					I18n.get("requiredFieldsMissing"));
			return false;
		}

		return true;
	}

	/**
	 * Method to validate if the introduced name in the field is valid. The name
	 * for the experiment must be unique.
	 * 
	 * @return True if name is valid, false otherwise.
	 */
	private boolean validateName() {
		String expName = this.setupPanel.getFieldName().getText().trim();
//		interExpSetup[0] = "InterExperiment_" + expName + " (";
//
//		String auxName = "";
//		for (String name : intraExpNames) {
//			if (auxName.length() == 0)
//				auxName = auxName.concat(name);
//			else
//				auxName = auxName.concat("#" + name);
//		}
//		interExpSetup[0] = interExpSetup[0].concat(auxName + ")");
		interExpSetup[0] = expName;

		return FunctionConstants.validateExperimentNames(interExpSetup[0]);
	}

	/**
	 * Method to get a specific dataSerie with its id from the input method.
	 * 
	 * @param rowsIndex
	 * @param m
	 * @return
	 */
	public Map<DataSeries, Integer> getDataSerie(int rowsIndex, Object m) {
		Method method = (Method) m;
		DataSeries dataSerie = method.getDataSeries().getElements()
				.get(rowsIndex);
		Map<DataSeries, Integer> ret = new HashMap<DataSeries, Integer>();

		// Setting the DataSerie parent to take it later
		dataSerie.setParent(method);

		ret.put(dataSerie, dataSerie.getId());

		return ret;
	}

	/**
	 * Method to obtain all the data from the input dataSerie. This method takes
	 * the replications of the condition values.
	 * 
	 * @param dataSerie
	 *            DataSerie to take the data.
	 * @return List<Object> with the information.
	 */
	public List<Object> getDSData(DataSeries dataSerie, int totalCol) {
		List<Object> ret = new ArrayList<Object>();

		// Get conditions
		for (Object condition : dataSerie.getMapCS().values()) {
			ret.add(condition);
		}

		// Get measurements
		for (Object value : dataSerie.getMeasurements()) {
			ret.add(value);
		}

		// If ret has less data than totalCol, we must create new columns with
		// NA (All method tables with the same size)
		if (ret.size() < totalCol) {
			for (int i = ret.size(); i < totalCol; i++) {
				ret.add(Double.NaN);
			}
		}

		return ret;
	}

	/**
	 * Method to retrieve the information of method that was selected.
	 */
	private void getMethodInformation() {
		// Go over each method
		Method auxMethod = null;
		int index = 0;
		for (Object m : methods) {
			auxMethod = (Method) m;

			// Get method units
			if (methodUnits.isEmpty()) {
				methodUnits = auxMethod.getUnits();
			} else {
				methodUnits = methodUnits
						.concat("_AND_" + auxMethod.getUnits());
			}

			// Get the parents name of the methods
			this.intraExpNames[index] = auxMethod.getParent().getName();
			index++;
		}

		if (auxMethod != null) {
			// Get method number of conditions
			numConditions = auxMethod.getNumConditions();
			methodName = auxMethod.getName();
		}
	}

	/**
	 * Method to create a MethodTable for the selected method of each
	 * intraExperiment.
	 */
	public void createMethodView() {
		int index = 0;
		for (Object m : this.methods) {
			// Object[] is a Method[]
			MethodTable view = new MethodTable((Method) m, false);
			JScrollPane scrollMethod = new JScrollPane(view);
			// Rows header
			RowNumberTable rowTable = new RowNumberTable(view);

			scrollMethod.setRowHeaderView(rowTable);
			scrollMethod.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					rowTable.getTableHeader());
			// Add dynamic table to a list
			methodTables.add(view);
			// Add view to the pane
			tabbedPane.addTab(intraExpNames[index], scrollMethod);
			index++;
		}
	}

	/**
	 * Method to fill the ExperimentSetup Array with the values of each
	 * TextField.
	 */
	public void fillExpSetup() {
		interExpSetup[1] = this.setupPanel.getFieldAuthors().getText().trim();
		interExpSetup[2] = this.setupPanel.getFieldOrganization().getText()
				.trim();
		interExpSetup[3] = this.setupPanel.getFieldContact().getText().trim();
		interExpSetup[4] = this.setupPanel.getFieldDate().getText().trim();
		interExpSetup[5] = this.setupPanel.getFieldPublication().getText()
				.trim();
		interExpSetup[6] = this.setupPanel.getFieldNotes().getText().trim();
	}

	/**
	 * Check exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return isExit;
	}

	/**
	 * Set exit.
	 * 
	 * @param isExit
	 */
	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	/**
	 * Get selected rows.
	 * 
	 * @return
	 */
	public List<List<Object>> getSelectedRows() {
		return selectedRows;
	}

	/**
	 * Get number of conditions.
	 * 
	 * @return
	 */
	public int getNumConditions() {
		return numConditions;
	}

	/**
	 * Get condition names.
	 * 
	 * @return
	 */
	public List<Condition> getConditionNames() {
		return conditionNames;
	}

	/**
	 * Get Method units.
	 * 
	 * @return
	 */
	public String getMethodUnits() {
		return methodUnits;
	}

	/**
	 * Get Method name.
	 * 
	 * @return
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Get intraExperiment names.
	 * 
	 * @return
	 */
	public String[] getIntraExpNames() {
		return intraExpNames;
	}

	/**
	 * Get intraExperiment rows.
	 * 
	 * @return
	 */
	public Map<Object, List<Object>> getMapExpRows() {
		return mapExpRows;
	}

	/**
	 * Get intraExperiment colors.
	 * 
	 * @return
	 */
	public Map<Object, Object> getMapExpColors() {
		return mapExpColors;
	}

	/**
	 * Get IntraExperiments list for the InterExperiment
	 * 
	 * @return
	 */
	public Set<IExperiment> getIntraExperiments() {
		return intraExperiments;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getInterExpSetup() {
		return interExpSetup;
	}
}
