package es.uvigo.ei.sing.bew.constants;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import es.uvigo.ei.sing.bew.exceptions.NoTimeException;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;

/**
 * This class is responsible for calculating the plot functions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public final class PlotFunctions {

	// Constants with the type of the plots
	public final static String STATISTICAL = "Statistical bar";
	public final static String TIME = "Time scatter";

	/**
	 * Creates a new Plot.
	 * 
	 * @param values
	 *            Values to create the dataSet.
	 * @param keys
	 *            Keys to create the dataSet.
	 * @param constantVal
	 *            String for adding information to the plot.
	 * @param method
	 *            Method of the Plot.
	 * @param type
	 *            Plot type.
	 * @param methodConditions
	 *            Method Conditions to put in the Plot.
	 * @param chart
	 *            ChartPanel to show the Plot.
	 * @return a new Plot.
	 * @throws NoTimeException
	 * @throws IOException
	 */
	public static Plot createPlot(List<List<Object>> values, List<Object> keys,
			String constantVal, Method method, String type,
			List<String> methodConditions, ChartPanel chart)
			throws NoTimeException, IOException {
		// Obtain the graphic title. Method name + constantCondition values if
		// exists
		String[] arrayGraphicTitle = constantVal.split("#");
		// We put the Experiment Name in the Title of the Plot
		String expName = method.getParent().getName();
		String graphicTitle = "Experiment <" + expName + ">" + "\n";
		// graphicTitle = graphicTitle.concat(method.getName() + "\n");

		int i = 0;
		for (String cad : arrayGraphicTitle) {
			if (cad.length() > 0 && i == 0) {
				graphicTitle = graphicTitle.concat(cad);
			} else if (cad.length() > 0) {
				graphicTitle = graphicTitle.concat(", " + cad);
			}
			i++;
		}

		// yAxis title. Values + method units
		String yAxisTitle = method.getName() + "(" + method.getUnits() + ")";

		// Create the graphic object without the JfreeChart object
		Plot plot = new Plot(graphicTitle, "", yAxisTitle, type, values, keys,
				method, methodConditions);

		JFreeChart jFreeChart = createJFreeChart(values, keys, method, type,
				graphicTitle, yAxisTitle, methodConditions);

		// Add chart for previsualize panel
		chart.setChart(jFreeChart);

		// Add the JfreeChart object to the plot
		plot.setChart(FunctionConstants.bufferedImgToByte(jFreeChart
				.createBufferedImage(BewConstants.PLOT_WIDTH,
						BewConstants.PLOT_HEIGTH)));

		return plot;
	}

	/**
	 * Selects which type of JFreeChart graphic created based on type.
	 * 
	 * @param values
	 *            Values to create the dataSet.
	 * @param keys
	 *            Keys to create the dataSet.
	 * @param method
	 *            Method of the Plot.
	 * @param type
	 *            Plot type.
	 * @param graphicTitle
	 *            Title of the Plot.
	 * @param yAxisTitle
	 *            Axis title.
	 * @param methodConditions
	 *            Method Conditions to put in the Plot.
	 * @return JFreeChart object that contains the graphic.
	 * @throws NoTimeException
	 */
	public static JFreeChart createJFreeChart(List<List<Object>> values,
			List<Object> keys, Method method, String type, String graphicTitle,
			String yAxisTitle, List<String> methodConditions)
			throws NoTimeException {
		JFreeChart ret = null;

		// Delegates the creation of JfreeChart in another method
		switch (type) {
		case PlotFunctions.STATISTICAL:
			ret = createStatisticalPlot(graphicTitle, "", yAxisTitle, keys,
					values);
			break;
		case PlotFunctions.TIME:
			ret = createTimePlot(graphicTitle, "", yAxisTitle, keys, values,
					methodConditions);
			break;
		default:
			break;
		}

		return ret;
	}

	/**
	 * Creates the Statistical JFreeChart object.
	 * 
	 * @param title
	 *            Title of the Plot.
	 * @param xAxisTitle
	 *            X Axis title.
	 * @param yAxisTitle
	 *            Y Axis title.
	 * @param keys
	 *            Keys to create the dataSet.
	 * @param values
	 *            Values to create the dataSet.
	 * @return Statistical JFreeChart object.
	 */
	private static JFreeChart createStatisticalPlot(String title,
			String xAxisTitle, String yAxisTitle, List<Object> keys,
			List<List<Object>> values) {
		DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();

		ExtendedCategoryAxis extendedXAxis = new ExtendedCategoryAxis(
				xAxisTitle);
		extendedXAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

		NumberAxis yAxis = new NumberAxis(yAxisTitle);
		yAxis.setStandardTickUnits(new StandardTickUnitSource());

		// Define the plot
		StatisticalBarRenderer renderer = new StatisticalBarRenderer();

		// This is the draw
		CategoryPlot plot = new CategoryPlot(dataset, extendedXAxis, yAxis,
				renderer);
		
		// Fill dataSet with data
		for (int index = 0; index < keys.size(); index++) {
			// We put in the legend the last value of the key
			String legend = String.valueOf(keys.get(index));
			String[] arrayLegend = legend.split("#");
			String conditionValue = "";
			conditionValue = getAxisByNumConditions(arrayLegend);

			// Obtain selected values for each key
			List<Object> aux = obtainValues(values, index);

			double media = FunctionConstants.calculateMean(aux);
			double desvest = FunctionConstants
					.calculateStandardDesv(aux, media);

			dataset.add(media, desvest, arrayLegend[arrayLegend.length - 1],
					conditionValue);
			// End adding values

			// We obtain the rest of selected conditions in order to
			// show all the information in the plot (4 or more conditions)
			String subLabel = "";
			for (int j = arrayLegend.length - 3; j >= 1; j--) {
				if ("".equals(subLabel))
					subLabel = subLabel.concat(arrayLegend[j]);
				else
					subLabel = subLabel.concat(", " + arrayLegend[j]);
			}
			// We need an ExtendedCategoryPlot to do SubLabels (Groups
			// of Conditions)
			// We create a group for the parent of the last condition
			// (the bars)
			extendedXAxis.addSubLabel(conditionValue, subLabel);
		}

		// This is the space where the draw is painted
		JFreeChart jFreeChart = new JFreeChart(title, new Font("Helvetica",
				Font.BOLD, 14), plot, true);

		return jFreeChart;
	}

	/**
	 * Creates the Time JFreeChart object.
	 * 
	 * @param title
	 *            Title of the Plot.
	 * @param xAxisTitle
	 *            X Axis title.
	 * @param yAxisTitle
	 *            Y Axis title.
	 * @param keys
	 *            Keys to create the dataSet.
	 * @param values
	 *            Values to create the dataSet.
	 * @return Time JFreeChart object.
	 */
	private static JFreeChart createTimePlot(String title, String xAxisTitle,
			String yAxisTitle, List<Object> keys, List<List<Object>> values,
			List<String> methodConditions) throws NoTimeException {
		// Number of time conditions in the method
		boolean timeCond = false;
		// Position of the time condition (the first)
		int timePos = 0;
		// xLabel
		String xLabel = "";
		JFreeChart jFreeChart;

		// If condition time is present in the method we can do the plot
		String cond;
		for (int i = 0; i < methodConditions.size(); i++) {
			cond = methodConditions.get(i);
			if ("time".equals(cond)) {
				timeCond = true;
				xLabel = cond;
				timePos = i;
			}
		}

		if (timeCond) {
			XYSeriesCollection dataset = new XYSeriesCollection();
			// Fill dataSet with data
			for (int index = 0; index < keys.size(); index++) {
				// Get time value
				String key = keys.get(index).toString();

				// Obtain selected values for each key
				List<Object> aux = obtainValues(values, index);

				double media = FunctionConstants.calculateMean(aux);
				// double desvest = FunctionConstants.calculateStandardDesv(aux,
				// media);

				XYSeries serie = new XYSeries(key);
				try {
					// Add serie
					serie.add(getTimeValue(key, timePos), media);

					dataset.addSeries(serie);
					// End adding values
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			jFreeChart = ChartFactory.createScatterPlot(title, // title
					xLabel, yAxisTitle, // axis labels
					dataset, // dataset
					PlotOrientation.VERTICAL, true, // legend? yes
					true, // tooltips? yes
					false // URLs? no
					);
		}
		// If there isn't time Condition in the Method
		else {
			throw new NoTimeException();
		}

		return jFreeChart;
	}

	/**
	 * Gets the measurements of a Method. Values with String will be change for
	 * NaN to do the plot.
	 * 
	 * @param values
	 *            Values in the Method.
	 * @param index
	 *            Index to get the values.
	 * @return List with the real values for the Plot.
	 */
	private static List<Object> obtainValues(List<List<Object>> values,
			int index) {
		List<Object> aux = new ArrayList<Object>();
		for (Object value : values.get(index)) {
			// If there are a NaN or other String value present in the
			// measurements...
			try {
				aux.add(Double.valueOf(FunctionConstants.replaceCommas(value
						.toString())));
			} catch (NumberFormatException e) {
				// NA present
				aux.add(Double.NaN);
			}
		}
		return aux;
	}

	/**
	 * Gets the value for the Condition time.
	 * 
	 * @param key
	 *            String with the values.
	 * @param timePos
	 *            int with the position value is.
	 * @return Double with the time value.
	 */
	private static double getTimeValue(String key, int timePos)
			throws Exception {
		// Key come like this: 'CondValue1#CondValue2#CondValue3'
		String[] arrayKey = key.split("#");
		// Get the condValue for time condition
		String auxTimeValue = (String) arrayKey[timePos];

		// If time comes from InterExperiment it will be have '_' (ex:
		// 2.0_Intra1)
		if (auxTimeValue.contains("_")) {
			auxTimeValue = auxTimeValue.split("_")[0];
		}
		// If the condition is in the last position (value')
		if ((arrayKey.length - 1 == timePos) && (timePos != 0)) {
			auxTimeValue = auxTimeValue.substring(0, auxTimeValue.length() - 1);
		}
		// Only condition selected ('Value')
		else if (arrayKey.length - 1 == 0) {
			auxTimeValue = auxTimeValue.substring(1, auxTimeValue.length() - 1);
		}
		// First position ('Value)
		else if (timePos == 0) {
			auxTimeValue = auxTimeValue.substring(1);
		}

		double timeValue = Double.parseDouble(auxTimeValue);

		return timeValue;
	}

	/**
	 * Creates the axis based on the number of Conditions.
	 * 
	 * @param arrayLegend
	 *            Legend of the array.
	 * @return String with the axis.
	 */
	private static String getAxisByNumConditions(String[] arrayLegend) {
		String ret = "";
		int numConditions = arrayLegend.length;

		switch (numConditions) {
		// One condition selected, you put the same in RowKey and ColumnKey
		case 1:
			ret = arrayLegend[0];
			break;
		// Two conditions, you put in RowKey the last one and in the ColumnKey
		// the parent
		case 2:
			ret = arrayLegend[arrayLegend.length - 2];
			break;
		// For 3 or more, last one in RowKey and parent with the the first
		// Condition in the table in ColumnKey
		default:
			ret = arrayLegend[arrayLegend.length - 2] + " (" + arrayLegend[0]
					+ ")";
			break;
		}

		return ret;
	}
}
