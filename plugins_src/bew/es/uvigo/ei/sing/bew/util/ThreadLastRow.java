package es.uvigo.ei.sing.bew.util;

import es.uvigo.ei.sing.bew.tables.DataTable;

/**
 * Thread to calculate the last row of one table. 
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class ThreadLastRow implements Runnable {

	private DataTable table;

	/**
	 * Default constructor.
	 * 
	 * @param sc
	 *            Sheet with the table.
	 */
	public ThreadLastRow(DataTable table) {
		// TODO Auto-generated constructor stub
		this.table = table;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int col = 0; col < table.getModel().getColumnCount(); col++) {
			for (int fil = 0; fil < table.getModel().getRowCount(); fil++) {
				try {
					if (table.getValueAt(fil, col).toString().length() > 0)
						table.setLastRow(fil + 1);
				} catch (NullPointerException e) {
					table.setValueAt("", fil, col);
				}
			}
		}
	}
}
