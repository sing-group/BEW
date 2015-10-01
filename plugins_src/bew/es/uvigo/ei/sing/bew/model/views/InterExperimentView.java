package es.uvigo.ei.sing.bew.model.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.InterLabExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class lets the user visualize an interExperiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class InterExperimentView extends JPanel {

	private static final long serialVersionUID = 1L;

	private InterLabExperiment exp;
	private Map<Object, Object> mapIntraColor;
	private List<IExperiment> intraExp;

	private JPanel panelMetodos;
	private JPanel panelConstant;
	private JScrollPane scrollConstant;

	private LongTextTable experimentSetup;
	private LongTextTable constantCond;
	private LongTextTable methods;
	private JScrollPane scrollSetup;
	private JScrollPane scrollMethods;
	private JTabbedPane tabbedPane;
	private JPanel panelTab;

	/**
	 * Default Constructor
	 * 
	 * @param exp
	 *            interExperiment to view
	 */
	public InterExperimentView(InterLabExperiment exp) {
		super();

		this.exp = exp;
		this.mapIntraColor = exp.getMapIntraExpsColors();
		this.intraExp = exp.getIntraExperiments();
//		System.out.println("MODEL VIEW: " + exp.getBioID());

		init();
	}

	/**
	 * Method to initialize the dialog
	 */
	private void init() {
		setLayout(new GridLayout(3, 1, 0, 0));

		// Experiments setup
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBorder(new TitledBorder(null, I18n.get("expSetup"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		add(tabbedPane);
		// End

		// ConstantConditions
		panelConstant = new JPanel();
		panelConstant.setBorder(new TitledBorder(null, I18n
				.get("constantConditions"), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(panelConstant);

		MyTableModel mtm = new MyTableModel(new Object[][] {}, new String[] {
				"Conditions", "Condition values", "Units" });
		mtm.addColumnClass(0, String.class);
		mtm.addColumnClass(1, String.class);
		mtm.addColumnClass(2, String.class);
		constantCond = new LongTextTable(mtm);
		panelConstant.setLayout(new GridLayout(0, 1, 0, 0));
		constantCond.setColumnSelectionAllowed(true);
		constantCond.setCellSelectionEnabled(true);

		scrollConstant = new JScrollPane(constantCond);
		panelConstant.add(scrollConstant);
		// End

		// Methods
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
		panelMetodos.add(scrollMethods);
		// End

		// Configure views
		putExpSetup();
		putConstants();
		putMethods();

		setVisible(true);
	}

	/**
	 * Method to put the methods in the Experiment dialog
	 */
	private void putMethods() {
		MyTableModel mtm = (MyTableModel) methods.getModel();
		Object[] row = new Object[mtm.getColumnCount()];

		for (Method met : this.exp.getMethods().getMetodos()) {
			int rowSize = met.getDataSeries().getElements().size();

			row[0] = met.getName() + "(" + met.getUnits() + ")";
			row[1] = rowSize;
			if (rowSize == 0) {
				row[2] = 0;
			} else {
				row[2] = met.getDataSeries().getElements().get(0)
						.getTotalSize();
			}

			mtm.addRow(row);
		}
	}

	/**
	 * Method to fill the constant condition table with the constant conditions
	 * of the intraExperiments. Each constant will be painted with the
	 * associated intraExperiment color.
	 */
	private void putConstants() {
		StripeTableCellRender renderer = new StripeTableCellRender();
		// Put the selected colors in the renderer
		renderer.setIntraExperimentColors(mapIntraColor);
		// Set renderer for JTable
		constantCond.setDefaultRenderer(Object.class, renderer);

		// Get TableModel
		MyTableModel dtm = (MyTableModel) this.constantCond.getModel();

		ConstantConditions constantCond;
		int row = 0;
		// Go over intraExperiments to put their constantConditions
		List<String> conditions = new ArrayList<String>();
		List<String> conditionValues = new ArrayList<String>();
		List<String> constantUnits = new ArrayList<String>();
		Vector<String> rowValues = new Vector<String>();
		for (IExperiment experiment : intraExp) {
			constantCond = experiment.getConstantCondition();

			if (constantCond != null) {
				// Put a row in the table with the constantConditions
				conditions = constantCond.getConstantConditions();
				conditionValues = constantCond.getConstantValues();
				constantUnits = constantCond.getConstantUnits();

				for (int pos = 0; pos < conditionValues.size(); pos++) {
					rowValues.add(conditions.get(pos));
					rowValues.add(conditionValues.get(pos));
					rowValues.add(constantUnits.get(pos));
					dtm.addRow(FunctionConstants.copyVector(rowValues));
					rowValues.clear();

					// Paint the row with the intraExperiment color
					renderer.addInterExp(row, experiment);
					row++;
				}
			}
		}
	}

	/**
	 * Method to put the Experiment Setup in the Experiment dialog
	 */
	private void putExpSetup() {
		// First tab is interExperiment
		createTab(exp, true);
		// Go over intraExperiments to put their constantConditions
		for (IExperiment experiment : intraExp) {
			createTab(experiment, false);
		}
	}

	/**
	 * Method to create a new tab that contain all the information about the
	 * experiment setup. Besides, if the experiment is an IntraExperiment this
	 * method add a button to change the color for painting the rows in the
	 * different tables.
	 * 
	 * @param experiment
	 *            Experiment that contain the information to represent
	 * @param isInterExperiment
	 *            boolean. True if Experiment == InterExperiment, false
	 *            otherwise
	 */
	private void createTab(final IExperiment experiment,
			boolean isInterExperiment) {
		// Get Experiment name
		String expName = experiment.getName();

		// Create panel inside the tab
		panelTab = new JPanel();
		panelTab.setLayout(new BorderLayout(0, 0));
		// Create new Tab
		tabbedPane.addTab(expName, null, panelTab, null);
		// Create table inside the tab
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
		mtmSetup.addColumnClass(2, String.class);
		mtmSetup.addColumnClass(3, String.class);
		mtmSetup.addColumnClass(4, String.class);
		mtmSetup.addColumnClass(5, String.class);
		mtmSetup.addColumnClass(6, String.class);
		experimentSetup = new LongTextTable(mtmSetup);
		experimentSetup.setDefaultRenderer(Object.class,
				new StripeTableCellRender());
		experimentSetup.setColumnSelectionAllowed(true);
		experimentSetup.setCellSelectionEnabled(true);

		scrollSetup = new JScrollPane(experimentSetup);
		panelTab.add(scrollSetup);

		// Fill the experiment setup information for this Experiment
		fillSetupTable(experimentSetup, experiment);
		// End table

		// If experiment == intraExperiment
		if (!isInterExperiment) {
			// Create color Panel to change the actual color
			JPanel panelButton = new JPanel();
			panelTab.add(panelButton, BorderLayout.SOUTH);
			GridBagLayout gblPanel1 = new GridBagLayout();
			gblPanel1.columnWidths = new int[] { 100, 50 };
			gblPanel1.rowHeights = new int[] { 10 };
			gblPanel1.columnWeights = new double[] { 1.0, 0.0 };
			gblPanel1.rowWeights = new double[] { 1.0 };
			panelButton.setLayout(gblPanel1);

			JLabel lblColor = new JLabel(I18n.get("changeColor"));
			GridBagConstraints gbcLblLabel = new GridBagConstraints();
			gbcLblLabel.insets = new Insets(0, 0, 0, 5);
			gbcLblLabel.gridx = 0;
			gbcLblLabel.gridy = 0;
			panelButton.add(lblColor, gbcLblLabel);

			final JButton btnColorButton = new JButton(I18n.get("clickMe"));
			GridBagConstraints gbcBtnNewButton = new GridBagConstraints();
			gbcBtnNewButton.gridx = 1;
			gbcBtnNewButton.gridy = 0;
			panelButton.add(btnColorButton, gbcBtnNewButton);

			final Color selectedColor = (Color) mapIntraColor.get(experiment);
			btnColorButton.setBackground(selectedColor);
			// End color panel

			// ColorChooser Listener
			btnColorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Show color chooser
					Color color = JColorChooser.showDialog(null,
							I18n.get("pickColor"), selectedColor);

					// If the selected color != null
					if (color != null) {
						// Paint the button with the color
						btnColorButton.setBackground(color);
						// Introduce the color inside the map
						mapIntraColor.put(experiment, color);

						repaint();
					}
				}
			});
		}
	}

	/**
	 * Method to fill the setup table with the information of the experiment
	 * 
	 * @param table
	 *            Table to fill with data
	 * @param experiment
	 *            Experiment with the information
	 */
	private void fillSetupTable(JTable table, IExperiment experiment) {
		MyTableModel mtm = (MyTableModel) table.getModel();
		String[] setup = experiment.getExpSetup();

		int index = 0;
		for (String s : setup) {
			try {
				mtm.setValueAt(s, index, 1);
				index++;
			} catch (IndexOutOfBoundsException e) {

			}

		}
	}
}
