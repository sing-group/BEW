package es.uvigo.ei.sing.bew.model.views;

import java.awt.Component;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class lets the user visualize a Condition.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ConditionView extends JPanel {

	private static final long serialVersionUID = 1L;

	private LongTextTable table;
	private JLabel labelTitle;

	private Condition cond;

	/**
	 * Default Constructor
	 * 
	 * @param cond
	 *            Condition to visualize
	 */
	public ConditionView(Condition cond) {
		// TODO Auto-generated constructor stub
		super();

		this.cond = cond;

		initialize();
	}

	/**
	 * Method to initialize the Dialog
	 */
	public void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		labelTitle = new JLabel(I18n.get("condProp") + this.cond.getName());
		labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(labelTitle);

		table = new LongTextTable(new MyTableModel());
		table.setDefaultRenderer(Object.class, new StripeTableCellRender());
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		initTable();
		putTable();
	}

	/**
	 * Method to initialize the table inside the Dialog
	 */
	public void initTable() {
		MyTableModel mtm = (MyTableModel) table.getModel();

		mtm.addColumn(this.cond.getName());
		mtm.addColumnClass(0, String.class);
	}

	/**
	 * Method to add data from the Condition to the table
	 */
	public void putTable() {
		MyTableModel mtm = (MyTableModel) table.getModel();
		List<Object> values = this.cond.getConditionValues();
		LinkedHashSet<String> valuesSet = new LinkedHashSet<String>();
		Vector<String> rowValues = new Vector<String>();

		for (Object value : values) {
			valuesSet.add(value.toString());
		}

		// Each String in different Row but in the same column
		for (String value : valuesSet) {
			rowValues.add(value);
			mtm.addRow(FunctionConstants.copyVector(rowValues));
			rowValues.clear();
		}
	}
}
