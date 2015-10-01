package es.uvigo.ei.sing.bew.sheets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;

/**
 * This class create a DataSheet to let the user introduce the data for each
 * method.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CreateMethodSheetConfigurator extends
		AbstractMethodSheetConfigurator implements IWizardStep {

	private static final long serialVersionUID = 1L;

	private JSpinner spinner;
	private JButton newCol;
	private JButton deleteCol;

	/**
	 * Empty constructor with no parameters.
	 */
	public CreateMethodSheetConfigurator() {
		super();

		initialize();
	}

	/**
	 * This method initiate the Java dialog.
	 */
	public void initialize() {
		// Create buttons
		newCol = new JButton("New replicate");
		newCol.setToolTipText("Create a new replicate for the method.");
		newCol.setIcon(new ImageIcon(CreateMethodSheetConfigurator.class
				.getResource("/img/addColumn.png")));
		deleteCol = new JButton("Delete replicate");
		deleteCol.setToolTipText("Delete the selected replicates.");
		deleteCol.setIcon(new ImageIcon(CreateMethodSheetConfigurator.class
				.getResource("/img/deleteColumn.png")));

		// Create the listener to change the table when the user modify data
		MyTableModel mtm = (MyTableModel) dataTable.getModel();
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
				for (int i = selectedColumn; i < condValues.size(); i++) {
					ret = ret * condValues.get(i);
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
		}); // End listener

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(250);
		add(splitPane);

		JPanel splitPane1 = new JPanel();
		splitPane.setLeftComponent(splitPane1);
		splitPane1.setLayout(new BorderLayout(0, 10));

		JPanel panelName = new JPanel();
		panelName.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Method of analysis",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
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
		panelName.add(super.textComboName, gbcComboBox);

		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 0, 10);
		gbcTextField.fill = GridBagConstraints.CENTER;
		gbcTextField.ipadx = 50;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 0;
		panelName.add(unitsField, gbcTextField);
		unitsField.setColumns(10);

		JPanel panelConditions = new JPanel();
		panelConditions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Conditions analyzed by this method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane1.add(panelConditions, BorderLayout.CENTER);
		panelConditions.setLayout(new BorderLayout(0, 0));

		JPanel conditionsButtons = new JPanel();
		panelConditions.add(conditionsButtons, BorderLayout.NORTH);
		GridBagLayout gblCondButtons = new GridBagLayout();
		gblCondButtons.columnWidths = new int[] { 72, 72, 72, 72, 72, 0 };
		gblCondButtons.rowHeights = new int[] { 23, 0 };
		gblCondButtons.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gblCondButtons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		conditionsButtons.setLayout(gblCondButtons);

		// Button to add as rows as the value of the spinner
		JButton insFilCond = new JButton("1. Add condition");
		insFilCond.setToolTipText("Create the conditions.");
		insFilCond.setIcon(new ImageIcon(CreateMethodSheetConfigurator.class
				.getResource("/img/addRow.png")));
		insFilCond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newRowButton();
			}
		});

		JLabel lblNumber = new JLabel(I18n.get("condsNumber"));
		lblNumber.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbcLblNumber = new GridBagConstraints();
		gbcLblNumber.fill = GridBagConstraints.BOTH;
		gbcLblNumber.insets = new Insets(0, 0, 0, 5);
		gbcLblNumber.gridx = 0;
		gbcLblNumber.gridy = 0;
		conditionsButtons.add(lblNumber, gbcLblNumber);

		spinner = new JSpinner();
		spinner.setToolTipText("Introduce the number of conditions to create for this method.");
		spinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer
				.valueOf(0), Integer.valueOf(10000), Integer.valueOf(1)));
		GridBagConstraints gbcSpinner = new GridBagConstraints();
		gbcSpinner.fill = GridBagConstraints.BOTH;
		gbcSpinner.insets = new Insets(0, 0, 0, 5);
		gbcSpinner.gridx = 1;
		gbcSpinner.gridy = 0;
		conditionsButtons.add(spinner, gbcSpinner);
		GridBagConstraints gbcInsFilCond = new GridBagConstraints();
		gbcInsFilCond.fill = GridBagConstraints.BOTH;
		gbcInsFilCond.insets = new Insets(0, 0, 0, 5);
		gbcInsFilCond.gridx = 2;
		gbcInsFilCond.gridy = 0;
		conditionsButtons.add(insFilCond, gbcInsFilCond);

		// Button to create the DataTable taking the different values of the
		// ConditionsTable
		JButton btnOk = new JButton("2. Create data table");
		btnOk.setToolTipText("Once the conditions are created, this button creates the data table.");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableConditions.cancelEditing();
				checkButton();
			}
		});

		// Button to delete the selected row in the ConditionsTable
		JButton delFilCond = new JButton("Delete condition");
		delFilCond.setToolTipText("Delete the selected condition.");
		delFilCond.setIcon(new ImageIcon(CreateMethodSheetConfigurator.class
				.getResource("/img/deleteRow.png")));
		delFilCond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = tableConditions.getSelectedRows();
				if (rows.length > 0)
					deleteRowButton(rows);
			}
		});

		GridBagConstraints gbcDelFilCond = new GridBagConstraints();
		gbcDelFilCond.fill = GridBagConstraints.BOTH;
		gbcDelFilCond.insets = new Insets(0, 0, 0, 5);
		gbcDelFilCond.gridx = 3;
		gbcDelFilCond.gridy = 0;
		conditionsButtons.add(delFilCond, gbcDelFilCond);
		GridBagConstraints gbcBtnOk = new GridBagConstraints();
		gbcBtnOk.fill = GridBagConstraints.BOTH;
		gbcBtnOk.gridx = 4;
		gbcBtnOk.gridy = 0;
		conditionsButtons.add(btnOk, gbcBtnOk);

		JPanel conditionsTable = new JPanel();
		panelConditions.add(conditionsTable, BorderLayout.CENTER);

		conditionsTable.setLayout(new GridLayout(0, 1, 0, 0));
		JScrollPane scrollConditions = new JScrollPane(tableConditions);
		// Rows header
		JTable rowTable = new RowNumberTable(tableConditions);

		scrollConditions.setRowHeaderView(rowTable);
		scrollConditions.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		conditionsTable.add(scrollConditions);

		JPanel splitPane2 = new JPanel();
		splitPane2.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18n.get("data"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(splitPane2);
		splitPane2.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelTable = new JPanel();
		splitPane2.add(panelTable);
		panelTable.setLayout(new BorderLayout(0, 0));

		JPanel tableButtons = new JPanel();
		panelTable.add(tableButtons, BorderLayout.EAST);
		tableButtons.setLayout(new GridLayout(0, 1, 0, 0));

		// Button to create a new column in the DataTable
		newCol.setEnabled(false);
		newCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataTable.newColumn();
			}
		});
		tableButtons.add(newCol);

		// Button to delete the selected column in the DataTable
		deleteCol.setEnabled(false);
		deleteCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataTable.deleteColumn();
			}
		});
		tableButtons.add(deleteCol);

		JScrollPane scrollData = new JScrollPane(dataTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Rows header
		rowTable = new RowNumberTable(dataTable);

		scrollData.setRowHeaderView(rowTable);
		scrollData.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelTable.add(scrollData);
	}

	/**
	 * This method creates rows and columns in the DataTable from the data that
	 * the user introduced in the Condition Table. If the user has introduced
	 * data before, this method ask him to overwrite.
	 */
	private void checkButton() {
		int resp = 0;

		// Purge variables
		dataTable.clearSelection();
		condNames.clear();
		condValues.clear();

		try {
			if (tableConditions.getRowCount() != 0) {
				if (tableConditions.validateTableContent()) {
					tableConditions.cancelEditing();

					// If we find a 0 or ConditionsTable doesn't have any rows,
					// the
					// user can't continue
					for (int fil = 0; fil < tableConditions.getRowCount(); fil++) {
						try {
							if ((Integer) tableConditions.getValueAt(fil, 1) == 0)
								resp = 1;
						} catch (NullPointerException e) {
							resp = 1;
						}
					}

					if (resp == 0) {
						// There are created data in the DataTable...
						if (dataTable.getRowCount() != 0) {
							// We ask for overwrite
							resp = JOptionPane.showConfirmDialog(this,
									I18n.get("rowsCreated"),
									I18n.get("overwrite"),
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
						}

						// If the user want to overwrite or DataTable is empty
						if (resp == 0 || this.dataTable.getRowCount() == 0) {
							// We delete all the content of the table (to
							// overwrite)
							dataTable.deleteTableContent();

							MyTableModel mtmCond = tableConditions.getModel();
							condNames.addAll(tableConditions
									.getConditionNames());
							condValues.addAll(tableConditions
									.getNumberOfCondValues());

							dataTable.fillDataTable(tableConditions.getModel()
									.getRowCount(), this.condValues,
									this.condNames);

							// The DataTable was created properly
							if (this.dataTable.getRowCount() > 0
									&& this.dataTable.getColumnCount() > 0) {
								// Activating the buttons of the DataTable
								this.newCol.setEnabled(true);
								this.deleteCol.setEnabled(true);

								if (mtmCond.getRowCount() > 0)
									this.numConditions = mtmCond.getRowCount();
							}
						}
					} else {
						ShowDialog.showError(I18n.get("incorrectNumber"),
								I18n.get("above0"));
						condNames.clear();
						condValues.clear();
					}
				} else {
					ShowDialog.showError("Condition table error!",
							tableConditions.getErrors());
					condNames.clear();
					condValues.clear();
				}

			} else {
				ShowDialog.showError(I18n.get("noRowsTitle"),
						I18n.get("noRows"));
				condNames.clear();
				condValues.clear();
			}
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			ShowDialog.showError(I18n.get("dataTableCreationTitle"),
					I18n.get("dataTableCreation"));
			condNames.clear();
			condValues.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowDialog.showError(I18n.get("reviseCondTableTitle"),
					I18n.get("reviseCondTable"));
			condNames.clear();
			condValues.clear();
		}

	}

	/**
	 * Button method add a new Row in the Conditions Table. It create as many
	 * rows as the spinner says. If the user has introduced data before, this
	 * method ask him to overwrite.
	 */
	private void newRowButton() {
		int resp = 0;

		// Purge variables
		dataTable.clearSelection();
		condNames.clear();
		condValues.clear();

		if (dataTable.getRowCount() != 0) {
			// Ask to overwrite
			resp = JOptionPane.showConfirmDialog(this, I18n.get("rowsCreated"),
					I18n.get("overwrite"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}
		if (resp == 0) {
			dataTable.deleteTableContent();
			newCol.setEnabled(false);
			deleteCol.setEnabled(false);

			int spinnerValue = (int) spinner.getValue();
			for (int i = 0; i < spinnerValue; i++) {
				tableConditions.insertBlankRow();
			}
		}
	}

	/**
	 * Button method delete the selected rows for the user. If the user has
	 * introduced data before, this method ask him to overwrite.
	 * 
	 * @param rows
	 *            Index of the selected rows.
	 */
	private void deleteRowButton(int[] rows) {
		int resp = 0;

		// Purge variables
		dataTable.clearSelection();
		condNames.clear();
		condValues.clear();

		if (dataTable.getRowCount() != 0) {
			// Ask to overwrite
			resp = JOptionPane.showConfirmDialog(this, I18n.get("rowsCreated"),
					I18n.get("overwrite"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}
		if (resp == 0) {
			dataTable.deleteTableContent();
			newCol.setEnabled(false);
			deleteCol.setEnabled(false);

			MyTableModel mtm = tableConditions.getModel();
			for (int i = 0; i < rows.length; i++) {
				mtm.removeRow(rows[i] - i);
			}
		}
	}

	@Override
	public String getSheetName() {
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
		return null;
	}

	@Override
	public boolean validateStructure() {
		return dataTable.validateStructure(condValues, condNames, false);
	}
}
