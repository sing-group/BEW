package es.uvigo.ei.sing.bew.tables.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import es.uvigo.ei.sing.bew.tables.models.MyTableModel;

/**
 * Custom table renderer to paint the cells with different colors.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class MyTableCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	private List<Integer> addedColumns;

	/**
	 * Default constructor.
	 */
	public MyTableCellRender() {
		super();
		addedColumns = new ArrayList<Integer>();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		MyTableModel mtm = (MyTableModel) table.getModel();

		Component cell = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (mtm.isCellEditable(row, column)) {
			cell.setForeground(Color.BLACK); // Editable cells
			cell.setBackground(new Color(145, 213, 164)); // Green
		} else {
			cell.setForeground(Color.BLACK); // Non editable cells
			cell.setBackground(new Color(216, 216, 216)); // Grey
		}
		// New columns in table
		if (addedColumns.contains(column)) {
			if (row % 2 != 0) {
				cell.setForeground(Color.BLACK);
				cell.setBackground(new Color(240, 234, 228));
			} else {
				cell.setForeground(Color.BLACK);
				cell.setBackground(Color.WHITE);
			}
		}
		if (table.isCellSelected(row, column)) { // When cell is selected
			cell.setForeground(Color.BLACK);
			cell.setBackground(new Color(204, 255, 255)); // Cyan
		}
		
		return cell;
	}

	/**
	 * Add a new column to paint all the cells.
	 * 
	 * @param column
	 *            Column index.
	 */
	public void addColumn(Integer column) {
		this.addedColumns.add(column);
	}

	/**
	 * Delete a column.
	 * 
	 * @param col
	 *            Column index.
	 */
	public void deleteCol(Integer col) {
		this.addedColumns.remove(col);
	}

	/**
	 * Delete all the columns.
	 */
	public void deleteAllCol() {
		this.addedColumns.clear();
	}
}
