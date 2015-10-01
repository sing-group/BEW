package es.uvigo.ei.sing.bew.model.views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class lets the user visualize Constant Conditions.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class ConstantConditionView extends JPanel {

	private static final long serialVersionUID = 1L;
	private LongTextTable constantTable;

	private ConstantConditions constantCond;
	private JPanel panelConstant;

	/**
	 * Default Constructor
	 * 
	 * @param constantCond
	 *            Constant Condition to visualize
	 */
	public ConstantConditionView(ConstantConditions constantCond) {
		super();

		this.constantCond = constantCond;

		init();
	}

	/**
	 * Method to initialize the Dialog
	 */
	private void init() {
		setLayout(new GridLayout(0, 1, 0, 0));

		panelConstant = new JPanel();
		panelConstant.setBorder(new TitledBorder(null,
				"Constant Condition properties", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(panelConstant);
		panelConstant.setLayout(new BorderLayout(0, 0));

		MyTableModel mtmConstant = new MyTableModel(new Object[][] {},
				new String[] { "Conditions", "Condition values", "Units" });
		mtmConstant.addColumnClass(0, String.class);
		mtmConstant.addColumnClass(1, String.class);
		mtmConstant.addColumnClass(2, String.class);
		constantTable = new LongTextTable(mtmConstant);
		constantTable.setDefaultRenderer(Object.class,
				new StripeTableCellRender());
		constantTable.setColumnSelectionAllowed(true);
		constantTable.setCellSelectionEnabled(true);

		JScrollPane scrollPane = new JScrollPane(constantTable);
		// Rows header
		JTable rowTable = new RowNumberTable(constantTable);

		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelConstant.add(scrollPane);

		loadConditionData();
	}

	/**
	 * Method to put the data from the ConstantCondition in the dialog
	 */
	private void loadConditionData() {
		MyTableModel dtm = (MyTableModel) this.constantTable.getModel();

		List<String> conditions = constantCond.getConstantConditions();
		List<String> conditionValues = constantCond.getConstantValues();
		List<String> constantUnits = constantCond.getConstantUnits();
		Vector<String> rowValues = new Vector<String>();

		for (int pos = 0; pos < conditionValues.size(); pos++) {
			rowValues.add(conditions.get(pos));
			rowValues.add(conditionValues.get(pos));
			rowValues.add(constantUnits.get(pos));
			dtm.addRow(FunctionConstants.copyVector(rowValues));
			rowValues.clear();
		}
	}
}
