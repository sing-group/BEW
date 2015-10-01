package es.uvigo.ei.sing.bew.tables;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import jxl.Cell;
import jxl.Sheet;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.tables.dialogs.TableDialog;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.models.SortedComboBoxModel;
import es.uvigo.ei.sing.bew.tables.renderer.CellComboEditor;
import es.uvigo.ei.sing.bew.tables.renderer.CellComboRenderer;
import es.uvigo.ei.sing.bew.tables.renderer.CellRenderer;

/**
 * Custom table to show the Experiment setup.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class SetupTable extends LongTextTable {

	private static final long serialVersionUID = 1L;

	private MyTableModel mtm;
	private SetupTable me;
	private Map<Integer, String> actualRowCond;
	private Map<Integer, TableDialog> rowDialog;

	private String errors;
	private Object[][] tableData;
	private boolean isEditing;
	private boolean isInterExp;

	/**
	 * Default constructor.
	 */
	public SetupTable(boolean isInter) {
		super();

		this.mtm = new MyTableModel(new Object[][] {}, new String[] {
				"Conditions", "Condition values", "Units" });
		mtm.addColumnClass(0, String.class);
		mtm.addColumnClass(1, String.class);
		mtm.addColumnClass(2, String.class);

		actualRowCond = new HashMap<Integer, String>();
		rowDialog = new HashMap<Integer, TableDialog>();
		me = this;
		errors = "";
		this.isInterExp = isInter;

		init();
	}

	/**
	 * Initializes the table.
	 */
	private void init() {
		setModel(mtm);
		setGridColor(Color.BLACK);
		// Default Renderer for String cells. Empty cells are red.
		setDefaultRenderer(String.class, new CellRenderer());

		getTableHeader().setReorderingAllowed(false);
		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Set combo with values in 0
		setComboBox(0);
		if (!isInterExp) {
			// Index column 2 is editable
			mtm.setColumnIndex(2);
		}

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
				// If user insert a row, we set editable the cell for the
				// first
				// column
				else if (e.getType() == TableModelEvent.INSERT) {
					mtm.setCellIndex(row, 0);
				}
			}
		});

		if (!isInterExp) {
			// Mouse listener on col 1 to open table dialogs
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					// Get clicked row
					int row = rowAtPoint(e.getPoint());
					// Get clicked column
					int col = columnAtPoint(e.getPoint());
					// Get the value of the col 1 for this row
					String actualValueRow = actualRowCond.get(row);

					// Always works in col 1
					if (col == 1 && actualValueRow != null) {
						TableDialog savedDialog = rowDialog.get(row);
						String finalValue = null;
						// If didn't user open a previous dialog for this
						// condition
						if (savedDialog == null) {
							try {
								// Get values for selected condition
								List<String> valuesForList = FunctionConstants
										.readValuesForCondition(actualValueRow);
								// Creating the specific table dialog
								TableDialog tableDialog = FunctionConstants
										.createSpecificTableDialog(
												valuesForList, actualValueRow,
												me);
								// Saving the dialog
								rowDialog.put(row, tableDialog);

								tableDialog.setVisible(true);
								finalValue = tableDialog.getFinalValue();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							try {
								// Open saved dialog
								savedDialog.setVisible(true);
								finalValue = savedDialog.getFinalValue();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						// When user close dialog properly
						if (finalValue != null) {
							setValueAt(finalValue, row, 1);
						}
					}

				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
		}
	}

	/**
	 * Method to introduce a comboBox with the actual Conditions in a column of
	 * the table.
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

		data.setSelectedItem(null);
	}

	/**
	 * Loads data from the input matrix.
	 * 
	 * @param data
	 *            Matrix with data.
	 */
	public void loadDataFromMatrix(Object[][] data) {
		String condValue;

		for (int index = 0; index < data.length; index++) {
			Object[] dataRow = new Object[data[index].length];
			int i = 0;
			// Insert imported data in our array
			for (Object o : data[index]) {
				// Only in the first column
				if (i == 0) {
					// Take care of metaconditions_X
					o = o.toString().split("\\_")[0];
				}

				dataRow[i] = o;
				i++;
			}

			// Insert data per rows
			this.mtm.insertRow(mtm.getRowCount(), dataRow);
			// Fire the listener for column 0 on each row
			mtm.fireTableCellUpdated(mtm.getRowCount() - 1, 0);

			try {
				// Put specific dialog
				condValue = dataRow[0].toString();
				// Get values for selected condition
				List<String> valuesForList;
				valuesForList = FunctionConstants
						.readValuesForCondition(condValue);
				// Creating the specific table dialog
				TableDialog tableDialog = FunctionConstants
						.createSpecificTableDialog(valuesForList, condValue, me);
				// Set loaded values from data in the dialog
				tableDialog.setFinalValue(dataRow[1].toString());
				// Saving the dialog
				rowDialog.put(mtm.getRowCount() - 1, tableDialog);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	/**
	 * Loads data in the table from a XLS Sheet.
	 * 
	 * @param sheet
	 *            Sheet with data.
	 */
	public void loadDataFromSheet(Sheet sheet) {
		// Get comboEditor
		CellComboEditor cellEditor = (CellComboEditor) getColumnModel()
				.getColumn(0).getCellEditor();
		String condValue;
		boolean valid = true;

		// All Conditions are unique except metaconditions
		List<String> repConds = new ArrayList<String>();
		for (int index = 0; index < sheet.getRows(); index++) {
			// If condition doesn't exist in the local file the program won't
			// load it
			condValue = sheet.getCell(0, index).getContents().toLowerCase();
			// Take care of metaconditions_X
			condValue = condValue.split("\\_")[0].trim();
			try {
				if (!condValue.isEmpty()) {
					Integer res = FunctionConstants.condValidation(condValue,
							isEditing);

					if (res == 0 && !repConds.contains(condValue)) {
						// System.out.println("SetupLoadData=0");
					} else if (res == 1) {
						condValue = FunctionConstants.oppositeValue(condValue);
					} else if (res == 2) {
						// Refresh comboBoxes with new values
						this.errors += "- Constant Condition: "
								+ condValue
								+ ", added to available Conditions. Revise the conditions for this experiment if you don't see it.\n";
						condValue = FunctionConstants.putAsterisks(condValue);
						setComboBox(0);

					} else if (res == 3) {
						// Warning the user that the new value
						// is
						// available
						errors = errors.concat("- Constant Condition: "
								+ condValue
								+ " has forbbiden characters. Change it.\n");
						valid = false;
					}

					if (valid && !repConds.contains(condValue)) {
						Object[] dataRow = new Object[sheet.getRow(index).length];
						int i = 0;
						for (Cell cell : sheet.getRow(index)) {
							if (i != 0) {
								dataRow[i] = FunctionConstants
										.replaceCommas(cell.getContents()
												.toLowerCase());
							}
							// First time we add the condition
							else {
								dataRow[i] = FunctionConstants
										.replaceCommas(condValue);
							}

							i++;
						}
						// Insert data per rows
						this.mtm.insertRow(mtm.getRowCount(), dataRow);
						// Fire the listener for column 0 on each row
						mtm.fireTableCellUpdated(mtm.getRowCount() - 1, 0);

						// Get values for selected condition
						List<String> valuesForList;
						valuesForList = FunctionConstants
								.readValuesForCondition(condValue);
						// Creating the specific table dialog
						TableDialog tableDialog = FunctionConstants
								.createSpecificTableDialog(valuesForList,
										condValue, me);
						// Set loaded values from data in the dialog
						tableDialog.setFinalValue(sheet.getCell(1, index)
								.getContents().toLowerCase());
						// Saving the dialog
						rowDialog.put(mtm.getRowCount() - 1, tableDialog);

						// Delete used conditions from combo
						cellEditor.deleteComboValue(condValue);
					}

					// Metaconditions are not unique
					if (!condValue.equals(BewConstants.METACONDITION))
						repConds.add(condValue);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		actualRowCond.remove(row);
		rowDialog.remove(row);

		String auxValue;
		TableDialog auxPanel;
		// We have to make a copy of the keySet
		Set<Integer> setAux = new HashSet<Integer>();

		// Go over all the maps to reduce the index number
		setAux.addAll(actualRowCond.keySet());
		for (Integer key : setAux) {
			if (key > row) {
				auxValue = actualRowCond.get(key);
				actualRowCond.put((key - 1), auxValue);

				actualRowCond.remove(key);
			}
		}

		setAux.clear();
		setAux.addAll(rowDialog.keySet());
		for (Integer key : setAux) {
			if (key > row) {
				auxPanel = rowDialog.get(key);
				// Need to refresh the row index in the TableDialog too
				auxPanel.setTableRow(key - 1);
				rowDialog.put((key - 1), auxPanel);

				rowDialog.remove(key);
			}
		}
	}

	/**
	 * Validate the setup table content. If the validation find a strange value
	 * in one cell, the value will be added to the condition values files so the
	 * user can use it later.
	 * 
	 * @return True if content is valid, false otherwise.
	 * @throws Exception
	 */
	public boolean validateTableContent() {
		boolean toRet = true;
		int metaIndex = 0;

		int rowCount = getRowCount();
		int colCount = getColumnCount();
		Object value;
		tableData = new Object[rowCount][colCount];
		String condition = "";
		// Condition values are unique per row
		List<String> repeteadCondValue = new ArrayList<String>();

		this.errors = "";
		// Go over rows
		for (int row = 0; row < rowCount; row++) {
			// Go over columns
			for (int col = 0; col < colCount; col++) {
				value = getValueAt(row, col);

				// Get Condition for this column
				if (col == 0) {
					if (value == null)
						condition = "";
					else
						condition = value.toString();
				}
				// Validate Condition value for this column and if there is a
				// value
				if (col == 1 && value != null && !value.toString().isEmpty()) {
					// Split compound values (_and_)
					String[] condValues = value.toString().split(
							BewConstants.AND);
					// New value for the cell (if needed)
					String newValue = "";
					// Get the validation function return
					Integer isInFile = 0;

					// Split _and_ and go over each condition value
					for (String val : condValues) {
						val = val.trim();
						String auxValue = val;
						// Each value is unique per row so bew don't let
						// repeated values
						if (!repeteadCondValue.contains(val)) {
							// Create the condition value automatically
							if (!val.isEmpty()) {
								// Validate if the value exists in the
								// program
								isInFile = FunctionConstants
										.condValueValidation(val, condition,
												isEditing);

								if (isInFile == 0) {
									// System.out.println("Setup=0");
								} else if (isInFile == 1) {
									auxValue = FunctionConstants
											.oppositeValue(auxValue);
								} else if (isInFile == 2) {
									auxValue = FunctionConstants
											.putAsterisks(auxValue);

								} else if (isInFile == 3) {
									// Warning the user that the new
									// value
									// is
									// available
									errors = errors
											.concat("- Value: "
													+ auxValue
													+ ", in row: "
													+ (row + 1)
													+ " and col 'Condition values' for: "
													+ condition
													+ " has forbbiden characters. Change it.\n");
									toRet = false;
								}
							}
							// For compound values
							if (!newValue.isEmpty()) {
								newValue += BewConstants.AND + auxValue;
							} else {
								newValue += auxValue;
							}
						}

						// Add to repeated values when introduced
						repeteadCondValue.add(val);
					}

					// Set the new value in the cell
					setValueAt(newValue, row, col);

				}

				// If cell is empty we save the position
				if (value == null || value.toString().isEmpty()) {
					String colHeader = getColumnModel().getColumn(col)
							.getHeaderValue().toString();
					errors = errors.concat("- Cell in row: " + (row + 1)
							+ " and col: " + colHeader
							+ " is empty. Fill it with data.\n");
					toRet = false;
				} else {
					String savedValue = actualRowCond.get(row);
					if (col == 0
							&& savedValue.equals(BewConstants.METACONDITION)) {
						tableData[row][col] = savedValue + "_" + metaIndex;
						metaIndex++;
					} else
						tableData[row][col] = value;
				}
			}
			// Clear repeated values for each row
			repeteadCondValue.clear();
		}

		// If table has errors, delete the tableData
		if (!toRet)
			tableData = null;

		return toRet;
	}

	/**
	 * Cancels editing in the table.
	 */
	public void cancelEditing() {
		if (isEditing())
			getCellEditor().stopCellEditing();
	}

	/**
	 * Inserts blank row with NaN in the units.
	 */
	public void insertBlankRow() {
		mtm.insertRow(mtm.getRowCount(), new Object[] { null, "",
				BewConstants.DEFAULT_VALUE });
	}

	/**
	 * 
	 */
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
	 * 
	 * @param isEditing
	 */
	public void setIsEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public boolean isInter() {
		return isInterExp;
	}

	public void setInter(boolean isInter) {
		this.isInterExp = isInter;
	}
}
