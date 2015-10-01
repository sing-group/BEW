package es.uvigo.ei.sing.bew.tables.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import es.uvigo.ei.sing.bew.model.IExperiment;

/**
 * Custom table cell renderer to have pair rows of one color.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class StripeTableCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	// Map with DataSerie row and the associate interExperiment
	private Map<Integer, IExperiment> rowNames = new HashMap<Integer, IExperiment>();
	// Custom colors for each intraExperiment
	private Map<Object, Object> expsColors;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component cell = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (row % 2 != 0) {
			cell.setForeground(Color.BLACK);
			cell.setBackground(new Color(240, 234, 228));
		} else {
			cell.setForeground(Color.BLACK);
			cell.setBackground(Color.WHITE);
		}
		// If the map contains the key
		if (rowNames.get(row) != null) {
			// We take the value for that key
			IExperiment exp = rowNames.get(row);

			// Put the custom color for the value
			cell.setForeground(Color.BLACK);
			cell.setBackground((Color) expsColors.get(exp));
		}
		if (table.isCellSelected(row, column)) { // When cell is selected
			cell.setForeground(Color.BLACK);
			cell.setBackground(new Color(204, 255, 255)); // Cyan
		}

		return cell;
	}

	/**
	 * Method to add an InterExperiment name to the list and associate the
	 * number of the DataSerie with the name of the interExperiment.
	 * 
	 * @param row
	 * @param exp
	 */
	public void addInterExp(int row, IExperiment exp) {
		rowNames.put(row, exp);
	}

	/**
	 * 
	 * @param intraColors
	 */
	public void setIntraExperimentColors(Map<Object, Object> intraColors) {
		expsColors = intraColors;
	}
}
