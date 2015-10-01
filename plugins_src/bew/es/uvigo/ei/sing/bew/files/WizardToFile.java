package es.uvigo.ei.sing.bew.files;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTable;
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

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.filters.XlsSaveFilter;
import es.uvigo.ei.sing.bew.files.filters.XmlSaveFilter;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;
import es.uvigo.ei.sing.bew.view.dialogs.WizardDialog;

/**
 * This class provides methods to save data into xls or xml file from the
 * wizard.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class WizardToFile {
	private WizardDialog wizard;

	/**
	 * Default constructor.
	 * 
	 * @param wizardD
	 *            Wizard.
	 */
	public WizardToFile(WizardDialog wizardD) {
		// TODO Auto-generated constructor stub
		this.wizard = wizardD;
	}

	/**
	 * Opens a fileChooser to save the data in XML.
	 */
	public void saveXMLData() {
		CustomFileChooser cfc = new CustomFileChooser();
		cfc.setCurrentDirectory(new File(BewConstants.SAVEDIR));

		cfc.setFileFilter(new XmlSaveFilter());
		cfc.setAcceptAllFileFilterUsed(false);

		int retrieval = cfc.showSaveDialog(null);

		if (retrieval == JFileChooser.APPROVE_OPTION) {
			String path = cfc.getSelectedFile().getAbsolutePath();
			BewConstants.SAVEDIR = path;

			if (!path.endsWith(".xml"))
				path = path.concat(".xml");
			dataToXml(path);
		}
	}

	/**
	 * Opens a fileChooser to save the data in XLS (DEPRECATED).
	 */
	public void saveXLSData() {
		CustomFileChooser cfc = new CustomFileChooser();
		cfc.setCurrentDirectory(new File(BewConstants.SAVEDIR));

		cfc.setFileFilter(new XlsSaveFilter());
		cfc.setAcceptAllFileFilterUsed(false);

		int retrieval = cfc.showSaveDialog(null);

		if (retrieval == JFileChooser.APPROVE_OPTION) {
			String path = cfc.getSelectedFile().getAbsolutePath();
			BewConstants.SAVEDIR = path;

			if (!path.endsWith(".xls"))
				path = path.concat(".xls");
			dataToXls(path);
		}
	}

	/**
	 * Method to save data from Wizard to XLS (DEPRECATED).
	 * 
	 * @param path
	 *            File path from FileChooser.
	 */
	private void dataToXls(String path) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(new File(path));

			List<IWizardStep> steps = this.wizard.getSteps();

			int index = 0;
			// Create the xls sheets. One per ISheetConfigurator (IWizardStep)
			for (IWizardStep step : steps) {
				JTable table = step.getDataTable();

				// Saving Experiment Setup first
				if (step.getSheetName().equals(I18n.get("conditionSheetName"))) {
					WritableSheet hojaSetup = workbook.createSheet(
							I18n.get("expSetup"), index);

					// Set sheet as protected (no editable)
					hojaSetup.getSettings().setProtected(true);

					// Create object for every editable cell
					// WritableCellFormat unlocked = new WritableCellFormat();
					// Set cell editable
					// unlocked.setLocked(false);

					int row = 0;
					for (String value : step.getExpSetup()) {
						// Label column = new Label(0, row, I18n.get("column"
						// + row), unlocked);
						Label column = new Label(0, row, I18n.get("column"
								+ row));
						hojaSetup.addCell(column);
						try {
							Label cell = new Label(1, row, value);
							hojaSetup.addCell(cell);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							EmptyCell cell = new EmptyCell(1, row);
							hojaSetup.addCell(cell);
						}
						row++;
					}
					index++;

					// We save the other data in each Step (like tables)
					WritableSheet hoja = workbook.createSheet(
							step.getSheetName(), index);

					fillWorkbook(0, hoja, table);
				} else {
					if (table.getRowCount() != 0 || table.getColumnCount() != 0) {
						// We save the other data in each Step (like tables)
						workbook.createSheet(step.getSheetName(), index);
						WritableSheet hoja = workbook.getSheet(index);

						// [0,0] to label of units
						// [0,1] to value of units
						Label cellUnits = new Label(0, 0,
								I18n.get("methodUnits"));
						hoja.addCell(cellUnits);
						Label units = new Label(1, 0, step.getUnits());
						hoja.addCell(units);

						// We need first row to introduce units for each method
						fillWorkbook(1, hoja, table);
					}
				}
				index++;

			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			ShowDialog.showError(I18n.get("errorSavingTitle"),
					I18n.get("errorSaving"));
		}
	}

	/**
	 * Fills the XLS Sheet with data (DEPRECATED).
	 * 
	 * @param rowStart
	 *            Row to start filling the data.
	 * @param sheet
	 *            Sheet to fill the data-
	 * @param table
	 *            Table to get the data.
	 * @throws Exception
	 */
	private void fillWorkbook(int rowStart, WritableSheet sheet, JTable table)
			throws Exception {

		for (int fil = 0; fil < table.getRowCount(); fil++) {
			for (int col = 0; col < table.getColumnCount(); col++) {
				Object value = table.getModel().getValueAt(fil, col);
				try {
					if (value.toString().length() == 0) {
						EmptyCell cell = new EmptyCell(col, rowStart);
						sheet.addCell(cell);
					} else {
						try {
							if (!value.equals(Double.NaN)
									&& !value.toString().equals("NaN")) {
								Double numero = Double.parseDouble(value
										.toString());
								Number cell = new Number(col, rowStart, numero);
								sheet.addCell(cell);
							} else {
								Label cell = new Label(col, rowStart, "NaN");
								sheet.addCell(cell);
							}
						} catch (Exception e) {
							Label cell = new Label(col, rowStart,
									(String) value);
							sheet.addCell(cell);
						}
					}
				} catch (Exception e) {
					EmptyCell cell = new EmptyCell(col, rowStart);
					sheet.addCell(cell);
				}
			}
			rowStart++;
		}
	}

	/**
	 * Method to save data from wizard to XML.
	 * 
	 * @param path
	 *            File path from FileChooser.
	 */
	private void dataToXml(String path) {
		List<IWizardStep> steps = this.wizard.getSteps();

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

		for (IWizardStep ws : steps) {
			if (ws.getSheetName().equals(I18n.get("conditionSheetName"))) {
				// Saving ExperimentSetup
				String[] expSetup = ws.getExpSetup();

				experimentElement.setAttribute("experimentName", expSetup[0]);
				experimentElement.setAttribute("organization", expSetup[2]);
				experimentElement.setAttribute("contact", expSetup[3]);
				experimentElement.setAttribute("date", expSetup[4]);
				experimentElement.setAttribute("publication", expSetup[5]);
				experimentElement.setAttribute("bioID", expSetup[7]);

				CDATASection authorsText = document
						.createCDATASection(expSetup[1]);
				final Element authors = document.createElement("authors");
				authors.appendChild(authorsText);

				CDATASection notesText = document
						.createCDATASection(expSetup[6]);
				final Element notes = document.createElement("notes");
				notes.appendChild(notesText);

				experimentElement.appendChild(authors);
				experimentElement.appendChild(notes);

				// Saving ConstantConditions
				int row = ws.getDataTable().getRowCount();
				for (int i = 0; i < row; i++) {
					String condition = (String) ws.getDataTable().getValueAt(i,
							0);
					String conditionValue = (String) ws.getDataTable()
							.getValueAt(i, 1);
					String units = (String) ws.getDataTable().getValueAt(i, 2);

					final Element constantElement = document
							.createElement("constantCondition");

					constantElement.setAttribute("condition", condition);
					constantElement.setAttribute("conditionValue",
							conditionValue);
					constantElement.setAttribute("conditionUnits", units);

					constantsElement.appendChild(constantElement);
				}
			}

			// Saving methods
			else {
				JTable table = ws.getDataTable();
				List<String> condNames = ws.getConditionNames();
				List<String> condUnits = ws.getConditionUnits();

				if (!condNames.isEmpty()
						&& (table.getRowCount() != 0 || table.getColumnCount() != 0)) {
					final Element method = document.createElement("method");
					method.setAttribute("methodName", ws.getSheetName());
					method.setAttribute("methodUnits", ws.getUnits());

					final Element conditions = document
							.createElement("conditions");
					final Element dataSeriesSet = document
							.createElement("dataSeriesSet");

					method.appendChild(conditions);
					method.appendChild(dataSeriesSet);

					JTable dataTable = ws.getDataTable();

					// Saving methods Conditions
					for (int col = 0; col < ws.getNumConditions(); col++) {
						final Element condition = document
								.createElement("condition");

						// We save the number of the column
						condition.setAttribute("conditionName",
								condNames.get(col));
						condition.setAttribute("conditionUnits",
								condUnits.get(col));
						// Go over each column (each column is a condition)
						String aux = "";
						for (int fil = 0; fil < dataTable.getRowCount(); fil++) {
							// Saving condition values
							String value = dataTable.getValueAt(fil, col)
									.toString();
							if (value.length() != 0) {
								aux = aux.concat(value);
								aux = aux.concat("#");
							}
						}
						// If end with ',' we delete it to do split later
						if (aux.endsWith("#")) {
							aux = aux.substring(0, aux.length() - 1);
						}

						condition.setTextContent(aux);

						conditions.appendChild(condition);
					}

					// Saving DataSeries
					for (int fil = 0; fil < dataTable.getRowCount(); fil++) {
						final Element dataSerie = document
								.createElement("dataSerie");
						final Element conditionValues = document
								.createElement("conditionValues");
						final Element measurements = document
								.createElement("measurements");

						dataSerie.appendChild(conditionValues);
						dataSerie.appendChild(measurements);
						// Saving dataSeries conditions
						String aux = "";
						for (int col = 0; col < ws.getNumConditions(); col++) {
							aux = aux.concat(ws.getDataTable()
									.getValueAt(fil, col).toString());
							if (col + 1 < ws.getNumConditions())
								aux = aux.concat("#");
						}
						conditionValues.setTextContent(aux);
						// Saving measurements
						String aux2 = "";
						for (int col = ws.getNumConditions(); col < ws
								.getDataTable().getColumnCount(); col++) {
							aux2 = aux2.concat(ws.getDataTable()
									.getValueAt(fil, col).toString());
							if (col + 1 < ws.getDataTable().getColumnCount())
								aux2 = aux2.concat("#");
						}
						measurements.setTextContent(aux2);

						dataSeriesSet.appendChild(dataSerie);
					}
					methods.appendChild(method);
				}
			}

		}
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
}
