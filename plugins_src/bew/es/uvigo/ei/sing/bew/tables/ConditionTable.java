package es.uvigo.ei.sing.bew.tables;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.models.SortedComboBoxModel;
import es.uvigo.ei.sing.bew.tables.renderer.CellComboEditor;
import es.uvigo.ei.sing.bew.tables.renderer.CellComboRenderer;
import es.uvigo.ei.sing.bew.tables.renderer.CellRenderer;

/**
 * Custom table for Conditions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ConditionTable extends LongTextTable {

	private static final long serialVersionUID = 1L;

	private MyTableModel mtm;
	private Map<Integer, String> actualRowCond;
	private Object[][] tableData;

	private String errors;

	/**
	 * Default constructor.
	 */
	public ConditionTable() {
		super();

		this.mtm = new MyTableModel(new Object[][] {}, new String[] {
				"Conditions", "Number of condition values", "Units" });
		mtm.addColumnClass(0, String.class);
		mtm.addColumnClass(1, Integer.class);
		mtm.addColumnClass(2, String.class);
		
		actualRowCond = new HashMap<Integer, String>();
		errors = "";

		init();
	}

	/**
	 * Initialize the dialog.
	 */
	private void init() {
		setModel(mtm);
		setGridColor(Color.BLACK);
		// Default Renderer for String cells. Empty cells are red.
		setDefaultRenderer(Object.class, new CellRenderer());

		getTableHeader().setReorderingAllowed(false);
		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Editable columns
		mtm.setColumnIndex(1);
		mtm.setColumnIndex(2);
		// Set combo with values in 0
		setComboBox(0);

		// Table change listener
		mtm.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(final TableModelEvent e) {
				int col = e.getColumn();
				int row = e.getFirstRow();
				// Get comboEditor
				CellComboEditor cellEditor = (CellComboEditor) getColumnModel()
						.getColumn(0).getCellEditor();

				// If column 0 changes
				if (col == 0) {
					Object value = getValueAt(row, col);
					if (value != null) {
						if (value.toString().equals(BewConstants.METACONDITION)) {
							// We save the actual value
							actualRowCond.put(row, value.toString());
							// We set editable false to this cell
							mtm.removeCellIndex(row, 0);
						} else {
							// We save the actual value
							actualRowCond.put(row, value.toString());
							// We delete the selected value in combo (cannot
							// repeat
							// conditions)
							cellEditor.deleteComboValue(value.toString());
							// We set editable false to this cell
							mtm.removeCellIndex(row, 0);
						}
					}
				}
				// If user delete the row...
				else if (e.getType() == TableModelEvent.DELETE) {
					// Need save the deleted value
					String actualValue = actualRowCond.get(row);
					if (actualValue != null) {
						// Metacondition is always added in combo
						if (!actualValue.equals(BewConstants.METACONDITION))
							cellEditor.addComboValue(actualValue);
					}
					// Refresh map keys
					refreshMapsKeys(row);
					// Refresh editable index in the table model
					mtm.refreshCellIndexes(row);
				}
				// If user insert a row, we set editable the cell for the first
				// column
				else if (e.getType() == TableModelEvent.INSERT) {
					mtm.setCellIndex(row, 0);
				}
			}
		});

	}

	/**
	 * Method to introduce the Conditions of the program in the table.
	 * 
	 * @param columnIndex
	 *            Column to introduce the comboBox.
	 */
	public void setComboBox(int columnIndex) {
		SortedComboBoxModel<String> model = new SortedComboBoxModel<String>();
		JComboBox<String> data = new JComboBox<String>(model);
		List<String> values = FunctionConstants.loadConditionsFromFile();

		for (String value : values)
			data.addItem(value);

		getColumnModel().getColumn(columnIndex).setCellEditor(
				new CellComboEditor(data));
		getColumnModel().getColumn(columnIndex).setCellRenderer(
				new CellComboRenderer(values.toArray()));
	}

	/**
	 * Loads data to the table from the input matrix.
	 * 
	 * @param data
	 *            Static matrix with the data.
	 */
	public void loadDataFromMatrix(Object[][] data) {
		List<String> conditions = FunctionConstants.loadConditionsFromFile();
		
		for (int index = 0; index < data.length; index++) {
			Object[] dataRow = new Object[data[index].length];
			int i = 0;
			// Insert imported data in our array
			for (Object o : data[index]) {
				if (i == 0 && o instanceof String) {
					// Take care of metaconditions_X
					o = o.toString().split("\\_")[0];
					if(!conditions.contains(o)){
						o = null;
					}
				}
				dataRow[i] = o;
				i++;
			}
			// Insert data per rows
			this.mtm.insertRow(mtm.getRowCount(), dataRow);
			// Fire the listener for column 0 on each row
			mtm.fireTableCellUpdated(mtm.getRowCount() - 1, 0);
		}
	}

	/**
	 * Refreshes the keys inside the maps of this class. Need to call this void
	 * when the user delete something in the table (row).
	 * 
	 * @param row
	 *            Deleted row.
	 */
	private void refreshMapsKeys(Integer row) {
		// Remove the input row
		actualRowCond.remove(row);

		String auxValue;
		// We have to make a copy of the keySet
		Set<Integer> setAux = new HashSet<Integer>();

		// Go over all the maps to reduce the index number
		setAux.addAll(actualRowCond.keySet());
		for (Integer key : setAux) {
			if (key > row) {
				auxValue = actualRowCond.get(key);
				actualRowCond.put(key - 1, auxValue);

				actualRowCond.remove(key);
			}
		}
	}

	/**
	 * Validates the content of the table.
	 * 
	 * @return True if content is valid, false otherwise.
	 */
	public boolean validateTableContent() {
		boolean toRet = false;
		int metaIndex = 0;

		int rowCount = getRowCount();
		int colCount = getColumnCount();
		Object value;
		List<Integer> numberOfValues = new ArrayList<Integer>();
		tableData = new Object[rowCount][colCount];

		this.errors = "";
		// Go over the table
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				value = getValueAt(row, col);

				// Validate if there are empty values
				if (value == null || value.toString().isEmpty()) {
					String colHeader = getColumnModel().getColumn(col)
							.getHeaderValue().toString();
					errors = errors.concat("- Cell in row: " + (row + 1)
							+ " and col: " + colHeader
							+ " is empty. Fill it with data.\n");
				} else {
					Object savedValue = actualRowCond.get(row);
					if (col == 0
							&& savedValue.equals(BewConstants.METACONDITION)) {
						tableData[row][col] = savedValue + "_" + metaIndex;
						metaIndex++;
					} else
						tableData[row][col] = value;

					// Save number of values
					if (col == 1) {
						numberOfValues.add(Integer.parseInt(value.toString()));
					}
				}
			}
		}

		// Check for 0 values
		if (!checkNumberOfValuesOrder(numberOfValues)) {
			this.errors = "There are one or more values with 0 or less.";
		}

		if (errors.isEmpty())
			toRet = true;
		// If table has errors, delete the tableData
		else
			tableData = null;

		return toRet;
	}

	/**
	 * Checks the order of the condition numbers in the second column of the
	 * table. If a 0 is present, the function will return false.
	 * 
	 * @param numberOfValues
	 *            The numbers to check.
	 * @return True if the order is correct, false otherwise.
	 */
	private boolean checkNumberOfValuesOrder(List<Integer> numberOfValues) {
		for (Integer i : numberOfValues) {
			Integer value = i;
			if (value <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Cancels editing for table.
	 */
	public void cancelEditing() {
		if (isEditing())
			getCellEditor().stopCellEditing();
	}

	/**
	 * Inserts a blank row in the table with NaN in the units.
	 */
	public void insertBlankRow() {
		mtm.insertRow(mtm.getRowCount(), new Object[] { null, "",
				BewConstants.DEFAULT_VALUE });
	}

	@Override
	public MyTableModel getModel() {
		return this.mtm;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrors() {
		return this.errors;
	}

	/**
	 * 
	 * @return
	 */
	public Object[][] getTableData() {
		return this.tableData;
	}

	/**
	 * Gets the names of the different Conditions in the table.
	 * 
	 * @return List<String> with the Condition names.
	 */
	public List<String> getConditionNames() {
		List<String> toRet = new ArrayList<String>();
		int metaIndex = 0;
		for (int fil = 0; fil < this.mtm.getRowCount(); fil++) {
			String value = this.mtm.getValueAt(fil, 0).toString();
			if (value.equals(BewConstants.METACONDITION)) {
				toRet.add(value + "_" + metaIndex);
				metaIndex++;
			} else {
				toRet.add(value);
			}
		}

		return toRet;
	}

	/**
	 * Gets the number of condition values in the table.
	 * 
	 * @return List<Integer> with the Condition values number.
	 */
	public List<Integer> getNumberOfCondValues() {
		List<Integer> toRet = new ArrayList<Integer>();
		for (int fil = 0; fil < this.mtm.getRowCount(); fil++)
			toRet.add((Integer) this.mtm.getValueAt(fil, 1));

		return toRet;
	}

	/**
	 * Gets Condition units in the table.
	 * 
	 * @return List<String> with the Condition units.
	 */
	public List<String> getConditionUnits() {
		List<String> toRet = new ArrayList<String>();
		for (int fil = 0; fil < this.mtm.getRowCount(); fil++)
			toRet.add((String) this.mtm.getValueAt(fil, 2));

		return toRet;
	}
}
