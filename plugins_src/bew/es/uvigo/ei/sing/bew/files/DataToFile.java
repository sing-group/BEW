package es.uvigo.ei.sing.bew.files;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.filters.XlsSaveFilter;
import es.uvigo.ei.sing.bew.files.filters.XmlSaveFilter;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.DataSeries;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;

/**
 * This class provides methods to store data in xls and xml files.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class DataToFile {

	/**
	 * Saves an Experiment in a XML file.
	 * 
	 * @param exp
	 *            Experiment to save.
	 */
	public static void saveXMLData(IExperiment exp) {
		CustomFileChooser cfc = new CustomFileChooser();
		cfc.setCurrentDirectory(new File(BewConstants.SAVEDIR));

		// Auxiliary variables
		List<Method> methods = exp.getMethods().getMetodos();
		ConstantConditions constant = exp.getConstantCondition();
		String[] expSetup = exp.getExpSetup();

		// We set FileChooser custom filter
		cfc.setFileFilter(new XmlSaveFilter());
		cfc.setAcceptAllFileFilterUsed(false);

		// We open a save dialog
		int retrieval = cfc.showSaveDialog(null);

		if (retrieval == JFileChooser.APPROVE_OPTION) {
			String path = cfc.getSelectedFile().getAbsolutePath();
			BewConstants.SAVEDIR = path;

			if (!path.endsWith(".xml"))
				path = path.concat(".xml");

			// If Experiment == IntraExperiment, the map is empty
			if (exp.getMapIntraExpsColors() == null)
				intraToXml(path, methods, constant, expSetup);
			else
				interToXml(path, methods, constant, expSetup, exp);

		}
	}

	/**
	 * Saves an Experiment in a XML file.
	 * 
	 * @param exp
	 *            Experiment to save.
	 * @param path
	 *            Input path (with file name) to save the Experiment.
	 */
	public static void saveXMLData(IExperiment exp, String path) {
		// Auxiliary variables
		List<Method> methods = exp.getMethods().getMetodos();
		ConstantConditions constant = exp.getConstantCondition();
		String[] expSetup = exp.getExpSetup();

		if (!path.endsWith(".xml"))
			path = path.concat(".xml");

		// If Experiment == IntraExperiment, the map is empty
		if (exp.getMapIntraExpsColors() == null)
			intraToXml(path, methods, constant, expSetup);
		else
			interToXml(path, methods, constant, expSetup, exp);
	}

	/**
	 * Saves an IntraExperiment in a XLS file.
	 * 
	 * @param exp
	 *            IntraExperiment to save.
	 */
	public static void saveXLSData(IExperiment exp) {
		CustomFileChooser cfc = new CustomFileChooser();
		cfc.setCurrentDirectory(new File(BewConstants.SAVEDIR));

		// Auxiliary variables
		List<Method> methods = exp.getMethods().getMetodos();
		ConstantConditions constant = exp.getConstantCondition();
		String[] expSetup = exp.getExpSetup();

		// We set FileChooser custom filter
		cfc.setFileFilter(new XlsSaveFilter());
		cfc.setAcceptAllFileFilterUsed(false);

		// We open a save dialog
		int retrieval = cfc.showSaveDialog(null);

		if (retrieval == JFileChooser.APPROVE_OPTION) {
			String path = cfc.getSelectedFile().getAbsolutePath();
			BewConstants.SAVEDIR = path;

			if (!path.endsWith(".xls"))
				path = path.concat(".xls");
			dataToXls(path, methods, constant, expSetup);
		}
	}

	/**
	 * Saves an IntraExperiment in a XLS file.
	 * 
	 * @param exp
	 *            IntraExperiment to save.
	 * @param path
	 *            Input path (with file name) to save the Experiment.
	 */
	public static void saveXLSData(IExperiment exp, String path) {
		// Auxiliary variables
		List<Method> methods = exp.getMethods().getMetodos();
		ConstantConditions constant = exp.getConstantCondition();
		String[] expSetup = exp.getExpSetup();

		if (!path.endsWith(".xls"))
			path = path.concat(".xls");
		dataToXls(path, methods, constant, expSetup);
	}

	/**
	 * Method to save the data in to a XLS File using JXL.
	 * 
	 * @param path
	 *            The path that the user has selected in the File Chooser.
	 * @param methods
	 *            Methods that the user has created.
	 * @param constant
	 *            Constant Conditions that the user has created.
	 * @param expSetup
	 *            Experiment Setup that the user has created.
	 */
	public static void dataToXls(String path, List<Method> methods,
			ConstantConditions constant, String[] expSetup) {
		try {
			// We open a new Workbook to save the data inside
			WritableWorkbook workbook = Workbook.createWorkbook(new File(path));

			int index = 0;
			try {
				if (!methods.isEmpty() && methods != null) {
					// Create the xls sheets. One per ISheetConfigurator
					// (IWizardStep)
					for (Method method : methods) {
						saveMethod(workbook, index, method);
					}
					index++;
				}

			} catch (NullPointerException e) {

			}

			try {
				saveCondition(constant, workbook, index);
				saveExpSetup(expSetup, workbook, index);
			} catch (NullPointerException e) {

			}

			workbook.write();
			workbook.close();
		} catch (Exception e) {
			ShowDialog.showError(I18n.get("errorSavingTitle"),
					I18n.get("errorSaving"));
		}
	}

	/**
	 * Method to introduce the data for a method in a specific XLS Sheet.
	 * 
	 * @param workbook
	 *            The XLS File.
	 * @param index
	 *            The index for this Sheet in the XLS. Its a number.
	 * @param method
	 *            The method that we want to save.
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private static void saveMethod(WritableWorkbook workbook, int index,
			Method method) throws WriteException, RowsExceededException {
		// We create the specific Sheet
		workbook.createSheet(method.getName(), index);
		// We select it
		WritableSheet sheet = workbook.getSheet(index);

		// [0,0] to label of units
		// [0,1] to value of units
		Label cellUnits = new Label(0, 0, I18n.get("methodUnits"));
		sheet.addCell(cellUnits);
		Label units = new Label(1, 0, method.getUnits());
		sheet.addCell(units);

		int i = 1;
		int pos = 0;
		List<Condition> conditions = method.getArrayCondition().getElements();
		String condName = "";

		// Put number of conditions in second row
		Label conditionsCell = new Label(0, i, "Number of Conditions:");
		sheet.addCell(conditionsCell);
		conditionsCell = new Label(1, i, String.valueOf(conditions.size()));
		sheet.addCell(conditionsCell);

		i = 2;
		// Go over all Conditions for this method
		for (Condition c : conditions) {
			condName = c.getName();

			conditionsCell = new Label(pos, i, condName);
			sheet.addCell(conditionsCell);

			pos++;
		}

		i = 3;
		// We go over all the DataSerie for this method
		List<DataSeries> list = method.getDataSeries().getElements();
		for (DataSeries ds : list) {

			List<Object> dsValues = ds.getDataRow();
			pos = 0;
			// Saving all the data serie values
			for (Object value : dsValues) {
				try {
					if (value.toString().length() == 0) {
						EmptyCell cell = new EmptyCell(pos, i);
						sheet.addCell(cell);
					} else {
						try {
							if (!value.equals(Double.NaN)
									&& !value.toString().equals("NaN")) {
								Double numero = Double.parseDouble(value
										.toString());
								Number cell = new Number(pos, i, numero);
								sheet.addCell(cell);
							} else {
								Label cell = new Label(pos, i, "NaN");
								sheet.addCell(cell);
							}
						} catch (Exception e) {
							Label cell = new Label(pos, i, (String) value);
							sheet.addCell(cell);
						}
					}
				} catch (Exception e) {
					EmptyCell cell = new EmptyCell(pos, i);
					sheet.addCell(cell);
				}
				pos++;
			}
			i++;
		}
	}

	/**
	 * Method to introduce the data for a Constant Condition in a specific XLS
	 * Sheet.
	 * 
	 * @param constant
	 *            The Constant Condition to save.
	 * @param workbook
	 *            The XLS File.
	 * @param index
	 *            The index for this Sheet in the XLS. Its a number.
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private static void saveCondition(ConstantConditions constant,
			WritableWorkbook workbook, int index) throws WriteException,
			RowsExceededException {
		// Same idea
		workbook.createSheet(constant.getName(), index);
		WritableSheet sheet = workbook.getSheet(index);

		List<String> conditions = constant.getConstantConditions();
		List<String> conditionValues = constant.getConstantValues();
		List<String> constantUnits = constant.getConstantUnits();

		// We save the condition variables
		for (int pos = 0; pos < conditions.size(); pos++) {
			Object condition = conditions.get(pos);
			Object conditionValue = conditionValues.get(pos);
			Object units = constantUnits.get(pos);

			saveConditionVariable(condition, sheet, 0, pos);
			saveConditionVariable(conditionValue, sheet, 1, pos);
			saveConditionVariable(units, sheet, 2, pos);
		}
	}

	/**
	 * Saves the variables for a Condition in the XLS Sheet.
	 * 
	 * @param condValue
	 *            Value to save.
	 * @param sheet
	 *            Sheet on which data is stored.
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private static void saveConditionVariable(Object condValue,
			WritableSheet sheet, int col, int row)
			throws RowsExceededException, WriteException {
		try {
			if (condValue.toString().length() == 0) {
				EmptyCell cell = new EmptyCell(col, row);
				sheet.addCell(cell);
			} else {
				try {
					if (!condValue.equals(Double.NaN)
							&& !condValue.toString().equals("NaN")) {
						Double numero = Double
								.parseDouble(condValue.toString());
						Number cell = new Number(col, row, numero);
						sheet.addCell(cell);
					} else {
						Label cell = new Label(col, row, "NaN");
						sheet.addCell(cell);
					}
				} catch (Exception e) {
					Label cell = new Label(col, row, (String) condValue);
					sheet.addCell(cell);
				}
			}
		} catch (Exception e) {
			EmptyCell cell = new EmptyCell(col, row);
			sheet.addCell(cell);
		}
	}

	/**
	 * Method to save the Experiment Setup.
	 * 
	 * @param expSetup
	 *            Experiment Setup to save.
	 * @param workbook
	 *            The XLS File.
	 * @param index
	 *            The index for this Sheet in the XLS. Its a number.
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private static void saveExpSetup(String[] expSetup,
			WritableWorkbook workbook, int index) throws RowsExceededException,
			WriteException {
		workbook.createSheet(I18n.get("expSetup"), index);
		WritableSheet sheet = workbook.getSheet(index);

		// Set sheet as protected (no editable)
		sheet.getSettings().setProtected(true);

		// Create object for every editable cell
		// WritableCellFormat unlocked = new WritableCellFormat();
		// Set cell editable
		// unlocked.setLocked(false);

		int row = 0;
		for (String value : expSetup) {
			Label column = new Label(0, row, I18n.get("column" + row));
			sheet.addCell(column);
			try {
				// Label cell = new Label(1, row, value, unlocked);
				Label cell = new Label(1, row, value);
				sheet.addCell(cell);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				EmptyCell cell = new EmptyCell(1, row);
				sheet.addCell(cell);
			}
			row++;
		}
	}

	/**
	 * Method to save an intraExperiment into a XML file.
	 * 
	 * @param path
	 *            File Chooser path.
	 * @param methodsExp
	 *            Methods of the experiment.
	 * @param constant
	 *            Constant Condition of the experiment.
	 * @param expSetup
	 *            Experiment Setup.
	 */
	public static void intraToXml(String path, List<Method> methodsExp,
			ConstantConditions constant, String[] expSetup) {
		final Document document = FunctionConstants.builder.newDocument();

		// Root Element bml
		final Element bmlElement = document.createElement("ns0:bml");

		bmlElement.setAttribute("xmlns:ns0", BewConstants.NS_URL);
		bmlElement.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		bmlElement.setAttribute("xsi:schemaLocation",
				"http://www.example.org/bml " + BewConstants.SCHEMA_PATH);

		document.appendChild(bmlElement);

		final Element experimentElement = document.createElement("experiment");
		final Element constantsElement = document
				.createElement("constantConditions");
		final Element methods = document.createElement("methods");

		// Introducing Experiment Setup
		setExpSetupXml(expSetup, document, experimentElement);
		// End Experiment Setup

		// Introducing Constant Conditions
		setConstantXml(constant, document, constantsElement);
		// End Constant Conditions

		// Introducing methods
		setMethodsXml(methodsExp, document, methods);
		// End methods

		experimentElement.appendChild(methods);
		experimentElement.appendChild(constantsElement);
		bmlElement.appendChild(experimentElement);

		Source source;
		Result result;
		Result console;
		Transformer transformer;

		// We save in .xml
		source = new DOMSource(document);
		result = new StreamResult(new File(path));
		console = new StreamResult(System.out);

		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

			// Encryption
			byte[] keyBytes = BewConstants.KEYSTRING.getBytes("UTF-8");

			FunctionConstants.insertHash(path,
					FunctionConstants.getMD5Checksum(path));
			FunctionConstants.encrypt(BewConstants.IVBYTES, keyBytes, path);
		} catch (TransformerFactoryConfigurationError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to save an interExperiment into a XML file.
	 * 
	 * @param path
	 *            File Chooser path.
	 * @param methodsExp
	 *            Methods of the experiment.
	 * @param constant
	 *            Constant Condition of the experiment.
	 * @param expSetup
	 *            Experiment Setup.
	 * @param exp
	 *            Experiment to save.
	 */
	public static void interToXml(String path, List<Method> methodsExp,
			ConstantConditions constant, String[] expSetup,
			IExperiment exp) {
		// Xml file
		final Document document = FunctionConstants.builder.newDocument();
		// Map with IntraExps and colors
		Map<Object, Object> mapIntraColors = exp.getMapIntraExpsColors();
		// Map with IntraExps and dataSeries
		Map<Object, List<Object>> mapIntraRows = exp.getMapIntraExpsAndRows();

		// Root Element bml_inter
		final Element bmlElement = document.createElement("ns0:bml_inter");

		bmlElement.setAttribute("xmlns:ns0", BewConstants.NS_URL_INTER);
		bmlElement.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		bmlElement.setAttribute("xsi:schemaLocation",
				"http://www.example.org/bml_inter "
						+ BewConstants.SCHEMA_PATH_INTER);

		document.appendChild(bmlElement);

		// Main element nodes
		final Element experimentElement = document
				.createElement("interExperiment");
		final Element constantsElement = document
				.createElement("constantConditions");
		final Element comparedIntra = document
				.createElement("comparedIntraExp");
		final Element comparedMethod = document.createElement("comparedMethod");

		// Introducing Experiment Setup
		setExpSetupXml(expSetup, document, experimentElement);
		// End Experiment Setup

		// Introducing Constant Conditions
		setConstantXml(constant, document, constantsElement);
		// End Constant Conditions

		// Introducing comparedIntraExperiments
		// Set with intraExperiments
		Set<Object> intraExperiments = mapIntraColors.keySet();
		for (Object intra : intraExperiments) {
			// Create intraExperiments nodes
			final Element intraNode = document.createElement("intraExperiment");
			final Element intraConstants = document
					.createElement("intraConstantConditions");
			final Element intraRows = document.createElement("dataSerieRows");
			final Element intraColor = document.createElement("color");
			// Get intraExperiment from the Set
			IExperiment intraExp = (IExperiment) intra;

			// Set intraExperiment setup
			setExpSetupXml(intraExp.getExpSetup(), document, intraNode);
			// Set intraConstants
			setConstantXml(intraExp.getConstantCondition(), document,
					intraConstants);
			// Set dataSerie rows
			List<Object> rows = mapIntraRows.get(intra);
			String aux = "";
			for (Object row : rows) {
				// Adding rows
				if (aux.isEmpty())
					aux = aux.concat(String.valueOf(row));
				else
					aux = aux.concat("#" + String.valueOf(row));
			}
			intraRows.setTextContent(aux);
			// Set color
			Color color = (Color) mapIntraColors.get(intra);
			intraColor.setTextContent(String.valueOf(color.getRGB()));

			// Put nodes below intraExperiment node
			intraNode.appendChild(intraConstants);
			intraNode.appendChild(intraRows);
			intraNode.appendChild(intraColor);

			// Put intraExperiment node below comparedIntra node
			comparedIntra.appendChild(intraNode);
		}
		// End IntraExperiments

		// Introducing methods
		setMethodsXml(methodsExp, document, comparedMethod);
		// End methods

		experimentElement.appendChild(comparedIntra);
		experimentElement.appendChild(comparedMethod);
		experimentElement.appendChild(constantsElement);
		bmlElement.appendChild(experimentElement);

		Source source;
		Result result;
		Result console;
		Transformer transformer;

		// We save in .xml
		source = new DOMSource(document);
		result = new StreamResult(new File(path));
		console = new StreamResult(System.out);

		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

			// Encryption
			byte[] keyBytes = BewConstants.KEYSTRING.getBytes("UTF-8");

			FunctionConstants.insertHash(path,
					FunctionConstants.getMD5Checksum(path));
			FunctionConstants.encrypt(BewConstants.IVBYTES, keyBytes, path);
		} catch (TransformerFactoryConfigurationError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to put the experiment setup in a xml file.
	 * 
	 * @param expSetup
	 *            Experiment Setup data to save.
	 * @param document
	 *            Document on which data is stored.
	 * @param experimentElement
	 */
	private static void setExpSetupXml(String[] expSetup,
			final Document document, final Element experimentElement) {
		experimentElement.setAttribute("experimentName", expSetup[0]);
		experimentElement.setAttribute("organization", expSetup[2]);
		experimentElement.setAttribute("contact", expSetup[3]);
		experimentElement.setAttribute("date", expSetup[4]);
		experimentElement.setAttribute("publication", expSetup[5]);
		experimentElement.setAttribute("bioID", expSetup[7]);

		CDATASection authorsText = document.createCDATASection(expSetup[1]);
		final Element authors = document.createElement("authors");
		authors.appendChild(authorsText);

		CDATASection notesText = document.createCDATASection(expSetup[6]);
		final Element notes = document.createElement("notes");
		notes.appendChild(notesText);

		experimentElement.appendChild(authors);
		experimentElement.appendChild(notes);
	}

	/**
	 * Method to put the constant condition in a xml file.
	 * 
	 * @param constant
	 *            Constant Condition data to save.
	 * @param document
	 *            Document on which data is stored.
	 * @param constantsElement
	 */
	private static void setConstantXml(ConstantConditions constant,
			final Document document, final Element constantsElement) {
		int row = constant.getConstantConditions().size();
		List<String> conditionsList = constant.getConstantConditions();
		List<String> conditionValuesList = constant.getConstantValues();
		List<String> constantUnits = constant.getConstantUnits();

		for (int i = 0; i < row; i++) {
			String condition = conditionsList.get(i);
			String conditionValue = conditionValuesList.get(i);
			String units = constantUnits.get(i);

			final Element constantElement = document
					.createElement("constantCondition");

			constantElement.setAttribute("condition", condition);
			constantElement.setAttribute("conditionValue", conditionValue);
			constantElement.setAttribute("conditionUnits", units);

			constantsElement.appendChild(constantElement);
		}
	}

	/**
	 * Method to put the intraExperiment method/s in a xml file.
	 * 
	 * @param methodsExp
	 *            List with Methods to save in the XML.
	 * @param document
	 *            Document on which data is stored.
	 * @param comparedMethod
	 */
	private static void setMethodsXml(List<Method> methodsExp,
			final Document document, final Element comparedMethod) {
		for (Method m : methodsExp) {
			final Element method = document.createElement("method");
			method.setAttribute("methodName", m.getName());
			method.setAttribute("methodUnits", m.getUnits());

			final Element conditions = document.createElement("conditions");
			final Element dataSeriesSet = document
					.createElement("dataSeriesSet");

			method.appendChild(conditions);
			method.appendChild(dataSeriesSet);

			// Introducing method conditions
			for (Condition c : m.getArrayCondition().getElements()) {
				final Element condition = document.createElement("condition");

				condition.setAttribute("conditionName", c.getName());
				condition.setAttribute("conditionUnits", c.getUnits());

				String aux = "";
				int index = 0;
				int numCond = c.getConditionValues().size();
				for (Object condValue : c.getConditionValues()) {
					// Adding conditions
					aux = aux.concat(condValue.toString());
					if (index + 1 < numCond)
						aux = aux.concat("#");

					index++;
				}
				condition.setTextContent(aux);

				conditions.appendChild(condition);
			} // End Conditions

			// Introducing method DataSeries
			for (DataSeries ds : m.getDataSeries().getElements()) {
				final Element dataSerie = document.createElement("dataSerie");
				final Element conditionValues = document
						.createElement("conditionValues");
				final Element measurements = document
						.createElement("measurements");

				dataSerie.appendChild(conditionValues);
				dataSerie.appendChild(measurements);

				// We use dataRow to take dataSerie data
				List<Object> dataRow = ds.getDataRow();
				int numCond = ds.getConditionSize();
				int i = 0;
				int j = numCond;
				String aux = "";
				String aux2 = "";
				for (Object dr : dataRow) {
					if (i < numCond) {
						// Adding conditions
						aux = aux.concat(dr.toString());
						if (i + 1 < numCond)
							aux = aux.concat("#");

						conditionValues.setTextContent(aux);
						i++;
					}

					else {
						// Adding measurements
						aux2 = aux2.concat(dr.toString());
						if (j + 1 < dataRow.size())
							aux2 = aux2.concat("#");

						measurements.setTextContent(aux2);
						j++;
					}

				}

				dataSeriesSet.appendChild(dataSerie);
			} // End DataSeries

			comparedMethod.appendChild(method);
		}
	}
}
