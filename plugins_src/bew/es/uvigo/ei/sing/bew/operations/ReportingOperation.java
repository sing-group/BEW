package es.uvigo.ei.sing.bew.operations;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.StatisticFunctions;
import es.uvigo.ei.sing.bew.files.DataToFile;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.util.CopyDirectory;

/**
 * This operation generates a HTML report for one Experiment.
 * 
 * @author Gael P�rez Rodr�guez
 * 
 */
@Operation(description = "This operations generates a HTML report of the selected Experiments.")
public class ReportingOperation {

	private IExperiment exp;
	private Boolean[] optionsList;
	private String path;
	private Map<Method, List<Plot>> mapMethodPlots = new HashMap<Method, List<Plot>>();
	private Map<Method, List<Statistic>> methodStat = new HashMap<Method, List<Statistic>>();

	private int index;

	/**
	 * Set path.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set directory", order = 1)
	public void setDirectory(String path) {
		this.path = path;
	}

	/**
	 * Set Experiment.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set Experiment", order = 2)
	public void setExp(IExperiment exp) {
		this.exp = exp;
	}

	/**
	 * Set Experiment options.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set Experiment options", order = 3)
	public void setExpOptions(Boolean[] optionsList) {
		this.optionsList = optionsList;
	}

	/**
	 * Set Method plots.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set Method plots", order = 4)
	public void setPlots(Map<Method, List<Plot>> mapMethodPlots) {
		this.mapMethodPlots.putAll(mapMethodPlots);
	}

	/**
	 * Set Method statistics.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set Method statistics", order = 5)
	public void setStatistics(Map<Method, List<Statistic>> methodStat) {
		this.methodStat.putAll(methodStat);

		try {
			// Get template file
			Document template = getTemplate();
			// Generate report html file
			generateReport(template);
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets HTML template to start reporting.
	 * 
	 * @return Document with the template.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private Document getTemplate() throws URISyntaxException, IOException {
		// Get table html template
		File template = new File(getClass()
				.getResource("/files/TableReport.html").toString().substring(5));

		return Jsoup.parse(template, "UTF-8");
	}

	/**
	 * Generates the HTML report and save it in local disk.
	 * 
	 * @param report
	 *            Document with the HTML template.
	 * @throws IOException
	 */
	private void generateReport(Document template) throws IOException {
		File dirHtml = new File(this.path);

		// Validate if the doesn't path exists
		if (!dirHtml.exists()) {
			dirHtml.mkdir();
		}

		String filePath = path + "/index.html";
		// Create html file with exp name
		try {
			// Copy css and js files
			Path source = Paths.get(new File(StatisticFunctions.class
					.getResource("/files/bew").toString().substring(5))
					.getPath());
			Path target = Paths.get(dirHtml.toURI());
			Files.walkFileTree(source,
					EnumSet.of(FileVisitOption.FOLLOW_LINKS),
					Integer.MAX_VALUE, new CopyDirectory(source, target));

			Files.createFile(Paths.get(filePath));
		} catch (FileAlreadyExistsException e) {
			Files.delete(Paths.get(filePath));
			Files.createFile(Paths.get(filePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File report = new File(filePath);
		// Needed variables if the Experiment is InterExperiment
		Map<Object, List<Object>> mapIntraRows = exp.getMapIntraExpsAndRows();
		Map<Object, Object> mapIntraColor = exp.getMapIntraExpsColors();

		if (optionsList[0]) {
			addSetupToHTML(template);
			// If Experiment is InterExperiment
			if (mapIntraRows != null) {
				for (Object exp : mapIntraRows.keySet()) {
					addIntraSetupToHTML(template, (IExperiment) exp,
							(Color) mapIntraColor.get(exp));
				}
			}
		}
		if (optionsList[1]) {
			addConstantToHTML(template);
		}
		if (optionsList[2]) {
			addInfoToHTML(template);
		}

		if (optionsList[0] || optionsList[1] || optionsList[2]) {
			addToNavBar(template, "experiment");
		}

		if (mapMethodPlots.keySet().size() > 0) {
			// Obtain all selected methods and go over them
			addMethods(template, mapIntraRows, mapIntraColor);
		}

		// Write report in file
		Files.write(report.toPath(), template.outerHtml().getBytes());

		File aux = new File(path + "/experiment.xml");
		// Save and store xml in the folder
		DataToFile.saveXMLData(exp, aux.getAbsolutePath());

		// Open in browser if Desktop API is supported
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(report);
		} else {
			ShowDialog.showInfo(I18n.get("reportingOpTitle"),
					I18n.get("reportingOp") + path);
		}
	}

	/**
	 * 
	 * @param id
	 */
	private void addToNavBar(Document template, String id) {
		if (id.equals("experiment")) {
			template.getElementsByClass("nav-stacked")
					.first()
					.append("<li class=\"active\"><a href=\"#" + id
							+ "\">Experiment</a></li>");
		} else {
			template.getElementsByClass("nav-stacked")
					.first()
					.append("<li><a href=\"#method" + index + "\">" + id
							+ "</a></li>");
		}
	}

	/**
	 * Add Experiment setup to HTML report.
	 * 
	 * @param template
	 *            HTML template.
	 */
	private void addSetupToHTML(Document template) {
		// SetupTable column 1 values
		String[] column1 = new String[] { I18n.get("experimentName"),
				I18n.get("authors"), I18n.get("organization"),
				I18n.get("emailContact"), I18n.get("date"),
				I18n.get("publication"), I18n.get("notes") };
		String[] setup = exp.getExpSetup();

		// H1 title
		Element body = template.body().getElementsByClass("row").last();
		body.prepend("<h1 class=\"page-header\">Reporting for " + setup[0]
				+ "</h1>");
		// H2 title
		Element divSetup = template.getElementsByClass("col-md-8").first();

		divSetup.append("<dl class = \"experimentData setupTableReport\" border=\"1\"></dl>");
		// Setup Table
		Element setupTable = template.getElementsByClass("experimentData")
				.last();
		String tableHtml = "";

		// Set Setup body
		for (int i = 0; i < column1.length; i++) {
			tableHtml = "<dt>" + column1[i] + "</dt><dd>" + setup[i]
					+ "&nbsp;</dd>";
			setupTable.append(tableHtml);
		}
	}

	/**
	 * Add IntraExperiments setup to HTML template.
	 * 
	 * @param template
	 *            HTML template.
	 * @param exp
	 *            IntarExperiment to be added.
	 * @param color
	 *            IntraExperiment color for the report.
	 */
	private void addIntraSetupToHTML(Document template, IExperiment exp,
			Color color) {
		// SetupTable column 1 values
		String[] column1 = new String[] { I18n.get("experimentName"),
				I18n.get("authors"), I18n.get("organization"),
				I18n.get("emailContact"), I18n.get("date"),
				I18n.get("publication"), I18n.get("notes") };
		String[] setup = exp.getExpSetup();

		// Add new section
		template.getElementsByClass("tab-pane").first()
				.append("<div class=\"row\"></div>");
		// Get previous added row
		Element row = template.getElementsByClass("row").last();
		// H2 title
		row.prepend("<h2>Reporting for " + setup[0] + " setup</h2>");
		// Add col 8 and get it
		row.append("<div class=\"col-md-8\"></div>");
		Element divSetup = row.getElementsByClass("col-md-8").first();

		divSetup.append("<dl class = \"experimentData setupTableReport\" border=\"1\"></dl>");
		// Setup Table
		Element setupTable = row.getElementsByClass("experimentData").last();
		String tableHtml = "";

		// Set Setup body
		for (int i = 0; i < column1.length; i++) {
			tableHtml = "<dt>" + column1[i] + "</dt><dd>" + setup[i]
					+ " &nbsp;</dd>";
			setupTable.append(tableHtml);
		}
		// Add Intra color
		tableHtml = "<dt>Color: </dt><dd><span class=\"spanColorReport\" style=\"background-color:rgba("
				+ color.getRed()
				+ ","
				+ color.getGreen()
				+ ","
				+ color.getBlue()
				+ ","
				+ color.getAlpha()
				+ ")\"></span>&nbsp;</dd>";
		setupTable.append(tableHtml);
	}

	/**
	 * Add Constant Condition to the HTML report.
	 * 
	 * @param template
	 *            HTML template.
	 */
	private void addConstantToHTML(Document template) {
		// Constant Table
		Element constantTable = template.getElementsByClass("tab-pane").first();
		constantTable.append("<div class=\"row\"></div>");

		Element rowConstant = constantTable.getElementsByClass("row").last();
		// Add H2
		rowConstant
				.append("<h2 class=\"sub-header\">Constant Condition table</h2>");
		// Add div and table
		rowConstant.append(
				"<div class=\"table-responsive bewTablesReport\"></div>")
				.append("<table class=\"table table-striped noSort\"></div>");
		// Get table
		Element table = rowConstant.getElementsByClass("table").last();
		// Set information in table
		String tableHtml = "";

		tableHtml = "<thead><tr><th>Conditions</th><th>Condition values</th><th>Units</th></tr></thead><tbody></tbody>";
		table.append(tableHtml);

		// Set Constant body
		Elements body = table.getElementsByTag("tbody");
		ConstantConditions constantCond = exp.getConstantCondition();
		if (constantCond != null) {
			List<String> conditions = constantCond.getConstantConditions();
			List<String> conditionValues = constantCond.getConstantValues();
			List<String> constantUnits = constantCond.getConstantUnits();

			// All lists have the same size
			for (int pos = 0; pos < conditionValues.size(); pos++) {
				tableHtml = "<tr><td>" + conditions.get(pos) + "</td><td>"
						+ conditionValues.get(pos) + "</td><td>"
						+ constantUnits.get(pos) + "</td></tr>";
				body.append(tableHtml);
			}
		}
	}

	/**
	 * Add Method basic statistics to the HTML report.
	 * 
	 * @param template
	 *            HTML template.
	 */
	private void addInfoToHTML(Document template) {
		// Add row section
		Element mainDiv = template.getElementsByClass("tab-pane").first();
		mainDiv.append("<div class=\"row\"></div>");

		Element rowConstant = mainDiv.getElementsByClass("row").last();
		// Add H2
		rowConstant
				.append("<h2 class=\"sub-header\">Method information table</h2>");
		// Add div and table
		rowConstant.append(
				"<div class=\"table-responsive bewTablesReport\"></div>")
				.append("<table class=\"table table-striped noSort\"></div>");
		// Get table
		Element table = rowConstant.getElementsByClass("table").last();
		// Set information in table
		String tableHtml = "";

		// Set Info headers
		tableHtml = "<thead><tr><th>" + I18n.get("methodName") + "</th><th>"
				+ I18n.get("dataReplicates") + "</th><th>"
				+ I18n.get("combinationOfConds")
				+ "</th></tr></thead><tbody></tbody>";
		table.append(tableHtml);

		// Set Info body
		Elements body = table.getElementsByTag("tbody");
		int rowSize;
		String name;
		int size = 0;
		for (Method met : this.exp.getMethods().getMetodos()) {
			rowSize = met.getDataSeries().getElements().size();
			name = met.getName() + "(" + met.getUnits() + ")";
			size = met.getDataSeries().getElements().get(0).getTotalSize();

			tableHtml = "<tr><td>" + name + "</td><td>" + rowSize + "</td><td>"
					+ size + "</td></tr>";

			body.append(tableHtml);
		}
	}

	/**
	 * Add Methods to the HTML report.
	 * 
	 * @param template
	 *            HTML template.
	 */
	private void addMethods(Document template,
			Map<Object, List<Object>> mapIntraRows,
			Map<Object, Object> mapIntraColor) {

		Set<Method> expMethods = mapMethodPlots.keySet();
		for (Method method : expMethods) {
			// Add method section
			template.getElementsByClass("main").append(
					"<div class=\"row tab-pane\" id=\"method" + index
							+ "\"></div>");
			// Get method div
			Element methodDiv = template.getElementsByClass("row").last();

			// Add to navBar
			addToNavBar(template, method.getName());
			index++;
			// Add method table
			addMethodToHTML(methodDiv, method, mapIntraRows, mapIntraColor);
			// Add conditions table
			addMethodCondsToHTML(methodDiv, method, mapIntraColor);
			// Add method statistics table
			addMethodInfoToHTML(methodDiv, method);

			try {
				// Add method plots (if needed)
				addPlotsToHTML(methodDiv, method);

				// Add method statistics (if needed)
				addStatisticsToHTML(methodDiv, method);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a single Method to the input Element.
	 * 
	 * @param generalDiv
	 *            Element to add the Method.
	 * @param method
	 *            Method to add.
	 * @param mapIntraRows
	 *            Variable for InterMethods with Intra rows.
	 * @param mapIntraColor
	 *            Variable for InterMethods with Intra colors.
	 */
	private void addMethodToHTML(Element generalDiv, Method method,
			Map<Object, List<Object>> mapIntraRows,
			Map<Object, Object> mapIntraColor) {
		// Add section
		generalDiv.append("<div class=\"row\"></div>");
		// Get section
		Element row = generalDiv.getElementsByClass("row").last();

		// Add h1
		row.append("<h1 class=\"sub-header\">" + method.getName() + "("
				+ method.getUnits() + ")" + "</h1>");

		// Add div and table
		row.append("<div class=\"table-responsive bewTablesReport\"></div>")
				.append("<table class=\"table table-striped noSort\"></div>");
		// Get table
		Element table = row.getElementsByClass("table").last();

		// Add table information
		String tableHtml = "<thead><tr>";

		// Set method table headers
		List<Condition> conditions = method.getArrayCondition().getElements();
		int condSize = method.getNumConditions();
		int numCol = condSize
				+ method.getDataSeries().getElements().get(0).getMeasurements()
						.size();
		int index = 1;
		for (int i = 0; i < numCol; i++) {
			if (i < condSize) {
				tableHtml = tableHtml.concat("<th>"
						+ conditions.get(i).getName() + "</th>");
			} else {
				tableHtml = tableHtml.concat("<th>Data" + index + "</th>");
				index++;
			}
		}
		tableHtml = tableHtml.concat("</tr></thead><tbody></tbody>");
		table.append(tableHtml);

		// Set method table data
		Elements body = table.getElementsByTag("tbody");
		List<DataSeries> dataSeries = method.getDataSeries().getElements();
		Color intraColor;
		int dsIndex = 0;
		for (DataSeries ds : dataSeries) {
			// Get intra color
			intraColor = findColorForDataSerie(dsIndex, mapIntraRows,
					mapIntraColor);
			// If intra color != null, its a InterMethod so we put the color
			if (intraColor != null) {
				tableHtml = "<tr style=\"background-color:rgba("
						+ intraColor.getRed() + "," + intraColor.getGreen()
						+ "," + intraColor.getBlue() + ","
						+ intraColor.getAlpha() + ")\">";
				if (dsIndex == 0) {
					table.addClass("noPaint");
				}
			} else
				tableHtml = "<tr>";
			for (Object value : ds.getDataRow()) {
				tableHtml = tableHtml.concat("<td>" + value + "</td>");
			}
			tableHtml = tableHtml.concat("</tr>");
			body.append(tableHtml);
			dsIndex++;
		}
	}

	/**
	 * Add Method conditions to the input Element.
	 * 
	 * @param generalDiv
	 *            Input Element to add the Conditions.
	 * @param method
	 *            Method to get the Conditions.
	 * @param mapIntraColor
	 *            Map with colors of the IntraExperiments.
	 */
	private void addMethodCondsToHTML(Element generalDiv, Method method,
			Map<Object, Object> mapIntraColor) {
		// Add section
		generalDiv.append("<div class=\"row\"></div>");
		// Get section
		Element row = generalDiv.getElementsByClass("row").last();

		// Add h2
		row.append("<h2 class=\"sub-header\">Method Conditions</h2>");

		// Add div and table
		row.append("<div class=\"table-responsive bewTablesReport\"></div>")
				.append("<table class=\"table table-striped noSort\"></div>");
		// Get table
		Element table = row.getElementsByClass("table").last();
		String tableHtml = "";

		// Set method conds table headers
		tableHtml = "<thead><tr><th>Condition name</th><th>Values</th><th>Units</th></tr></thead><tbody></tbody>";
		table.append(tableHtml);

		// Set conditions body
		Elements body = table.getElementsByTag("tbody");
		String name;
		String units;
		String values = "";
		int index = 0;
		int realCond = method.getNumConditions();
		// Get first color if exists
		Color intraColor = findColorForCondition(index, mapIntraColor);
		for (Condition c : method.getArrayCondition().getElements()) {
			// The Inter has repeated conditions. Each Intra has the same
			// Conditions. When it goes over all the conditions for a
			// Intra, it pass to the next IntraExperiment (and next color).
			if (realCond == 0) {
				index++;
				intraColor = findColorForCondition(index, mapIntraColor);

				realCond = method.getNumConditions();
			}

			name = c.getName();
			units = c.getUnits();
			for (Object value : c.getConditionValues()) {
				if (!values.contains(value.toString())) {
					if (values.isEmpty())
						values = value.toString();
					else
						values = values.concat(", " + value.toString());
				}
			}

			if (intraColor != null) {
				// Each String in different Row but in the same column
				tableHtml = "<tr style=\"background-color:rgba("
						+ intraColor.getRed() + "," + intraColor.getGreen()
						+ "," + intraColor.getBlue() + ","
						+ intraColor.getAlpha() + ")\"><td>" + name
						+ "</td><td>" + values + "</td><td>" + units
						+ "</td></tr>";
			} else {
				// Each String in different Row but in the same column
				tableHtml = "<tr><td>" + name + "</td><td>" + values
						+ "</td><td>" + units + "</td></tr>";
			}
			body.append(tableHtml);

			values = "";
			realCond--;
		}
		if (intraColor != null) {
			table.addClass("noPaint");
		}
	}

	/**
	 * Add Method information to the input Element.
	 * 
	 * @param generalDiv
	 *            Element to add the info.
	 * @param method
	 *            Method to get the info.
	 */
	private void addMethodInfoToHTML(Element generalDiv, Method method) {
		// Add section
		generalDiv.append("<div class=\"row\"></div>");
		// Get section
		Element row = generalDiv.getElementsByClass("row").last();

		// Add h2
		row.append("<h2 class=\"sub-header\">Descriptive Statistics</h2>");

		// Add div and table
		row.append("<div class=\"table-responsive bewTablesReport\"></div>")
				.append("<table class=\"table table-striped noSort\"></div>");
		// Get table
		Element table = row.getElementsByClass("table").last();
		String tableHtml = "";

		// Set statistics headers
		tableHtml = "<thead><tr><th>DataSeries</th><th>Min</th><th>Max</th><th>Mean</th><th>Stdv</th></tr></thead><tbody></tbody>";
		table.append(tableHtml);

		// Set statistics body
		Elements body = table.getElementsByTag("tbody");

		int index = 1;
		// DataSeries of the method
		List<DataSeries> dataSeries = method.getDataSeries().getElements();
		// Variable to save all the measurements of the dataSeries to calculate
		// the total
		List<Object> allMeasurements = new ArrayList<Object>();

		double mean;
		double stdv;
		try {
			// Go over dataSeries
			for (DataSeries ds : dataSeries) {
				// Get measurements of the current ds
				List<Object> measurements = ds.getMeasurements();

				mean = FunctionConstants.calculateMean(measurements);
				stdv = FunctionConstants.calculateStandardDesv(measurements,
						mean);
				mean = Math.rint(mean * 1000) / 1000;
				stdv = Math.rint(stdv * 1000) / 1000;

				tableHtml = "<tr><td>DataSeries" + index + ":</td><td>"
						+ FunctionConstants.getMin(measurements) + "</td><td>"
						+ FunctionConstants.getMax(measurements) + "</td><td>"
						+ mean + "</td><td>" + stdv + "</td></tr>";
				body.append(tableHtml);

				// Save this measurements for calculate the total
				allMeasurements.addAll(measurements);
				index++;
			}

			mean = FunctionConstants.calculateMean(allMeasurements);
			stdv = FunctionConstants.calculateStandardDesv(allMeasurements,
					mean);
			mean = Math.rint(mean * 1000) / 1000;
			stdv = Math.rint(stdv * 1000) / 1000;

			tableHtml = "<tr><td>Total:</td><td>"
					+ FunctionConstants.getMin(allMeasurements) + "</td><td>"
					+ FunctionConstants.getMax(allMeasurements) + "</td><td>"
					+ mean + "</td><td>" + stdv + "</td></tr>";
			body.append(tableHtml);
		} catch (Exception e) {
			// No measurementes
		}
	}

	/**
	 * Adds Methods plots to the input Element.
	 * 
	 * @param generalDiv
	 *            Input Element to add the Plots.
	 * @param method
	 *            Method to get the Plots.
	 * @throws IOException
	 */
	private void addPlotsToHTML(Element generalDiv, Method method)
			throws IOException {
		// Add section
		generalDiv.append("<div class=\"row\"></div>");
		// Get section
		Element row = generalDiv.getElementsByClass("row").last();

		// Add h2
		row.append("<h2 class=\"sub-header\">Method Plots</h2>");

		// Get Method plots and go over them
		Set<Method> keySet = mapMethodPlots.keySet();
		List<Plot> methodPlots = null;
		for (Method m : keySet) {
			// Method name is unique
			if (m.getName().equals(method.getName())) {
				methodPlots = mapMethodPlots.get(m);
			}
		}

		BufferedImage chart;
		String imagePath;
		if (methodPlots != null) {
			Element plotDiv;
			for (Plot p : methodPlots) {
				// Add plot thumbnail and get it
				row.append("<div class=\"col-xs-6 col-md-3\"></div>");
				plotDiv = row.getElementsByClass("col-xs-6").last();

				// Get Plot image and write into a .png file
				chart = FunctionConstants.byteToBufferedImg(p.getChart());

				imagePath = path + "/" + p.getNameClipboard() + ".png";
				// Create image file
				try {
					Files.createFile(Paths.get(imagePath));
				} catch (FileAlreadyExistsException e) {
					Files.delete(Paths.get(imagePath));
					Files.createFile(Paths.get(imagePath));
				}
				ImageIO.write(chart, "png", new File(imagePath));

				// Add img
				plotDiv.append("<a href=\"" + p.getNameClipboard()
						+ ".png\" target=\"_blank\" class=\"thumbnail\"></a>");
				plotDiv.getElementsByClass("thumbnail").append(
						"<img src=\"" + p.getNameClipboard() + ".png\" alt=\""
								+ p.getNameClipboard() + "\"></img>");
			}
		}
	}

	/**
	 * Adds the Method Statistics to the input Element.
	 * 
	 * @param generalDiv
	 *            Element to add the Statistics.
	 * @param method
	 *            Method to get the Statistics.
	 * @throws IOException
	 */
	private void addStatisticsToHTML(Element generalDiv, Method method)
			throws IOException {
		// Add section
		generalDiv.append("<div class=\"row\"></div>");
		// Get section
		Element row = generalDiv.getElementsByClass("row").last();

		// Add h2
		row.append("<h2 class=\"sub-header\">Method Statistics</h2>");

		// Get Method statistics and go over them
		Set<Method> keySet = methodStat.keySet();
		List<Statistic> methodStatistics = null;
		for (Method m : keySet) {
			// Method name is unique
			if (m.getName().equals(method.getName())) {
				methodStatistics = methodStat.get(m);
			}
		}

		String testFilePath = "";
		if (methodStatistics != null) {
			// Add statistic list and get it
			row.append("<div class=\"list-group\"></div>");
			Element testDiv = row.getElementsByClass("list-group").last();
			for (Statistic s : methodStatistics) {
				// If user don't pre generate the html, the program must
				// do now
				if (s.getTempHTMLDir().isEmpty()) {
					StatisticFunctions.createTestReportInFolder(s);
					// Create HTML report
					StatisticFunctions.createTestReport(s, path);
				} else {
					// Create HTML report
					StatisticFunctions.createTestReport(s, path);
				}

				// Pointer to html file
				testFilePath = "." + "/" + s.getNameClipboard() + "/"
						+ s.getNameClipboard() + ".html";

				// Add link to statistic file
				testDiv.append("<a href=\""
						+ testFilePath
						+ "\" target=\"_blank\" class=\"list-group-item\">Statistical report for "
						+ s.getNameClipboard() + "</a>");
			}
		}
	}

	/**
	 * Finds the colors for each dataSerie.
	 * 
	 * @param index
	 *            Index to get the row of the dataSerie.
	 * @param mapIntraRows
	 *            Map with IntraExperiment rows.
	 * @param mapIntraColor
	 *            Map with IntraExperiment colors.
	 * @return Color or null if Color doesn't exist.
	 */
	private Color findColorForDataSerie(int index,
			Map<Object, List<Object>> mapIntraRows,
			Map<Object, Object> mapIntraColor) {
		if (mapIntraRows != null) {
			for (Object key : mapIntraRows.keySet()) {
				if (mapIntraRows.get(key).contains(index)) {
					return (Color) mapIntraColor.get(key);
				}
			}
		}

		return null;
	}

	/**
	 * Finds the colors for each Constant Condition.
	 * 
	 * @param index
	 *            Index to get the Constant Condition.
	 * @param mapIntraRows
	 *            Map with IntraExperiment rows.
	 * @param mapIntraColor
	 *            Map with IntraExperiment colors.
	 * @return Map with IntraExperiment colors.
	 */
	private Color findColorForCondition(int index,
			Map<Object, Object> mapIntraColor) {
		Color toRet = null;
		if (mapIntraColor != null) {
			toRet = (Color) mapIntraColor.values().toArray()[index];
		}

		return toRet;
	}

}
