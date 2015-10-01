package es.uvigo.ei.sing.bew.files;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.exceptions.RepeatedExpNameException;
import es.uvigo.ei.sing.bew.exceptions.RepeatedMethodNameException;
import es.uvigo.ei.sing.bew.files.filters.XlsSaveFilter;
import es.uvigo.ei.sing.bew.files.filters.XmlSaveFilter;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.InterLabExperiment;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.sheets.ImportMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.sheets.SetupSheetConfig;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;
import es.uvigo.ei.sing.bew.view.panels.PreviewPanel;

/**
 * This class provides methods to retrieve data from xls or xml files.
 * 
 * @author Gael P�rez Rodr�guez
 * 
 */
public final class FileToData {
	/**
	 * Loads and XML from a fileChooser. This Method have to decrypt the file
	 * before load it.
	 * 
	 * @param temporaryFiles
	 *            Temporary files that will contain the decrypted XMLs.
	 * @return String with the path of the file.
	 */
	public static String loadXMLFile(List<File> temporaryFiles) {
		// NOTE: The XLS part of the code is unused now.
		final CustomFileChooser cfc = new CustomFileChooser();
		final PreviewPanel previewerPanel = new PreviewPanel();
		String path = "";

		// Decryption
		final byte[] keyBytes;
		try {
			keyBytes = BewConstants.KEYSTRING.getBytes("UTF-8");

			cfc.setCurrentDirectory(new File(BewConstants.LOADDIR));
			cfc.setDialogTitle(I18n.get("loadFile"));

			cfc.setFileFilter(new XmlSaveFilter());
			cfc.setAcceptAllFileFilterUsed(false);

			// Set TextPreviewer
			cfc.setAccessory(previewerPanel);

			// Listener for visualizing the selected file in the chooser
			cfc.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(
							JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
							&& e.getNewValue() != null) {
						// XML filter selected
						if (cfc.getFileFilter().getClass()
								.equals(XmlSaveFilter.class)) {
							File file = (File) e.getNewValue();

							byte[] decrypted;
							String originalPath;

							try {
								// Set the decrypted text in the previewer
								// text
								originalPath = file.getAbsolutePath();
								decrypted = FunctionConstants.decrypt(
										BewConstants.IVBYTES, keyBytes,
										originalPath);

								previewerPanel.getPreviewer().configure(
										originalPath, decrypted);
							} catch (Exception e1) {
								previewerPanel.getPreviewer().clearText();
							}
						}
						// XLS filter selected
						else {
							// Nothing to do now
							File file = (File) e.getNewValue();
							previewerPanel.getPreviewer().configure(file);
						}
					}
				}
			});
			int returnVal = cfc.showOpenDialog(null);

			// If selected file is xml
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				temporaryFiles.clear();

				File file = cfc.getSelectedFile();

				// BewConstants.LOAD_DIR = files[0].getAbsolutePath();
				BewConstants.LOADDIR = file.getAbsolutePath();

				// Create the decrypted file from the original encrypted one.
				// Its a temporal file
				// if (files[0].getAbsolutePath().endsWith(".xml")) {
				if (file.getAbsolutePath().endsWith(".xml")) {
					// Get decrypted file contents
					List<byte[]> filesContent = new ArrayList<byte[]>(
							previewerPanel.getPreviewer().getContent().values());

					// Create one temporal file for each decrypted file
					for (int i = 0; i < filesContent.size(); i++) {
						temporaryFiles.add(File.createTempFile(
								BewConstants.TEMPFILENAME, ".xml"));

						Path tempPath = Paths.get(temporaryFiles.get(i)
								.getAbsolutePath());
						Files.write(tempPath, filesContent.get(i));
					}
					previewerPanel.getPreviewer().clearContent();
				}

				path = file.getAbsolutePath();
			}

		} catch (IOException e2) {
			// e2.printStackTrace();
		}

		return path;
	}

	/**
	 * Loads an XLS file from the fileChooser.
	 * 
	 * @return String with the path of the selected file.
	 */
	public static String loadXLSFile(List<File> temporaryFiles) {
		final CustomFileChooser cfc = new CustomFileChooser();
		String path = "";

		cfc.setCurrentDirectory(new File(BewConstants.LOADDIR));
		cfc.setAcceptAllFileFilterUsed(false);
		cfc.setDialogTitle(I18n.get("loadFile"));

		cfc.setFileFilter(new XlsSaveFilter());
		cfc.setAcceptAllFileFilterUsed(false);

		int returnVal = cfc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			temporaryFiles.clear();

			File file = cfc.getSelectedFile();

			BewConstants.LOADDIR = file.getAbsolutePath();

			path = file.getAbsolutePath();
		}

		return path;
	}

	/**
	 * Method to load an XLS to memory.
	 * 
	 * @param path
	 *            String with the path of the file.
	 * @return List<IWizardStep> with the data of each xls sheet.
	 * @throws IOException
	 *             Error during the load process.
	 */
	public static List<IWizardStep> xlsToData(String path) throws IOException {
		List<IWizardStep> steps = new ArrayList<IWizardStep>();

		Sheet auxCondSheet = null;
		String[] auxExpSetup = new String[8];
		List<Sheet> methodSheets = new ArrayList<Sheet>();

		try {
			// Select source file
			Workbook archivoExcel = Workbook.getWorkbook(new File(path));
			// Go over all the Sheets in the file
			for (Sheet hoja : archivoExcel.getSheets()) {
				if (hoja.getName().equals(I18n.get("conditionSheetName"))) {
					auxCondSheet = hoja;
				} else if (hoja.getName().equals(I18n.get("expSetup"))) {
					auxExpSetup = FileToData.obtainExpSetup(hoja);
				} else {
					// Add method sheet
					methodSheets.add(hoja);
				}
			}

			if (auxCondSheet != null) {
				// Create the sheet for Experiment Setup and Constant Conditions
				SetupSheetConfig isc = new SetupSheetConfig(auxCondSheet,
						auxExpSetup);
				steps.add(isc);
			}

			// Need to load method sheets at the end because setup sheet can add
			// new conditions and the method combos would be loaded.
			for (Sheet s : methodSheets) {
				steps.add(new ImportMethodSheetConfigurator(s));
			}

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

		return steps;
	}

	/**
	 * Method to obtain the data of Experiment Setup.
	 * 
	 * @param sheet
	 *            Sheet with this data.
	 * @return String[] with the data of experiment setup in each position.
	 */
	private static String[] obtainExpSetup(Sheet sheet) {
		String[] ret = new String[8];

		int totalRow = sheet.getRows();

		try {
			for (int row = 0; row < totalRow; row++) {
				Cell cell = sheet.getCell(1, row);
				ret[row] = cell.getContents();
			}
		} catch (Exception e) {
			// System.out.println("No value in column 1 in XLS");
		}

		return ret;
	}

	/**
	 * Method to do a quickly load of a XML file to memory.
	 * 
	 * @param path
	 *            Path of the file.
	 * @return List<IWizardStep> with the data of the xml.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws IllegalArgumentException
	 */
	public static IExperiment xmlToData(File file)
			throws ParserConfigurationException, SAXException, IOException,
			IllegalArgumentException, URISyntaxException, Exception {
		Document bml = null;
		IExperiment exp;

		try {
			// If Inter
			if (FunctionConstants.isXmlInter(file)) {
				exp = loadInterXml(bml, file);
			} else {
				exp = loadIntraXml(bml, file);
			}
		} finally {
			// Trying to delete the temporal file
			file.delete();
		}

		return exp;
	}

	/**
	 * Method to load an IntraExperiment xml file and create the IntraExperiment
	 * with the information.
	 * 
	 * @param bml
	 *            Xml document.
	 * @param temporaryFile
	 *            Temporary decrypted XML to load.
	 * @return Created IntraExperiment with the information of the file.
	 * @throws DOMException
	 * @throws Exception
	 */
	private static IExperiment loadIntraXml(Document bml, File temporaryFile)
			throws DOMException, Exception {
		// Obtain the hash from the file
		String hash = FunctionConstants.getAndDeleteHash(temporaryFile
				.getAbsolutePath());
		// Create IntraExperiment
		Experiment experiment = new Experiment();

		// Compare the hash of the decrypted file
		if (FunctionConstants.compareHashes(hash,
				temporaryFile.getAbsolutePath())) {
			try {
				// Trying to validate online Schema
				bml = FunctionConstants
						.loadDocumentAndValidateOnlineXSD(temporaryFile
								.getAbsolutePath());
			} catch (SAXParseException e) {
				// If something goes wrong, we validate with local Schema
				bml = FunctionConstants.loadDocumentAndValidateLocalXSD(
						temporaryFile.getAbsolutePath(),
						FileToData.class.getResource("/xml/"
								+ BewConstants.INTRASCHEMA));
			}

			if (bml != null) {
				// Obtain Experiment attributes
				Node bmlNode = bml.getFirstChild();
				Node expNode = bmlNode.getFirstChild();

				NamedNodeMap expAttr = expNode.getAttributes();

				// Variables for the ConstantConditionSheet (Experiment
				// Setup)
				String[] arrayExpSetup = new String[8];
				String authorsText = "";
				String notesText = "";

				// Obtain and validate if experiment name is already created
				// in the
				// clipboard
				String expName = expAttr.getNamedItem("experimentName")
						.getTextContent().trim();

				if (FunctionConstants.validateExperimentNames(expName)) {
					// Get Experiment child nodes
					NodeList expChilds = expNode.getChildNodes();

					// Experiment child nodes
					NodeList methodsNodes = null;
					NodeList constantNodes = null;

					// Variable to save the data of each method (dynamic
					// matrix)
					List<List<Object>> data = new ArrayList<List<Object>>();

					// Getting child nodes
					for (int j = 0; j < expChilds.getLength(); j++) {
						Node expChild = expChilds.item(j);
						String expChildName = expChild.getNodeName();

						if (expChildName.toLowerCase().trim().equals("authors")) {
							authorsText = expChild.getTextContent().trim();
						} else if (expChildName.toLowerCase().trim()
								.equals("notes")) {
							notesText = expChild.getTextContent().trim();
						} else if (expChildName.toLowerCase().trim()
								.equals("methods")) {
							methodsNodes = expChild.getChildNodes();
						} else if (expChildName.toLowerCase().trim()
								.equals("constantconditions")) {
							constantNodes = expChild.getChildNodes();
						}
					}

					// Get ExpSetup
					getXmlSetup(expAttr, arrayExpSetup, expName, authorsText,
							notesText);

					experiment.setExpSetup(arrayExpSetup);
					// End ExpSetup

					Integer numCondValues = 0;
					// Variables for Method Conditions
					List<String> condNames = new ArrayList<String>();
					List<String> condUnits = new ArrayList<String>();

					// Obtaining methods...
					if (methodsNodes != null) {
						// List with saved method names to validate them
						List<String> savesMethodNames = new ArrayList<String>();
						for (int index = 0; index < methodsNodes.getLength(); index++) {
							Node method = methodsNodes.item(index);

							// Saving method name
							String methodName = method.getAttributes().item(0)
									.getTextContent().trim();
							if (!savesMethodNames.contains(methodName)) {
								savesMethodNames.add(methodName);

								String methodUnis = method.getAttributes()
										.item(1).getTextContent().trim();

								// Obtaining method childs...
								NodeList methodNodeList = method
										.getChildNodes();

								for (int i = 0; i < methodNodeList.getLength(); i++) {
									Node methodChild = methodNodeList.item(i);
									String methodChildName = methodChild
											.getNodeName();

									// If the child is a DataSerieSet...
									if (methodChildName.toLowerCase().trim()
											.equals("dataseriesset")) {
										NodeList dataSerieSet = methodChild
												.getChildNodes();

										// Go over all the dataSeries
										for (int i1 = 0; i1 < dataSerieSet
												.getLength(); i1++) {
											// Variable to save dataSerie
											// values
											List<Object> dataSerieList = new ArrayList<Object>();

											Node dataSerie = dataSerieSet
													.item(i1);
											String[] cond = dataSerie
													.getFirstChild()
													.getTextContent().trim()
													.split("#");
											String[] meas = dataSerie
													.getFirstChild()
													.getNextSibling()
													.getTextContent().trim()
													.split("#");

											// Save Condition values in the
											// dataSerie
											int condNumber = 0;
											int res = 0;
											String aux = "";
											for (String c : cond) {
												String[] splitted = c
														.split(BewConstants.AND);
												for (String s : splitted) {
													s = s.trim();

													// Validate each condValue
													res = FunctionConstants
															.condValueValidation(
																	s,
																	condNames
																			.get(condNumber),
																	false);

													if (aux.isEmpty()) {
														aux = validateConditionValue(
																res, s);
													} else {
														aux += BewConstants.AND
																+ validateConditionValue(
																		res, s);
													}
												}
												dataSerieList.add(aux);
												aux = "";
												condNumber++;
											}

											for (String ms : meas)
												dataSerieList.add(ms);

											data.add(dataSerieList);
										}
									}

									// Obtaining conditions
									else if (methodChildName.toLowerCase()
											.trim().equals("conditions")) {
										NodeList conditions = methodChild
												.getChildNodes();

										// Saving numConditions
										numCondValues = conditions.getLength();

										// Saving all conditions and its
										// values
										int res = 0;
										for (int i1 = 0; i1 < numCondValues; i1++) {
											Node condition = conditions
													.item(i1);

											// Saving condition name
											String condName = condition
													.getAttributes()
													.getNamedItem(
															"conditionName")
													.getTextContent().trim();

											// Condition validation
											res = FunctionConstants
													.condValidation(condName,
															false);

											condNames.add(validateCondition(
													res, condName));

											// Saving condition units
											condUnits.add(condition
													.getAttributes()
													.getNamedItem(
															"conditionUnits")
													.getTextContent().trim());

										}
									}
								}
								experiment.addMetodo(FunctionConstants
										.createMethod(methodName, methodUnis,
												FunctionConstants
														.dataToArray(data),
												condNames.toArray(), condUnits
														.toArray(),
												numCondValues, experiment));

								condNames.clear();
								condUnits.clear();
								data.clear();
							} else {
								// Method name is repeated
								throw new RepeatedMethodNameException();
							}
						}
					}

					// Obtaining constant conditions
					if (constantNodes != null) {
						data.clear();

						getXmlConstant(constantNodes, data);

						experiment.addConstantCondition(FunctionConstants
								.createConstant(I18n.get("conditionSheetName"),
										FunctionConstants.dataToArray(data)));
					}
				} else {
					// Experiment already created
					throw new RepeatedExpNameException();
				}
			}
		} else {
			throw new Exception();
		}

		return experiment;
	}

	/**
	 * Method to load an InterExperiment xml file and create the InterExperiment
	 * with the information.
	 * 
	 * @param bml
	 *            Xml document.
	 * @param temporaryFile
	 *            Temporary decrypted xml.
	 * @return Created InterExperiment with the information of the file.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	private static IExperiment loadInterXml(Document bml, File temporaryFile)
			throws ParserConfigurationException, SAXException, IOException,
			IllegalArgumentException, URISyntaxException, Exception {
		// Obtain the hash from the file
		String hash = FunctionConstants.getAndDeleteHash(temporaryFile
				.getAbsolutePath());

		// Create IntraExperiment
		InterLabExperiment experiment = new InterLabExperiment();
		Map<Object, List<Object>> mapIntraExpsRows = new HashMap<Object, List<Object>>();
		Map<Object, Object> expColors = new HashMap<Object, Object>();

		if (FunctionConstants.compareHashes(hash,
				temporaryFile.getAbsolutePath())) {
			try {
				// Trying to validate online Schema
				bml = FunctionConstants
						.loadDocumentAndValidateOnlineXSD(temporaryFile
								.getAbsolutePath());
			} catch (SAXParseException e) {
				// If something goes wrong, we validate with local Schema
				bml = FunctionConstants.loadDocumentAndValidateLocalXSD(
						temporaryFile.getAbsolutePath(),
						FileToData.class.getResource("/xml/"
								+ BewConstants.INTERSCHEMA));
			}

			if (bml != null) {
				// Obtain Experiment attributes
				Node bmlNode = bml.getFirstChild();
				Node expNode = bmlNode.getFirstChild();

				NamedNodeMap expAttr = expNode.getAttributes();

				// Variables for the ConstantConditionSheet (Experiment
				// Setup)
				String[] arrayExpSetup = new String[8];
				String authorsText = "";
				String notesText = "";

				// Obtain and validate if experiment name is already created
				// in the
				// clipboard
				String expName = expAttr.getNamedItem("experimentName")
						.getTextContent().trim();

				if (FunctionConstants.validateExperimentNames(expName)) {
					// Get Experiment child nodes
					NodeList expChilds = expNode.getChildNodes();

					// Experiment child nodes
					NodeList comparedIntra = null;
					NodeList comparedMethod = null;
					NodeList constantNodes = null;

					// Variable to save the data of each method (dynamic
					// matrix)
					List<List<Object>> data = new ArrayList<List<Object>>();

					// Getting child nodes
					for (int j = 0; j < expChilds.getLength(); j++) {
						Node expChild = expChilds.item(j);
						String expChildName = expChild.getNodeName();

						if (expChildName.toLowerCase().trim().equals("authors")) {
							authorsText = expChild.getTextContent().trim();
						} else if (expChildName.toLowerCase().trim()
								.equals("notes")) {
							notesText = expChild.getTextContent().trim();
						} else if (expChildName.toLowerCase().trim()
								.equals("comparedintraexp")) {
							comparedIntra = expChild.getChildNodes();
						} else if (expChildName.toLowerCase().trim()
								.equals("comparedmethod")) {
							comparedMethod = expChild.getChildNodes();
						} else if (expChildName.toLowerCase().trim()
								.equals("constantconditions")) {
							constantNodes = expChild.getChildNodes();
						}
					}

					// Get ExpSetup
					getXmlSetup(expAttr, arrayExpSetup, expName, authorsText,
							notesText);

					experiment.setExpSetup(arrayExpSetup);
					// End ExpSetup

					// Get constantConditions
					if (constantNodes != null) {
						getXmlConstant(constantNodes, data);

						experiment.setConstantCondition(FunctionConstants
								.createConstant(I18n.get("conditionSheetName"),
										FunctionConstants.dataToArray(data)));
						data.clear();
					}
					// End constant

					// Get comparedIntraExp
					Node intraExp;
					String intraExpName;
					List<String> auxDataSeriesRow = new ArrayList<String>();
					List<Integer> dataSeriesRow = new ArrayList<Integer>();
					Color color = null;
					for (int i = 0; i < comparedIntra.getLength(); i++) {
						intraExp = comparedIntra.item(i);

						// Obtain IntraExperiment child nodes
						NodeList intraChilds = intraExp.getChildNodes();
						for (int j = 0; j < intraChilds.getLength(); j++) {
							Node intraChild = intraChilds.item(j);
							intraExpName = intraChild.getNodeName();

							if (intraExpName.toLowerCase().trim()
									.equals("authors")) {
								authorsText = intraChild.getTextContent()
										.trim();
							} else if (intraExpName.toLowerCase().trim()
									.equals("notes")) {
								notesText = intraChild.getTextContent().trim();
							} else if (intraExpName.toLowerCase().trim()
									.equals("intraconstantconditions")) {
								constantNodes = intraChild.getChildNodes();
							} else if (intraExpName.toLowerCase().trim()
									.equals("dataserierows")) {
								auxDataSeriesRow = Arrays.asList(intraChild
										.getTextContent().trim().split("#"));
								// Convert String into Integer
								for (String s : auxDataSeriesRow)
									dataSeriesRow.add(Integer.valueOf(s));
							} else if (intraExpName.toLowerCase().trim()
									.equals("color")) {
								// Convert String to Color
								color = Color.decode(intraChild
										.getTextContent().trim());
							}
						}

						expAttr = intraExp.getAttributes();
						// Get ExpSetup
						getXmlSetup(expAttr, arrayExpSetup, expAttr
								.getNamedItem("experimentName")
								.getTextContent().trim(), authorsText,
								notesText);

						// Get constantConditions and save them in data
						getXmlConstant(constantNodes, data);

						// Create IntraExp
						Experiment intraExperiment = new Experiment();
						// Set IntraSetup
						intraExperiment.setExpSetup(arrayExpSetup);
						// Set IntraConstants
						intraExperiment.addConstantCondition(FunctionConstants
								.createConstant(I18n.get("conditionSheetName"),
										FunctionConstants.dataToArray(data)));

						// Put Intra and dataSeries in map
						mapIntraExpsRows.put(intraExperiment,
								new ArrayList<Object>(dataSeriesRow));
						// Put Intra and color in map
						expColors.put(intraExperiment, color);

						data.clear();
						dataSeriesRow.clear();
					}

					experiment.setIntraColors(expColors);
					experiment.setIntraExperiments(mapIntraExpsRows);
					// End comparedIntraExp

					Integer conditionsLength = 0;
					int numConditions = 0;

					// Obtaining methods...
					if (comparedMethod != null) {
						// List with saved method names to validate them
						List<String> savesMethodNames = new ArrayList<String>();
						Node method = comparedMethod.item(0);

						// Saving method name
						String methodName = method.getAttributes().item(0)
								.getTextContent().trim();
						if (!savesMethodNames.contains(methodName)) {
							savesMethodNames.add(methodName);

							String methodUnis = method.getAttributes().item(1)
									.getTextContent().trim();

							// Obtaining method childs...
							NodeList methodNodeList = method.getChildNodes();
							List<Condition> conditionList = new ArrayList<Condition>();
							for (int i = 0; i < methodNodeList.getLength(); i++) {
								Node methodChild = methodNodeList.item(i);
								String methodChildName = methodChild
										.getNodeName();

								// If the child is a DataSerieSet...
								if (methodChildName.toLowerCase().trim()
										.equals("dataseriesset")) {
									NodeList dataSerieSet = methodChild
											.getChildNodes();

									// Go over all the dataSeries
									for (int i1 = 0; i1 < dataSerieSet
											.getLength(); i1++) {
										// Variable to save dataSerie values
										List<Object> dataSerieList = new ArrayList<Object>();

										Node dataSerie = dataSerieSet.item(i1);
										String[] cond = dataSerie
												.getFirstChild()
												.getTextContent().trim()
												.split("#");
										String[] meas = dataSerie
												.getFirstChild()
												.getNextSibling()
												.getTextContent().trim()
												.split("#");

										int condNumber = 0;
										int res = 0;
										String aux = "";
										for (String c : cond) {
											String[] splitted = c
													.split(BewConstants.AND);
											for (String s : splitted) {
												s = s.trim();
												// Validate each condValue
												res = FunctionConstants
														.condValueValidation(
																s,
																conditionList
																		.get(condNumber)
																		.getName(),
																false);

												if (aux.isEmpty()) {
													aux = validateConditionValue(
															res, s);
												} else {
													aux += BewConstants.AND
															+ validateConditionValue(
																	res, s);
												}
											}
											dataSerieList.add(aux);
											aux = "";
											condNumber++;
										}

										for (String ms : meas)
											dataSerieList.add(ms);

										data.add(dataSerieList);
									}
								}

								// Obtaining conditions
								else if (methodChildName.toLowerCase().trim()
										.equals("conditions")) {
									NodeList conditions = methodChild
											.getChildNodes();

									// Saving numConditions
									conditionsLength = conditions.getLength();
									String name;
									List<String> previousName = new ArrayList<String>();
									String units;
									int res = 0;
									// Saving all conditions and its values
									for (int i1 = 0; i1 < conditionsLength; i1++) {
										Node condition = conditions.item(i1);

										// Saving condition name
										name = condition.getAttributes()
												.getNamedItem("conditionName")
												.getTextContent().trim();

										// Condition validation
										res = FunctionConstants.condValidation(
												name, false);
										name = validateCondition(res, name);

										// Conditions are repeated in the xml
										// (each Intra give their conditions)
										if (!previousName.contains(name)) {
											previousName.add(name);
											numConditions++;
										}
										// Saving condition units
										units = condition.getAttributes()
												.getNamedItem("conditionUnits")
												.getTextContent().trim();
										Object[] condValues = condition
												.getTextContent().split("#");
										conditionList.add(new Condition(name,
												Arrays.asList(condValues),
												units));
									}
								}
							}
							experiment
									.addMetodo(FunctionConstants
											.createInterMethod(methodName,
													methodUnis,
													FunctionConstants
															.dataToArray(data),
													conditionList,
													numConditions, experiment));
							conditionList.clear();
							data.clear();
						} else {
							// Method name is repeated
							throw new RepeatedMethodNameException();
						}
					}

				} else {
					// Experiment already created
					throw new RepeatedExpNameException();
				}
			}
		} else {
			throw new Exception();
		}

		return experiment;
	}

	/**
	 * Gets XML setup for XML load.
	 * 
	 * @param expAttr
	 *            NamedNodeMap with Element that contains the information.
	 * @param arrayExpSetup
	 *            Variable to store the information.
	 * @param expName
	 *            Name of the Experiment.
	 * @param authorsText
	 *            Authors of the Experiment.
	 * @param notesText
	 *            Notes of the Experiment.
	 */
	private static void getXmlSetup(NamedNodeMap expAttr,
			String[] arrayExpSetup, String expName, String authorsText,
			String notesText) {
		String aux = "";
		// Obtain experiment attributes
		if (expName.length() > BewConstants.NAME_LIMIT) {
			arrayExpSetup[0] = expName.substring(0, BewConstants.NAME_LIMIT);
		} else
			arrayExpSetup[0] = expName;

		aux = expAttr.getNamedItem("organization").getTextContent().trim();
		if (aux.length() > BewConstants.ORGANIZATION_LIMIT) {
			arrayExpSetup[2] = aux
					.substring(0, BewConstants.ORGANIZATION_LIMIT);
		} else
			arrayExpSetup[2] = aux;

		arrayExpSetup[3] = expAttr.getNamedItem("contact").getTextContent()
				.trim();

		aux = expAttr.getNamedItem("date").getTextContent().trim();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.setLenient(false);
			sdf.parse(aux);
			arrayExpSetup[4] = aux;
		} catch (ParseException e) {
			arrayExpSetup[4] = "";
		}

		arrayExpSetup[5] = expAttr.getNamedItem("publication").getTextContent()
				.trim();

		arrayExpSetup[7] = expAttr.getNamedItem("bioID").getTextContent()
				.trim();

		if (authorsText.length() > BewConstants.AUTHORS_LIMIT) {
			arrayExpSetup[1] = authorsText.substring(0,
					BewConstants.AUTHORS_LIMIT);
		} else
			arrayExpSetup[1] = authorsText;

		if (notesText.length() > BewConstants.DESCRIPTION_LIMIT) {
			arrayExpSetup[6] = notesText.substring(0,
					BewConstants.DESCRIPTION_LIMIT);
		} else
			arrayExpSetup[6] = notesText;
	}

	/**
	 * Obtains the XML Constant from the file.
	 * 
	 * @param constantNodes
	 *            NodeList with the information.
	 * @param data
	 *            Variable to store the information.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private static void getXmlConstant(NodeList constantNodes,
			List<List<Object>> data) throws URISyntaxException, IOException {
		String cond = "";
		String val = "";
		String aux = "";
		String[] splitted;
		int res = 0;
		for (int i = 0; i < constantNodes.getLength(); i++) {
			Node constantCondition = constantNodes.item(i);
			List<Object> constantList = new ArrayList<Object>();

			cond = constantCondition.getAttributes().getNamedItem("condition")
					.getTextContent().trim();
			res = FunctionConstants.condValidation(cond, false);
			// Validation
			constantList.add(validateCondition(res, cond));

			val = constantCondition.getAttributes()
					.getNamedItem("conditionValue").getTextContent().trim();
			splitted = val.split(BewConstants.AND);

			for (String s : splitted) {
				s = s.trim();
				res = FunctionConstants.condValueValidation(s, cond, false);
				if (aux.isEmpty()) {
					aux = validateConditionValue(res, s);
				} else {
					aux += BewConstants.AND + validateConditionValue(res, s);
				}
			}

			// Validation
			constantList.add(aux);
			aux = "";

			constantList.add(constantCondition.getAttributes()
					.getNamedItem("conditionUnits").getTextContent().trim());

			data.add(constantList);
		}
	}

	/**
	 * Validates a Condition of the file to load it properly.
	 * 
	 * @param res
	 *            Code for the Condition.
	 * @param cond
	 *            Condition to evaluate.
	 * @return String with the real Condition.
	 */
	private static String validateCondition(int res, String cond) {
		String toRet = "";

		if (res == 0) {
			toRet = cond;
		} else if (res == 1) {
			toRet = FunctionConstants.oppositeValue(cond);
		} else if (res == 2) {
			toRet = FunctionConstants.putAsterisks(cond);
		} else if (res == 3) {
			// System.out.println("SetupLoadData=3");
			// Warning the user that the new
			// value
			// is
			// available
		}

		return toRet;

	}

	/**
	 * Validates a Condition value of the file to load it properly.
	 * 
	 * @param res
	 *            Code for the Condition value.
	 * @param val
	 *            Value to evaluate.
	 * @return String with the real Condition value.
	 */
	private static String validateConditionValue(int res, String val) {
		String toRet = "";

		if (res == 0) {
			toRet = val;
		} else if (res == 1) {
			toRet = FunctionConstants.oppositeValue(val);
		} else if (res == 2) {
			toRet = FunctionConstants.putAsterisks(val);
		} else if (res == 3) {
			// System.out.println("=3");
			// Warning the user that the
			// new value
			// is
			// available
		}

		return toRet;
	}
}
