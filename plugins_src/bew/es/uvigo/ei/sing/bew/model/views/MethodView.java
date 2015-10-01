package es.uvigo.ei.sing.bew.model.views;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.InterLabExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.tables.LongTextTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * This class lets the user visualize a Method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class MethodView extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane scrollDescriptive;
	private MethodTable methodTable;
	private LongTextTable tableInfo;
	private LongTextTable condTable;

	private Method method;
	private JPanel methodPane;
	private JPanel infoPane;
	private JScrollPane scrollCond;
	private JPanel descriptivePanel;
	private JPanel conditionsPanel;

	/**
	 * Default Constructor
	 * 
	 * @param method
	 *            Method to view
	 */
	public MethodView(Method method) {
		super();

		this.method = method;

		init();
	}

	/**
	 * Method to initialize the dialog
	 */
	private void init() {
		setLayout(new GridLayout(0, 1, 0, 0));

		methodPane = new JPanel();
		methodPane.setBorder(new TitledBorder(null, method.getName() + " ("
				+ method.getUnits() + ")", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(methodPane);
		methodPane.setLayout(new GridLayout(0, 1, 0, 0));

		methodTable = new MethodTable(method, false);

		JScrollPane scrollMethod = new JScrollPane(methodTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Rows header
		RowNumberTable rowTable = new RowNumberTable(methodTable);

		scrollMethod.setRowHeaderView(rowTable);
		scrollMethod.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		methodPane.add(scrollMethod);
		{
			MyTableModel tableCondModel = new MyTableModel(new Object[][] {},
					new String[] { "Condition name", "Values", "Units" });
			tableCondModel.addColumnClass(0, String.class);
			tableCondModel.addColumnClass(1, String.class);
			tableCondModel.addColumnClass(2, String.class);
			conditionsPanel = new JPanel();
			add(conditionsPanel);
			conditionsPanel.setBorder(new TitledBorder(null,
					"Method conditions", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			conditionsPanel.setLayout(new GridLayout(0, 1, 0, 0));

			condTable = new LongTextTable(tableCondModel);
			condTable.setDefaultRenderer(Object.class,
					new StripeTableCellRender());

			scrollCond = new JScrollPane(condTable);
			// Rows header
			rowTable = new RowNumberTable(condTable);

			scrollCond.setRowHeaderView(rowTable);
			scrollCond.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					rowTable.getTableHeader());

			conditionsPanel.add(scrollCond);
			fillCondTable();
		}
		{

			MyTableModel tableInfoModel = new MyTableModel(new Object[][] {},
					new String[] { "DataSeries", "Min", "Max", "Mean", "Stdv" });
			tableInfoModel.addColumnClass(0, String.class);
			tableInfoModel.addColumnClass(1, Double.class);
			tableInfoModel.addColumnClass(2, Double.class);
			tableInfoModel.addColumnClass(3, Double.class);
			tableInfoModel.addColumnClass(4, Double.class);
			infoPane = new JPanel();
			infoPane.setLayout(new GridLayout(0, 1, 0, 0));
			add(infoPane);
			descriptivePanel = new JPanel();
			descriptivePanel.setBorder(new TitledBorder(null,
					"Descriptive statistics", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			descriptivePanel.setLayout(new GridLayout(0, 1, 0, 0));
			infoPane.add(descriptivePanel);

			tableInfo = new LongTextTable(tableInfoModel);
			tableInfo.setDefaultRenderer(Object.class,
					new StripeTableCellRender());

			scrollDescriptive = new JScrollPane(tableInfo);
			// Rows header
			rowTable = new RowNumberTable(tableInfo);

			scrollDescriptive.setRowHeaderView(rowTable);
			scrollDescriptive.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					rowTable.getTableHeader());

			descriptivePanel.add(scrollDescriptive);
			fillTableInformation();
		}
	}

	/**
	 * Fills Condition table with data.
	 */
	private void fillCondTable() {
		MyTableModel dtm = (MyTableModel) condTable.getModel();
		String name;
		String units;
		String values = "";
		Map<Object, Object> mapIntraColors = method.getParent()
				.getMapIntraExpsColors();
		StripeTableCellRender renderer = (StripeTableCellRender) condTable
				.getDefaultRenderer(Object.class);

		for (Condition c : method.getArrayCondition().getElements()) {
			name = c.getName();
			units = c.getUnits();
			// We save all the values in the same row inside 'values' variable
			for (Object value : c.getConditionValues()) {
				// We don't want to repeat values
				if (!values.contains(value.toString())) {
					if (values.isEmpty())
						values = value.toString();
					else
						values = values.concat(", " + value.toString());
				}
			}

			// Each String in different Row but in the same column
			Object[] row = new Object[] { name, values, units };
			dtm.addRow(row);

			// Purge variables
			values = "";
		}

		if (mapIntraColors != null) {
			paintConditionTable(mapIntraColors, renderer);
		}
	}

	/**
	 * Puts the different colors to the table. Used in InterExperiments.
	 * 
	 * @param mapIntraRows
	 *            Map with IntraExperiments.
	 * @param renderer
	 *            Renderer to modify the colors.
	 */
	private void paintConditionTable(Map<Object, Object> mapIntraColors,
			StripeTableCellRender renderer) {
		// Put the selected colors in the renderer
		renderer.setIntraExperimentColors(method.getParent()
				.getMapIntraExpsColors());

		// Paint the dataSeries of each intraExperiment with the
		// selected color
		int row = 0;
		int index = 0;
		List<String> auxList = new ArrayList<String>();

		// Get IntraExperiments
		InterLabExperiment interExp = (InterLabExperiment) method.getParent();
		List<IExperiment> intraExp = interExp.getIntraExperiments();
		while (index < mapIntraColors.keySet().size()) {
			// Get IntraExperiment
			IExperiment exp = (IExperiment) intraExp.get(index);

			// Get method conditions (each Intra give their conditions
			// to the Inter. EX: 2 Intra with 3 conditions. The Inter will have
			// 6 conditions although 3 are repeated because all IntraExperiments
			// must have the same conditions).
			List<Condition> methodCond = method.getArrayCondition()
					.getElements();
			// Go over conditions
			for (int i = row; i < methodCond.size(); i++) {
				// Get actual condition
				String actualCond = methodCond.get(i).getName();
				// If the list doesn't have the condition...
				if (!auxList.contains(actualCond)) {
					// Add condition to the list
					auxList.add(actualCond);
					// Add color to the render (conditions are sorted by intra)
					renderer.addInterExp((Integer) row, exp);
					row++;
				} else {
					break;
				}
			}
			auxList.clear();
			index++;
		}
	}

	/**
	 * Method to fill with data the information table. This method provides the
	 * min value, max value, standard deviation and mean of each dataSerie in
	 * the method. Besides, it calculates these values for the total of the
	 * dataSeries in the last row of the table.
	 */
	private void fillTableInformation() {
		int index = 1;
		MyTableModel mtm = (MyTableModel) tableInfo.getModel();
		// DataSeries of the method
		List<DataSeries> dataSeries = method.getDataSeries().getElements();
		// Variable to save all the measurements of the dataSeries to calculate
		// the total
		List<Object> allMeasurements = new ArrayList<Object>();

		// Same size than the number of the columns in information table
		Object[] rowArray = new Object[tableInfo.getColumnCount()];
		double mean;
		double stdv;
		try {
			// Go over dataSeries
			for (DataSeries ds : dataSeries) {
				// Get measurements of the current ds
				List<Object> measurements = ds.getMeasurements();

				// First column is the number of the dataSerie
				rowArray[0] = "DataSeries " + index + ":";
				// Second column is the min
				rowArray[1] = FunctionConstants.getMin(measurements);
				// Third column is the max
				rowArray[2] = FunctionConstants.getMax(measurements);
				// Fourth column is the mean (3 decimals)
				mean = FunctionConstants.calculateMean(measurements);
				rowArray[3] = Math.rint(mean * 1000) / 1000;
				// Fifth column is the standard deviation (3 decimals)
				stdv = FunctionConstants.calculateStandardDesv(measurements,
						mean);
				rowArray[4] = Math.rint(stdv * 1000) / 1000;

				// Save this measurements for calculate the total
				allMeasurements.addAll(measurements);

				// Add row in the table
				mtm.addRow(rowArray);

				index++;
			}

			// Calculate the total
			rowArray[0] = "";
			rowArray[1] = FunctionConstants.getMin(allMeasurements);
			rowArray[2] = FunctionConstants.getMax(allMeasurements);

			mean = FunctionConstants.calculateMean(allMeasurements);
			rowArray[3] = Math.rint(mean * 1000) / 1000;

			stdv = FunctionConstants.calculateStandardDesv(allMeasurements,
					mean);
			rowArray[4] = Math.rint(stdv * 1000) / 1000;

			mtm.addRow(rowArray);
		} catch (Exception e) {
			// No measurementes
		}
	}

	/**
	 * Get methodTable.
	 * 
	 * @return
	 */
	public MethodTable getTable() {
		return this.methodTable;
	}
}
