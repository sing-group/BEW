package es.uvigo.ei.sing.bew.tables.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Custom table model to have editable and non editable cells.
 * 
 * @author Gael Pérez Rodríguez.
 */
public class MyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Class> mapColumnClass = new HashMap<Integer, Class>();

	private Set<Integer[]> editableIndex = new HashSet<Integer[]>();
	private Set<Integer> editableColumn = new HashSet<Integer>();
	private Set<Integer> editableRow = new HashSet<Integer>();

	/**
	 * Empty constructor
	 */
	public MyTableModel() {
		super();
	}

	/**
	 * 
	 * @param rowCount
	 * @param columnCount
	 */
	public MyTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	/**
	 * 
	 * @param columnNames
	 * @param rowCount
	 */
	public MyTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param data
	 * @param columnNames
	 */
	public MyTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param columnNames
	 * @param rowCount
	 */
	public MyTableModel(Vector<?> columnNames, int rowCount) {
		super(columnNames, rowCount);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param data
	 * @param columnNames
	 */
	public MyTableModel(Vector<?> data, Vector<?> columnNames) {
		super(data, columnNames);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		for (Integer[] cell : editableIndex) {
			if (cell[0] == rowIndex && cell[1] == columnIndex)
				return true;
		}
		for (Integer column : editableColumn) {
			if (column == columnIndex)
				return true;
		}
		for (Integer row : editableRow) {
			if (row == rowIndex)
				return true;
		}
		return false;
	}

	/**
	 * Method to check if the input index is editable.
	 * 
	 * @param columnIndex
	 * @return True if the index is editable.
	 */
	public boolean isColumnEditable(int columnIndex) {
		for (Integer column : editableColumn) {
			if (column == columnIndex)
				return true;
		}

		return false;
	}

	/**
	 * Add new editable column index.
	 * 
	 * @param columnIndex
	 */
	public void setColumnIndex(int columnIndex) {
		this.editableColumn.add(columnIndex);
	}

	/**
	 * Add new editable row index.
	 * 
	 * @param rowIndex
	 */
	public void addRowIndex(int rowIndex) {
		this.editableRow.add(rowIndex);
	}

	/**
	 * 
	 * @return
	 */
	public Set<Integer> getEditableRow() {
		return editableRow;
	}

	/**
	 * 
	 */
	public void setEditableRow(Set<Integer> editableRow) {
		this.editableRow = editableRow;
	}

	/**
	 * Add new editable cell index.
	 * 
	 * @param rowIndex
	 *            Row index.
	 * @param columnIndex
	 *            Column index.
	 */
	public void setCellIndex(int rowIndex, int columnIndex) {
		this.editableIndex.add(new Integer[] { rowIndex, columnIndex });
	}

	/**
	 * Remove cell index.
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 */
	public void removeCellIndex(int rowIndex, int columnIndex) {
		Iterator<Integer[]> ite = this.editableIndex.iterator();

		Integer[] num;
		while (ite.hasNext()) {
			num = ite.next();
			if (num[0] == rowIndex && num[1] == columnIndex) {
				this.editableIndex.remove(num);
				break;
			}
		}
	}

	/**
	 * Reduce 1 position by rows to the editable indexes that are greater than
	 * input integer.
	 * 
	 * @param deleteRow
	 */
	public void refreshCellIndexes(Integer deleteRow) {
		Set<Integer[]> copyEditableCells = new HashSet<Integer[]>();
		copyEditableCells.addAll(editableIndex);

		Iterator<Integer[]> ite = copyEditableCells.iterator();
		Integer[] num;
		try {
			while (ite.hasNext()) {
				num = ite.next();
				// Only remove
				if (num[0] == deleteRow) {
					editableIndex.remove(num);
				} else if (num[0] > deleteRow) {
					// Remove from the index
					editableIndex.remove(num);
					// Insert again with same values minus 1 for the rows (user
					// deleted one)
					editableIndex.add(new Integer[] { num[0] - 1, num[1] });
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
		}
		copyEditableCells.clear();
	}

	/**
	 * Remove all cell indexes.
	 */
	public void removeAllCellIndex() {
		this.editableIndex.clear();
	}

	/**
	 * Remove the input column index.
	 * 
	 * @param columnIndex
	 */
	public void removeColumnIndex(int columnIndex) {
		this.editableColumn.remove(columnIndex);
	}

	/**
	 * Remove all column indexes.
	 */
	public void removeAllColumnIndex() {
		this.editableColumn.clear();
	}

	/**
	 * Remove the input row index.
	 * 
	 * @param rowIndex
	 */
	public void removeRowIndex(int rowIndex) {
		this.editableRow.remove(rowIndex);
	}

	/**
	 * Remove all row indexes.
	 */
	public void removeAllRowIndex() {
		this.editableRow.clear();
	}

	/**
	 * Get editable indexes.
	 * 
	 * @return
	 */
	public Set<Integer[]> getEditableIndex() {
		return editableIndex;
	}

	/**
	 * Set editable indexes.
	 * 
	 * @param editableIndex
	 */
	public void setEditableIndex(Set<Integer[]> editableIndex) {
		this.editableIndex = editableIndex;
	}

	/**
	 * Get editable columns.
	 * 
	 * @return
	 */
	public Set<Integer> getEditableColumn() {
		return editableColumn;
	}

	/**
	 * Set editable columns.
	 * 
	 * @param editableColumn
	 */
	public void setEditableColumn(Set<Integer> editableColumn) {
		this.editableColumn = editableColumn;
	}

	@Override
	public Class getColumnClass(int column) {
		return mapColumnClass.get(column);
	}

	/**
	 * 
	 * @param column
	 * @param colClass
	 */
	public void addColumnClass(int column, Class colClass) {
		this.mapColumnClass.put(column, colClass);
	}

	/**
	 * 
	 * @param column
	 */
	public void deleteColumnClass(int column) {
		Class auxValue;
		// We have to make a copy of the keySet
		Set<Integer> setAux = new HashSet<Integer>();

		// Go over all the maps to reduce the index number
		setAux.addAll(mapColumnClass.keySet());
		for (Integer key : setAux) {
			if (key > column) {
				auxValue = mapColumnClass.get(key);
				mapColumnClass.put(key - 1, auxValue);

				mapColumnClass.remove(key);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, Class> getMapColumnClass() {
		return mapColumnClass;
	}

	/**
	 * 
	 * @param mapColumnClass
	 */
	public void setMapColumnClass(Map<Integer, Class> mapColumnClass) {
		this.mapColumnClass = mapColumnClass;
	}

}
