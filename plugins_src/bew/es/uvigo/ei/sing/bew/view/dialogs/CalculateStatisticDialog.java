package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.ShowProgressBar;
import es.uvigo.ei.sing.bew.constants.StatisticFunctions;
import es.uvigo.ei.sing.bew.exceptions.TooMuchDataException;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.model.views.MethodTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog for calculating statistic test.
 * 
 * @author Gael P�rez Rodr�guez.
 * 
 */
public class CalculateStatisticDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextArea textArea;
	private MethodTable methodTable;
	private boolean canExit;

	private Statistic statistic;
	private Method method;
	private List<String> selectedCondition;
	private List<String> selectedValues;
	private LinkedHashMap<Object, List<Object>> condMeasur;
	private String cond;
	private String result;

	private StatisticPlotPopupDialog plotPopup;
	private CalculateStatisticDialog me;

	private int numConditions;
	private String name;
	private String type;

	private JCheckBox chckbxHtml;

	/**
	 * Default constructor.
	 * 
	 * @param method
	 *            Indicate the method.
	 * @param name
	 *            Indicate the name of the statistical function.
	 * @param type
	 *            Indicate the type of the statistical function.
	 */
	public CalculateStatisticDialog(Method method, String name, String type) {
		setTitle(I18n.get("statisticDialog"));

		this.method = method;
		this.name = name;
		this.type = type;
		this.me = this;

		this.numConditions = this.method.getNumConditions();
		this.selectedCondition = new ArrayList<String>();
		this.selectedValues = new ArrayList<String>();
		this.setExit(false);

		// Create Statistic Object
		this.statistic = new Statistic(method, name, type);

		init();
		initButtons();

		setLocationRelativeTo(null);
	}

	/**
	 * Method to initialize dialog
	 */
	private void init() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblContent = new GridBagLayout();
		gblContent.columnWidths = new int[] { 307, 307, 0 };
		gblContent.rowHeights = new int[] { 250, 190 };
		gblContent.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gblContent.rowWeights = new double[] { 1.0, 1.0, 0.0 };
		contentPanel.setLayout(gblContent);
		{
			// Pane that contains the MethodView
			JPanel tablePane = new JPanel();
			tablePane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("data") + " ("
					+ method.getParent().getName() + ":" + method.getName()
					+ ")", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagConstraints gbcTablePane = new GridBagConstraints();
			gbcTablePane.gridwidth = 2;
			gbcTablePane.insets = new Insets(0, 0, 5, 5);
			gbcTablePane.fill = GridBagConstraints.BOTH;
			gbcTablePane.gridx = 0;
			gbcTablePane.gridy = 0;
			contentPanel.add(tablePane, gbcTablePane);
			tablePane.setLayout(new BorderLayout(0, 0));
			{
				// MethodTable to show the representation of the data method
				methodTable = new MethodTable(method, true);
				JScrollPane scrollMethod = new JScrollPane(methodTable);
				// Rows header
				RowNumberTable rowTable = new RowNumberTable(methodTable);

				scrollMethod.setRowHeaderView(rowTable);
				scrollMethod.setCorner(JScrollPane.UPPER_LEFT_CORNER,
						rowTable.getTableHeader());
				tablePane.add(scrollMethod);
			}
			{
				JPanel buttonTablePane = new JPanel();
				tablePane.add(buttonTablePane, BorderLayout.SOUTH);
				{
					GridBagLayout gblbtnTable = new GridBagLayout();
					gblbtnTable.columnWidths = new int[] { 50, 50, 500 };
					gblbtnTable.rowHeights = new int[] { 15, 15 };
					gblbtnTable.columnWeights = new double[] { 1.0,
							0.0, 1.0 };
					gblbtnTable.rowWeights = new double[] { 1.0, 0.0 };
					buttonTablePane.setLayout(gblbtnTable);

					{
						JButton btnSelectValues = new JButton(I18n.get("test"));
						btnSelectValues.setToolTipText("Execute the selected test for the selected data.");
						btnSelectValues.setIcon(new ImageIcon(
								CalculateStatisticDialog.class
										.getResource("/img/execute.png")));
						GridBagConstraints gbcBtnSelVal = new GridBagConstraints();
						gbcBtnSelVal.fill = GridBagConstraints.VERTICAL;
						gbcBtnSelVal.gridheight = 2;
						gbcBtnSelVal.insets = new Insets(0, 0, 0, 5);
						gbcBtnSelVal.gridx = 0;
						gbcBtnSelVal.gridy = 0;
						buttonTablePane.add(btnSelectValues,
								gbcBtnSelVal);

						// Button Test listener
						btnSelectValues.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								if (selectValuesFromTable()) {
									// LinkedMap to know which conditions values
									// belong the data (Keep
									// insert order)
									condMeasur = new LinkedHashMap<Object, List<Object>>();

									// We look the intraExperiments if the
									// method
									// comes from an interExperiment
									Map<Object, List<Object>> intraExpAndRows = method
											.getParent()
											.getMapIntraExpsAndRows();

									if (FunctionConstants
											.validateSelectionTable(
													condMeasur,
													methodTable, numConditions,
													selectedCondition,
													selectedValues,
													intraExpAndRows).length() > 0) {
										try {
											// Number of selected rows in the
											// table
											int numSelectedRows = methodTable
													.getSelectedRowCount();

											result = StatisticFunctions
													.calculateStringDataFromTable(
															condMeasur,
															numSelectedRows);
											cond = StatisticFunctions
													.createStringConditionsFromTable(
															condMeasur,
															numSelectedRows);

											// Execute Swing Worker
											new ShowProgressBar(new HeavyTask());
										} catch (TooMuchDataException e) {
											ShowDialog.showError(I18n
													.get("amountOfDataTitle"),
													I18n.get("amountOfData"));
										} catch (NullPointerException e) {
											// User cancel operation
										}
									} else {
										ShowDialog.showError(
												I18n.get("badSelectionTitle"),
												I18n.get("badSelection"));
									}
								} else {
									ShowDialog.showError(
											I18n.get("badSelectionTitle"),
											I18n.get("badSelection"));
								}
							}
						});
					}
					{
						chckbxHtml = new JCheckBox("Generate HTML?");
						chckbxHtml.setSelected(true);
						GridBagConstraints gbcchckbxHtml = new GridBagConstraints();
						gbcchckbxHtml.insets = new Insets(0, 0, 0, 5);
						gbcchckbxHtml.gridx = 2;
						gbcchckbxHtml.gridy = 0;
						buttonTablePane.add(chckbxHtml, gbcchckbxHtml);
					}
					{
						JLabel lblInfo = new JLabel(
								"It's highly recommended to enable this option unless you won't save the Statistic in the future.");
						GridBagConstraints gbcLblInfo = new GridBagConstraints();
						gbcLblInfo.gridx = 2;
						gbcLblInfo.gridy = 1;
						buttonTablePane.add(lblInfo, gbcLblInfo);
					}
				}
			}
		}
		{
			JPanel listPane = new JPanel();
			listPane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("output"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagConstraints gbcListPane = new GridBagConstraints();
			gbcListPane.gridheight = 2;
			gbcListPane.gridwidth = 2;
			gbcListPane.fill = GridBagConstraints.BOTH;
			gbcListPane.insets = new Insets(0, 0, 5, 5);
			gbcListPane.gridx = 0;
			gbcListPane.gridy = 1;
			contentPanel.add(listPane, gbcListPane);
			GridBagLayout gblListPane = new GridBagLayout();
			gblListPane.columnWidths = new int[] { 640 };
			gblListPane.rowHeights = new int[] { 30, 50, 200 };
			gblListPane.columnWeights = new double[] { 1.0, 1.0 };
			gblListPane.rowWeights = new double[] { 1.0, 1.0, 0.0 };
			listPane.setLayout(gblListPane);
			{
				JLabel lblOutliersReport = new JLabel(type + ", " + name
						+ " Report");
				lblOutliersReport.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbcLblOutReport = new GridBagConstraints();
				gbcLblOutReport.insets = new Insets(0, 0, 5, 0);
				gbcLblOutReport.gridwidth = 2;
				gbcLblOutReport.gridx = 0;
				gbcLblOutReport.gridy = 0;
				listPane.add(lblOutliersReport, gbcLblOutReport);
			}
			{
				JButton btnShowPlot = new JButton(I18n.get("showPlot"));
				btnShowPlot.setIcon(new ImageIcon(
						CalculateStatisticDialog.class
								.getResource("/img/visualize.png")));
				GridBagConstraints gbcBtnShow = new GridBagConstraints();
				gbcBtnShow.gridwidth = 2;
				gbcBtnShow.insets = new Insets(0, 0, 0, 5);
				gbcBtnShow.gridx = 0;
				gbcBtnShow.gridy = 1;
				listPane.add(btnShowPlot, gbcBtnShow);
				btnShowPlot.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Press the button and the PlotImage was created
						if (plotPopup != null) {
							if (!plotPopup.isVisible()) {
								try {
									plotPopup.setNewImage(FunctionConstants
											.byteToBufferedImg(statistic
													.getStatisticsPlot()));
									plotPopup.setVisible(true);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				});
			}
			{
				textArea = new JTextArea();
				textArea.setEditable(false);
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				JScrollPane scrollPane = new JScrollPane(textArea);
				GridBagConstraints gbcSp = new GridBagConstraints();
				gbcSp.insets = new Insets(0, 75, 0, 75);
				gbcSp.gridwidth = 2;
				gbcSp.fill = GridBagConstraints.BOTH;
				gbcSp.gridx = 0;
				gbcSp.gridy = 2;
				listPane.add(scrollPane, gbcSp);
			}
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "OK", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (statistic.getStatisticsResult().size() > 0) {
					setExit(true);
					dispose();
				} else {
					ShowDialog.showError(I18n.get("doTheTestTitle"),
							I18n.get("doTheTest"));
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExit(false);
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						"createTest", this), BorderLayout.SOUTH);
	}

	/**
	 * Select which statistical function the method have to do. It need the type
	 * variable inside the class.
	 * 
	 * @param condMeasur
	 *            Map with the conditions and values from the Method table.
	 * @param statistic
	 *            Statistic object.
	 * @param saveHtml
	 *            True if the user want to generate a HTML report in temporary
	 *            folder, false otherwise.
	 * @return A List of files with the results of the operations.
	 * @throws NullPointerException
	 *             Throws NullPointerException if the statistical operation went
	 *             wrong.
	 * @throws IOException
	 */
	public List<File> selectStatisticFunction(
			LinkedHashMap<Object, List<Object>> condMeasur,
			Statistic statistic, boolean saveHtml) throws NullPointerException,
			IOException {
		// Position 0 = Function, Position 1 = Plot
		List<File> ret = new ArrayList<File>();

		switch (name) {
		case StatisticFunctions.GRUBBS:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"grubbsText.txt", "grubbs.test(frame$Data)", false, false));
			ret.add(StatisticFunctions
					.createStatisticBoxplot("grubbsPlot.png"));
			break;

		case StatisticFunctions.KOLMOGOROV:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"ksTest.txt", "ksnormTest(frame$Data)", false, false));
			ret.add(StatisticFunctions.createStatisticQqnorm("ksPlot.png",
					"Kolmogorov Plot"));
			break;

		case StatisticFunctions.SAPHIRO:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"shapiroTest.txt", "shapiroTest(frame$Data)", false, false));
			ret.add(StatisticFunctions.createStatisticQqnorm(
					"shapiroTest.png", "Shapiro Plot"));
			break;

		case StatisticFunctions.LILLIE:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"lillieTest.txt", "lillie.test(frame$Data)", false, false));
			ret.add(StatisticFunctions.createStatisticQqnorm("lillieTest.png",
					"Lilliefors Plot"));
			break;

		case StatisticFunctions.LEVENE:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"leveneTest.txt", "leveneTest(frame$Data, frame$Cond)",
					true, false));
			ret.add(StatisticFunctions
					.createStatisticBoxplot("leveneTest.png"));
			break;

		case StatisticFunctions.BARTLETT:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"bartlettTest.txt", "bartlett.test(frame$Data~frame$Cond)",
					false, false));
			ret.add(StatisticFunctions
					.createStatisticBoxplot("bartlettTest.png"));
			break;

		case StatisticFunctions.KRUSKAL:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"kruskalTest.txt", "kruskal.test(frame$Data, frame$Cond)",
					false, false));
			ret.add(StatisticFunctions
					.createStatisticBoxplot("kruskalTest.png"));
			break;

		case StatisticFunctions.TTEST:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"tTest.txt", "t.test(frame$Data~frame$Cond)", false, false));
			ret.add(StatisticFunctions.createStatisticBoxplot("tTest.png"));
			break;

		case StatisticFunctions.ANOVA:
			ret.add(StatisticFunctions.createStatisticFunction(result, cond,
					"anovaTest.txt", "aov(frame$Data~frame$Cond)", true, true));
			ret.add(StatisticFunctions.createStatisticTukey("anovaPlot.png",
					"aov(frame$Data~frame$Cond)"));
			break;

		default:
			break;
		}

		// Put the real and fake conditions in
		// the statistic
		statistic.setCond(cond);
		statistic.setConditionMeasurements(condMeasur);
		// Put the String with the values
		statistic.setResult(result);

		// If the user want to save the html files in temp folder
		if (saveHtml) {
			// Create temp html files and set the direction folder in the
			// statistic
			// object
			statistic.setTempHTMLDir(StatisticFunctions.createTestReportInTemp(
					statistic, ret.get(1).getAbsolutePath()));
		}

		return ret;
	}

	/**
	 * Method to put the statistical function results in the text area.
	 * 
	 * @param file
	 *            File with the results
	 */
	public void fillTextArea(File file) {
		// Clear the TextArea content
		this.textArea.setText("");

		// Write File in TextArea
		List<String> fileLines = FunctionConstants.readFile(file);
		for (String s : fileLines) {
			this.textArea.append(s + "\n");
		}

		// Add Output text
		this.statistic.setStatisticsResult(fileLines);

		// Delete temp text file
		file.delete();
	}

	/**
	 * Method to fill a JPanel with the plot image that was generated in the
	 * statistical function.
	 * 
	 * @param file
	 *            File with the plot image.
	 */
	private void fillStatisticPlot(File file) {
		try {
			// Transform byte[] in JavaImage
			BufferedImage image = ImageIO.read(file);

			// Save JavaImage in Statistic
			this.statistic.setStatisticsPlot(FunctionConstants
					.bufferedImgToByte(image));

		} catch (Exception e) {
			
		} finally {
			// Delete text plot file
			file.delete();
		}
	}

	/**
	 * Method to validate if the selected values and conditions in the Method
	 * table are correct.
	 * 
	 * @return True if the selection is correct, false otherwise.
	 */
	public boolean selectValuesFromTable() {
		int[] selectedCols = this.methodTable.getSelectedColumns();

		// Purge variables
		this.selectedCondition.clear();
		this.selectedValues.clear();

		if (selectedCols.length > 0) {
			// We put every selected columnName in the two variables
			for (int col : selectedCols) {
				String headerName = this.methodTable.getColumnName(col);
				if (col < this.numConditions)
					this.selectedCondition.add(headerName);
				else
					this.selectedValues.add(headerName);
			}

			// The user must select Conditions and Data
			if (this.selectedCondition.size() > 0
					&& this.selectedValues.size() > 0) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	/**
	 * Get Method.
	 * 
	 * @return
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Set Method.
	 * 
	 * @param method
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Check the exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return canExit;
	}

	/**
	 * Set exit.
	 * 
	 * @param isExit
	 */
	public void setExit(boolean isExit) {
		this.canExit = isExit;
	}

	/**
	 * Get statistic.
	 * 
	 * @return
	 */
	public Statistic getStatistic() {
		return statistic;
	}

	/**
	 * Set statistic.
	 * 
	 * @param statistic
	 */
	public void setStatistic(Statistic statistic) {
		this.statistic = statistic;
	}

	/**
	 * Swing worker to do heavy task in background. Realizes the statistics test
	 * logic.
	 * 
	 * @author Gael P�rez Rodr�guez
	 * 
	 */
	private class HeavyTask extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {

			List<File> files;
			try {
				// Obtain files for the function we want
				// to
				// calculate
				files = selectStatisticFunction(condMeasur,
						statistic, chckbxHtml.isSelected());
				// First file is the text file
				fillTextArea(files.get(0));
				// Second is the plot file
				fillStatisticPlot(files.get(1));

				// If the popup is visible, destroy
				// it
				if (plotPopup != null) {
					if (plotPopup.isVisible()) {
						plotPopup.dispose();
					}
				}
				// Create the popup with the graphic
				// report of the test
				plotPopup = new StatisticPlotPopupDialog(me, statistic,
						StatisticFunctions.showLegend(name), true);

			} catch (IOException | NullPointerException e) {
				// Close progress bar
				setProgress(100);
				e.printStackTrace();
				ShowDialog.showError(I18n.get("operationErrorTitle"),
						I18n.get("operationErrorS"));
			}

			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setProgress(100);
			setCursor(null); // turn off the wait cursor
		}
	}
}