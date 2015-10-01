package es.uvigo.ei.sing.bew.model.views;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class generates the table to visualize the data of a Method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class MethodTable extends JTable {

	private static final long serialVersionUID = 1L;

	private Method method;
	private int numCol;

	private MyTableModel mtm;

	/**
	 * Default constructor.
	 * 
	 * @param method
	 *            Method to view.
	 * @param cellSelection
	 *            Type of cell selection. If true the user can select columns
	 *            and cells.
	 */
	public MethodTable(Method method, boolean cellSelection) {
		super();

		this.method = method;

		this.numCol = this.method.getNumConditions()
				+ this.method.getDataSeries().getElements().get(0)
						.getMeasurements().size();

		init(cellSelection);
	}

	/**
	 * Method to initialize the dialog.
	 * 
	 * @param cellSelection
	 *            Type of cell selection.
	 */
	public void init(boolean cellSelection) {
		mtm = new MyTableModel();
		setModel(mtm);
		StripeTableCellRender renderer = new StripeTableCellRender();
		setDefaultRenderer(Object.class, renderer);

		if (cellSelection) {
			setColumnSelectionAllowed(cellSelection);
			setCellSelectionEnabled(cellSelection);
		}

		putMethodHeaders();
		putData();

		// If the View is for InterExperiment
		// Get the list with the intraExperiments names
		Map<Object, List<Object>> mapIntraExpsRows = method.getParent()
				.getMapIntraExpsAndRows();

		// If the method was called by a intraExperiment the list will be
		// null
		if (mapIntraExpsRows != null) {
			// Put the selected colors in the renderer
			renderer.setIntraExperimentColors(method.getParent()
					.getMapIntraExpsColors());

			// Paint the dataSeries of each intraExperiment with the
			// selected color
			for (Object intraExp : mapIntraExpsRows.keySet()) {
				IExperiment exp = (IExperiment) intraExp;

				// Go over the dataSerie rows
				for (Object row : mapIntraExpsRows.get(exp)) {
					renderer.addInterExp((Integer) row, exp);
				}
			}
			// End InterView
		}

		resizeColumnWidth();
	}

	/**
	 * Method to put the header of the table.
	 */
	private void putMethodHeaders() {
		List<Condition> conditions = this.method.getArrayCondition()
				.getElements();
		MyTableModel mtm = (MyTableModel) this.getModel();

		int index = 1;
		for (int i = 0; i < this.numCol; i++) {
			if (i < method.getNumConditions()) {
				mtm.addColumn(conditions.get(i).getName());
				mtm.addColumnClass(i, String.class);
			} else {
				mtm.addColumn("Replicate " + index);
				mtm.addColumnClass(i, Double.class);
				index++;
			}
		}
	}

	/**
	 * Method to put data from the Method in the table inside the dialog.
	 */
	private void putData() {
		List<DataSeries> dataSeries = this.method.getDataSeries().getElements();
		MyTableModel dtm = (MyTableModel) this.getModel();

		Vector<Object> values = new Vector<Object>();

		for (DataSeries ds : dataSeries) {
			for (Object value : ds.getDataRow())
				values.add(value);

			dtm.addRow(FunctionConstants.copyVector(values));
			values.clear();
		}
	}

	/**
	 * Get Method.
	 * 
	 * @return
	 */
	public Method getMethod() {
		return method;
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

	@Override
	public String getToolTipText(MouseEvent e) {
		int row = rowAtPoint(e.getPoint());
		int column = columnAtPoint(e.getPoint());

		Object value = getValueAt(row, column);
		return value == null ? null : value.toString();
	}
}
