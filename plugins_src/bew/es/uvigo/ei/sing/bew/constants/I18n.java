package es.uvigo.ei.sing.bew.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores the text strings that are displayed in the application.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public final class I18n {

	private static Map<String, String> texts = new HashMap<String, String>();

	static {
		// ImportMethodSheet
		texts.put("importStructureTitle", "Invalid file structure!");
		texts.put("importStructure1", "The Method: ");
		texts.put("importStructure2",
				", has an invalid structure, please revise the xls file.");

		// ReportingOperation
		texts.put("reportingOpTitle", "Report created successfully!");
		texts.put("reportingOp",
				"The report has been successfully created at the following path: ");

		texts.put("dataTableCreationTitle", "Error during creation!");
		texts.put("dataTableCreation",
				"The table has too many values. Please, do the table smaller.");

		// UploadExpDialog
		texts.put("uploadExpDialogTitle", "Upload Experiment to BioFomics");
		texts.put("uploadExpDialogButton", "Upload");
		texts.put("uploadLoginMessage",
				"To upload a file you need to login with a valid account in BiofOmics.");
		texts.put("updloadErrorTitle", "Error uploading the file!");
		texts.put(
				"uploadError",
				"There was an unknown problem uploading the file. Please, upload the file again and if the problem persists contact with the administrators.");
		texts.put("updloadOkTitle", "Upload successfully!");
		texts.put("uploadOk1", "The Experiment: ");
		texts.put("uploadOk2", ", has been successfully uploaded to the user: ");
		texts.put("uploadBadLogin",
				"Invalid username or password, plese try again.");
		texts.put(
				"uploadServerError",
				"There has been a problem in the server, please try again. If the problem persists contact with the administrators.");
		texts.put(
				"uploadIDError",
				"BiofOmics was unable to obtain an ID for your experiment, please try again later. If the problem persists contact with the administrators.");

		// UpdateMetadataOperation
		texts.put("updateErrorTitle", "Error updating Metadata!");
		texts.put("updateOkTitle", "Success!");
		texts.put("updateOkBody", "The Metadata successfully updated.");
		texts.put("webServicesConnection",
				"Unable to reach the server, please check your internet connection.");

		// DownloadExpOperation
		texts.put("downloadErrorTitle", "Error downloading the file!");
		texts.put(
				"fileNotFound",
				"There isn't a file to download for this Experiment. Please verify through the web if the problem persists.");
		texts.put("downloadOkTitle", "Error downloading the file!");
		texts.put("downloadOkBody1", "The file ");
		texts.put("downloadOkBody2",
				" has been downloaded successfully in the following directory ");
		texts.put("downloadLoginMessage",
				"Anonymous users can list public experiments.");

		// DownloadExpDialog
		texts.put("listsErrorTitle", "Error listing Experiments!");
		texts.put(
				"unknownError",
				"An unknown error ocurred during the exection. Please try again later. If the problem persists send a message to the developers.");
		texts.put("authenticationError",
				"The user and password don't exist. Please, change them.");
		texts.put(
				"serverError",
				"BioFomics is under heavy load and could not perform the operation. Please, try again later. If the problem persists send a message to the developers.");
		texts.put(
				"getExpError",
				"Something went wrong during the operation, probably the data can not be displayed for this experiment.");
		texts.put("listsOkTitle", "Listing operation successful!");
		texts.put("listsOkBody1", "BioFomics lists ");
		texts.put("listsOkBody2", " Experiments for the input user.");

		// Show progress bar
		texts.put("ShowProgressBarTitle", "Please be patient...");
		texts.put("ShowProgressBarWorking", "Working...");

		// VisualizeBioExperiment
		texts.put("VisualizeBioExperimentTitle",
				"Visualize BioFomics Experiment");
		texts.put("VisualizeBioExperimentClose", "Close");
		texts.put("VisualizeBioExperimentName", "Name");
		texts.put("VisualizeBioExperimentInter", "Is InterExperiment?");
		texts.put("VisualizeBioExperimentMail", "Email");
		texts.put("VisualizeBioExperimentDate", "Date");
		texts.put("VisualizeBioExperimentOrg", "Organization");
		texts.put("VisualizeBioExperimentAuthors", "Authors");
		texts.put("VisualizeBioExperimentPubli", "Publications");
		texts.put("VisualizeBioExperimentNotes", "Notes");

		// UpdateMainFilesDialog
		texts.put("DownloadMainFilesTitle", "Update Metadata from BioFomics");
		texts.put("DownloadMainFilesOptions", "Update options");
		texts.put("DownloadMainFilesMethods", "Update Methods");
		texts.put("DownloadMainFilesConditions", "Overwrite Conditions");
		texts.put("DownloadMainFilesValues", "Overwrite values");
		texts.put("DownloadMainFilesUpdate", "Update");
		texts.put("DownloadMainFilesText",
				"Select one of the boxes to get information about them.");
		texts.put(
				"DownloadMainFilesTextCond",
				"If you check this box, the operation will delete your created Conditions. It's usefull if you have a lot of useless Conditions that you don't want.");
		texts.put(
				"DownloadMainFilesTextVal",
				"If you check this box, the operation will delete your created Condition values. It's usefull if you have a lot of useless values that you don't want.");
		texts.put(
				"DownloadMainFilesTextMet",
				"If you check this box, the operation will delete your created Methods. It's usefull if you have a lot of useless Methods that you don't want.");

		// DownloadExpDialog
		texts.put("DownloadExpDialogTitle", "Experiment list from BioFomics");
		texts.put("DownloadExpDialogExpList", "Experiment list");
		texts.put("DownloadExpDialogSearch", "Search");
		texts.put("DownloadExpDialogList", "List");
		texts.put("DownloadExpDialogDownload", "Download");
		texts.put("DownloadExpDialogVisualize", "Visualize");

		// Login panel
		texts.put("LoginPanelLogin", "Login");
		texts.put("LoginPanelUser", "User");
		texts.put("LoginPanelPassword", "Password");
		texts.put("LoginPanelLogged", "You are logged as");

		// Manage methods dialog
		texts.put("methodCreate",
				"Please, revise the Method name, it cannot be empty.");
		texts.put("methodCreateTitle", "Invalid Method name!");
		texts.put("methodCreateOk", "The Method: ");
		texts.put("methodCreateOkTitle", "Method created successfully!");
		texts.put("methodCreateError",
				"There was an error creating the Method, please try again later.");
		texts.put("methodCreateErrorTitle", "Error creating Method!");

		// Manage condition dialog
		texts.put(
				"conditionCreate",
				"Please, revise the Condition name. Probably is empty or has invalid characters (=, \", _, _and_, #).");
		texts.put(
				"conditionValueCreate",
				"Please, if the Condition is nominal, introduce at least one Condition value.\n If you create one value, revise the forbbiden characters (=, \", _, _and_, #)");
		texts.put("conditionCreateTitle", "Invalid Condition!");
		texts.put("conditionCreateOk", "The Condition: ");
		texts.put("conditionCreateOk2",
				", was successfully created and is ready for use.");
		texts.put("conditionCreateOkTitle", "Condition created successfully!");
		texts.put("conditionCreateError",
				"There was an error creating the Condition, please try again later.");
		texts.put("conditionCreateErrorTitle", "Error creating Condition!");

		// Manage condition value dialog
		texts.put("valueCreate",
				"Please, select at least one Condition to create values.");
		texts.put(
				"valueCreateChars",
				"Revise the following errors: \n- There are incorrect characters (=, \", _, _and_, #) in the value/s.\n - You don't select any value/s.\n - If you are selecting antimicrobial agents you have to put their concentrations.\n");
		texts.put("valueCreateCharsTitle", "Invalid value/s!.");
		texts.put("valueCreateError",
				"There were some errors during the creation. Revise them:\n");
		texts.put("valueCreateErrorTitle", "Error creating value/s!");
		texts.put("valueCreateOk",
				"All values were created successfully and are ready for use.");
		texts.put("valueCreateOkTitle", "Value/s created successfully!");

		// Reporting Dialog
		texts.put("reportingExp",
				"Please, select at least one Experiment to do the report.");
		texts.put("reportingExpTitle", "Nothing selected!");
		texts.put("reportingPath",
				"Please, select a path to generate the report.");
		texts.put("reportingPathTitle", "Error!");

		// Dialog titles
		texts.put("aboutDialog", "About Dialog");
		texts.put("newMethodInExp", "New method in experiment");
		texts.put("statisticDialog", "Statistic Dialog");
		texts.put("compareExpsDialog", "Compare experiments dialog");
		texts.put("copyMethod?", "Do you want to copy an existed method?");
		texts.put("editingExpSetup", "Editing experiment setup");
		texts.put("editingMethod", "Editing a method");
		texts.put("plotDialog", "Creating a ");
		texts.put("selectMethodFromExp", "Select a method from one experiment");
		texts.put("selectTest", "Select statistic test");
		texts.put("selectPlot", "Select type of plot");
		texts.put("bewWizard", "BEW Wizard");
		texts.put("textPrev", "Text previewer");

		// Titled borders
		texts.put("methodAnalysis", "Method of analylis");
		texts.put("conditionsTest", "Conditions in test");
		texts.put("data", "Data");
		texts.put("methodName", "Method Name");
		texts.put("methodView", "Methods view");
		texts.put("methodTable", "Method table");
		texts.put("methods", "Methods");
		texts.put("conditions", "Conditions");
		texts.put("info", "Information");
		texts.put("output", "Output View");
		texts.put("interExperiment", "Inter-Experiment information");
		texts.put("experimentalSetup", "Experimental setup");
		texts.put("constantConditions", "Constant conditions");
		texts.put("basicInfo", "Basic information");
		texts.put("plotDesign", "Plot design");
		texts.put("plotPreview", "Plot preview");
		texts.put("pickColor", "Pick a color for the experiment");

		// JLabels
		texts.put("condsNumber", "Number of Conditions:");
		texts.put("methodUnits", "Method Units: ");
		texts.put("introduceName", "Introduce a name*:");
		texts.put("introduceOrg", "Introduce an organization*:");
		texts.put("introduceContact", "Introduce a contact*:");
		texts.put("experimentName", "Experiment name:*");
		texts.put("authors", "Author(s):");
		texts.put("organization", "Institution/Laboratory:*");
		texts.put("emailContact", "Email contact:*");
		texts.put("date", "Date (dd/MM/yyyy):");
		texts.put("publication", "Publication (PMID):");
		texts.put("notes", "Notes:");
		texts.put("selectMethod", "Select method: ");
		texts.put("selectedData", "Selected data");
		texts.put("selectedCond", "Selected conditions");
		texts.put("selectExperiment", "Select experiment:");
		texts.put("selectColor", "Select a color:");
		texts.put("changeColor", "Change experiment color:");
		texts.put("experimentsFound", "Number of experiments found: ");
		texts.put("introduceNumberExps", "Introduce the number of experiments:");
		texts.put("constantCondProperties", "Constant Condition Properties");
		texts.put("dataReplicates", "Number of data replicates");
		texts.put("combinationOfConds", "Combination of conditions in test");
		texts.put("properties", "Properties");
		texts.put("rInstallation",
				"BEW is installing some stuff on R application. Please, be patient!");

		// Buttons
		texts.put("newData", "New Data");
		texts.put("newDataCol", "New Data Column");
		texts.put("newCondSet", "New Condition Set");
		texts.put("newCondition", "New condition");
		texts.put("createDataTable", "Create data table");
		texts.put("add", "Add");
		texts.put("deleteData", "Delete Data");
		texts.put("deleteCondSet", "Delete Condition Set");
		texts.put("deleteCondition", "Delete condition");
		texts.put("deleteDataCol", "Delete Data Column");
		texts.put("delete", "Delete");
		texts.put("deletePage", "Delete page");
		texts.put("deleteRow", "Delete selected row");
		texts.put("nameCond", "Name Conditions");
		texts.put("finalize", "Finalize");
		texts.put("validate", "Validate");
		texts.put("test", "Test");
		texts.put("showPlot", "Show Plot");
		texts.put("cancel", "Cancel");
		texts.put("selectValues", "Select values");
		texts.put("createPlot", "Create plot");
		texts.put("createFields", "Create fields");
		texts.put("clickMe", "Click me!");
		texts.put("next", "Next");
		texts.put("previous", "Previous");
		texts.put("addMethod", "Add Method");
		texts.put("saveAs", "Save As...");
		texts.put("exit", "Exit");
		texts.put("yes", "Yes");
		texts.put("print", "Print");
		texts.put("saveTestHTML", "Save Test in HTML");

		// JMenu
		texts.put("help", "Help");
		texts.put("helpContents", "Help contents...");
		texts.put("about", "About BEW...");
		texts.put("close", "Close...");

		// Exceptions
		texts.put("noColors", "One or more experiments don't have a color.");
		texts.put("noColorsTitle", "Unselected colors!");

		texts.put("oneExp", "You must have two or more experiments to compare.");
		texts.put("oneExpTitle", "Cannot compare!");

		texts.put("differentMethods", "The selected methods must be the same.");
		texts.put("differentMethodsTitle", "Different methods!");

		texts.put("differentExp",
				"The selected intraExperiments must be different.");
		texts.put("differentExpTitle", "Same experiments!");

		texts.put("fieldsNotCover",
				"The following mandatory field is not cover: ");
		texts.put("fieldsNotCoverTitle", "Field not cover!");

		texts.put(
				"duplicateNameLoad",
				"There is already one experiment in the system with this name. Delete the created one to load the file.");
		texts.put("duplicateNameLoadTitle", "Invalid name!");

		texts.put("duplicateName",
				"The name for this experiment exists in the system. Change it.");
		texts.put("duplicateNameTitle", "Invalid name!");

		texts.put(
				"duplicateMNameLoad",
				"This Experiment has one or more methods with the same name. Revise the file first.");

		texts.put("duplicateMName",
				"The name for this method exists in the experiment. Change it.");

		texts.put("duplicateWSName",
				"The name for this Sheet is taken. Please change it.");

		texts.put("noSelection",
				"One or more methods don't have any data selected. Revise them.");
		texts.put("noSelectionTitle", "Nothing selected!");

		texts.put("errorReading",
				"Error reading file a file, try the operation again.");
		texts.put("errorReadingTitle", "Error reading!");

		texts.put("amountOfData",
				"Cannot proccess that amount of data. Please select less values.");
		texts.put("amountOfDataTitle", "Heavy amount of data selected!");

		texts.put("differentCond",
				"The selected methods don't have the same Conditions. Please revise them.");
		texts.put("differentCondTitle", "Different Conditions!");

		texts.put("selectMethodTest",
				"You must select a method and a one option in the tree to proceed.");
		texts.put("selectMethodTestTitle", "Invalid selection!");

		texts.put(
				"doTheTest",
				"You must calculate the statistical test for your selected data before exiting.\nPlese select some data from"
						+ " the table and press 'Test'.");
		texts.put("doTheTestTitle", "Test not calculated!");

		// XLS Columns
		texts.put("column0", "Experiment name:");
		texts.put("column1", "Author(s):");
		texts.put("column2", "Institution/Laboratory:");
		texts.put("column3", "Email contact:");
		texts.put("column4", "Date:");
		texts.put("column5", "Publication (PMID)");
		texts.put("column6", "Notes:");
		texts.put("column7", "BiofOmics ID:");

		// Generic Text
		texts.put("conditionSheetName", "Constant Conditions");
		texts.put("conditionName", "Condition name");
		texts.put("conditionValue", "Condition value");
		texts.put("conditionUnits", "Condition units");
		texts.put("expSetup", "Experiment Setup");
		texts.put("comboNameDefault", "Select method...");
		texts.put(
				"copyMethodText",
				"Select one method of this experiment if you want to copy its data. Otherwise select new Method to create an empty one.");
		texts.put("plotConditions", "Plot conditions");
		texts.put("realConditions", "Real conditions");
		texts.put("detailsInfo", "Details info");
		texts.put("descriptiveStat", "Descriptive statistics");

		// CreateDataSheetConfigurator
		texts.put(
				"rowsCreated",
				"There are already created rows in the table below. Do you want to create them again?");
		texts.put("overwrite", "Overwrite?");
		texts.put("above0",
				"You must write a number above than 0 for number of childs");
		texts.put("incorrectNumber", "Incorrect number!");
		texts.put("noRows",
				"Before creating data table, insert some conditions in condition table.");
		texts.put("noRowsTitle", "There are no rows!");
		texts.put(
				"reviseCondTable",
				"Revise the Conditions Table. You probably have one of this errors:\n"
						+ "- The number of childs must be greater than its predecessor\n"
						+ "- You didnt select any condition name\n");
		texts.put("reviseCondTableTitle", "Error while inserting data!");

		texts.put("nameRequired", "You must introduce a name for this method.");
		texts.put("nameRequiredTitle", "Introduce a new name!");
		texts.put("nullCondition",
				"There are some conditions empty. Revise them!");
		texts.put("nullConditionTitle", "Empty Conditions!");

		// DataToFile
		texts.put("fileSaved", "File saved with no errors.");
		texts.put("fileSavedTitle", "File saved!");
		texts.put("errorSaving", "Error saving the file.");
		texts.put("errorSavingTitle", "Error while saving!");

		// FileToData
		texts.put("loadFile", "Select a file to load");
		texts.put("noFile", "There is no file selected");
		texts.put("noFileTitle", "No file selected!");
		texts.put(
				"errorLoad",
				"Error loading the file. Probably the error is:\n - The structure of the file is incorrect.\n"
						+ " - The name for this experiment is not unique.\n"
						+ " - The name of the methods are not unique.\n"
						+ " - The file was modify outside the program. \n"
						+ "Please, revise all the previous dots.");
		texts.put("errorLoadTitle", "Error loading!");
		texts.put(
				"errorLoadXml",
				"Error loading the XML file. Plese revise file structure first, here is the BEW trace: \n");
		texts.put("selectFile", "Select a file to load...");

		// ImportDataSheetConfigurator
		texts.put("changeColumnName", "Change the header name of the column ");
		texts.put("noColumnsSelected",
				"No columns selected in the upper table. Please select one or more columns.");
		texts.put("noColumnsSelectedTitle", "Nothing selected");

		// AddConstantToExperiment
		texts.put(
				"constantCreated",
				"There is a Constant Condition created for this experiment. Do you want to overwrite it");

		// ConditionSheetConfigurator
		texts.put("incorrectValues",
				"You have introduced some incorrect values.");
		texts.put("incorrectValuesTitle", "Incorrect structure!");
		texts.put("correctStructure", "The structure is correct.");
		texts.put("correctStructureTitle", "Correct structure!");
		texts.put(
				"requiredFieldsMissing",
				"One of the fields with * are empty or the date is invalid (dd/MM/yyyy). Revise them:\n"
						+ "- \"Experiment Name\" \n"
						+ "- \"Institution/Laboratory\" \n"
						+ "- \"Email contact\" \n" + "- \"Date\" \n");
		texts.put("requiredFieldsMissingTitle", "Required are fields empty!");

		// DataSheetConfigurator
		texts.put(
				"sumValues",
				"There are more rows in the data table than the sum of the values in the number of childs.");
		texts.put("sumValuesTitle", "Incorrect number of childs!");
		texts.put("checkFileStructure",
				"There are some values incorrect. Check structure.");
		texts.put("childValues", "Revise the number of child values.");
		texts.put("childValuesTitle", "Values mismatch!");
		texts.put("child0s",
				"There are one or more 0s in the number of childs column.");
		texts.put("child0sTitle", "Zero detected!");

		// Wizard
		texts.put("createWizard", "Welcome to the file create wizard");
		texts.put("importWizard", "Welcome to the file import wizard");
		texts.put("saveBeforeQuit",
				"Do you want to save the experiment before quit?");
		texts.put("sure", "Are you sure?");
		texts.put(
				"constantNoCreated",
				"There is no Constant Condition created for this experiment. Are you sure you want to finalize?");
		texts.put(
				"expSetupAlredyCreated",
				"You have created an experimental setup before. If you press Yes, data will be overwrite. Do you want to continue?");
		texts.put("continue", "Continue?");

		// ConditionView
		texts.put("condProp", "Condition Properties: ");

		// ConstantConditionView
		texts.put("consCondProp", "Constant Condition Properties: ");

		// DataSerieView
		texts.put("dataSerieProp", "Data Series Properties");

		// MethodView
		texts.put("methodProp", "Method Properties: ");

		// PlotDialog && StaticsDialog
		texts.put("badSelection",
				"You cannot select these combination of Conditions and Data. Please revise it.");
		texts.put("badSelectionTitle", "Invalid Selection!");
		texts.put("operationError",
				"The program cannot execute the operation for your selection.");
		texts.put(
				"operationErrorS",
				"The program could not execute the operation, may be due to one of the following errors:\n - The selected values ​​and conditions are not compatible with the type of test. Please check the help for each test.\n - R packages are outdated. Please run the operation udpate R packages.\n");
		texts.put("operationErrorTitle", "Error in operation!");
		texts.put("noTimePresent",
				"The method need a condition 'time' to create this type of plot.");
		texts.put("noTimePresentTitle", "Condition time missing!");

		// SelectStatisticDialog information about different functions
		texts.put("Statistic", "Please select a statistic function...");
		texts.put(
				"Outliers",
				"An outlier is an observation that lies an abnormal distance from other values in a random sample from a population. Before abnormal observations can be singled out, it is necessary to characterize normal observations. There are two graphical techniques for identifying outliers, scatter plots and box plots, along with an analytic procedure for detecting outliers when the distribution is normal (Grubbs' Test).");
		texts.put(
				"Homogeneity",
				"The assumption of homogeneity of variance (homoscedasticity) is that the variance within each of the populations is equal. This is an assumption of analysis of variance (ANOVA).\n\n The Bartlett test can be used to verify that assumption, but it is sensitive to departures from normality. That is, if the samples come from non-normal distributions, then Bartlett's test may simply be testing for non-normality. Levene's test and the Brown–Forsythe test are alternatives to the Bartlett test that are less sensitive to departures from normality.");
		texts.put("varianceDescription", "");
		texts.put(
				"Normality",
				"The normal distribution is one of the most useful distributions for statistical analysis. It is de�?ned by just two statistics, the mean and the standard deviation. Since the theoretical normal distribution has no �?nite upper or lower limits, one must be careful about assuming the normality of data.\n\n Biological data are frequently not normally distributed, but are asymmetrical. Often, data peak well to the left with a long thin tail of high values. This is often found with random or clumped data, a common phenomenon in biology. With this kind of distribution the normal distribution statistics are clearly not appropriate.\n\n The real problem is whether our experimental data are even an approximate �?t to a normal distribution. This is easily checked with large samples. There should be roughly equal numbers of observations on either side of the mean. Things are more dif�?cult when, as usually, we have only a few samples. However, even here we can get clues. If the distribution is normal, there should be no relationship between the magnitude of the mean and its standard deviation.\n\n Some authors suggest plotting the cumulative frequency distribution of the sample. The easiest way to do this is to use a statistics package to give you a probability plot (often called a P-P plot). This graphs the actual cumulative frequency against the expected cumulative frequency assuming the data are normally distributed. If they are, the P-P plot will be a straight line. Any gross departures from this should be analyzed cautiously and perhaps a non-parametric test used.");
		texts.put(
				"Variance",
				"The essential difference between analysis with the t-test and analysis of variance is as follows: For the t-test, we calculate the background variability by addition. We add (pool) the sum of squares of the biological material calculated separately for the two columns of numbers. In the analysis of variance we use subtraction. We �?rst calculate the total variability of the data – including both background and intentional variation– and then subtract the variability from various sources until there is left a remainder (residual) which represents the background variability of the biological material. From this remainder we calculate a pooled s2 which has contributions from all the data.\n\n The analysis of variance actually relies on partitioning the total variability into how much has come from the various sources of variability in the experiment, including the “remainder�? referred to above, �?rst as sums of squares. The variance ratio (F) tells us whether, if at all, our experimental data cause variation in the numbers greater than the chance residual variation. An F of less than 1 indicates that the effect of our treatments/tests is even less than the variation that happens by chance. It is therefore negligible in biological terms, and only F values well in excess of 1 are worth checking for signi�?cance. So, the tabulated values of F are checked to see whether our calculated variance ratio (F) is suf�?ciently large for us to reject the null hypothesis, i.e. it is unlikely that we could have obtained such a large value of F by chance sampling of the material without any imposed variation applied by us.");
		texts.put(
				StatisticFunctions.TTEST,
				"The t-test enables us to determine the probability that two sample means could have been drawn from the same population of numbers, i.e. the two sample means are really two estimates of one and the same mean. Only if that probability is suf�?ciently low (often a 5% probability level) do we claim a “statistically signi�?cant difference�? between the two means, and that it is therefore unlikely that the two means could have been sampled from a single population of numbers, and therefore are much more likely to be estimates of means which truly differ in magnitude.");
		texts.put(
				StatisticFunctions.ANOVA,
				"The t-test can only be used to test differences between two means. When there are more than two means, it is possible to compare each mean with each other mean using many t-tests. But conducting such multiple t-tests can lead to severe complications (an increased chance of committing a type I error). For this reason, ANOVAs are useful in comparing (testing) three or more means (groups or variables) for statistical significance.\n\n The calculations of ANOVA can be characterized as computing a number of means and variances, dividing two variances and comparing the ratio to a handbook value to determine statistical significance. The simplest experiment suitable for ANOVA analysis is the completely randomized experiment with a single factor. The ANOVA tests the null hypothesis that samples in two or more groups are drawn from populations with the same mean values. To do this, two estimates are made of the population variance.\n\n The ANOVA produces an F-statistic, the ratio of the variance calculated among the means to the variance within the samples. If the group means are drawn from populations with the same mean values, the variance between the group means should be lower than the variance of the samples. A higher ratio therefore implies that the samples were drawn from populations with different mean values. ");
		texts.put(
				"nonParametricDescription",
				"Parametric statistics analyze the distances between numbers and the mean or each other, and �?t these distances to theoretical distributions (particularly the normal distribution). Estimates of the parameters which can be derived from the theoretical distribution (such as the s.e.d.m.) are used to calculate the probability of other distances (e.g. between means of two treatments) arising by chance sampling of the single theoretical population. Parametric methods are therefore essentially quantitative. The absolute value of a datum matters. By contrast, nonparametric methods are more qualitative. The absolute value of a datum is hardly important – what matters is sometimes merely whether it is larger or smaller than other data.");
		texts.put(PlotFunctions.STATISTICAL,
				"This option creates a statistical bar for the selected method.");
		texts.put(PlotFunctions.TIME,
				"This option creates a time scatter plot for the selected method.");
		texts.put(
				"lotDataCond",
				"There a lot of data and Conditions selected in the table.\nThe execution of the method will be slow. Do you want to continue?");

	}

	/**
	 * Method to get the String of text associated to the input key.
	 * 
	 * @param key
	 *            String, input key.
	 * @return String of text.
	 */
	public static String get(String key) {
		return texts.get(key);
	}
}