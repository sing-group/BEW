package es.uvigo.ei.sing.bew.model.views;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;

/**
 * This class lets the user visualize a dataSerie (DEPRECATED).
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class DataSerieView extends JPanel {

	private static final long serialVersionUID = 1L;
	private LongTextTable table;
	private JLabel labelTitle;

	private DataSeries dataSer;

	/**
	 * Default Constructor
	 * 
	 * @param dataSer
	 *            DataSerie to view
	 */
	public DataSerieView(DataSeries dataSer) {
		// TODO Auto-generated constructor stub
		super();

		this.dataSer = dataSer;

		initialize();
	}

	/**
	 * Method to initialize the dialog
	 */
	public void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		labelTitle = new JLabel(I18n.get("dataSerieProp"));
		labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(labelTitle);

		table = new LongTextTable(new MyTableModel());
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);

		JScrollPane scroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Rows header
		RowNumberTable rowTable = new RowNumberTable(table);

		scroll.setRowHeaderView(rowTable);
		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());

		add(scroll);

		initTable();
		putTable();

		table.resizeColumnWidth();
	}

	/**
	 * Method to initialize the table
	 */
	public void initTable() {
		MyTableModel mtm = (MyTableModel) table.getModel();

		int numConditions = dataSer.getMapCS().keySet().size();

		int index = 1;
		List<Condition> condList = new ArrayList<Condition>();

		condList.addAll(dataSer.getMapCS().keySet());
		for (int i = 0; i < dataSer.getTotalSize(); i++) {
			if (i < numConditions) {
				mtm.addColumn(condList.get(i).getName());
				mtm.addColumnClass(i, String.class);
			} else {
				mtm.addColumn("Replicate " + index);
				mtm.addColumnClass(i, String.class);
				index++;
			}
		}
	}

	/**
	 * Method to put data from the DataSerie in the table inside the dialog
	 */
	public void putTable() {
		MyTableModel dtm = (MyTableModel) table.getModel();

		Vector<Object> values = new Vector<Object>();

		// We don't use DataRow from DataSeries because we want replicate
		// values in each DataSerie
		for (Object condition : dataSer.getMapCS().values()) {
			values.add(condition);
		}

		for (Object value : dataSer.getMeasurements()) {
			values.add(value);
		}

		dtm.addRow(values);
	}

}
