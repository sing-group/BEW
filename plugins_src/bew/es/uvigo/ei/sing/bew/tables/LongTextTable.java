package es.uvigo.ei.sing.bew.tables;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class LongTextTable extends JTable {

	private static final long serialVersionUID = 1L;

	public LongTextTable() {
		// TODO Auto-generated constructor stub
		super();
	}

	public LongTextTable(TableModel model) {
		// TODO Auto-generated constructor stub
		super(model);
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int row = rowAtPoint(e.getPoint());
		int column = columnAtPoint(e.getPoint());

		String value = "";
		try{
			value = getValueAt(row, column).toString();
			value = "<html>" + value + "</html>";
			
			int lastIndex = 0;
			while (lastIndex != -1) {
				
				lastIndex = value.indexOf(". ", lastIndex);
				
				if (lastIndex != -1) {
					lastIndex += ". ".length();
					value = value.substring(0, lastIndex) + "<br />"
							+ value.substring(lastIndex, value.length());
				}
			}
			lastIndex = 0;
			while (lastIndex != -1) {
				
				lastIndex = value.indexOf("\n", lastIndex);
				
				if (lastIndex != -1) {
					lastIndex += "\n".length();
					value = value.substring(0, lastIndex) + "<br />"
							+ value.substring(lastIndex, value.length());
				}
			}
			
		}catch(Exception e1){
			
		}

		return value == null ? null : value;
	}

	/**
	 * 
	 * @param table
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
}
