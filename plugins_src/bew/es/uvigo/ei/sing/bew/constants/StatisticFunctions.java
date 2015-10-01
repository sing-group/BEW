package es.uvigo.ei.sing.bew.constants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import es.uvigo.ei.sing.bew.exceptions.TooMuchDataException;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.util.CopyDirectory;
import es.uvigo.ei.sing.r.RengineFactory;

/**
 * This class is responsible for calculating the statistical functions.
 * 
 * @author Gael P�rez Rodr�guez
 * 
 */
public final class StatisticFunctions {

	// Constants with the type of the functions
	public final static String GRUBBS = "Grubbs";
	public final static String BONFERRONI = "Bonferroni";
	public final static String KOLMOGOROV = "Kolmogorov-Smirnov";
	public final static String SAPHIRO = "Shapiro";
	public final static String LILLIE = "Lilliefors";
	public final static String LEVENE = "Levene";
	public final static String BARTLETT = "Bartlett";
	public final static String BROWN = "Brown-Forsythe";
	public final static String KRUSKAL = "Kruskal-Wallis";
	public final static String TTEST = "t-test";
	public final static String ANOVA = "Anova (One way)";

	// REngine, can reuse it in the class.
	private final static Rengine ENGINE;

	static {
		// Initialize REngine
		ENGINE = RengineFactory.getEngine();

		// Need to load R packages the first time
		loadAllPackages();
	}

	/**
	 * Method to convert the selected data from the Method table in a String.
	 * 
	 * @param condMeasur
	 *            Map with the selected conditions and values.
	 * @param numSelectedRows
	 *            Integer with the number of rows selected.
	 * @return String with all the selected values in the table.
	 */
	public static String calculateStringDataFromTable(
			Map<Object, List<Object>> condMeasur, int numSelectedRows) {
		// Obtain the selected values in the Table
		List<List<Object>> values = new ArrayList<List<Object>>(
				condMeasur.values());

		// String with the data of the selected rows in the method table
		String ret = "";
		String aux = "";
		String value = "";
		Double doubleAux;
		// Create RArray with the values. All data in the same array
		for (List<Object> list : values) {
			for (Object o : list) {
				// If there are a NaN or other String value present in the
				// measurements...
				try {
					value = o.toString();
					value = FunctionConstants.replaceCommas(value);

					// If exception is not thrown then assign the value
					doubleAux = Double.parseDouble(value);
					if (!doubleAux.isNaN()) {
						aux = value;
					} else {
						// R use NA not NaN
						aux = "NA";
					}
				} catch (NumberFormatException e) {
					// NA present
					aux = "NA";
				}

				if (ret.length() == 0)
					ret = ret.concat(aux);
				else
					ret = ret.concat("," + aux);
			}
		}

		return ret;
	}

	/**
	 * Method to convert the selected conditions from the Method table in a
	 * String.
	 * 
	 * @param condMeasur
	 *            Map with the selected conditions and values.
	 * @param numSelectedRows
	 *            Integer with the number of rows selected.
	 * @return String with all the selected conditions in the table.
	 */
	public static String calculateStringConditionsFromTable(
			Map<String, List<Object>> condMeasur, int numSelectedRows) {
		// String with the conditions of the selected rows in the method table
		String ret = "";

		// Obtain the selected values in the Table
		Set<String> keys = condMeasur.keySet();

		// Create iterator to go over the Set
		Iterator<String> ite = keys.iterator();
		while (ite.hasNext()) {
			if (ret.equals(""))
				ret = ret.concat(ite.next());
			else
				ret = ret.concat("," + ite.next());
		}

		return ret;
	}

	/**
	 * Method to create the legend for the statistic pop-up dialog.
	 * 
	 * @param condMeasur
	 *            Map with the selected conditions and values.
	 * @param numSelectedRows
	 *            Integer with the number of rows selected.
	 * @return String with the legend.
	 * @throws TooMuchDataException
	 *             If the user select more than 702 rows in the table.
	 */
	public static String createStringConditionsFromTable(
			Map<Object, List<Object>> condMeasur, int numSelectedRows)
			throws TooMuchDataException, NullPointerException {
		// 'A' in ASCII
		int ascii = 65;
		String condition;

		// String with the conditions of the selected rows in the method table
		String ret = "";

		// Obtain the selected values in the Table
		Set<Object> keys = condMeasur.keySet();

		int n = 0;
		// Alphabet has 25 characters, you can combine individual and pairs
		// 26*26+26 = 702 row
		// max.
		if (keys.size() > 702) {
			throw new TooMuchDataException();
		}
		// Warning the user when there are a lot of data and conditions selected
		// (75 rows)
		else if (keys.size() > 75) {
			Object[] options = { I18n.get("yes"), "No" };
			n = JOptionPane.showOptionDialog(null, I18n.get("lotDataCond"),
					I18n.get("sure"), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		}

		// If user accept the message or the rows are < 75
		if (n == 0) {
			String auxConcat = "";
			int index = 0;
			for (int i = 0; i < keys.size(); i++) {
				condition = "'" + auxConcat + Character.toString((char) ascii)
						+ "'";
				if (ret.equals(""))
					ret = ret.concat(condition);
				else
					ret = ret.concat("," + condition);

				ascii++;
				if (ascii > 90) {
					ascii = 65;
					// If we go over all letters, we concatenate AA,
					// AB...
					auxConcat = Character.toString((char) (ascii + index));

					index++;
				}
			}
		} else {
			// Return nothing
			throw new NullPointerException();
		}

		return ret;
	}

	/**
	 * Method to install all the necessary packages in R to execute the BEW
	 * statistical functions.
	 */
	public static void installAllPackages() {
		String packagePath;
		int os = FunctionConstants.getOS();
		if (os == 0) {
			// Install Outliers
			packagePath = StatisticFunctions.class.getResource(
					"/res/Windows/outliers_0.14.zip").getPath();
			installPackages(new String[] { packagePath }, 0);

			// Install nortest
			packagePath = StatisticFunctions.class.getResource(
					"/res/Windows/nortest_1.0-2.zip").getPath();
			installPackages(new String[] { packagePath }, 0);

			// Install multcomp
			installPackages(getMultcomp(0), 0);

			// Install car
			installPackages(getCar(0), 0);

			// Install fBasics
			installPackages(getFBasics(0), 0);

			// Install R2HTML
			packagePath = StatisticFunctions.class.getResource(
					"/res/Windows/R2HTML_2.2.1.zip").getPath();
			installPackages(new String[] { packagePath }, 0);
		} else if (os == 1) {
			// Install Outliers
			packagePath = StatisticFunctions.class.getResource(
					"/res/Linux/outliers_0.14.tar.gz").getPath();
			installPackages(new String[] { packagePath }, 1);

			// Install nortest
			packagePath = StatisticFunctions.class.getResource(
					"/res/Linux/nortest_1.0-2.tar.gz").getPath();
			installPackages(new String[] { packagePath }, 1);

			// Install multcomp
			installPackages(getMultcomp(1), 1);

			// Install car
			installPackages(getCar(1), 1);

			// Install fBasics
			installPackages(getFBasics(1), 1);

			// Install R2HTML
			packagePath = StatisticFunctions.class.getResource(
					"/res/Linux/R2HTML_2.2.1.tar.gz").getPath();
			installPackages(new String[] { packagePath }, 1);
		} else {
			// Install Outliers
			packagePath = StatisticFunctions.class.getResource(
					"/res/Mac/outliers_0.14.tgz").getPath();
			installPackages(new String[] { packagePath }, 1);

			// Install nortest
			packagePath = StatisticFunctions.class.getResource(
					"/res/Mac/nortest_1.0-2.tgz").getPath();
			installPackages(new String[] { packagePath }, 2);

			// Install multcomp
			installPackages(getMultcomp(1), 2);

			// Install car
			installPackages(getCar(1), 2);

			// Install fBasics
			installPackages(getFBasics(1), 2);

			// Install R2HTML
			packagePath = StatisticFunctions.class.getResource(
					"/res/Mac/R2HTML_2.2.1.tgz").getPath();
			installPackages(new String[] { packagePath }, 2);
		}

		loadAllPackages();
	}

	/**
	 * Method to load in R all the previous installed packages for BEW.
	 */
	private static void loadAllPackages() {
		// Refresh and load all packages
		ENGINE.eval("update.packages");
		// Load all libraries in R
		// engine.eval("lapply(.packages(all.available = TRUE), function(xx) library(xx,     character.only = TRUE))");

		// Activate packages
		// Default R packages
		ENGINE.eval("library('stats')");
		ENGINE.eval("library('graphics')");
		// car depends packages
		ENGINE.eval("library('MASS')");
		ENGINE.eval("library('nnet')");
		// multcomp depends packages
		ENGINE.eval("library('mvtnorm')");
		ENGINE.eval("library('splines')");
		ENGINE.eval("library('survival')");
		// fBasics depends packages
		ENGINE.eval("library('methods')");
		ENGINE.eval("library('timeDate')");
		ENGINE.eval("library('timeSeries')");

		ENGINE.eval("library('outliers')");
		ENGINE.eval("library('nortest')");
		ENGINE.eval("library('multcomp')");
		ENGINE.eval("library('car')");
		ENGINE.eval("library('fBasics')");
		ENGINE.eval("library('R2HTML')");
	}

	/**
	 * Installs input packages.
	 * 
	 * @param packagePath
	 *            String[] with the path of the packages.
	 */
	private static void installPackages(String[] packagePath, int os) {
		// Go over array
		String toEval = "";
		String path;
		for (String p : packagePath) {
			path = p;
			if (os == 0) {
				// Remove first bar '/' from path
				path = p.substring(1);
			}

			toEval = "install.packages(\"" + path + "\",repos=NULL)";
			ENGINE.eval(toEval);
		}
	}

	/**
	 * Method to return the path of the fBasics packages.
	 * 
	 * @return String[] with the paths of the different packages.
	 */
	private static String[] getFBasics(int os) {
		String fBasics;
		String timeDate;
		String timeSeries;
		String stableDist;
		if (os == 0) {
			fBasics = StatisticFunctions.class.getResource(
					"/res/Windows/fBasics_2160.85.zip").getPath();
			timeDate = StatisticFunctions.class.getResource(
					"/res/Windows/timeDate_2160.97.zip").getPath();
			timeSeries = StatisticFunctions.class.getResource(
					"/res/Windows/timeSeries_3000.96.zip").getPath();
			stableDist = StatisticFunctions.class.getResource(
					"/res/Windows/stabledist_0.6-5.zip").getPath();
		} else if (os == 1) {
			fBasics = StatisticFunctions.class.getResource(
					"/res/Linux/fBasics_2160.85.tar.gz").getPath();
			timeDate = StatisticFunctions.class.getResource(
					"/res/Linux/timeDate_2160.97.tar.gz").getPath();
			timeSeries = StatisticFunctions.class.getResource(
					"/res/Linux/timeSeries_3000.96.tar.gz").getPath();
			stableDist = StatisticFunctions.class.getResource(
					"/res/Linux/stabledist_0.6-5.tar.gz").getPath();
		} else {
			fBasics = StatisticFunctions.class.getResource(
					"/res/Mac/fBasics_3010.86.tgz").getPath();
			timeDate = StatisticFunctions.class.getResource(
					"/res/Mac/timeDate_3010.98.tgz").getPath();
			timeSeries = StatisticFunctions.class.getResource(
					"/res/Mac/timeSeries_3010.97.tgz").getPath();
			stableDist = StatisticFunctions.class.getResource(
					"/res/Mac/stabledist_0.6-6.tgz").getPath();
		}

		String[] paths = { timeDate, timeSeries, stableDist, fBasics };

		return paths;
	}

	/**
	 * Method to return the path of the car packages.
	 * 
	 * @return String[] with the paths of the different packages.
	 */
	private static String[] getCar(int os) {
		String mass;
		String nnet;
		String car;
		if (os == 0) {
			mass = StatisticFunctions.class.getResource(
					"/res/Windows/MASS_7.3-26.zip").getPath();
			nnet = StatisticFunctions.class.getResource(
					"/res/Windows/nnet_7.3-6.zip").getPath();
			car = StatisticFunctions.class.getResource(
					"/res/Windows/car_2.0-16.zip").getPath();
		} else if (os == 1) {
			mass = StatisticFunctions.class.getResource(
					"/res/Linux/MASS_7.3-26.tar.gz").getPath();
			nnet = StatisticFunctions.class.getResource(
					"/res/Linux/nnet_7.3-6.tar.gz").getPath();
			car = StatisticFunctions.class.getResource(
					"/res/Linux/car_2.0-16.tar.gz").getPath();
		} else {
			mass = StatisticFunctions.class.getResource(
					"/res/Mac/MASS_7.3-31.tgz").getPath();
			nnet = StatisticFunctions.class.getResource(
					"/res/Mac/nnet_7.3-8.tgz").getPath();
			car = StatisticFunctions.class.getResource(
					"/res/Mac/car_2.0-19.tgz").getPath();
		}

		String[] paths = { mass, nnet, car };

		return paths;
	}

	/**
	 * Method to return the path of the multcomp packages.
	 * 
	 * @return String[] with the paths of the different packages.
	 */
	private static String[] getMultcomp(int os) {
		String survival;
		String mvtnorm;
		String multcomp;
		if (os == 0) {
			survival = StatisticFunctions.class.getResource(
					"/res/Windows/survival_2.37-4.zip").getPath();
			mvtnorm = StatisticFunctions.class.getResource(
					"/res/Windows/mvtnorm_0.9-9994.zip").getPath();
			multcomp = StatisticFunctions.class.getResource(
					"/res/Windows/multcomp_1.2-17.zip").getPath();
		} else if (os == 1) {
			survival = StatisticFunctions.class.getResource(
					"/res/Linux/survival_2.37-4.tar.gz").getPath();
			mvtnorm = StatisticFunctions.class.getResource(
					"/res/Linux/mvtnorm_0.9-9994.tar.gz").getPath();
			multcomp = StatisticFunctions.class.getResource(
					"/res/Linux/multcomp_1.2-17.tar.gz").getPath();
		} else {
			survival = StatisticFunctions.class.getResource(
					"/res/Mac/survival_2.37-7.tgz").getPath();
			mvtnorm = StatisticFunctions.class.getResource(
					"/res/Mac/mvtnorm_0.9-9997.tgz").getPath();
			multcomp = StatisticFunctions.class.getResource(
					"/res/Mac/multcomp_1.3-2.tgz").getPath();
		}

		String[] paths = { mvtnorm, survival, multcomp };

		return paths;
	}

	/**
	 * Method to execute a statistical function and its summary in R. This
	 * method save the output inside a file in temporary folder.
	 * 
	 * @param fileName
	 *            Name of the file to save the results.
	 * @param function
	 *            R function to execute.
	 * @param summary
	 *            True if the user wants to do a summary of the function, false
	 *            otherwise.
	 * @param isAnova
	 *            True if the user wants to generate some extra information for
	 *            Anova functions.
	 * @return String with the path of the temporary file.
	 * @throws NullPointerException
	 *             If something goes wrong and R can't execute the function.
	 * @throws IOException
	 */
	private static String executeFunction(String fileName, String function,
			boolean summary, boolean isAnova) throws NullPointerException,
			IOException {
		// Create temporary File
		String filePath = Files.createTempDirectory("").toString() + "/"
				+ fileName;
		filePath = filePath.replace("\\", "/");

		// Do function and save Output in File
		ENGINE.eval("calc <- " + function);
		if (ENGINE.eval("calc <- " + function) != null) {
			ENGINE.eval("res <- capture.output(calc)");
			// If the operation returns a String[] (type 34) is correct
			if (ENGINE.eval("res").getType() == REXP.XT_ARRAY_STR) {
				ENGINE.eval("cat(res, file=\"" + filePath
						+ "\", append=TRUE,sep=\"\\n\")");
				// If the user want a summary of the function
				if (summary) {
					ENGINE.eval("summa <- summary(" + function + ")");
					ENGINE.eval("res <- capture.output(summa)");
					ENGINE.eval("cat(res, file=\"" + filePath
							+ "\", append=TRUE,sep=\"\\n\")");
				}
				if (isAnova) {
					ENGINE.eval("tuk <- TukeyHSD(" + function + ")");
					ENGINE.eval("res <- capture.output(tuk)");
					ENGINE.eval("cat(res, file=\"" + filePath
							+ "\", append=TRUE,sep=\"\\n\")");
				}
			}
		} else {
			throw new NullPointerException();
		}

		return filePath;
	}

	/**
	 * Method to calculate the input function in R with the low interface rJava.
	 * 
	 * @param rArray
	 *            String with the data.
	 * @param rCond
	 *            String with the conditions (factor).
	 * @param fileName
	 *            String with the name of the output temporary file.
	 * @param function
	 *            Function to execute in R.
	 * @param summary
	 *            Boolean to indicate if user wants a summary of the function.
	 * @param isAnova
	 *            Boolean to indicate if user wants extra information for Anova
	 *            function.
	 * @return File with the result of the function
	 * @throws IOException
	 */
	public static File createStatisticFunction(String rArray, String rCond,
			String fileName, String function, boolean summary, boolean isAnova)
			throws NullPointerException, IOException {
		// Prepare Data to use in function
		ENGINE.eval("data <- c(" + rArray + ")");
		ENGINE.eval("cond <- c(" + rCond + ")");
		ENGINE.eval("frame <- data.frame(Cond=gl(length(cond), length(data)/length(cond), label=cond), Data=data)");
		// Execute R function
		String filePath = executeFunction(fileName, function, summary, isAnova);

		// Create File with R results
		File file = new File(filePath);

		return file;
	}

	/**
	 * Method to create a boxplot in R of the previous function. This method
	 * need createStatisticFunction() first.
	 * 
	 * @param imageFile
	 *            String with the name of the image file with the plot.
	 * @return The file with the plot
	 * @throws IOException
	 */
	public static File createStatisticBoxplot(String imageFile)
			throws IOException {
		// Create temporary File
		String filePath = Files.createTempDirectory("").toString() + "/"
				+ imageFile;
		filePath = filePath.replace("\\", "/");

		// Call R Code
		ENGINE.eval("newCond <- gsub('#','\n',frame$Cond)");
		ENGINE.eval("png('" + filePath + "')");
		ENGINE.eval("myPlot <- boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
		ENGINE.eval("dev.off()");

		File file = new File(filePath);

		return file;
	}

	/**
	 * Method to create a qqnorm with a qqline in R of the previous function.
	 * This method need createStatisticFunction() first.
	 * 
	 * @param imageFile
	 *            String with the name of the image file with the plot.
	 * @param title
	 *            String with the title of the plot
	 * @return The file with the plot
	 * @throws IOException
	 */
	public static File createStatisticQqnorm(String imageFile, String title)
			throws IOException {
		// Create temporary File
		String filePath = Files.createTempDirectory("").toString() + "/"
				+ imageFile;
		filePath = filePath.replace("\\", "/");

		// Call R Code
		ENGINE.eval("png('" + filePath + "')");
		ENGINE.eval("myPlot <- qqnorm(frame$Data, main=\"" + title
				+ "\", las=1)");
		ENGINE.eval("myPlot2 <- qqline(frame$Data)");
		ENGINE.eval("dev.off()");

		File file = new File(filePath);

		return file;
	}

	/**
	 * Method to create a TukeyHSD in R of the input function.
	 * 
	 * @param imageFile
	 *            String with the name of the image file with the plot.
	 * @param function
	 *            String with the function in R we want to calculate the plot
	 * @return The file with the plot
	 * @throws IOException
	 */
	public static File createStatisticTukey(String imageFile, String function)
			throws IOException {
		// Create temporary File
		String filePath = Files.createTempDirectory("").toString() + "/"
				+ imageFile;
		filePath = filePath.replace("\\", "/");

		// Call R Code
		ENGINE.eval("func <- " + function);
		ENGINE.eval("resTukey <- TukeyHSD(func)");
		// Saving graphic report of tukeyHSD function
		ENGINE.eval("png('" + filePath + "')");
		ENGINE.eval("plot(resTukey,cex.axis=0.9, las=1)");
		ENGINE.eval("dev.off()");

		File file = new File(filePath);

		return file;
	}

	/**
	 * Creates the HTML report for the statistics in a temporary folder and add
	 * the path to the input Statistic object.
	 * 
	 * @param test
	 *            Statistic object.
	 * @param path
	 *            Path with the temporary files previously for the Statistic.
	 * @return String with the target path of the report.
	 */
	public static String createTestReportInTemp(Statistic test, String filePlot) {
		String dirPath = "";
		try {
			String fileName = test.getNameClipboard();

			dirPath = Files.createTempDirectory(test.getName() + "_")
					.toString();

			// Parse templateCSS and dirPath
			dirPath = dirPath.replace("\\", "/");

			// Generate report file
			ENGINE.eval("HTMLStart(outdir=\""
					+ dirPath
					+ "\", file=\""
					+ fileName
					+ "\", extension=\"html\", echo=FALSE, HTMLframe=FALSE, autobrowse=FALSE)");

			// Change CSS for ours
			ENGINE.eval("HTML(\"<div>\")");

			ENGINE.eval("HTML.title(\"" + test.getName() + "\", HR=1)");

			// Execute function
			switch (test.getName()) {
			case GRUBBS:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/BoxPlot.png"));
				ENGINE.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case KOLMOGOROV:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/QQNorm.png"));
				ENGINE.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case SAPHIRO:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/QQNorm.png"));
				ENGINE.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case LILLIE:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/QQNorm.png"));
				ENGINE.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case LEVENE:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTML(summa)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/BoxPlot.png"));
				ENGINE.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case BARTLETT:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/BoxPlot.png"));
				ENGINE.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case KRUSKAL:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/BoxPlot.png"));
				ENGINE.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case TTEST:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/BoxPlot.png"));
				ENGINE.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			case ANOVA:
				ENGINE.eval("HTML(calc)");
				ENGINE.eval("HTML(summa)");
				ENGINE.eval("HTML(tuk)");
				ENGINE.eval("HTMLhr()");

				ENGINE.eval("HTML.title(\"" + test.getType()
						+ " - TukeyHSD\", HR=1)");
				// Copy plot file previously created
				Files.copy(Paths.get(filePlot),
						Paths.get(dirPath + "/Tukey.png"));
				ENGINE.eval("HTMLInsertGraph('Tukey.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				ENGINE.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				ENGINE.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				ENGINE.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				ENGINE.eval("HTML(matrix)");
				break;
			default:
				break;
			}

			ENGINE.eval("HTML(\"</div>\")");
			ENGINE.eval("HTMLStop()");
		} catch (IOException e) {
//			System.out.println("Cannot create temp files");
		}

		return dirPath;
	}

	/**
	 * Creates the HTML report for the input statistic and save it in the input
	 * folder. This method will be called when the user saves the statistic if
	 * the check box was false during the creation.
	 * 
	 * @param test
	 *            Statistic object.
	 * 
	 * @return Target path for the HTML report.
	 */
	public static String createTestReportInFolder(Statistic test) {
		Rengine engine = RengineFactory.getEngine();
		String aux = "";
		String dirPath = "";
		try {
			String fileName = test.getNameClipboard();

			dirPath = Files.createTempDirectory(test.getName() + "_")
					.toString();

			// Parse templateCSS and dirPath
			dirPath = dirPath.replace("\\", "/");

			// Generate report file
			engine.eval("HTMLStart(outdir=\""
					+ dirPath
					+ "\", file=\""
					+ fileName
					+ "\", extension=\"html\", echo=FALSE, HTMLframe=FALSE, autobrowse=FALSE)");

			// Change CSS for ours
			engine.eval("HTML(\"<div>\")");

			engine.eval("HTML.title(\"" + test.getName() + "\", HR=1)");

			// Get data
			engine.eval("data <- c(" + test.getResult() + ")");

			// Get conditions
			String[] conditions = test.getCond();
			for (String s : conditions) {
				if (aux.isEmpty())
					aux += s;
				else
					aux += "," + s;
			}
			engine.eval("cond <- c(" + aux + ")");
			engine.eval("frame <- data.frame(Cond=gl(length(cond), length(data)/length(cond), label=cond), Data=data)");

			// Execute function
			switch (test.getName()) {
			case GRUBBS:
				engine.eval("HTML(grubbs.test(frame$Data))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				engine.eval("newCond <- gsub('#','\n',frame$Cond)");
				engine.eval("png('" + dirPath + "/BoxPlot.png')");
				engine.eval("boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case KOLMOGOROV:
				engine.eval("HTML(ksnormTest(frame$Data))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				engine.eval("png('" + dirPath + "/QQNorm.png')");
				engine.eval("qqnorm(frame$Data, main=\"" + test.getType()
						+ " - QNorm\", las=1)");
				engine.eval("qqline(frame$Data)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case SAPHIRO:
				engine.eval("HTML(shapiroTest(frame$Data))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				engine.eval("png('" + dirPath + "/QQNorm.png')");
				engine.eval("qqnorm(frame$Data, main=\"" + test.getType()
						+ " - QNorm\", las=1)");
				engine.eval("qqline(frame$Data)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case LILLIE:
				engine.eval("HTML(lillie.test(frame$Data))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(" + test.getType() + " - QNorm, HR=1)");
				engine.eval("png('" + dirPath + "/QQNorm.png')");
				engine.eval("qqnorm(frame$Data, main=\"" + test.getType()
						+ " - QNorm\", las=1)");
				engine.eval("qqline(frame$Data)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('QQNorm.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case LEVENE:
				engine.eval("HTML(leveneTest(frame$Data, frame$Cond))");
				engine.eval("HTML(summary(leveneTest(frame$Data, frame$Cond)))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				engine.eval("newCond <- gsub('#','\n',frame$Cond)");
				engine.eval("png('" + dirPath + "/BoxPlot.png')");
				engine.eval("boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case BARTLETT:
				engine.eval("HTML(bartlett.test(frame$Data~frame$Cond))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				engine.eval("newCond <- gsub('#','\n',frame$Cond)");
				engine.eval("png('" + dirPath + "/BoxPlot.png')");
				engine.eval("boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case KRUSKAL:
				engine.eval("HTML(kruskal.test(frame$Data, frame$Cond))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				engine.eval("newCond <- gsub('#','\n',frame$Cond)");
				engine.eval("png('" + dirPath + "/BoxPlot.png')");
				engine.eval("boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case TTEST:
				engine.eval("HTML(t.test(frame$Data~frame$Cond))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - Boxplot\", HR=1)");
				engine.eval("newCond <- gsub('#','\n',frame$Cond)");
				engine.eval("png('" + dirPath + "/BoxPlot.png')");
				engine.eval("boxplot(frame$Data ~ newCond, cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('BoxPlot.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			case ANOVA:
				engine.eval("HTML(aov(frame$Data~frame$Cond))");
				engine.eval("HTML(summary(aov(frame$Data~frame$Cond)))");
				engine.eval("HTML(TukeyHSD(aov(frame$Data~frame$Cond)))");

				engine.eval("HTMLhr()");
				engine.eval("HTML.title(\"" + test.getType()
						+ " - TukeyHSD\", HR=1)");

				engine.eval("resTukey <- TukeyHSD(aov(frame$Data~frame$Cond))");
				engine.eval("png('" + dirPath + "/Tukey.png')");
				engine.eval("plot(resTukey,cex.axis=0.9, las=1)");
				engine.eval("dev.off()");
				engine.eval("HTMLInsertGraph('Tukey.png',file='" + dirPath
						+ "/" + fileName + ".html')");

				engine.eval("matrix <- matrix(c(" + putLegendInHtml(test)
						+ "),ncol=2,byrow=TRUE)");
				engine.eval("colnames(matrix) <- c(\"Plot Conditions\", \"Real Conditions\")");
				engine.eval("rownames(matrix) <- c(seq(1,nrow(matrix)))");
				engine.eval("HTML(matrix)");
				break;
			default:
				break;
			}
			engine.eval("HTML(\"</div>\")");
			engine.eval("HTMLStop()");

			// Set HTML for next saves
			test.setTempHTMLDir(dirPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dirPath;
	}

	/**
	 * Creates a HTML report for the input Statistic. If the user checked the
	 * box during the statistic creation this method only copy the temporary
	 * files to the input path.
	 * 
	 * @param test
	 *            Statistic object.
	 * @param path
	 *            Target path.
	 * @return False if something goes wrong, true otherwise.
	 */
	public static boolean createTestReport(Statistic test, String path)
			throws NullPointerException {
		boolean toRet = true;

		String dirPath = path + "/" + test.getNameClipboard();
		try {
			// Copy from temporary folder to user selected folder
			File[] dirToCopy = new File(test.getTempHTMLDir()).listFiles();

			// If someone delete temporary files...
			if (dirToCopy.length < 4) {
				throw new NullPointerException();
			}

			// Create new folder for the report
			File dirHtml = new File(dirPath);

			if (!dirHtml.exists()) {
				dirHtml.mkdir();
			}

			dirPath = dirPath.replace("\\", "/");

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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (File toCopy : dirToCopy) {
				// Copy all except R2HTML file
				if (!toCopy.getName().endsWith(".html")
						&& !toCopy.getName().endsWith(".css")
						&& !toCopy.getName().endsWith(".gif")) {
					Files.copy(Paths.get(toCopy.getAbsolutePath()),
							Paths.get(dirPath + "/" + toCopy.getName()),
							StandardCopyOption.REPLACE_EXISTING);
				} else if (toCopy.getName().endsWith(".html")) {
					File report = new File(dirPath + "/" + toCopy.getName());

					// Write report in file
					Files.write(report.toPath(), generatePrettyReport(toCopy));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			toRet = false;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toRet;
	}

	/**
	 * Transform the old statistic report in a new pretty report, using the same
	 * css that the generic html report from BEW.
	 * 
	 * @param oldReport
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static byte[] generatePrettyReport(File oldReport)
			throws IOException, URISyntaxException {
		// Parse old html
		Document doc = Jsoup.parse(oldReport, "UTF-8", "");

		// Get statistic content
		String content = doc.select("div").first().html();

		// Delete leftovers if neccesary
		content = content.replaceAll("(?s)<xmp.*</xmp>", "");

		// Get statistic html template
		File template = new File(StatisticFunctions.class
				.getResource("/files/StatisticReport.html").toString()
				.substring(5));
		Document newHtml = Jsoup.parse(template, "UTF-8");

		// Put the content from old file
		newHtml.getElementsByClass("col-md-8").first().append(content);

		return newHtml.outerHtml().getBytes();
	}

	/**
	 * Method to put the legend inside the statistic pop-up dialog. Only works
	 * if type is not equal to Kolmogorov, Shapiro and Lillie. (DEPRECATED)
	 * 
	 * @param type
	 *            Type of the function.
	 * @return True to show, false otherwise.
	 */
	public static boolean showLegend(String type) {
		// if (type.equals(KOLMOGOROV) || type.equals(SAPHIRO)
		// || type.equals(LILLIE))
		// return false;
		// else
		// return true;
		return true;
	}

	/**
	 * Method to set the legend table in the html report.
	 * 
	 * @param test
	 *            Statistic object.
	 * @return String with the legend.
	 */
	private static String putLegendInHtml(Statistic test) {
		String toRet = "";

		Set<Object> keys = test.getConditionMeasurements().keySet();
		String[] cond = test.getCond();
		Iterator<Object> ite = keys.iterator();

		int index = 0;
		while (ite.hasNext()) {
			if (toRet.isEmpty())
				toRet += cond[index] + ",";
			else
				toRet += "," + cond[index] + ",";
			toRet += ite.next();

			index++;
		}

		return toRet;
	}
}
