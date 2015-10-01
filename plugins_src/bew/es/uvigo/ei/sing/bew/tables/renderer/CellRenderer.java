package es.uvigo.ei.sing.bew.tables.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer for custom tables. Paint the cells in different to do a
 * stripe.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (value == null || value.toString().isEmpty()) {
			comp.setBackground(Color.red.darker());
		} else if (!isSelected) {
			if (row % 2 != 0) {
				comp.setForeground(Color.BLACK);
				comp.setBackground(new Color(240, 234, 228));
			} else {
				comp.setForeground(Color.BLACK);
				comp.setBackground(Color.WHITE);
			}
		}
		if (table.isCellSelected(row, column)) { // When cell is selected
			comp.setForeground(Color.BLACK);
			comp.setBackground(new Color(204, 255, 255)); // Cyan
		}

		return comp;
	}
}