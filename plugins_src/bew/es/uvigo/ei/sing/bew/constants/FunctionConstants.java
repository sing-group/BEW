package es.uvigo.ei.sing.bew.constants;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.InterMethod;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.operations.UpdateMetadataOperation;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.tables.dialogs.TableDialog;
import es.uvigo.ei.sing.bew.util.SimpleErrorHandler;
import es.uvigo.ei.sing.bew.util.StringComparatorFirst;
import es.uvigo.ei.sing.bew.util.StringComparatorSecond;
import es.uvigo.ei.sing.bew.view.panels.SetupPanel;

/**
 * This class provides static functions of different types at other system
 * classes.
 * 
 * @author Gael P�rez Rodr�guez
 * 
 */
public final class FunctionConstants {
	// Variables
	public static DocumentBuilderFactory factory;
	public static DocumentBuilder builder;

	static {
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to prepare the values for AIBench. The method get the data of
	 * sheetConfigurators and fill the other variables with them.
	 * 
	 * @param sheetConf
	 *            The Sheets with the information.
	 * @param methodNames
	 *            The names of the different methods.
	 * @param methodUnits
	 *            Units of the different methods.
	 * @param numConditions
	 *            Condition number of each method.
	 * @param conditionNames
	 *            Condition names of each method.
	 * @param data
	 *            Data of each method.
	 * @param expSetup
	 *            Experiment setup values.
	 * @param condUnits
	 *            Units for the Conditions.
	 */
	public static void setAIBenchValues(List<IWizardStep> sheetConf,
			String[] methodNames, String[] methodUnits,
			Integer[] numConditions, String[][] conditionNames,
			List<Object[][]> data, String[] expSetup, String[][] condUnits) {
		int i = 0;

		// Go over each Sheet
		for (IWizardStep configurator : sheetConf) {
			// Sheet name
			methodNames[i] = configurator.getSheetName();

			// Sheet Units. For ConditionSheet is ""
			methodUnits[i] = configurator.getUnits();

			// Number of conditions of the sheet
			numConditions[i] = configurator.getNumConditions();

			// Name of the headers of each sheet
			List<String> condNames = configurator.getConditionNames();
			String[] names = new String[condNames.size()];
			int y = 0;
			for (String iterator : condNames) {
				names[y] = iterator;
				y++;
			}
			conditionNames[i] = names;

			// Get Units
			List<String> conditionUnits = configurator.getConditionUnits();
			String[] units = new String[conditionUnits.size()];
			y = 0;
			for (String iterator : conditionUnits) {
				units[y] = iterator;
				y++;
			}
			condUnits[i] = units;

			data.add(configurator.getTableToObject());

			// Get Experiment Setup if is a Condition Sheet
			if (configurator.getSheetName().equals(
					I18n.get("conditionSheetName"))) {
				String[] aux = configurator.getExpSetup();
				int pos = 0;
				for (String value : aux) {
					expSetup[pos] = value;
					pos++;
				}
			}
			i++;
		}
	}

	/**
	 * Imports a XLS from local disk and creates and IntraExperiment in the
	 * system.
	 * 
	 * @param sheetConf
	 *            Data from the Wizard.
	 * @return IntraExperiment ready to be created in AIBench.
	 */
	public static Experiment importXlsExperiment(List<IWizardStep> sheetConf) {
		// Variables to save the information
		int i = 0;
		String[] methodNames = new String[sheetConf.size()];
		String[] methodUnits = new String[sheetConf.size()];
		Integer[] numConditions = new Integer[sheetConf.size()];
		String[][] conditionNames = new String[sheetConf.size()][];
		String[][] condUnits = new String[sheetConf.size()][];
		LinkedList<Object[][]> data = new LinkedList<>();
		String[] expSetup = new String[8];

		// IntraExperiment
		Experiment exp = new Experiment();

		// Go over the sheets of the Wizard
		for (IWizardStep configurator : sheetConf) {
			// Sheet name
			methodNames[i] = configurator.getSheetName();

			// Sheet Units. For ConditionSheet is ""
			methodUnits[i] = configurator.getUnits();

			// Number of conditions of the sheet
			numConditions[i] = configurator.getNumConditions();

			// Name of the headers of each sheet
			List<String> condNames = configurator.getConditionNames();
			String[] names = new String[condNames.size()];
			int y = 0;
			for (String iterator : condNames) {
				names[y] = iterator;
				y++;
			}
			conditionNames[i] = names;

			// Units of the Conditions
			List<String> conditionUnits = configurator.getConditionUnits();
			String[] units = new String[conditionUnits.size()];
			y = 0;
			for (String iterator : conditionUnits) {
				units[y] = iterator;
				y++;
			}
			condUnits[i] = units;

			data.add(configurator.getTableToObject());

			// Get Experiment Setup
			if (configurator.getSheetName().equals(
					I18n.get("conditionSheetName"))) {
				String[] aux = configurator.getExpSetup();
				int pos = 0;
				for (String value : aux) {
					expSetup[pos] = value;
					pos++;
				}
			}
			i++;
		}

		// Adding information to the IntraExperiment
		for (int j = 0; j < methodNames.length; j++) {
			if (methodNames[j].equals(I18n.get("conditionSheetName"))) {
				exp.addConstantCondition(FunctionConstants.createConstant(
						methodNames[j], data.get(j)));
				exp.setExpSetup(expSetup);
			}

			else
				exp.addMetodo(FunctionConstants.createMethod(methodNames[j],
						methodUnits[j], data.get(j), conditionNames[j],
						condUnits[j], numConditions[j], exp));
		}

		return exp;
	}

	/**
	 * Creates a new Constant Condition.
	 * 
	 * @param constantName
	 *            Name of the Constant Condition.
	 * @param data
	 *            Data of the Constant Condition.
	 * @return a new ConstantConditions.
	 */
	public static ConstantConditions createConstant(String constantName,
			Object[][] data) {

		ArrayList<String> conditions = new ArrayList<String>();
		ArrayList<String> conditionValues = new ArrayList<String>();
		ArrayList<String> condUnits = new ArrayList<String>();

		for (int row = 0; row < data.length; row++) {
			conditions.add((String) data[row][0]);
			conditionValues.add((String) data[row][1]);
			condUnits.add((String) data[row][2]);
		}

		return new ConstantConditions(constantName, conditions,
				conditionValues, condUnits);
	}

	/**
	 * Creates a new Method.
	 * 
	 * @param methodNames
	 *            Name of the Method.
	 * @param methodUnits
	 *            Units of the Method.
	 * @param data
	 *            Data of the Method.
	 * @param conditionNames
	 *            Condition names of the Method.
	 * @param condUnits
	 *            Condition units of the Method.
	 * @param numConditions
	 *            Condition numbers of the Method.
	 * @param parent
	 *            Parent Experiment of the Method.
	 * @return A new Method.
	 */
	public static Method createMethod(String methodNames, String methodUnits,
			Object[][] data, Object[] conditionNames, Object[] condUnits,
			int numConditions, IExperiment parent) {

		int numPairs = numConditions;
		ArrayList<Condition> arrayConditions = new ArrayList<Condition>();
		ArrayList<DataSeries> dataSeries = new ArrayList<DataSeries>();

		// Temp variables
		ArrayList<Object> measurements = new ArrayList<Object>();
		Map<Condition, Object> mapCS = new LinkedHashMap<Condition, Object>();
		List<Object> condValues = new ArrayList<Object>();
		List<Object> dataRow = new ArrayList<Object>();

		// We save the condition values to replicate them in each row if we
		// found a whiteSpace in this position
		Object[] conditionValues = new Object[numPairs];

		// Create the Conditions
		for (int col = 0; col < numPairs; col++) {
			for (int fil = 0; fil < data.length; fil++) {
				Object obj = data[fil][col];
				// If ob has something
				if (obj.toString().matches(".*\\w.*"))
					condValues.add(obj);
			}
			// Each row is a condition from the same IntraExperiments (same
			// names and units)
			arrayConditions.add(new Condition(conditionNames[col].toString(),
					condValues, condUnits[col].toString()));

			condValues.clear();
		}

		// Go over the data
		for (int fil = 0; fil < data.length; fil++) {
			// First we take the condition values columns
			for (int col = 0; col < numPairs; col++) {
				Object obj = data[fil][col];
				// Save the value to replicate later if necessary
				if (obj.toString().matches(".*\\w.*")) {
					conditionValues[col] = obj;
					mapCS.put(arrayConditions.get(col), obj);
					dataRow.add(obj);
				}

				// Put the replicated value if the cell is empty (tree
				// structure)
				else {
					mapCS.put(arrayConditions.get(col), conditionValues[col]);
					dataRow.add(obj);
				}
			}
			// Go over Measurements
			for (int col = numPairs; col < data[0].length; col++) {
				// Replace all ',' for '.'
				String str = replaceCommas(data[fil][col].toString());

				// measurements.add(Double.parseDouble(s));
				// dataRow.add(Double.parseDouble(s));
				measurements.add(str);
				dataRow.add(str);
			}
			// One DataSerie per data row
			dataSeries.add(new DataSeries(fil, mapCS, measurements, dataRow));

			// Purge variables
			mapCS.clear();
			measurements.clear();
			dataRow.clear();
		}

		return new Method(methodNames, methodUnits, dataSeries,
				arrayConditions, parent);
	}

	/**
	 * Creates a new InterMethod. An InterMethod is used by InterExperiments.
	 * 
	 * @param methodNames
	 *            Name of the Method.
	 * @param methodUnits
	 *            Units of the Method.
	 * @param data
	 *            Data of the Method.
	 * @param conditionNames
	 *            Condition names of the Method.
	 * @param numConditions
	 *            Condition numbers of the Method.
	 * @param parent
	 *            Parent Experiment of the Method.
	 * @return A new InterMethod.
	 */
	public static InterMethod createInterMethod(String methodNames,
			String methodUnits, Object[][] data,
			List<Condition> conditionNames, int numPairs, IExperiment parent) {
		// Temporary variables
		ArrayList<DataSeries> dataSeries = new ArrayList<DataSeries>();
		ArrayList<Object> measurements = new ArrayList<Object>();
		Map<Condition, Object> mapCS = new LinkedHashMap<Condition, Object>();
		List<Object> dataRow = new LinkedList<Object>();

		String value = null;
		// We save the condition values to replicate them in each row if we
		// found a whiteSpace in this position
		String[] conditionValues = new String[numPairs];

		// Go over the data
		for (int fil = 0; fil < data.length; fil++) {
			// First we take the condition values columns
			for (int col = 0; col < numPairs; col++) {
				Object obj = data[fil][col];

				if (obj instanceof Double)
					value = new DecimalFormat("#").format(obj);

				else if (obj instanceof Long)
					value = Long.toString((Long) obj);

				else
					value = (String) data[fil][col];

				// Save the value to replicate later if necessary
				if (value.matches(".*\\w.*")) {
					conditionValues[col] = value;
					mapCS.put(conditionNames.get(col), value);
					dataRow.add(value);
				}
				// Put the replicated value if the cell is empty (tree
				// structure)
				else {
					mapCS.put(conditionNames.get(col), conditionValues[col]);
					dataRow.add(value);
				}
			}
			// Go over Measurements
			for (int col = numPairs; col < data[0].length; col++) {
				// Replace all ',' for '.'
				String str = replaceCommas(data[fil][col].toString());

				measurements.add(str);
				dataRow.add(str);
			}
			// One DataSerie per data row
			dataSeries.add(new DataSeries(fil, mapCS, measurements, dataRow));

			// Purge variables
			mapCS.clear();
			measurements.clear();
			dataRow.clear();
		}

		return new InterMethod(methodNames, methodUnits, dataSeries,
				conditionNames, parent);
	}

	/**
	 * Replace all the commas in the input String for dots.
	 * 
	 * @param value
	 *            String to replace.
	 * @return String with dots.
	 */
	public static String replaceCommas(String value) {
		String ret = value;

		ret = value.replaceAll(",", ".");

		return ret;
	}

	/**
	 * Puts asterisks in the input String if and only if the String doesn't
	 * start and end with one. If the String has and equal this function only
	 * put the asterisks in the part before.
	 * 
	 * e.x: Home=Something -> *Home*=Something
	 * 
	 * @param value
	 *            String to put the asterisks.
	 * @return String with the asterisks.
	 */
	public static String putAsterisks(String value) {
		String[] values = value.split("=");

		// Validate if the Strings has asterisks and put them only if the part
		// before the equal
		if (!values[0].startsWith("*") && !values[0].endsWith("*")) {
			value = "*" + values[0] + "*";
		} else if (values[0].startsWith("*") && !values[0].endsWith("*")) {
			value = values[0] + "*";
		} else if (!values[0].startsWith("*") && values[0].endsWith("*")) {
			value = "*" + values[0];
		} else {
			value = values[0];
		}

		if (values.length > 1) {
			value += "=" + values[1];
		}

		return value;
	}

	/**
	 * This method transforms a String in the following way: 1) If the String
	 * starts and ends with * the method delete them. 2) If the String doesn't
	 * star and end with * the method put them only in the parte before the
	 * equal.
	 * 
	 * @param value
	 *            String to transform.
	 * @return Transformed String.
	 */
	public static String oppositeValue(String value) {
		String[] values = value.split("=");

		if (values[0].startsWith("*") && values[0].endsWith("*")) {
			value = values[0].substring(1, values[0].length() - 1);
		} else {
			value = "*" + values[0] + "*";
		}

		if (values.length > 1) {
			value += "=" + values[1];
		}

		return value;
	}

	/**
	 * Validates a Condition name. If the method returns: 0 = condition exists
	 * in the file. 1 = Opposite condition exists (condition -> *condition* or
	 * *cond* -> cond). 2 = Have to put *Cond*. 3 = invalid value, error.
	 * 
	 * @param condition
	 *            Condition name to validate
	 * @param isEditing
	 *            Variable to indicate if the user is editing or importing
	 * @return Integer with a code for the validation
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Integer condValidation(String condition, boolean isEditing)
			throws URISyntaxException, IOException {
		List<String> conditions = loadConditionsFromFile();
		// Split for Metacondition_X
		condition = condition.split("_")[0];

		if (!condition.isEmpty()) {
			// If is in the file
			if (conditions.contains(condition)) {
				return 0;
			} else {
				// If user is importing
				if (!isEditing) {
					condition = oppositeValue(condition);
					// If opposite is in the file
					if (conditions.contains(condition)) {
						return 1;
					} else {
						// If not, always write value between * *
						writeCondFile(condition);
						return 2;
					}
				} else {
					return 3;
				}
			}
		}
		return 0;
	}

	/**
	 * Validates a Condition value name for a Condition name. If the method
	 * returns: 0 = condition value exists in the file. 1 = Opposite condition
	 * value exists (value -> *value* or *value* -> value). 2 = Have to put
	 * *Value*. 3 = invalid value, error.
	 * 
	 * @param value
	 *            Value to validate.
	 * @param condition
	 *            Condition name to validate.
	 * @param isEditing
	 *            Check if user is editing or importing.
	 * @return Integer with a code for the validation.
	 */
	public static Integer condValueValidation(String value, String condition,
			boolean isEditing) {
		Integer toRet = 0;
		// Take care of metaconditions_X
		condition = condition.split("_")[0];
		String[] splitted;

		if (!value.isEmpty()) {
			try {
				if (condition.equals(BewConstants.METACONDITION)) {
					if (value.contains("=")) {
						// If value is antimicrobial
						splitted = value.split("\\=");

						// Validate number of equals in the value
						if (equalsValidation(splitted)) {
							// Only verify the first value
							toRet = analyseMetaCondition(splitted[0], isEditing);
						} else {
							toRet = 3;
						}
					} else {
						// Only verify the first value (antimicrobial value)
						toRet = analyseMetaCondition(value, isEditing);
					}
				} else {
					if (value.contains("=")) {
						// If value is antimicrobial
						splitted = value.split("\\=");

						// Validate number of equals in the value
						if (equalsValidation(splitted)) {
							// Only verify the first value
							toRet = analyseConditionValue(condition,
									splitted[0], isEditing);
						} else {
							toRet = 3;
						}
					} else {
						// Verify the value
						toRet = analyseConditionValue(condition, value,
								isEditing);
					}
				}
			} catch (Exception e) {
				toRet = 1;
			}
		}

		return toRet;
	}

	/**
	 * Verify the length of the input array and return true if the length is 2.
	 * False in other cases.
	 * 
	 * e.x.: value=0.5 -> [0] = value, [1] = 0.5
	 * 
	 * @param splitted
	 *            String[] to verify the length.
	 * @return True if length is 2, false otherwise.
	 */
	private static boolean equalsValidation(String[] splitted) {
		// e.x. val=u=e=0.5
		if (splitted.length > 2) {
			return false;
		}
		// e.x. value=0.5a
		else if (splitted.length <= 2) {
			// e.x. value=
			if (splitted.length == 1)
				return false;
			else
				return true;
		}

		return false;
	}

	/**
	 * Validates a Condition value name for a Condition name. If the method
	 * returns: 0 = condition value exists in the file. 1 = Opposite condition
	 * value exists (value -> *value* or *value* -> value). 2 = Have to put
	 * *Value*. 3 = invalid value, error.
	 * 
	 * @param condition
	 *            Condition name to validate.
	 * @param value
	 *            Value to validate.
	 * @param isEditing
	 *            Check if user is editing or importing.
	 * @return Integer with a code for the validation.
	 * @throws Exception
	 */
	private static Integer analyseConditionValue(String condition,
			String value, Boolean isEditing) throws Exception {
		Integer toRet = 0;
		List<String> valuesFromFile = new ArrayList<String>();
		// Read values for the input Condition name
		valuesFromFile = FunctionConstants.readValuesForCondition(condition);

		// Validate symbols in the value
		if (simbolValidationInCond(value)) {
			// Nominal conditions have values in the file
			if (!valuesFromFile.isEmpty()) {
				// If the value is in the file
				if (valuesFromFile.contains(value)) {
					toRet = 0;
				} else {
					// If user is importing validate other cases
					if (!isEditing) {
						value = oppositeValue(value);
						if (valuesFromFile.contains(value)) {
							toRet = 1;
						} else {
							// Always write value with * *
							writeCondValuesFile(condition,
									new Object[] { value });
							toRet = 2;
						}
					} else {
						toRet = 3;
					}
				}
			}
			// If the condition is numerical
			else {
				// Validate if the value is Double
				try {
					Double.parseDouble(value);
					toRet = 0;
				} catch (Exception e) {
					// If not, put it between *
					toRet = 2;
				}
			}
		} else {
			toRet = 3;
		}

		return toRet;
	}

	/**
	 * Validates a Condition value name for a Condition name. If the method
	 * returns: 0 = condition value exists in the file. 1 = Opposite condition
	 * value exists (value -> *value* or *value* -> value). 2 = Have to put
	 * *Value*. 3 = invalid value, error.
	 * 
	 * @param value
	 *            Value to validate.
	 * @param isEditing
	 *            Check if user is editing or importing.
	 * @return Integer with a code for the validation.
	 * @throws Exception
	 */
	private static Integer analyseMetaCondition(String value, Boolean isEditing)
			throws Exception {
		Integer toRet = 1;
		List<String> conditions = loadConditionsFromFile();
		List<String> valuesFromFile = new ArrayList<String>();
		String auxValue = "";

		// Symbol validation
		if (simbolValidationInCond(value)) {
			// Validate if the value is Double (numerical)
			try {
				Double.parseDouble(value);
				toRet = 0;
			}
			// If not, then is nominal. Check all conditions
			catch (Exception e) {
				// Go over all conditions in the file
				for (String cond : conditions) {
					// Check all condition values for this Condition
					valuesFromFile = readValuesForCondition(cond);
					if (valuesFromFile.contains(value)) {
						toRet = 0;
						break;
					} else {
						auxValue = oppositeValue(value);
						if (valuesFromFile.contains(auxValue)) {
							toRet = 1;
							break;
						} else {
							toRet = 2;
						}
					}
				}
			}
		} else {
			toRet = 3;
		}
		return toRet;
	}

	/**
	 * Load Conditions from the file.
	 * 
	 * @return List<Strig> with the Conditions.
	 */
	public static List<String> loadConditionsFromFile() {
		List<String> conditions = new ArrayList<String>();
		InputStream in = FunctionConstants.class.getResourceAsStream("/files/"
				+ BewConstants.CONDITIONFILE);

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			String line = null;
			String aux = null;

			// Read line per line
			while ((line = reader.readLine()) != null) {
				aux = line.toLowerCase().trim();
				if (!aux.isEmpty())
					conditions.add(aux);
			}

			reader.close();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conditions;
	}

	/**
	 * Method to recover the condition values for the input Condition in the
	 * file.
	 * 
	 * @param condition
	 *            The key of the values (a Condition).
	 * @return List<String> with the values for the given key.
	 * @throws Exception
	 */
	public static List<String> readValuesForCondition(String condition)
			throws Exception {
		List<String> values = new ArrayList<String>();

		InputStream in = FunctionConstants.class.getResourceAsStream("/files/"
				+ BewConstants.VALUESFILE);

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			String line = null;
			String[] splitted;

			// Read line per line
			while ((line = reader.readLine()) != null) {
				splitted = line.split("\t");

				// Only obtain the value, not the key (Condition)
				if (splitted[0].equals(condition)) {
					values.add(splitted[1]);
				}
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		in.close();

		return values;
	}

	/**
	 * Recover the Method units in the Method file.
	 * 
	 * @return Map<String, String>, key=Method, value=Units
	 * @throws Exception
	 */
	public static Map<String, String> readValuesForMethod() throws Exception {
		Map<String, String> ret = new HashMap<String, String>();

		InputStream in = FunctionConstants.class.getResourceAsStream("/files/"
				+ BewConstants.METHODFILE);

		String[] splitLine = null;

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line = null;
		// Read line per line
		while ((line = reader.readLine()) != null) {
			try {
				// Structure of the file like this: methodName,methodUnits
				splitLine = line.split("\t");
				ret.put(splitLine[0], splitLine[1]);
			} catch (Exception e) {
				ret.put(splitLine[0], "???");
			}
		}

		reader.close();

		return ret;
	}

	/**
	 * Recover the Method units in the Method file.
	 * 
	 * @return List<String>, Method\tUnits
	 * @throws Exception
	 */
	public static List<String> readValuesForMethodInList() throws Exception {
		List<String> ret = new ArrayList<String>();

		InputStream in = FunctionConstants.class.getResourceAsStream("/files/"
				+ BewConstants.METHODFILE);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line = null;
		// Read line per line
		while ((line = reader.readLine()) != null) {
			ret.add(line);
		}

		reader.close();

		return ret;
	}

	/**
	 * Method to copy the content of a vector into Object[].
	 * 
	 * @param v1
	 *            Vector to copy.
	 * @return Object[] with the vector data.
	 */
	public static Object[] copyVector(Vector<?> v1) {
		Object[] array = new Object[v1.size()];

		int i = 0;

		for (Object value : v1) {
			array[i] = value;
			i++;
		}
		return array;
	}

	/**
	 * Method to transform a dynamic matrix (List<List<Object>>) in a static
	 * matrix Object[][].
	 * 
	 * @param data
	 *            Data of the dynamic matrix.
	 * @return Static matrix created with the same data.
	 */
	public static Object[][] dataToArray(List<List<Object>> data) {

		if (!data.isEmpty()) {
			Object[][] ret = new Object[data.size()][data.get(0).size()];

			for (int i = 0; i < data.size(); i++) {
				List<Object> values = data.get(i);
				int j = 0;

				for (Object value : values) {
					ret[i][j] = value;
					j++;
				}
			}
			return ret;
		}

		return new Object[0][0];
	}

	/**
	 * Method to create a static matrix (Object[][]) from arrays (Object[]).
	 * 
	 * @param data
	 *            Indeterminate number of Object[].
	 * @return
	 */
	public static Object[][] arrayToMatrix(Object[]... data) {
		Object[][] ret = new Object[data[0].length][3];
		int col = 0;

		for (Object[] array : data) {
			if (array.length > 0) {
				for (int i = 0; i < array.length; i++) {
					ret[i][col] = array[i];
				}
				col++;
			} else {
				return null;
			}
		}

		return ret;
	}

	/**
	 * Method to add more values to a created key in the input map.
	 * 
	 * @param map
	 *            The HashMap to add more values (Map<Object, List<Object>>).
	 * @param key
	 *            The key of the map.
	 * @param values
	 *            Values to add the key in the map (List<Object>).
	 */
	public static <K, V> void addValuesToKey(Map<K, List<V>> map, K key,
			List<V> values) {
		List<V> auxValues = map.get(key);
		for (V value : values) {
			auxValues.add(value);
		}
	}

	/**
	 * Method to add one more value to a created key in the input map.
	 * 
	 * @param map
	 *            The HashMap to add more values (Map<Object, List<Object>>).
	 * @param key
	 *            The key of the map.
	 * @param value
	 *            Value to add.
	 */
	public static <K, V> void addValueToKey(Map<K, List<V>> map, K key, V value) {
		List<V> auxValues = map.get(key);

		auxValues.add(value);
	}

	/**
	 * Method to load and validate a XML file with an online Schema. The Schema
	 * is always defined in the XML file inside the attribute SchemaLocation.
	 * 
	 * @param documentPath
	 *            XML file.
	 * @return a Document validated.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document loadDocumentAndValidateOnlineXSD(String documentPath)
			throws ParserConfigurationException, IOException, SAXException {
		// Parser construction
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature(BewConstants.APACHE, true);
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);

		// Add Error Handler
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());

		return builder.parse(new File(documentPath));
	}

	/**
	 * Method to validate a created XML with the local Schema.
	 * 
	 * @param documentPath
	 *            XML file.
	 * @param schemaPath
	 *            Local Schema path.
	 * @return a Document validated.
	 * @throws SAXException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws IllegalArgumentException
	 * @throws ParserConfigurationException
	 */
	public static Document loadDocumentAndValidateLocalXSD(String documentPath,
			URL schemaPath) throws SAXException, IOException,
			IllegalArgumentException, URISyntaxException,
			ParserConfigurationException {
		// Create parsing validator
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Overwriting the Schema declared in the XML file
		factory.setAttribute(BewConstants.JAXP_SCHEMA_LANGUAGE,
				BewConstants.W3C_XML_SCHEMA);
		factory.setAttribute(BewConstants.JAXP_SCHEMA_SOURCE, new File(
				schemaPath.toString().substring(5)));
		factory.setFeature(BewConstants.APACHE, true);
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);

		// Adding Handler
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());

		return builder.parse(new File(documentPath));
	}

	/**
	 * Method to read all the lines of the input file and put them into a String
	 * List.
	 * 
	 * @param file
	 *            File to read.
	 * @return List<String> with the File lines. One line per position.
	 */
	public static List<String> readFile(File file) {
		List<String> ret = new ArrayList<String>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream din = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(din));
			String strLine;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				ret.add(strLine);
			}
			// Close the input stream
			din.close();
		} catch (Exception e) {// Catch exception if any
			ShowDialog.showError(I18n.get("errorReadingTitle"),
					I18n.get("errorReading"), e.getMessage());
		}

		return ret;
	}

	/**
	 * Method to validate the Conditions and Data selected in the Plot and
	 * Statistic Table.
	 * 
	 * @param condMeasur
	 *            Map, key=combination of the selected Conditions in the table,
	 *            value=data for the combination
	 * @param methodTable
	 *            Table to validate.
	 * @param numConditions
	 *            Number of Conditions.
	 * @param selectedCondition
	 *            List with the selected Conditions in the table.
	 * @param selectedValues
	 *            List with the selected values in the table.
	 * @param intraExpAndRows
	 *            Map, key=IntraExperiment, value=Rows for the IntraExperiment
	 * @return String with the unselected Conditions in the table.
	 */
	public static String validateSelectionTable(
			Map<Object, List<Object>> condMeasur, JTable methodTable,
			int numConditions, List<String> selectedCondition,
			List<String> selectedValues,
			Map<Object, List<Object>> intraExpAndRows) {

		List<Object> measurements = new ArrayList<Object>();

		Collection<List<Object>> dataSeries = null;
		Set<Object> intraKeys = null;
		if (intraExpAndRows != null) {
			dataSeries = intraExpAndRows.values();
			intraKeys = intraExpAndRows.keySet();
		}

		// Variable to indicate which is the reference constant condition to
		// take
		// the data
		String constantVal = "";

		// Variable to take the selected rows in the table
		int[] selectedRows = methodTable.getSelectedRows();

		// Create the Map Key for each row
		String key = "";
		// Obtain the constantCondition for each row
		String keyConstant = "";

		// Go over the selected rows in the table
		for (int row : selectedRows) {
			// Go over all the Condition columns in the table
			for (int col = 0; col < numConditions; col++) {
				String columnName = methodTable.getColumnName(col);

				// If the user doesn't select this column it's a
				// constantCondition
				if (!selectedCondition.contains(columnName)) {
					String value = methodTable.getValueAt(row, col).toString();

					// If value is not a whitespace we save it in the map
					if (value.trim().equals(""))
						value = findNonEmptyValue(row, col, value, methodTable);

					if (keyConstant.trim().equals(""))
						keyConstant = keyConstant.concat("'" + value);
					else
						keyConstant = keyConstant.concat("#" + value);
				}
				// We find a selected condition for the user
				else {
					String value = methodTable.getValueAt(row, col).toString();

					// If value is not a whitespace we save it in the map
					if (value.trim().equals(""))
						value = findNonEmptyValue(row, col, value, methodTable);

					// We concatenate #Value except for the first
					if (key.trim().equals(""))
						key = key.concat("'" + value);
					else
						key = key.concat("#" + value);
				}
			}
			// R needs string between ' '
			if (keyConstant.trim().equals(""))
				keyConstant = keyConstant.concat("''");
			else
				keyConstant = keyConstant.concat("'");
			key = key.concat("'");

			// If method comes from InterExperiment
			if (dataSeries != null) {
				Iterator<List<Object>> ite = dataSeries.iterator();
				List<Object> itNext;

				while (ite.hasNext()) {
					// We find the intraExperiment for each selected row
					// App must compare the same rows for different
					// intraExperiments
					itNext = ite.next();
					if (itNext.contains(row)) {
						for (Object intraKey : intraKeys) {
							// Find the intraExperiment that contains the row
							if (intraExpAndRows.get(intraKey).equals(itNext)) {
								Experiment exp = (Experiment) intraKey;

								// Put the _IntraName inside between ' '
								StringBuilder strBuilder = new StringBuilder(
										key);
								strBuilder.replace(key.lastIndexOf("\'"),
										key.lastIndexOf("\'") + 1,
										"_" + exp.getName() + "'");
								key = strBuilder.toString();
								break;
							}
						}
					}
				}
			}

			// The first time we fill the constantConditionValues. This
			// variable
			// doesn't change in all the execution because the user only can
			// have 1
			// constantCondition for the graphic
			if (constantVal.trim().equals("")) {
				constantVal = keyConstant;
			}

			// If the map doesn't have the key, we introduce it
			if (!condMeasur.containsKey(key))
				condMeasur.put(key, new ArrayList<Object>());

			// We validate the ConstantCondition of each row with the other
			// variable. If there are the same the data are valid
			if (keyConstant.equals(constantVal)) {
				// We take the selected measurements for this key in this
				// row
				for (String data : selectedValues) {
					// Obtain the selected data columns
					int columnIndex = methodTable.getColumn(data)
							.getModelIndex();

					Object value = methodTable.getValueAt(row, columnIndex);
					measurements.add(value);
				}
			} else {
				constantVal = "";
				// If the constantCondition are not valid we stop
				return constantVal;
			}

			// Add the new values to this key in the map
			FunctionConstants.addValuesToKey(condMeasur, key, measurements);

			// Purge conditions
			key = "";
			keyConstant = "";
			measurements.clear();
		}

		return constantVal;
	}

	/**
	 * Find the previous non empty value in the table. Method starts in the
	 * number that the parameter row indicate.
	 * 
	 * @param row
	 *            Position to start.
	 * @param col
	 *            Column to search the value.
	 * @param value
	 *            Value to return.
	 * @param methodTable
	 *            Table to go over.
	 * @return String with the non empty value. Otherwise return value variable.
	 */
	private static String findNonEmptyValue(int row, int col, String value,
			JTable methodTable) {
		// We search the previous non empty value
		for (int index = row; index >= 0; index--) {
			String aux = methodTable.getValueAt(index, col).toString();
			if (!aux.trim().equals("")) {
				value = aux;
				break;
			}
		}
		return value;
	}

	/**
	 * Calculates mean of the input numbers. Method don't calculate Double.NaN.
	 * 
	 * @param numbers
	 *            Numerical parameters. Is a List<Double>.
	 * @return Double with the mean.
	 */
	public static Double calculateMean(List<Object> numbers) {
		double sum = 0;
		double numberSize = numbers.size();
		Double aux;

		// If Object is double use it for statistic, otherwise discard it
		for (Object number : numbers) {
			try {
				aux = Double.parseDouble(number.toString());
				if (!aux.isNaN())
					sum += aux;
				else
					numberSize--;
			} catch (Exception e) {
				numberSize--;
			}

		}

		return sum / numberSize;
	}

	/**
	 * Calculates standard deviation of the input parameters. Method don't
	 * calculate Double.NaN.
	 * 
	 * @param numbers
	 *            Numerical parameters. Is a List<Double>.
	 * @param media
	 *            Mean of the numbers (Double).
	 * @return Double with the deviation.
	 */
	public static Double calculateStandardDesv(List<Object> numbers,
			double media) {
		double difference = 0;
		double desviation = 0;
		int numberSize = numbers.size();
		Double aux;

		// If Object is double use it for statistic, otherwise discard it
		for (Object number : numbers) {
			try {
				aux = Double.parseDouble(number.toString());
				if (!aux.isNaN()) {
					difference = aux - media;
					desviation = desviation + difference * difference;
				} else
					numberSize--;
			} catch (Exception e) {
				numberSize--;
			}
		}

		desviation = desviation / numberSize;
		desviation = Math.sqrt(desviation);

		return desviation;
	}

	/**
	 * Method to validate if the Experiment name is unique in the system.
	 * 
	 * @param expName
	 *            Name to validate.
	 * @return True if the name is unique. False otherwise.
	 */
	public static boolean validateExperimentNames(String expName) {
		List<ClipboardItem> items = Core.getInstance().getClipboard()
				.getItemsByClass(IExperiment.class);

		for (ClipboardItem item : items) {
			if (item.getName().equals(expName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method to validate if the Experiment names is unique in the system.
	 * 
	 * @param exp
	 *            Experiment to validate the name.
	 * @param expName
	 *            Name of the Experiment.
	 * @return True if the name is unique. False otherwise.
	 */
	public static boolean validateExperimentNames(IExperiment exp,
			String expName) {
		List<ClipboardItem> items = Core.getInstance().getClipboard()
				.getItemsByClass(IExperiment.class);

		IExperiment expInterface;
		for (ClipboardItem item : items) {
			expInterface = (IExperiment) item.getUserData();

			if (!expInterface.equals(exp)
					&& expInterface.getName().equals(expName))
				return false;
		}
		return true;
	}

	/**
	 * Method to sorts an input Map by its values (ascendant order).
	 * 
	 * @param map
	 *            to sort.
	 * @return SortedSet<Map.Entry<K, V>>.
	 */
	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1; // Special fix to preserve
													// items with equal values
					}
				});
		sortedEntries.addAll(map.entrySet());

		return sortedEntries;
	}

	/**
	 * Method to validates if a xml file contains an InterExperiment or an
	 * IntraExperiment. If the method return true it will be an InterExperiment,
	 * otherwise it will be an IntraExperiment. The method only revise the first
	 * 5 lines.
	 * 
	 * @param path
	 *            Path of the file
	 * @return True of false
	 */
	public static boolean isXmlInter(File file) {
		Scanner scanner = null;
		int cont = 0;

		try {
			scanner = new Scanner(file);

			String line;
			while (scanner.hasNextLine() && cont < 5) {
				line = scanner.nextLine();

				if (line.contains(BewConstants.XML_INTER_HEADER)) {
					scanner.close();
					return true;
				}

				cont++;
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		}

		return false;
	}

	/**
	 * Method to validates if the required fields are not empty.
	 * 
	 * @return True if all the fields are not empty. Else otherwise.
	 */
	public static boolean validateRequiredFields(SetupPanel setupPanel) {
		String expName = setupPanel.getFieldName().getText().trim();
		String expOrg = setupPanel.getFieldOrganization().getText().trim();
		String expContact = setupPanel.getFieldContact().getText().trim();
		String expDate = setupPanel.getFieldDate().getText().trim();
		if (!expName.isEmpty() && !expOrg.isEmpty() && !expContact.isEmpty()) {
			if (!expDate.isEmpty()) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					sdf.setLenient(false);
					sdf.parse(expDate);
					return true;
				} catch (ParseException e) {
					return false;
				}
			} else
				return true;
		} else
			return false;
	}

	/**
	 * This method creates a MD5 hash for the input file.
	 * 
	 * @param filename
	 *            Input file name (path).
	 * @return String with the MD5.
	 * @throws Exception
	 */
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Creates the checksum for the input file.
	 * 
	 * @param filename
	 *            Input file name (path).
	 * @return byte[] with the SHA1.
	 * @throws Exception
	 */
	private static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA1");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();

		return complete.digest();
	}

	/**
	 * Open a file and insert a hash in the last line.
	 * 
	 * @param path
	 *            String with the path of the file.
	 * @param hash
	 *            Hash to put in the file.
	 * @throws IOException
	 */
	public static void insertHash(String path, String hash) throws IOException {
		Writer output;

		output = new BufferedWriter(new FileWriter(path, true));
		output.append("\n" + hash);

		output.flush();
		output.close();
	}

	/**
	 * Seek the hash in the last line of the file. The method obtain it and
	 * delete the line (\n includes).
	 * 
	 * @param path
	 *            String with the path of the file.
	 * @return String with the deleted hash.
	 * @throws IOException
	 */
	public static String getAndDeleteHash(String path) throws IOException {
		RandomAccessFile file = new RandomAccessFile(path, "rw");
		long length = file.length() - 1;
		byte b;

		do {
			length -= 1;
			file.seek(length);
			b = file.readByte();
		} while (b != 10);

		String ret = file.readLine();
		file.setLength(length);
		file.close();

		return ret;
	}

	/**
	 * This method compares the input hash with the hash of the input file (file
	 * == path).
	 * 
	 * @param hash
	 *            Input hash.
	 * @param path
	 *            String with the path of the file.
	 * @return True if the hashes are equal, false otherwise.
	 * @throws Exception
	 */
	public static boolean compareHashes(String hash, String path)
			throws Exception {
		return getMD5Checksum(path).equals(hash);
	}

	/**
	 * Encrypts a file.
	 * 
	 * @param ivBytes
	 *            Variable to do the encryption.
	 * @param keyBytes
	 *            Variable to do the encryption.
	 * @param path
	 *            String with the path of the file.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
	public static void encrypt(byte[] ivBytes, byte[] keyBytes, String path)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, IOException {

		Path filePath = Paths.get(path);
		byte[] data = Files.readAllBytes(filePath);

		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, "Blowfish");
		Cipher cipher = null;
		cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

		byte[] encrpytFile = cipher.doFinal(data);

		// Save the encrypted file
		File temp = new File(path);
		filePath = Paths.get(temp.getAbsolutePath());

		Files.write(filePath, encrpytFile);
	}

	/**
	 * Decrypts a file.
	 * 
	 * @param ivBytes
	 *            Variable to do the decryption.
	 * @param keyBytes
	 *            Variable to do the decryption.
	 * @param path
	 *            String with the path of the file.
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws URISyntaxException
	 */
	public static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, String path)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, IOException,
			ClassNotFoundException, URISyntaxException {

		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

		Path filePath = Paths.get(path);
		byte[] data = Files.readAllBytes(filePath);

		byte[] byteFile = cipher.doFinal(data);

		System.gc();

		return byteFile;
	}

	/**
	 * Creates a specific table dialog for each row based on the input
	 * variables.
	 * 
	 * @param condValues
	 *            Type of condition values. List<String> with the values.
	 * @param actualCondition
	 *            String with the actual Condition.
	 * @param selectedRow
	 *            JComponent parent.
	 * @return TableDialog with the specific panel.
	 */
	public static TableDialog createSpecificTableDialog(
			List<String> condValues, String actualCondition, JComponent me) {
		TableDialog toRet = null;
		int typePanel = FunctionConstants.calculateTypePanel(condValues,
				actualCondition);

		toRet = new TableDialog(me, typePanel, condValues);

		return toRet;
	}

	/**
	 * Calculates the type of the pan
	 * 
	 * @param condValues
	 *            Type of condition values. List<String> with the values.
	 * @param actualCondition
	 *            String with the actual Condition.
	 * @return int with the type of the panel. 0 = nominal, 1 = numerical, 2 =
	 *         metacondition, 3 = antimicrobial.
	 */
	public static int calculateTypePanel(List<String> condValues,
			String actualCondition) {
		int toRet;
		int condValuesSize = condValues.size();
		// Need split for metaconditions (Ex: metacondition_0, metacond_1...)
		String[] cond = actualCondition.split("\\_");

		switch (cond[0].trim().toLowerCase()) {
		case BewConstants.METACONDITION:
			toRet = 2;
			break;
		case BewConstants.ANTIMICROBIAL:
			toRet = 3;
			break;
		default:
			// Condition are nominal, has values
			if (condValuesSize > 0) {
				toRet = 0;
			} else {
				toRet = 1;
			}
			break;
		}

		return toRet;
	}

	/**
	 * Writes a validated Condition name in the file.
	 * 
	 * @param condName
	 *            String with the Condition name
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void writeCondFile(String condName)
			throws URISyntaxException, IOException {
		List<String> savedCond = FunctionConstants.loadConditionsFromFile();
		String condNameWith = "";
		String condNameWithout = "";

		// Conditions with valid values
		if (!condName.isEmpty() && simbolValidationInCond(condName)) {
			if (condName.startsWith("*") && condName.endsWith("*")) {
				condNameWith = condName;

				condNameWithout = condName.substring(1, condName.length() - 1);
			} else {
				condNameWithout = condName;

				condNameWith = "*" + condName + "*";
			}

			if (!savedCond.contains(condNameWithout)
					&& !savedCond.contains(condNameWith)) {
				savedCond.add(condNameWith.toLowerCase());

				// Delete current file
				String auxPath = FunctionConstants.class.getResource(
						"/files/" + BewConstants.CONDITIONFILE).getPath();
				File condFile = new File(auxPath);

				Path aux = Files.createTempFile(null, null);
				// Get actual conditions file
				Path path = condFile.toPath();
				Files.move(path, aux, StandardCopyOption.REPLACE_EXISTING);

				try {
					// Create new empty file and get it
					Files.createFile(path);
					File newConditions = new File(
							UpdateMetadataOperation.class.getResource(
									"/files/" + BewConstants.CONDITIONFILE)
									.getPath());

					Collections.sort(savedCond, new StringComparatorFirst());
					Files.write(newConditions.toPath(), savedCond,
							StandardCharsets.UTF_8, StandardOpenOption.CREATE);

					// Delete the actual condition file
					Files.deleteIfExists(aux);
				} catch (IOException e) {
					Files.move(aux, path, StandardCopyOption.REPLACE_EXISTING);
					throw new IOException();
				}
			}
		}
	}

	/**
	 * Writes validated condition values in the file.
	 * 
	 * @param condName
	 *            String with the Condition name for the values.
	 * @param condValues
	 *            Object[] with the values for the Condition.
	 * @throws Exception
	 */
	public static void writeCondValuesFile(String condName, Object[] condValues)
			throws Exception {
		// User introduce values?
		if (condValues.length > 0) {
			String condValueWith = "";
			String condValueWithout = "";

			List<String> savedValues = FunctionConstants
					.readValuesForCondition(condName);
			List<String> writtenValues = new ArrayList<String>();

			String auxPath = FunctionConstants.class.getResource(
					"/files/" + BewConstants.VALUESFILE).getPath();
			File valFile = new File(auxPath);

			// Get actual conditions file and delete it
			Path path = valFile.toPath();

			List<String> currentFileValues = Files.readAllLines(path,
					StandardCharsets.UTF_8);

			Path aux = Files.createTempFile(null, null);
			Files.move(path, aux, StandardCopyOption.REPLACE_EXISTING);

			try {
				// Create new empty file and get it
				Files.createFile(path);
				File newValues = new File(UpdateMetadataOperation.class
						.getResource("/files/" + BewConstants.VALUESFILE)
						.getPath());

				for (Object value : condValues) {
					String val = value.toString();
					if (!val.isEmpty()) {
						if (val.startsWith("*") && val.endsWith("*")) {
							condValueWith = val;

							condValueWithout = val.substring(1,
									val.length() - 1);
						} else {
							condValueWithout = val;

							condValueWith = "*" + val + "*";
						}
						// Avoid duplicate values
						if (!savedValues.contains(condValueWithout)
								&& !savedValues.contains(condValueWith)
								&& !writtenValues.contains(condValueWith
										.toLowerCase().trim())) {
							currentFileValues.add(condName.toLowerCase() + "\t"
									+ condValueWith.toLowerCase().trim()
									+ "\tnull");

							writtenValues.add(condValueWith.toLowerCase()
									.trim());
						}
					}
				}

				Collections.sort(currentFileValues,
						new StringComparatorSecond());
				Collections
						.sort(currentFileValues, new StringComparatorFirst());
				Files.write(newValues.toPath(), currentFileValues,
						StandardCharsets.UTF_8, StandardOpenOption.CREATE);

				// Delete current file
				Files.deleteIfExists(aux);
			} catch (Exception e) {
				Files.move(aux, path, StandardCopyOption.REPLACE_EXISTING);
				throw new Exception();
			}

		}
	}

	/**
	 * 
	 * @param methodName
	 * @param methodUnits
	 * @throws Exception
	 */
	public static void writeMethodsFile(String methodName, String methodUnits)
			throws Exception {
		List<String> savedMethods = FunctionConstants
				.readValuesForMethodInList();
		List<String> methodNames = new ArrayList<String>();

		for (String str : savedMethods) {
			// Get abbreviation
			methodNames.add(str.split("\t")[0]);
		}

		String methodWith = "";
		String methodWithout = "";

		// Conditions with valid values
		if (!methodName.isEmpty()) {
			if (methodName.startsWith("*") && methodName.endsWith("*")) {
				methodWith = methodName;

				methodWithout = methodName
						.substring(1, methodName.length() - 1);
			} else {
				methodWithout = methodName;

				methodWith = "*" + methodName + "*";
			}

			if (!methodNames.contains(methodWithout)
					&& !methodNames.contains(methodWith)) {
				savedMethods.add(methodWith + "\t" + methodUnits);

				// Delete current file
				String auxPath = FunctionConstants.class.getResource(
						"/files/" + BewConstants.METHODFILE).getPath();
				File methodFile = new File(auxPath);

				// Get actual conditions file and delete it
				Path path = methodFile.toPath();

				Path aux = Files.createTempFile(null, null);
				Files.move(path, aux, StandardCopyOption.REPLACE_EXISTING);

				try {
					// Create new empty file and get it
					Files.createFile(path);
					File newMethod = new File(UpdateMetadataOperation.class
							.getResource("/files/" + BewConstants.METHODFILE)
							.getPath());

					Collections.sort(savedMethods, new StringComparatorFirst());
					Files.write(newMethod.toPath(), savedMethods,
							StandardCharsets.UTF_8, StandardOpenOption.CREATE);

					Files.deleteIfExists(aux);
				} catch (Exception e) {
					Files.move(aux, path, StandardCopyOption.REPLACE_EXISTING);
					throw new Exception();
				}
			}
		}
	}

	/**
	 * Validates the symbols in the input text.
	 * 
	 * @param text
	 *            String with a text.
	 * @return True if the text doesn't contain the following: =, ", _, _and_,
	 *         #.
	 */
	public static boolean simbolValidationInCond(String text) {
		if (!text.contains("=") && !text.contains("\"") && !text.contains("_")
				&& !text.contains("_and_") && !text.contains("#"))
			return true;
		else
			return false;
	}

	/**
	 * Validates the symbols in the input text.
	 * 
	 * @param text
	 *            String with a text.
	 * @return True if the text doesn't contain the following: ", _, _and_, #.
	 */
	public static boolean simbolValidationInData(String text) {
		if (!text.contains("\"") && !text.contains("_")
				&& !text.contains("_and_") && !text.contains("#"))
			return true;
		else
			return false;
	}

	/**
	 * Gets the max value in the input list of Doubles.
	 * 
	 * @param measurements
	 *            List with values.
	 * @return Double with the max value.
	 */
	public static Double getMax(List<Object> measurements) {
		Double toRet = Double.NaN;
		Double aux;

		for (Object o : measurements) {
			try {
				aux = Double.parseDouble(o.toString());
				if (aux > toRet || toRet.isNaN())
					toRet = aux;
			} catch (Exception e) {

			}
		}

		return toRet;
	}

	/**
	 * Gets the min value in the input list of Doubles.
	 * 
	 * @param measurements
	 *            List with values.
	 * @return Double with the min value.
	 */
	public static Double getMin(List<Object> measurements) {
		Double toRet = Double.NaN;
		Double aux;

		for (Object o : measurements) {
			try {
				aux = Double.parseDouble(o.toString());
				if (aux < toRet || toRet.isNaN()) {
					toRet = aux;
				}
			} catch (Exception e) {

			}
		}

		return toRet;
	}

	/**
	 * Converts a buffered image in a byte[].
	 * 
	 * @param bi
	 *            Buffered image to convert.
	 * @return byte[] with the converted image.
	 * @throws IOException
	 */
	public static byte[] bufferedImgToByte(BufferedImage bi) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(bi, "png", baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		baos.close();

		return bytes;
	}

	/**
	 * Converts a byte[] in a buffered image.
	 * 
	 * @param byteImg
	 *            byte[] to convert.
	 * @return buffered image with the converted image.
	 * @throws IOException
	 */
	public static BufferedImage byteToBufferedImg(byte[] byteImg)
			throws IOException {
		InputStream in = new ByteArrayInputStream(byteImg);
		BufferedImage bImageFromConvert = ImageIO.read(in);

		return bImageFromConvert;
	}

	/**
	 * Resizes an input image with different qualities.
	 * 
	 * @param img
	 *            Buffered image to resize.
	 * @param targetWidth
	 *            Target width.
	 * @param targetHeight
	 *            Target height.
	 * @param hint
	 *            Value for the hint.
	 * @param higherQuality
	 *            True for a better quality.
	 * @return Resized Buffered image.
	 */
	public static BufferedImage imageResize(BufferedImage img, int targetWidth,
			int targetHeight, Object hint, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	/**
	 * Return an Integer based of the OS of the user.
	 * 
	 * @return 0 for Windows, 1 for Linux and others and 2 for Mac.
	 */
	public static int getOS() {
		if (BewConstants.OS.indexOf("win") >= 0)
			return 0;
		else if (BewConstants.OS.indexOf("nix") >= 0
				|| BewConstants.OS.indexOf("nux") >= 0
				|| BewConstants.OS.indexOf("aix") > 0)
			return 1;
		else if (BewConstants.OS.indexOf("mac") >= 0)
			return 2;
		else
			return 1;
	}
}
