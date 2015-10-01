package es.uvigo.ei.sing.bew.tables.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Custom cell renderer for custom tables. Used to put the comboBoxes.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CellComboRenderer extends JComboBox implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param items
	 */
	public CellComboRenderer(Object[] items) {
		super(items);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// Red is nothing is selected
		if (value == null || value.toString().isEmpty()) {
			setBackground(Color.red.darker());
		} else if (!isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		setSelectedItem(value);

		return this;
	}
}
