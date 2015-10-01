package es.uvigo.ei.sing.bew.sheets;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import jxl.Sheet;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.MyTableCellRender;

/**
 * This class shows the form to import a method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class ImportMethodSheetConfigurator extends
		AbstractMethodSheetConfigurator implements IWizardStep {

	private static final long serialVersionUID = 1L;

	private Sheet sheet;
	private Object[] realCondValues;

	private JButton newDataRow;
	private JButton newDataCol;
	private JButton deleteDataRow;
	private JButton deleteDataCol;
	private JButton btnCheckCond;
	private JButton btnLock;
	private JButton btnDelAll;

	/**
	 * Constructor to load a sheet. (Used by FileToData when load a XLS).
	 * 
	 * @param sheet
	 *            Sheet to load.
	 */
	public ImportMethodSheetConfigurator(Sheet sheet) {
		super();

		this.sheet = sheet;
		this.sheetName = sheet.getName();

		// Take MethodUnits from [0 row,1 col]
		try {
			this.sheetUnits = FunctionConstants.replaceCommas(sheet.getCell(1,
					0).getContents());
		} catch (ArrayIndexOutOfBoundsException e) {

		}

		super.dataTable.initTable(this.sheet);

		init(false);

		// Fill Conditions table
		completeConditionTable();
	}

	/**
	 * Constructor for adding and editing a Method to the Experiment.
	 * 
	 * @param name
	 *            Sheet name.
	 * @param units
	 *            Sheet units.
	 * @param data
	 *            Data matrix.
	 * @param condNames
	 *            Condition names.
	 * @param condValues
	 *            Condition Values.
	 * @param condUnits
	 *            Condition units.
	 * @param numCond
	 *            Number of conditions.
	 */
	public ImportMethodSheetConfigurator(String name, String units,
			Object[][] data, List<String> condNames, List<Integer> condValues,
			List<String> condUnits, Integer numCond) {
		super(condNames, condValues, condUnits, numCond);

		this.sheet = null;
		this.sheetName = name;
		this.sheetUnits = units;

		super.dataTable.initTableWithHeaders(data, condNames);
		// Set IsEditing to true
		super.dataTable.setIsEditing(true);

		realCondValues = calculateRealConditions(condValues).toArray();

		super.tableConditions.loadDataFromMatrix(FunctionConstants
				.arrayToMatrix(condNames.toArray(), realCondValues,
						condUnits.toArray()));

		init(true);
	}

	/**
	 * Method to initialize the dialog.
	 */
	public void init(boolean isEditing) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		add(splitPane);

		JPanel splitPane1 = new JPanel();
		splitPane1
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		splitPane.setLeftComponent(splitPane1);
		splitPane1.setLayout(new BorderLayout(0, 0));

		JPanel panelName = new JPanel();
		panelName.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18n.get("methodName"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane1.add(panelName, BorderLayout.NORTH);
		GridBagLayout gblPanelName = new GridBagLayout();
		gblPanelName.columnWidths = new int[] { 400, 100 };
		gblPanelName.rowHeights = new int[] { 40 };
		gblPanelName.columnWeights = new double[] { 0.0, 1.0 };
		gblPanelName.rowWeights = new double[] { 0.0 };
		panelName.setLayout(gblPanelName);

		GridBagConstraints gbcComboBox = new GridBagConstraints();
		gbcComboBox.insets = new Insets(0, 0, 0, 5);
		gbcComboBox.anchor = GridBagConstraints.EAST;
		gbcComboBox.gridx = 0;
		gbcComboBox.gridy = 0;
		panelName.add(textComboName, gbcComboBox);
		textComboName.addItem(sheetName);
		textComboName.setSelectedItem(this.sheetName);

		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 0, 10);
		gbcTextField.fill = GridBagConstraints.CENTER;
		gbcTextField.ipadx = 50;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 0;
		panelName.add(unitsField, gbcTextField);
		unitsField.setColumns(10);
		unitsField.setText(sheetUnits);

		JPanel panelTable = new JPanel();
		panelTable.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18n.get("data"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane1.add(panelTable);
		panelTable.setLayout(new BorderLayout(0, 0));

		JPanel tableButtons = new JPanel();
		panelTable.add(tableButtons, BorderLayout.EAST);
		tableButtons.setLayout(new GridLayout(0, 1, 0, 0));
		{
			// Button to add a new row in the dataTable
			newDataRow = new JButton(I18n.get("newCondSet"));
			newDataRow.setToolTipText("Create a new row in the table.");
			newDataRow.setIcon(new ImageIcon(
					ImportMethodSheetConfigurator.class
							.getResource("/img/addRow.png")));
			newDataRow.setEnabled(false);
			newDataRow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataTable.insertRow();
				}
			});

			tableButtons.add(newDataRow);
		}
		{
			// Button to add a new column in the dataTable
			newDataCol = new JButton(I18n.get("newDataCol"));
			newDataCol.setToolTipText("Create a new column in the table.");
			newDataCol.setIcon(new ImageIcon(
					ImportMethodSheetConfigurator.class
							.getResource("/img/addColumn.png")));
			newDataCol.setEnabled(false);
			newDataCol.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataTable.newColumn();
				}
			});

			tableButtons.add(newDataCol);
		}
		{
			// Button to delete the selected row in the dataTable
			deleteDataRow = new JButton(I18n.get("deleteCondSet"));
			deleteDataRow
					.setToolTipText("Delete the selected rows in the table.");
			deleteDataRow.setIcon(new ImageIcon(
					ImportMethodSheetConfigurator.class
							.getResource("/img/deleteRow.png")));
			deleteDataRow.setEnabled(false);
			deleteDataRow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataTable.deleteRow();
				}
			});

			tableButtons.add(deleteDataRow);
		}
		{
			// Button to delete the selected column in the dataTable
			deleteDataCol = new JButton(I18n.get("deleteDataCol"));
			deleteDataCol
					.setToolTipText("Delete the selected columns in the table.");
			deleteDataCol.setIcon(new ImageIcon(
					ImportMethodSheetConfigurator.class
							.getResource("/img/deleteColumn.png")));
			deleteDataCol.setEnabled(false);
			deleteDataCol.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataTable.deleteColumn();
				}
			});

			tableButtons.add(deleteDataCol);
		}
		JScrollPane scrollData = new JScrollPane(dataTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Rows header
		JTable rowTable = new RowNumberTable(dataTable);

		scrollData.setRowHeaderView(rowTable);
		scrollData.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelTable.add(scrollData);

		JPanel splitPane2 = new JPanel();
		splitPane2.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18n.get("conditions"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(splitPane2);
		splitPane2.setLayout(new BorderLayout(0, 0));

		JPanel panelConditions = new JPanel();
		splitPane2.add(panelConditions);
		panelConditions.setLayout(new BorderLayout(0, 0));

		JPanel conditionsButtons = new JPanel();
		panelConditions.add(conditionsButtons, BorderLayout.NORTH);
		GridBagLayout gblCondButtons = new GridBagLayout();
		gblCondButtons.columnWidths = new int[] { 87, 0, 87, 0 };
		gblCondButtons.rowHeights = new int[] { 23, 0 };
		gblCondButtons.columnWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gblCondButtons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		conditionsButtons.setLayout(gblCondButtons);

		{
			// Button to select the real Conditions in the DataTable
			btnLock = new JButton("1. Name Conditions");
			btnLock.setToolTipText("Select the conditions in the data table.");
			if (isEditing) {
				btnLock.setEnabled(false);
			}
			GridBagConstraints gbcBtnLock = new GridBagConstraints();
			gbcBtnLock.fill = GridBagConstraints.BOTH;
			gbcBtnLock.insets = new Insets(0, 0, 0, 5);
			gbcBtnLock.gridx = 0;
			gbcBtnLock.gridy = 0;

			// Listener to select which cells are Conditions.
			btnLock.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tableConditions.cancelEditing();
					dataTable.cancelEditing();

					if (lockDatos()) {
						btnLock.setEnabled(false);
						btnCheckCond.setEnabled(true);
						btnDelAll.setEnabled(true);
					} else {
						ShowDialog.showError(
								I18n.get("noColumnsSelectedTitle"),
								I18n.get("noColumnsSelected"));
					}
				}
			});

			conditionsButtons.add(btnLock, gbcBtnLock);
		}
		{
			btnCheckCond = new JButton("2. Check Conditions");
			btnCheckCond
					.setToolTipText("Enable the data table to edit data. Only if the conditions are locked.");
			if (!isEditing)
				btnCheckCond.setEnabled(false);
			GridBagConstraints gbcBtnCheck = new GridBagConstraints();
			gbcBtnCheck.insets = new Insets(0, 0, 0, 5);
			gbcBtnCheck.gridx = 1;
			gbcBtnCheck.gridy = 0;

			// Listener to do the cells in the table editable
			btnCheckCond.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tableConditions.cancelEditing();
					dataTable.cancelEditing();
					// If user push NameConds button, this variable will be
					// created
					if (realCondValues != null) {
						// Validate table content
						if (tableConditions.validateTableContent()) {
							// Set numConditions
							numConditions = tableConditions.getRowCount();
							dataTable.setNumConditions(numConditions);
							// Enable DataTable buttons
							changeDataButtonState(true);
							// Save condition values
							realCondValues = tableConditions
									.getNumberOfCondValues().toArray();

							// Set condition columns editable
							MyTableModel mtm = (MyTableModel) dataTable
									.getModel();
							MyTableCellRender render = (MyTableCellRender) dataTable
									.getDefaultRenderer(Object.class);
							Object[] columnNames = new Object[mtm
									.getColumnCount()];
							// Get conditions name
							for (int condRow = 0; condRow < tableConditions
									.getRowCount(); condRow++)
								columnNames[condRow] = tableConditions
										.getValueAt(condRow, 0);

							int index = 1;
							for (int col = 0; col < mtm.getColumnCount(); col++) {
								// Set columns editable
								mtm.setColumnIndex(col);

								// Set data columns in the renderer
								if (col >= numConditions) {
									render.addColumn(col);
									columnNames[col] = "Replicate " + index;
									index++;
								}
							}
							// Set new column names
							mtm.setColumnIdentifiers(columnNames);

							btnLock.setEnabled(false);
							btnCheckCond.setEnabled(false);
							btnDelAll.setEnabled(true);

							repaint();
						} else {
							// Show getErrors
							ShowDialog.showError("Errors in Condition table!",
									tableConditions.getErrors());
						}
					} else {
						// Press button 1 first
						ShowDialog
								.showError(
										"Errors in Condition table!",
										"Please, name the conditions first in the data table and press Name Conditions button.");
					}
				}
			});

			conditionsButtons.add(btnCheckCond, gbcBtnCheck);
		}
		{
			// Button to delete the selected row in the conditionsTable
			btnDelAll = new JButton("Delete all Conditions");
			btnDelAll
					.setToolTipText("Delete all locked conditions in this table.");
			// btnDelAll.setEnabled(false);
			GridBagConstraints gbcDelFilCond = new GridBagConstraints();
			gbcDelFilCond.fill = GridBagConstraints.BOTH;
			gbcDelFilCond.gridx = 2;
			gbcDelFilCond.gridy = 0;

			// Listener to delete all Conditions created in the table
			btnDelAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tableConditions.cancelEditing();
					dataTable.cancelEditing();

					MyTableModel model = tableConditions.getModel();

					// Delete all rows
					int rows = model.getRowCount();
					for (int i = rows - 1; i >= 0; i--) {
						model.removeRow(i);
					}

					deleteDataTable();
					btnLock.setEnabled(true);
					changeDataButtonState(false);
					// btnDelAll.setEnabled(false);
					btnCheckCond.setEnabled(false);
				}
			});

			conditionsButtons.add(btnDelAll, gbcDelFilCond);
		}

		JPanel conditionsTable = new JPanel();
		panelConditions.add(conditionsTable, BorderLayout.CENTER);

		conditionsTable.setLayout(new GridLayout(0, 1, 0, 0));
		JScrollPane scrollConditions = new JScrollPane(tableConditions);
		// Rows header
		rowTable = new RowNumberTable(tableConditions);

		scrollConditions.setRowHeaderView(rowTable);
		scrollConditions.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		conditionsTable.add(scrollConditions);

		// Listener to copy values in the correct positions of the table tree
		final MyTableModel mtm = (MyTableModel) super.dataTable.getModel();
		mtm.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int selectedColumn = dataTable.getSelectedColumn();
				int selectedRow = dataTable.getSelectedRow();
				try {
					if (selectedColumn > 0 && selectedColumn < numConditions
							&& dataTable.getRowCount() > 0
							&& dataTable.getColumnCount() > 0) {
						int nextCell;
						int firstRow;
						Object valueToCopy = dataTable.getValueAt(selectedRow,
								selectedColumn);

						nextCell = nextCell(selectedColumn);
						firstRow = findFirstRow(selectedRow, nextCell);

						for (int row = firstRow; row < dataTable.getRowCount(); row = row
								+ nextCell) {
							Object auxValue = dataTable.getValueAt(row,
									selectedColumn);
							if (row != selectedRow && auxValue != valueToCopy) {
								dataTable.getModel().setValueAt(valueToCopy,
										row, selectedColumn);
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException e1) {
					// e1.printStackTrace();
				}
			}

			/**
			 * Method to calculate how many positions the table need to go over
			 * until copy the value.
			 * 
			 * @param selectedColumn
			 *            Selected column of the table.
			 * @return Int with the positions to go over.
			 */
			private int nextCell(int selectedColumn) {
				int ret = 1;
				for (int i = selectedColumn; i < realCondValues.length; i++) {
					ret = ret * (Integer) realCondValues[i];
				}

				return ret;
			}

			/**
			 * Method to find the first row in the table to start going over the
			 * rests of positions.
			 * 
			 * @param selectedRow
			 *            Selected row in the table.
			 * @param nextCell
			 *            Value with the positions to go over to copy the value.
			 * @return Int with the row position of the starting row in the
			 *         table.
			 */
			private int findFirstRow(int selectedRow, int nextCell) {
				int ret = selectedRow;

				for (int row = selectedRow; row >= 0; row = row - nextCell) {
					try {
						ret = row;
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}

				return ret;
			}
		});

		// Listener to change dataTable header value when the user change the
		// condition in ConditionTable
		MyTableModel mtmCond = super.tableConditions.getModel();
		mtmCond.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int changedCol = e.getColumn();
				int changedRow = e.getFirstRow();

				// One column per time and only the first
				if (changedCol != TableModelEvent.ALL_COLUMNS
						&& changedCol == 0) {
					// Purge rowDialog in dataTable to refresh the dialogs
					// inside
					dataTable.purgeRowDialog();
				}
				// Refresh the conditionValues to validate the DataTable
				// structure
				else if (changedCol != TableModelEvent.ALL_COLUMNS
						&& changedCol == 1) {
					Object changedValue = tableConditions.getValueAt(
							changedRow, changedCol);

					realCondValues[changedRow] = (Integer) changedValue;
				}
			}
		});
	}

	/**
	 * Changes the buttons setEnable to the input value.
	 * 
	 * @param state
	 *            New state of the buttons.
	 */
	private void changeDataButtonState(boolean state) {
		newDataRow.setEnabled(state);
		newDataCol.setEnabled(state);
		deleteDataRow.setEnabled(state);
		deleteDataCol.setEnabled(state);
	}

	/**
	 * Deletes the data of the data table.
	 */
	private void deleteDataTable() {
		// Set dataTable buttons to false
		changeDataButtonState(false);
		// Purge variables
		realCondValues = null;
		// Set condition columns no editable
		MyTableModel mtm = (MyTableModel) dataTable.getModel();
		for (int col = 0; col < mtm.getColumnCount(); col++) {
			mtm.removeColumnIndex(col);
		}
		// Set data columns no editable
		MyTableCellRender render = (MyTableCellRender) dataTable
				.getDefaultRenderer(Object.class);
		render.deleteAllCol();
		repaint();

		// More purge
		numConditions = 0;
		dataTable.setNumConditions(numConditions);
	}

	/**
	 * This method calculates the condition and condition values that exist in
	 * the XLS file and in the data table. If the structure of the file is
	 * correct, the load will be automatically. Otherwise, the user must
	 * introduce the conditions and number of conditions by hand (like before).
	 */
	private void completeConditionTable() {
		try {
			int numberCond = Integer
					.parseInt(sheet.getCell(1, 1).getContents());

			if (numberCond > 0) {
				Object[][] conditionData = new Object[numberCond][3];

				String name = "";
				List<String> currentConds = FunctionConstants
						.loadConditionsFromFile();
				for (int row = 0; row < numberCond; row++) {
					name = dataTable.getColumnName(row);
					if (!name.matches("[0-9]") && currentConds.contains(name)) {
						conditionData[row][0] = name;
					} else {
						conditionData[row][0] = null;
					}
					conditionData[row][2] = "...";
				}

				int count = 0;
				int last = 1;
				String value;
				for (int col = 0; col < numberCond; col++) {
					for (int row = 0; row < dataTable.getRowCount(); row++) {
						value = dataTable.getValueAt(row, col).toString();
						if (!value.isEmpty()) {
							count++;
						}
					}
					conditionData[col][1] = count / last;

					// Saver number of conditions for each Condition
					condValues.add(count / last);

					last = count;
					count = 0;
				}

				tableConditions.loadDataFromMatrix(conditionData);

				// Activate buttons
				btnLock.setEnabled(false);
				btnCheckCond.setEnabled(true);
				btnDelAll.setEnabled(true);

				// Save realCondValues for this Sheet
				this.realCondValues = calculateRealConditions(condValues)
						.toArray();
			} else {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			ShowDialog.showError(
					I18n.get("importStructureTitle"),
					I18n.get("importStructure1") + sheetName
							+ I18n.get("importStructure2"));
		}
	}

	/**
	 * Method to lock the selected columns in the DataTable. The selected
	 * columns will be the real conditions of this sheet.
	 */
	private boolean lockDatos() {
		int[] colSeleccionadas = this.dataTable.getSelectedColumns();
		int resp = 0;

		// If something selected
		if (colSeleccionadas.length != 0) {
			// If the user select something before
			if (this.tableConditions.getRowCount() != 0) {
				// Ask to overwrite
				resp = JOptionPane
						.showConfirmDialog(this, I18n.get("rowsCreated"),
								I18n.get("overwrite"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
			}

			// If the user want to overwrite or the table is empty
			if (resp == 0 || this.tableConditions.getRowCount() == 0) {
				// Purge dataTable values
				deleteDataTable();
				realCondValues = new Integer[colSeleccionadas.length];

				MyTableModel mtm = this.tableConditions.getModel();

				// Delete all the content
				while (mtm.getRowCount() > 0) {
					mtm.removeRow(mtm.getRowCount() - 1);
				}

				// We put default text in the first column
				for (int i = 0; i < colSeleccionadas.length; i++) {
					mtm.addRow(new Object[] { null, "",
							BewConstants.DEFAULT_VALUE });
					numConditions++;
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Method to divide the condition values in order to get the real conditions
	 * for the tree structure.
	 * 
	 * @param condValues
	 *            Condition values.
	 * 
	 * @return List<Integer> with the real number of condition values for each
	 *         condition.
	 */
	private List<Integer> calculateRealConditions(List<Integer> condValues) {
		List<Integer> ret = new ArrayList<Integer>();

		int aux = 1;
		for (Integer number : condValues) {
			ret.add(number / aux);
			aux = number;
		}

		return ret;
	}

	@Override
	public String getSheetName() {
		// return super.textComboName.getSelectedItem().toString().trim();
		return this.sheetName;
	}

	/**
	 * Set sheet name.
	 * 
	 * @param sheetName
	 */
	@Override
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public String[] getExpSetup() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method validate the structure of the data in the DataTable. It need
	 * read the data that the user introduced in the ConditionsTable first.
	 */
	@Override
	public boolean validateStructure() {
		dataTable.setNumConditions(numConditions);
		return dataTable.validateStructure(condValues, condNames, true);
	}
}
