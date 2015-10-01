package es.uvigo.ei.sing.bew.sheets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.tables.ConditionTable;
import es.uvigo.ei.sing.bew.tables.DataTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;

/**
 * This class provide information to create sheets for Methods.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public abstract class AbstractMethodSheetConfigurator extends JPanel implements
		ISheetConfigurator {

	private static final long serialVersionUID = 1L;

	// Sheet variables
	protected String sheetName;
	protected String sheetUnits;
	// Method name variables
	protected JComboBox<String> textComboName;
	protected CustomTextField unitsField;
	protected Map<String, String> methodUnits;
	// Condition table variables
	protected ConditionTable tableConditions;
	protected List<String> condNames;
	protected List<Integer> condValues;
	protected List<String> arrayCondUnits;
	protected int numConditions;
	// Data table variables
	protected DataTable dataTable;
	protected int lastRow;
	protected int lastCol;

	/**
	 * Empty constructor.
	 */
	public AbstractMethodSheetConfigurator() {
		super();

		this.methodUnits = new HashMap<String, String>();
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<Integer>();
		this.arrayCondUnits = new ArrayList<String>();
		this.numConditions = 0;
		this.sheetName = "EmptyMethod";

		init();
	}

	/**
	 * Constructor for adding a new Method to the Experiment.
	 * 
	 * @param condNames
	 *            Condition names.
	 * @param condValues
	 *            Condition Values.
	 * @param condUnits
	 *            Condition units.
	 * @param numCond
	 *            Number of conditions.
	 */
	public AbstractMethodSheetConfigurator(List<String> condNames,
			List<Integer> condValues, List<String> condUnits, Integer numCond) {
		super();

		this.methodUnits = new HashMap<String, String>();
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<Integer>();
		this.arrayCondUnits = new ArrayList<String>();

		this.condNames.addAll(condNames);
		this.condValues.addAll(condValues);
		this.arrayCondUnits.addAll(condUnits);
		this.numConditions = numCond;
		this.sheetName = "EmptyMethod";

		init();
	}

	/**
	 * Method to initialize dialog.
	 */
	private void init() {
		{
			// Method name variables
			this.textComboName = new JComboBox<String>();
			this.unitsField = new CustomTextField("");
			this.unitsField.setPlaceholder("m/s");
			this.unitsField.setToolTipText("Set the units for this method.");;
			putMethodNames();
			// If the user change the method name we put its units of measure
			this.textComboName.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						String item = textComboName.getSelectedItem()
								.toString();

						unitsField.setText(methodUnits.get(item));
					}
				}
			});
		}
		{
			// Condition table variables
			this.tableConditions = new ConditionTable();
		}
		{
			// Data table
			this.dataTable = new DataTable();
		}
	}

	@Override
	public boolean next() {
		// Cancel editing of the tables, saving the content
		this.dataTable.cancelEditing();
		this.tableConditions.cancelEditing();

		// Purge variables
		this.condNames.clear();
		this.condValues.clear();
		this.arrayCondUnits.clear();
		this.numConditions = 0;

		// Taking the method name
		String actualName = (String) this.textComboName.getSelectedItem();

		// The user must select a name
		if (!actualName.equals(I18n.get("comboNameDefault"))) {
			// Save the name
			this.sheetName = actualName;
			// Save the units
			this.sheetUnits = getUnits();

			// We save the Conditions
			MyTableModel mtm = this.tableConditions.getModel();
			if (tableConditions.validateTableContent()
					&& tableConditions.getRowCount() > 0) {

				condNames.addAll(tableConditions.getConditionNames());
				condValues.addAll(tableConditions.getNumberOfCondValues());
				arrayCondUnits.addAll(tableConditions.getConditionUnits());

				this.numConditions = mtm.getRowCount();

				// Validate the structure of the data
				if (validateStructure())
					return true;
				else
					ShowDialog.showError(I18n.get("Custom"),
							dataTable.getErrors());
			} else {
				String conditionErrors = tableConditions.getErrors();

				if (conditionErrors.isEmpty())
					conditionErrors = "Condition table is empty, fill it with data.";
				ShowDialog.showError("Condition table error!", conditionErrors);
			}
		} else {
			ShowDialog.showError(I18n.get("nameRequiredTitle"),
					I18n.get("nameRequired"));
		}
		return false;
	}

	/**
	 * Method to introduce the method names of the file in the comboBox.
	 */
	private void putMethodNames() {
		try {
			// Read all the method names and units from the file
			this.methodUnits = FunctionConstants.readValuesForMethod();

			// Key of the map for names, value for the units
			for (String name : methodUnits.keySet())
				this.textComboName.addItem(name);

			// Default value and units
			this.textComboName.addItem(I18n.get("comboNameDefault"));
			this.textComboName.setSelectedItem(I18n.get("comboNameDefault"));
			this.unitsField.setText("???");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get JPanel.
	 * 
	 * @return
	 */
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	/**
	 * Get data table.
	 * 
	 * @return
	 */
	public JTable getDataTable() {
		this.dataTable.cancelEditing();
		return this.dataTable;
	}

	/**
	 * Get conditions table.
	 * 
	 * @return
	 */
	public JTable getTableConditions() {
		this.tableConditions.cancelEditing();
		return tableConditions;
	}

	/**
	 * Set condition names.
	 * 
	 * @param condNames
	 */
	public void setArrayConditionNames(List<String> condNames) {
		this.condNames = condNames;
	}

	/**
	 * Get condition values.
	 * 
	 * @return
	 */
	public List<Integer> getArrayConditionValues() {
		return condValues;
	}

	/**
	 * Set condition values.
	 * 
	 * @param condValues
	 */
	public void setArrayConditionValues(List<Integer> condValues) {
		this.condValues = condValues;
	}

	/**
	 * Get number of conditions.
	 * 
	 * @return
	 */
	public Integer getNumConditions() {
		return numConditions;
	}

	/**
	 * Set number of conditions.
	 * 
	 * @param numConditions
	 */
	public void setNumConditions(int numConditions) {
		this.numConditions = numConditions;
	}

	/**
	 * Get condition names.
	 * 
	 * @return
	 */
	public List<String> getConditionNames() {
		return this.condNames;
	}

	/**
	 * Get condition names.
	 * 
	 * @return
	 */
	public List<String> getConditionUnits() {
		return this.arrayCondUnits;
	}

	/**
	 * Get last row.
	 * 
	 * @return
	 */
	public int getLastRow() {
		return lastRow;
	}

	/**
	 * Set last row.
	 * 
	 * @param lastRow
	 */
	public void setLastRow(int lastRow) {
		if (this.lastRow < lastRow)
			this.lastRow = lastRow;
	}

	/**
	 * Get last column.
	 * 
	 * @return
	 */
	public int getLastCol() {
		return lastCol;
	}

	/**
	 * Set last column.
	 * 
	 * @param lastCol
	 */
	public void setLastCol(int lastCol) {
		if (this.lastCol < lastCol)
			this.lastCol = lastCol;
	}

	/**
	 * Method to convert the data table in an Object matrix.
	 * 
	 * @return Object[][].
	 */
	public Object[][] getTableToObject() {
		Object[][] data = new Object[dataTable.getModel().getRowCount()][dataTable
				.getModel().getColumnCount()];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = dataTable.getModel().getValueAt(i, j);
			}

		}
		return data;
	}

	/**
	 * Get units.
	 * 
	 * @return
	 */
	public String getUnits() {
		return this.unitsField.getText();
	}

	@Override
	public String getExperimentName() {
		return null;
	}
}
