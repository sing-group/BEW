package es.uvigo.ei.sing.bew.model.views;

import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class lets the user visualize an intraExperiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class IntraExperimentView extends JPanel {
	private static final long serialVersionUID = 1L;

	private Experiment exp;

	private JPanel panelMetodos;
	private JPanel panelConstant;
	private JPanel panelExpSetup;
	private JScrollPane scrollConstant;

	private LongTextTable experimentSetup;
	private LongTextTable constantCond;
	private LongTextTable methods;
	private JScrollPane scrollSetup;
	private JScrollPane scrollMethods;

	/**
	 * Default Constructor
	 * 
	 * @param exp
	 *            intraExperiment to view
	 */
	public IntraExperimentView(Experiment exp) {
		super();

		this.exp = exp;

//		System.out.println("MODEL VIEW: " + exp.getBioID());

		init();
	}

	/**
	 * Method to initialize the dialog
	 */
	private void init() {
		setLayout(new GridLayout(3, 1, 0, 0));

		panelExpSetup = new JPanel();
		panelExpSetup.setBorder(new TitledBorder(null, I18n.get("expSetup"),
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		add(panelExpSetup);

		MyTableModel mtmSetup = new MyTableModel(
				new Object[][] { { I18n.get("experimentName"), null },
						{ I18n.get("authors"), null },
						{ I18n.get("organization"), null },
						{ I18n.get("emailContact"), null },
						{ I18n.get("date"), null },
						{ I18n.get("publication"), null },
						{ I18n.get("notes"), null }, }, new String[] {
						I18n.get("info"), I18n.get("detailsInfo") });
		mtmSetup.addColumnClass(0, String.class);
		mtmSetup.addColumnClass(1, String.class);
		experimentSetup = new LongTextTable(mtmSetup);
		experimentSetup.setDefaultRenderer(Object.class,
				new StripeTableCellRender());
		panelExpSetup.setLayout(new GridLayout(0, 1, 0, 0));
		experimentSetup.setColumnSelectionAllowed(true);
		experimentSetup.setCellSelectionEnabled(true);

		scrollSetup = new JScrollPane(experimentSetup);
		// Rows header
		JTable rowTable = new RowNumberTable(experimentSetup);

		scrollSetup.setRowHeaderView(rowTable);
		scrollSetup.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelExpSetup.add(scrollSetup);

		panelConstant = new JPanel();
		panelConstant.setBorder(new TitledBorder(null, I18n
				.get("constantConditions"), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(panelConstant);

		MyTableModel mtmConstant = new MyTableModel(new Object[][] {},
				new String[] { "Conditions", "Condition values", "Units" });
		mtmConstant.addColumnClass(0, String.class);
		mtmConstant.addColumnClass(1, String.class);
		mtmConstant.addColumnClass(2, String.class);
		constantCond = new LongTextTable(mtmConstant);
		constantCond.setDefaultRenderer(Object.class,
				new StripeTableCellRender());
		panelConstant.setLayout(new GridLayout(0, 1, 0, 0));
		constantCond.setColumnSelectionAllowed(true);
		constantCond.setCellSelectionEnabled(true);

		scrollConstant = new JScrollPane(constantCond);
		// Rows header
		rowTable = new RowNumberTable(constantCond);

		scrollConstant.setRowHeaderView(rowTable);
		scrollConstant.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelConstant.add(scrollConstant);

		panelMetodos = new JPanel();
		panelMetodos.setBorder(new TitledBorder(null, I18n.get("methods"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panelMetodos);

		MyTableModel mtmMethod = new MyTableModel(new Object[][] {},
				new String[] { I18n.get("methodName"),
						I18n.get("dataReplicates"),
						I18n.get("combinationOfConds") });
		mtmMethod.addColumnClass(0, String.class);
		mtmMethod.addColumnClass(1, String.class);
		mtmMethod.addColumnClass(2, String.class);
		methods = new LongTextTable(mtmMethod);
		methods.setDefaultRenderer(Object.class, new StripeTableCellRender());
		panelMetodos.setLayout(new GridLayout(0, 1, 0, 0));
		methods.setColumnSelectionAllowed(true);
		methods.setCellSelectionEnabled(true);

		scrollMethods = new JScrollPane(methods);
		// Rows header
		rowTable = new RowNumberTable(methods);

		scrollMethods.setRowHeaderView(rowTable);
		scrollMethods.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panelMetodos.add(scrollMethods);

		putMethods();
		putConstants();
		putExpSetup();

		setVisible(true);
	}

	/**
	 * Method to put the methods in the intraExperiment dialog
	 */
	private void putMethods() {
		MyTableModel mtm = (MyTableModel) methods.getModel();
		Object[] row = new Object[mtm.getColumnCount()];

		for (Method met : this.exp.getMethods().getMetodos()) {
			int rowSize = met.getDataSeries().getElements().size();

			row[0] = met.getName() + "(" + met.getUnits() + ")";
			row[1] = rowSize;
			row[2] = met.getDataSeries().getElements().get(0).getTotalSize();

			mtm.addRow(row);
		}
	}

	/**
	 * Method to put the ConstantCondition in the intraExperiment dialog
	 */
	private void putConstants() {
		ConstantConditions constantCond = exp.getConstantCondition();

		MyTableModel dtm = (MyTableModel) this.constantCond.getModel();

		if (constantCond != null) {
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

	/**
	 * Method to put the Experiment Setup in the intraExperiment dialog
	 */
	private void putExpSetup() {
		MyTableModel mtm = (MyTableModel) experimentSetup.getModel();
		String[] setup = exp.getExpSetup();

		int index = 0;
		for (String s : setup) {
			try {
				mtm.setValueAt(s, index, 1);
				index++;
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		}
	}
}
