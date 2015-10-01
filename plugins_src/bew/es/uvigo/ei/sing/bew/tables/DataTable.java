package es.uvigo.ei.sing.bew.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.tables.dialogs.TableDialog;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.MyTableCellRender;
import es.uvigo.ei.sing.bew.util.ThreadLastColumn;
import es.uvigo.ei.sing.bew.util.ThreadLastRow;

/**
 * Custom table to show data.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class DataTable extends LongTextTable {

	private static final long serialVersionUID = 1L;

	private int numConditions;

	private MyTableModel mtm;
	private MyTableCellRender mtc;

	private Map<String, TableDialog> rowDialog;
	private String errors;
	private boolean isEditing;

	private int lastRow;
	private int lastCol;
	private int index = 1;

	/**
	 * Default constructor.
	 */
	public DataTable() {
		// TODO Auto-generated constructor stub
		super();

		rowDialog = new HashMap<String, TableDialog>();
		this.mtm = new MyTableModel(new Object[][] {}, new String[] {});
		this.mtc = new MyTableCellRender();
		this.errors = "";

		init();
	}

	/**
	 * Initializes the table.
	 */
	private void init() {
		setModel(mtm);
		setGridColor(Color.BLACK);
		setDefaultRenderer(Object.class, mtc);
		getTableHeader().setReorderingAllowed(false);

		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);

		// Mouse listener on col 1 to open table dialogs
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// Get clicked row
				int row = rowAtPoint(e.getPoint());
				// Get clicked column
				int col = columnAtPoint(e.getPoint());
				// Map key
				String key = String.valueOf(row) + "#" + String.valueOf(col);

				if (col != -1) {
					// Get the value of the col 1 for this row
					Object actualValueRow = getColumnModel().getColumn(col)
							.getHeaderValue();
					if (actualValueRow != null) {
						String colCondition = actualValueRow.toString();
						// We need must this to recognize
						// metacondition_X in metacondition
						colCondition = colCondition.split("_")[0];
						if (mtm.isCellEditable(row, col)) {
							List<String> conditions = FunctionConstants
									.loadConditionsFromFile();
							if (conditions.contains(colCondition)) {
								TableDialog savedDialog = rowDialog.get(key);
								String finalValue = null;
								// If didn't user open a previous dialog for
								// this
								// condition
								if (savedDialog == null) {
									try {
										// Get values for selected condition
										List<String> valuesForList = FunctionConstants
												.readValuesForCondition(colCondition);
										// Creating the specific table
										// dialog
										TableDialog tableDialog = FunctionConstants
												.createSpecificTableDialog(
														valuesForList,
														colCondition, null);
										// Saving the dialog
										rowDialog.put(key, tableDialog);

										tableDialog.setVisible(true);
										// When user close dialog properly
										finalValue = tableDialog
												.getFinalValue();
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								} else {
									try {
										// Open saved dialog
										savedDialog.setVisible(true);
										// When user close dialog properly
										finalValue = savedDialog
												.getFinalValue();
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								if (finalValue != null) {
									setValueAt(finalValue, row, col);
								}
							}
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * Inserts a row in the table.
	 */
	public void insertRow() {
		cancelEditing();

		Object[] dataRow = new Object[this.getColumnCount()];
		for (int i = 0; i < dataRow.length; i++) {
			dataRow[i] = "";
		}

		if (mtm.getColumnCount() > 0) {
			mtm.insertRow(this.getRowCount(), dataRow);
			mtm.addRowIndex(this.getRowCount() - 1);
		}
	}

	/**
	 * Method to add a new non editable row
	 */
	public void insertNonEditableRow() throws OutOfMemoryError {
		cancelEditing();

		Object[] dataRow = new Object[this.getColumnCount()];
		for (int i = 0; i < dataRow.length; i++) {
			dataRow[i] = "";
		}

		if (mtm.getColumnCount() > 0) {
			mtm.insertRow(this.getRowCount(), dataRow);
		}
	}

	/**
	 * Deletes a row in the table.
	 */
	public void deleteRow() {
		this.cancelEditing();

		int[] rows = getSelectedRows();

		for (int i = 0; i < rows.length; i++) {
			mtm.removeRow(rows[i] - i);
			mtm.removeRowIndex(rows[i] + 1);
		}
	}

	/**
	 * Inserts a new column in the table.
	 * 
	 * @param name
	 *            Optional parameter if the user send something the column
	 *            header will be this input.
	 */
	public void newColumn(String... name) {
		this.cancelEditing();

		Object[] dataRow = new Object[getRowCount()];
		for (int i = 0; i < dataRow.length; i++) {
			dataRow[i] = "";
		}

		// Introduce a column at the end
		if (name.length != 0) {
			mtm.addColumn(name[0], dataRow);
			mtm.addColumnClass(mtm.getColumnCount() - 1, String.class);
		} else {
			mtm.addColumn("Replicate " + this.index, dataRow);
			mtm.setColumnIndex(mtm.getColumnCount() - 1);

			// Data columns only have Double values
			mtm.addColumnClass(mtm.getColumnCount() - 1, String.class);
			mtc.addColumn(mtm.getColumnCount() - 1);

			this.index++;
		}
	}

	/**
	 * Inserts a new non editable column in the table.
	 * 
	 * @param name
	 *            Optional parameter if the user send something the column
	 *            header will be this input.
	 */
	public void newNoEditableColumn(String... name) {
		this.cancelEditing();

		Object[] dataRow = new Object[getRowCount()];
		for (int i = 0; i < dataRow.length; i++) {
			dataRow[i] = "";
		}

		// Introduce a column at the end
		if (name.length != 0) {
			mtm.addColumn(name[0], dataRow);
			mtm.addColumnClass(mtm.getColumnCount() - 1, String.class);
		} else {
			mtm.addColumn("Replicate " + this.index, dataRow);

			// Data columns only have Double values
			mtm.addColumnClass(mtm.getColumnCount() - 1, String.class);

			this.index++;
		}
	}

	/**
	 * Deletes a column in the table.
	 */
	public void deleteColumn() {
		this.cancelEditing();
		int[] columns = this.getSelectedColumns();
		if (columns.length != 0) {
			int col = 0;

			for (int index : columns) {
				// The method only delete the columns that the user has inserted
				// not the generate columns
				if (index >= this.numConditions) {
					// Create a new table model and copy the actual
					MyTableModel newTm = new MyTableModel();

					// Copy the editable columns and cells to the new model
					newTm.setEditableColumn(mtm.getEditableColumn());
					newTm.setEditableIndex(mtm.getEditableIndex());
					newTm.setEditableRow(mtm.getEditableRow());
					newTm.setMapColumnClass(mtm.getMapColumnClass());

					Vector<?> vector = mtm.getDataVector();

					Vector<?> vector1 = deleteColumns(vector, index - col);
					newTm.setDataVector(vector1,
							getColumnIdentifiers(mtm.getColumnCount() - 1));

					// Remove the editable column index of this col
					newTm.removeColumnIndex(index + 1);

					mtm = newTm;
					this.setModel(newTm);
					// Don't need to delete, all the columns that the user can
					// create are editable
					// mtc.deleteCol(index + 1);
					col++;

					this.index--;
				}
			}
		}
	}

	/**
	 * Method to fill the table with the entry values.
	 * 
	 * @param numCol
	 *            Number of conditions that the user created.
	 * @param nums
	 *            List<Integer> with the condition values column.
	 * @param conditions
	 *            LinkedList<String> with the conditions column.
	 * @throws Exception
	 */
	public void fillDataTable(int numCol, List<Integer> nums,
			List<String> conditions) throws OutOfMemoryError, Exception {
		int product = 1;

		// Saving number of conditions
		this.numConditions = numCol;

		// If there are rows created in the ConditionsTable
		if (!nums.isEmpty()) {
			// We do the product of the integers to know the total number of
			// rows in DataTable
			try {
				for (int num : nums) {
					// Only >= values to preserve tree structure
					// if (aux <= num) {
					product = product * num;
					// } else
					// throw new Exception();
				}

				// Adding as columns at the table as conditions in the list
				for (int i = 0; i < numCol; i++) {
					newColumn(conditions.get(i));
				}

				// Adding as rows as the product (nums)
				// Ex: 2, 2, 5; the prod = 20. You have 20 rows and 3 columns
				for (int i = 0; i < product; i++) {
					insertNonEditableRow();
				}

				// We only let some editable cells in order to keep the
				// structure. The user can not edit all
				createDataCells(product, nums, conditions);
			} catch (OutOfMemoryError e) {
				deleteTableContent();
				throw new OutOfMemoryError();
			} catch (Exception e) {
				deleteTableContent();
				throw new Exception(e);
			}
		}

	}

	/**
	 * Method to do non editable cells. The editable cells will be the one that
	 * appear during the call of this method Ex: 2, 2, 5 You have 20 row and 3
	 * columns 1º cells editable column -> 20/2 = 10, so the editable cells will
	 * be from 1 to la 11 2º cells editable column -> 10/2 = 5, so the editable
	 * cells will be 1, 6, 11...
	 * 
	 * @param prod
	 *            Total rows.
	 * @param nums
	 *            Number of childs per Condition.
	 * @param conditions
	 *            Conditions to add the popup dialog to the cells.
	 * 
	 */
	private void createDataCells(int prod, List<Integer> nums,
			List<String> conditions) {
		MyTableModel mtm = (MyTableModel) this.getModel();
		int division = 1;
		int producto = prod;

		for (int col = 0; col < mtm.getColumnCount(); col++) {
			division = producto / nums.get(col);
			producto = division;

			// Add dialog to the editable cell
			for (int fil = 0; fil < mtm.getRowCount(); fil++) {
				if (division != 1 && fil == division) {
					// Introduce the position of the cell so it will be editable
					mtm.setCellIndex(fil, col);
					// We add the product to obtain the next cell in the column
					division = division + producto;

				} else if (division == 1) {
					// If division == 1 all the column will be editable
					mtm.setCellIndex(fil, col);

				}
			}
			// Each first row will be editable too (keep structure)
			mtm.setCellIndex(0, col);
		}

	}

	/**
	 * Method to remove columns from a Table.
	 * 
	 * @param vector
	 *            Vector with the values of the actual model.
	 * @param index
	 *            Index to delete.
	 * @return Vector with the index deleted.
	 */
	private Vector<?> deleteColumns(Vector<?> vector, int index) {
		Vector aux;
		Vector res = new Vector();

		for (Object i : vector.toArray()) {
			aux = (Vector<?>) i;
			aux.remove(index);
			res.add(aux);
		}
		return res;
	}

	/**
	 * Method to rename the columns when you remove one.
	 * 
	 * @param index
	 *            Start index.
	 * @return Vector Vector with the new names.
	 */
	private Vector getColumnIdentifiers(int index) {
		Vector columnIdentifiers = new Vector();
		Vector<String> actualColumnNames = new Vector<String>();

		for (int col = 0; col < this.getColumnCount(); col++) {
			actualColumnNames.add(this.getColumnName(col));
		}

		int j = 0;
		while (j < index) {
			// You validate if the value is a number
			try {
				// If is a number you calculate the correct index for this
				// column
				Integer.parseInt(actualColumnNames.get(j));
				columnIdentifiers.add(j + 1);
			} catch (NumberFormatException e) {
				// If the column name has text, you put that text in the column
				columnIdentifiers.add(actualColumnNames.get(j));
			}
			j++;
		}

		return columnIdentifiers;
	}

	/**
	 * Method to delete all the content in a table. This method need
	 * MyTableModel.
	 */
	public void deleteTableContent() {
		mtm.setColumnCount(0);
		mtm.setRowCount(0);

		mtm.removeAllRowIndex();
		mtm.removeAllCellIndex();
		mtm.removeAllColumnIndex();
		mtc.deleteAllCol();
		rowDialog.clear();
		this.index = 1;
	}

	/**
	 * Method to create the data in the Table from an entry Sheet (only when
	 * load a file).
	 * 
	 * @param sheet
	 *            Sheet with the data.
	 */
	public void initTable(Sheet sheet) {
		int numCol = sheet.getColumns();
		int numRow = sheet.getRows();

		// Variable to save the data to introduce in the row
		Object[] array = new Object[numCol];

		// Introduce as many rows as the sheet
		String name = "";
		for (int col = 0; col < numCol; col++) {
			name = sheet.getCell(col, 2).getContents();
			if (name.isEmpty()) {
				newNoEditableColumn();
			} else {
				newNoEditableColumn(name);
			}
		}

		// We start at fourth row. First row always for MethodUnits
		if (numRow > 1) {
			for (int row = 3; row < numRow; row++) {
				for (int col = 0; col < numCol; col++) {
					// [0,0] first cell in Excel
					Cell cell = sheet.getCell(col, row);
					// Empty cell
					if (cell.getType() == CellType.EMPTY)
						array[col] = "";
					else {
						name = cell.getContents();
						if (name.equals("NaN")) {
							array[col] = name;
						} else {
							array[col] = FunctionConstants.replaceCommas(name
									.toLowerCase());
						}
					}

				}
				// Introduce the row with the data
				mtm.addRow(array);
				// Fire the listener for column 0 on each row
				mtm.fireTableCellUpdated(mtm.getRowCount() - 1, 0);
			}
		}
		resizeColumnWidth();
	}

	/**
	 * Method to pass the data from a matrix to the Table and initialize the
	 * table headers.
	 * 
	 * @param data
	 *            Matrix with the data.
	 * @param condName
	 *            Condition names to initialize the headers.
	 */
	public void initTableWithHeaders(Object[][] data, List<String> condName) {
		if (data != null) {
			// Introduce columns with headers
			String name = null;
			for (int col = 0; col < data[0].length; col++) {
				if (col < condName.size()) {
					name = condName.get(col);
					newNoEditableColumn(name);
				} else
					newNoEditableColumn();
			}
		}

		try {
			for (int row = 0; row < data.length; row++) {
				for (int col = 0; col < condName.size(); col++) {
					String dataValue = data[row][col].toString();
					if (!dataValue.isEmpty()) {
						// Map key
						String key = String.valueOf(row) + "#"
								+ String.valueOf(col);
						// Get values for selected condition
						List<String> valuesForList = FunctionConstants
								.readValuesForCondition(condName.get(col));
						// Creating the specific table
						// dialog
						TableDialog tableDialog = FunctionConstants
								.createSpecificTableDialog(valuesForList,
										condName.get(col), null);
						tableDialog.setFinalValue(dataValue);
						rowDialog.put(key, tableDialog);
					}
				}

				mtm.addRow(data[row]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resizeColumnWidth();
	}

	/**
	 * Cancels editing in table.
	 */
	public void cancelEditing() {
		if (isEditing())
			getCellEditor().stopCellEditing();
	}

	/**
	 * Purges the created row dialogs.
	 */
	public void purgeRowDialog() {
		this.rowDialog.clear();
	}

	/**
	 * Validates the structure of the data table.
	 * 
	 * @param condValues
	 *            Condition values.
	 * @param condNames
	 *            Condition names.
	 * @param clean
	 *            If true delete the leftovers columns and rows, false
	 *            otherwise.
	 * @return True if the structure is correct, false otherwise.
	 */
	public boolean validateStructure(List<Integer> condValues,
			List<String> condNames, boolean clean) {
		this.errors = "";
		String cellValue = "";

		boolean valid = true;

		int rowCount = getRowCount();
		int colCount = getColumnCount();

		// Validate if the user introduced conditions in the ConditionTable
		if (this.numConditions > 0 && rowCount > 0 && colCount > 0) {
			// If the first row has white spaces the file will be invalid
			for (int col = 0; col < this.numConditions; col++) {
				try {
					cellValue = getValueAt(0, col).toString();
					if (cellValue.length() <= 0)
						valid = false;
				} catch (Exception e) {
					valid = false;
				}
			}

			try {
				if (valid) {
					if (clean) {
						// This method delete leftovers rows and columns
						cleanTable();
					} else {
						this.lastRow = rowCount;
						this.lastCol = colCount;
					}

					int col = 0;
					int indiceArray = 1;
					// Variable to know how many positions we need to see until
					// find a value in each column
					int posToGo;
					// Variable to know the total positions we have to see in
					// each column
					int posTotal = 1;

					// Calculate the number of positions to move
					for (int number : condValues)
						posTotal *= number;

					// If there aren't a 0 present in the Condition values
					if (posTotal > 0) {
						// The product of the total positions to go
						// must be equal to the number of rows
						// table
						if (posTotal == lastRow) {
							// If there is only a condition and a row in the
							// table
							if (posTotal == 1 && getRowCount() == 1) {
								// We move one at one
								posToGo = 1;

								while (valid && col < this.numConditions) {
									valid = goOverCondTable(valid, posToGo,
											posTotal, col, condNames);
									col++;
								}

							}
							// Other cases...
							else if (posTotal > 1 && getRowCount() >= 1) {
								while (col < this.numConditions) {
									if (indiceArray >= this.numConditions)
										posToGo = 1;
									else
										posToGo = condValues.get(indiceArray);

									// We calculate the separation of the values
									// between the brothers in the tree
									for (int i = indiceArray + 1; i < numConditions; i++) {
										posToGo = posToGo * condValues.get(i);
									}

									valid = goOverCondTable(valid, posToGo,
											posTotal, col, condNames);
									col++;
									indiceArray++;
								}
							} else {
								this.errors = I18n.get("sumValues");
								// ShowDialog.showError(
								// I18n.get("sumValuesTitle"),
								// I18n.get("sumValues"));
								valid = false;
							}
						} else {
							this.errors = "The total number of condition values: "
									+ posTotal
									+ " isn't the same than the total number of rows in the table: "
									+ lastRow
									+ ". Revise number of conditions values or generate the data table again.";
							// ShowDialog.showError(I18n.get("childValuesTitle"),
							// I18n.get("childValues"));
							valid = false;
						}

					} else {
						valid = false;
						this.errors = "There is a 0 value in the conditions table. Condition values must be greater than 0.";
						// ShowDialog.showError(I18n.get("child0sTitle"),
						// I18n.get("child0s"));
					}

					// Validate the measurements part
					if (!measurementsValidation()) {
						valid = false;
					}
				} else {
					valid = false;
					this.errors = "The first row is invalid, revise it.";
					// ShowDialog.showError(I18n.get("incorrectValuesTitle"),
					// I18n.get("checkFileStructure"));
				}
			} catch (IndexOutOfBoundsException e) {
				valid = false;
				this.errors = "Data table is empty. Please, fill the cells with some data.";
				// ShowDialog.showError(I18n.get("incorrectValuesTitle"),
				// I18n.get("incorrectValues"));
			}
		} else {
			valid = false;
			this.errors = "Data table is empty. Please, fill the cells with some data.";
			// ShowDialog.showError(I18n.get("incorrectValuesTitle"),
			// I18n.get("incorrectValues"));
		}

		return valid;
	}

	/**
	 * Validate the data table content. If the validation find a strange value
	 * in one cell, the value will be added to the condition values files so the
	 * user can use it later
	 * 
	 * @param valid
	 *            Variable to indicate if the data is valid.
	 * @param end
	 *            Variable to indicate the start position.
	 * @param totalPos
	 *            Variable to know how many cell we have to see to discover a
	 *            new value.
	 * @param col
	 *            This is a column of the table.
	 * @return
	 */
	private boolean goOverCondTable(boolean valid, int end, int totalPos,
			int col, List<String> condNames) {
		int acumulado = 0;
		int row = 0;
		// Condition values are unique per row
		List<String> repeteadCondValue = new ArrayList<String>();
		// Get Condition for this column
		String condition = condNames.get(col);

		while (row < totalPos) {
			Object value = getValueAt(row, col);
			if (row == acumulado) {
				if (value != null) {
					// Split compound values (_and_))
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
							if (!val.isEmpty()) {
								// Validate if the value exists in the program
								isInFile = FunctionConstants
										.condValueValidation(val, condition,
												isEditing);

								if (isInFile == 0) {
									// System.out.println("=0");
								} else if (isInFile == 1) {
									auxValue = FunctionConstants
											.oppositeValue(auxValue);
								} else if (isInFile == 2) {
									auxValue = FunctionConstants
											.putAsterisks(auxValue);
								} else if (isInFile == 3) {
									// Warning the user that the new value
									// is
									// available
									errors = errors
											.concat("- Value: "
													+ auxValue
													+ ", in row: "
													+ (row + 1)
													+ " and col 'Condition values' for: "
													+ condition
													+ " is invalid. Change it.\n");
									valid = false;
								}
							} else {
								errors += "- For row: "
										+ (row + 1)
										+ " and Col: "
										+ condNames.get(col)
										+ " exists empty value. Please, cover it.\n";
								valid = false;
							}

							// For compound values
							if (!newValue.isEmpty()) {
								newValue += "_and_" + auxValue;
							} else {
								newValue += auxValue;
							}
						}
						repeteadCondValue.add(val);
					}
					// Set the new value in the cell
					setValueAt(newValue, row, col);
				} else {
					errors += "- For row: " + (row + 1) + " and Col: "
							+ condNames.get(col)
							+ " exists an empty value. Please, cover it.\n";
					valid = false;
				}
				acumulado += end;
			}
			// If we find something strange in the middle of the table...
			else if (value != null && value.toString().length() > 0) {
				errors += "- For row: " + (row + 1) + " and Col: "
						+ condNames.get(col) + " exists a strange value: "
						+ value.toString() + ". Please, delete it.\n";
				valid = false;
			}
			// Clear repeated values for each row
			repeteadCondValue.clear();
			row++;
		}

		return valid;
	}

	/**
	 * Method to clean all the leftovers in rows and columns.
	 */
	public void cleanTable() {
		this.lastRow = 0;
		this.lastCol = 0;

		ThreadLastColumn hUltCol = new ThreadLastColumn(this);
		ThreadLastRow hUltFil = new ThreadLastRow(this);

		Thread thread1 = new Thread(hUltCol);
		Thread thread2 = new Thread(hUltFil);

		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();

			// Validate that there is always at least one row and one column
			if (this.lastRow < getRowCount() && getRowCount() != 1) {
				MyTableModel model = (MyTableModel) getModel();

				while (getRowCount() > lastRow && getRowCount() != 1) {
					model.removeRow(lastRow);
				}
			}

			if (lastCol < getColumnCount() && getColumnCount() != 1) {
				MyTableModel model = (MyTableModel) getModel();

				if (lastCol == 0)
					model.setColumnCount(lastCol + 1);
				else
					model.setColumnCount(lastCol);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to validate measurements part in DataTable. This Method introduces
	 * NaN in empty cells and validate the content of the others. If one cell
	 * has forbidden values the method save it with the position to show the
	 * user later.
	 */
	private boolean measurementsValidation() {
		boolean toRet = true;
		MyTableModel dtm = (MyTableModel) getModel();
		String val = "";

		for (int fil = 0; fil < dtm.getRowCount(); fil++) {
			for (int col = numConditions; col < dtm.getColumnCount(); col++) {
				// DataTable always has String values
				val = dtm.getValueAt(fil, col).toString();
				// If empty change for NaN
				if (val.isEmpty()) {
					dtm.setValueAt(Double.NaN, fil, col);
				}
				// If val has forbidden symbols
				else if (!FunctionConstants.simbolValidationInData(val)) {
					this.errors += "- For row: " + fil + " and col: "
							+ getColumnName(col)
							+ ", exists a forbidden value: " + val + ".\n";
					toRet = false;
				}

			}
		}
		this.setModel(dtm);

		return toRet;
	}

	/**
	 * Resizes column widths according to their content.
	 */
	public void resizeColumnWidth() {
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final TableColumnModel columnModel = getColumnModel();
		for (int column = 0; column < getColumnCount(); column++) {
			int width = 50; // Min width
			for (int row = 0; row < getRowCount(); row++) {
				TableCellRenderer renderer = getCellRenderer(row, column);
				Component comp = prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	/**
	 * Set last row with content in the table.
	 * 
	 * @param lastRow
	 */
	public void setLastRow(int lastRow) {
		if (lastRow > this.lastRow)
			this.lastRow = lastRow;
	}

	/**
	 * Set last column with content in the table.
	 * 
	 * @param lastCol
	 */
	public void setLastCol(int lastCol) {
		if (lastCol > this.lastCol)
			this.lastCol = lastCol;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrors() {
		return errors;
	}

	/**
	 * 
	 * @param numConditions
	 */
	public void setNumConditions(int numConditions) {
		this.numConditions = numConditions;
	}

	/**
	 * 
	 * @param isEditing
	 */
	public void setIsEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int row = rowAtPoint(e.getPoint());
		int column = columnAtPoint(e.getPoint());

		Object value = getValueAt(row, column);
		return value == null ? null : value.toString();
	}
}
