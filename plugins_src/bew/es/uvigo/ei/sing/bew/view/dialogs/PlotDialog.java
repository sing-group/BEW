package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import org.jfree.chart.ChartPanel;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.PlotFunctions;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.exceptions.NoTimeException;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;
import es.uvigo.ei.sing.bew.model.views.MethodTable;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog for creating a Plot.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class PlotDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JPanel graphicPane;
	private ChartPanel chartPanel;
	private MethodTable methodTable;

	private JList<String> listCond;
	private JList<String> listData;
	private List<String> methodConditions;
	private boolean canExit;

	private Plot graphic;
	private Method method;

	private int numConditions;
	// Type of the plot to be created
	private String function;

	// Variables for the table listeners
	private boolean dragComplete = false;
	private int[] selectedRows;

	/**
	 * Default Constructor.
	 * 
	 * @param m
	 *            Parent of the future plot.
	 * @param function
	 *            Function to do the plot.
	 */
	public PlotDialog(Method m, String function) {
		super();

		setTitle(I18n.get("plotDialog") + function.toLowerCase() + " plot");

		this.method = m;
		this.function = function;

		this.numConditions = this.method.getNumConditions();

		this.methodConditions = new ArrayList<String>();

		this.setExit(false);

		init();
		initButtons();

		setLocationRelativeTo(null);
	}

	/**
	 * Method to initialize dialog
	 */
	private void init() {
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblContent = new GridBagLayout();
		gblContent.columnWidths = new int[] { 307, 307, 0 };
		gblContent.rowHeights = new int[] { 197, 197, 0 };
		gblContent.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gblContent.rowWeights = new double[] { 1.0, 1.0, 1.0 };
		contentPanel.setLayout(gblContent);
		{
			// Pane that contains the MethodView
			JPanel tablePane = new JPanel();
			tablePane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("methodTable"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
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

				// Listeners for deleting the plot and the lists when the user
				// moves the columns
				methodTable.getTableHeader().addMouseListener(
						new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e) {
								if (dragComplete) {
									// If the user moves a column but maintains
									// the rows...
									if (sameRowsSelected()) {
										// Refresh Jlists values
										lockSelectedColumns();
										// Create the plot
										createGraphic();
									} else {
										DefaultListModel<String> dlmListCond = (DefaultListModel<String>) listCond
												.getModel();
										DefaultListModel<String> dlmListData = (DefaultListModel<String>) listData
												.getModel();

										// First we clear all the data in the
										// Lists
										dlmListData.clear();
										dlmListCond.clear();
										// Delete the graphic from the View
										graphic = null;
										chartPanel.setChart(null);
									}
								}
								dragComplete = false;
							}
						});
				methodTable.getColumnModel().addColumnModelListener(
						new TableColumnModelListener() {

							public void columnAdded(TableColumnModelEvent e) {
							}

							public void columnRemoved(TableColumnModelEvent e) {
							}

							public void columnMoved(TableColumnModelEvent e) {
								// Column moved
								if (e.getFromIndex() != e.getToIndex())
									dragComplete = true;
							}

							public void columnMarginChanged(ChangeEvent e) {
							}

							public void columnSelectionChanged(
									ListSelectionEvent e) {
							}
						});
			}
			{
				JPanel buttonTablePane = new JPanel();
				tablePane.add(buttonTablePane, BorderLayout.SOUTH);
				{
					JButton btnSelectValues = new JButton(
							I18n.get("selectValues"));
					btnSelectValues
							.setToolTipText("Prepare the selected values and conditions in the table.");
					btnSelectValues.setIcon(new ImageIcon(PlotDialog.class
							.getResource("/img/selection.png")));
					btnSelectValues.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							lockSelectedColumns();
						}
					});
					buttonTablePane.add(btnSelectValues);
				}
			}
		}
		{
			JPanel listPane = new JPanel();
			listPane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("plotDesign"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagConstraints gbcListPane = new GridBagConstraints();
			gbcListPane.gridheight = 2;
			gbcListPane.fill = GridBagConstraints.BOTH;
			gbcListPane.insets = new Insets(0, 0, 5, 5);
			gbcListPane.gridx = 0;
			gbcListPane.gridy = 1;
			contentPanel.add(listPane, gbcListPane);
			GridBagLayout gblListPane = new GridBagLayout();
			gblListPane.columnWidths = new int[] { 106, 106, 0 };
			gblListPane.rowHeights = new int[] { 30, -107, 20 };
			gblListPane.columnWeights = new double[] { 1.0, 1.0,
					Double.MIN_VALUE };
			gblListPane.rowWeights = new double[] { 1.0, 50.0, 0.0 };
			listPane.setLayout(gblListPane);
			{
				JLabel lblDataList = new JLabel(I18n.get("selectedData"));
				GridBagConstraints gbcLblData = new GridBagConstraints();
				gbcLblData.insets = new Insets(0, 0, 5, 5);
				gbcLblData.gridx = 0;
				gbcLblData.gridy = 0;
				listPane.add(lblDataList, gbcLblData);
			}
			{
				JLabel lblCondList = new JLabel(I18n.get("selectedCond"));
				GridBagConstraints gbcLblCond = new GridBagConstraints();
				gbcLblCond.weighty = 5.0;
				gbcLblCond.insets = new Insets(0, 0, 5, 0);
				gbcLblCond.gridx = 1;
				gbcLblCond.gridy = 0;
				listPane.add(lblCondList, gbcLblCond);
			}
			{
				listData = new JList<String>(new DefaultListModel<String>());
				GridBagConstraints gbcScrollData = new GridBagConstraints();
				gbcScrollData.fill = GridBagConstraints.BOTH;
				gbcScrollData.insets = new Insets(0, 0, 5, 5);
				gbcScrollData.gridx = 0;
				gbcScrollData.gridy = 1;

				JScrollPane scrollData = new JScrollPane(listData);
				listPane.add(scrollData, gbcScrollData);
			}
			{
				listCond = new JList<String>(new DefaultListModel<String>());

				GridBagConstraints gbcScrollCond = new GridBagConstraints();
				gbcScrollCond.fill = GridBagConstraints.BOTH;
				gbcScrollCond.insets = new Insets(0, 0, 5, 0);
				gbcScrollCond.gridx = 1;
				gbcScrollCond.gridy = 1;

				JScrollPane scrollCond = new JScrollPane(listCond);
				listPane.add(scrollCond, gbcScrollCond);
			}
			{
				JButton btnAdd = new JButton("");
				btnAdd.setToolTipText("Move the selected value to the right.");
				btnAdd.setIcon(new ImageIcon(PlotDialog.class
						.getResource("/img/listArrowRight.png")));
				btnAdd.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addSelectedConditions();
					}
				});
				GridBagConstraints gbcBtnAdd = new GridBagConstraints();
				gbcBtnAdd.insets = new Insets(0, 0, 5, 5);
				gbcBtnAdd.gridx = 0;
				gbcBtnAdd.gridy = 2;
				listPane.add(btnAdd, gbcBtnAdd);
			}
			{
				JButton btnRemove = new JButton("");
				btnRemove.setToolTipText("Move the selected value to the left.");
				btnRemove.setIcon(new ImageIcon(PlotDialog.class
						.getResource("/img/listArrowLeft.png")));
				btnRemove.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						removeSelectedConditions();
					}
				});
				GridBagConstraints gbcBtnRemove = new GridBagConstraints();
				gbcBtnRemove.insets = new Insets(0, 0, 5, 0);
				gbcBtnRemove.gridx = 1;
				gbcBtnRemove.gridy = 2;
				listPane.add(btnRemove, gbcBtnRemove);
			}
		}
		this.chartPanel = new ChartPanel(null);
		{
			// Disable menu options
			this.chartPanel.setPopupMenu(null);

			JPanel reportPane = new JPanel();
			reportPane.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), I18n.get("plotPreview"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagConstraints gbcReportPane = new GridBagConstraints();
			gbcReportPane.gridheight = 2;
			gbcReportPane.insets = new Insets(0, 0, 5, 0);
			gbcReportPane.fill = GridBagConstraints.BOTH;
			gbcReportPane.gridx = 1;
			gbcReportPane.gridy = 1;
			contentPanel.add(reportPane, gbcReportPane);
			reportPane.setLayout(new BorderLayout(0, 0));
			{
				graphicPane = new JPanel();
				reportPane.add(graphicPane, BorderLayout.CENTER);
				graphicPane.setLayout(new GridLayout(1, 0, 0, 0));

				graphicPane.add(chartPanel);
			}
			{
				JPanel optionPane = new JPanel();
				reportPane.add(optionPane, BorderLayout.SOUTH);
				{
					JButton btnPrevisualize = new JButton(
							I18n.get("createPlot"));
					btnPrevisualize
							.setToolTipText("Create the plot for the selected conditions and values in the lists.");
					btnPrevisualize.setIcon(new ImageIcon(PlotDialog.class
							.getResource("/img/modelPlot.png")));
					btnPrevisualize.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							// Validate if the lists have Conditions and Data
							if (listCond.getModel().getSize() > 0
									&& listData.getModel().getSize() > 0)
								createGraphic();
							else {
								ShowDialog.showError(
										I18n.get("badSelectionTitle"),
										I18n.get("badSelection"));
							}
						}
					});
					optionPane.add(btnPrevisualize);
				}
			}
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "Ok", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chartPanel.getChart() != null) {
					setExit(true);
					dispose();
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
						"createPlot", this), BorderLayout.SOUTH);
	}

	/**
	 * Validate if the selected rows in the table changed when the user moves a
	 * column
	 * 
	 * @return
	 */
	private boolean sameRowsSelected() {
		if (Arrays.equals(methodTable.getSelectedRows(), selectedRows))
			return true;
		else
			return false;
	}

	/**
	 * Method to lock the selected columns in the table and fill the JLists with
	 * the selected columns names.
	 */
	public void lockSelectedColumns() {
		int[] selectedCols = this.methodTable.getSelectedColumns();

		if (selectedCols.length > 0) {
			DefaultListModel<String> dlmListCond = (DefaultListModel<String>) this.listCond
					.getModel();
			DefaultListModel<String> dlmListData = (DefaultListModel<String>) this.listData
					.getModel();

			// First we clear all the data in the Lists
			dlmListData.clear();
			dlmListCond.clear();
			this.methodConditions.clear();

			// We put every selected columnName in the two Lists
			for (int col : selectedCols) {
				String headerName = this.methodTable.getColumnName(col);
				// Save methodConditions
				if (col < this.numConditions) {
					dlmListCond.addElement(headerName);
					this.methodConditions.add(headerName);
				} else
					dlmListData.addElement(headerName);
			}

			// Obtain selectedRows
			this.selectedRows = methodTable.getSelectedRows();
		} else {
			ShowDialog.showError(I18n.get("noColumnsSelectedTitle"),
					I18n.get("noColumnsSelected"));
		}
	}

	/**
	 * Method to create the final graphic in the JPanel. In order to create the
	 * graphic, this Method take the selected Conditions for the user.
	 */
	public void createGraphic() {
		// LinkedMap to know which conditions values belong the data (Keep
		// insert order)
		Map<Object, List<Object>> condMeasur = new HashMap<Object, List<Object>>();

		List<String> selectedCondition = getAllListValues(listCond);
		List<String> selectedData = getAllListValues(listData);

		// We look the intraExperiments if the method
		// comes from an interExperiment
		Map<Object, List<Object>> intraExpAndRows = method.getParent()
				.getMapIntraExpsAndRows();

		// Variable to indicate which is the reference constant condition to
		// take
		// the data
		String constantVal = FunctionConstants.validateSelectionTable(
				condMeasur, methodTable, numConditions, selectedCondition,
				selectedData, intraExpAndRows);

		if (constantVal.length() > 0) {
			try {
				// Divide dataSet in two variables (keys and list of values)
				List<List<Object>> values = new ArrayList<List<Object>>(
						condMeasur.values());
				List<Object> keys = new ArrayList<Object>(condMeasur.keySet());

				// We create the selected plot and set the jfreechart in 'chart'
				this.graphic = PlotFunctions.createPlot(values, keys,
						constantVal, this.method, this.function,
						this.methodConditions, this.chartPanel);

				// Introduce the chart in a specific Panel
				this.chartPanel.setMouseWheelEnabled(true);
				this.chartPanel.setSize(graphicPane.getWidth(),
						graphicPane.getHeight());
			} catch (NoTimeException e) {
				ShowDialog.showError(I18n.get("noTimePresentTitle"),
						I18n.get("noTimePresent"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// Delete the graphic from the View
			this.graphic = null;
			this.chartPanel.setChart(null);
			ShowDialog.showError(I18n.get("badSelectionTitle"),
					I18n.get("badSelection"));
		}

		// Refresh the View
		repaint();
		revalidate();
	}

	/**
	 * Method to pass the selected Conditions from the first list to the second.
	 * This Method also remove the elements in the first list.
	 */
	private void addSelectedConditions() {
		if (this.listData.getSelectedIndex() != -1) {
			DefaultListModel<String> dlmListCond = (DefaultListModel<String>) this.listCond
					.getModel();
			DefaultListModel<String> dlmListData = (DefaultListModel<String>) this.listData
					.getModel();

			List<String> selectedValues = this.listData.getSelectedValuesList();

			for (String value : selectedValues) {
				// Add in SelectedConditions List
				dlmListCond.addElement(value);
				// Remove in AvailableConditions List
				dlmListData.removeElement(value);
			}
		}
	}

	/**
	 * Same as addSelectedConditions Method, but in opposite way.
	 */
	private void removeSelectedConditions() {
		if (this.listCond.getSelectedIndex() != -1) {
			DefaultListModel<String> dlmListCond = (DefaultListModel<String>) this.listCond
					.getModel();
			DefaultListModel<String> dlmListData = (DefaultListModel<String>) this.listData
					.getModel();

			List<String> selectedValues = this.listCond.getSelectedValuesList();

			for (String value : selectedValues) {
				// Add in AvailableConditions List
				dlmListData.addElement(value);
				// Remove in SelectedConditions List
				dlmListCond.removeElement(value);
			}
		}
	}

	/**
	 * Method to get all the values in a JList.
	 * 
	 * @param list
	 *            JList to extract the values.
	 * @return List<String> with the values in the JList.
	 */
	private List<String> getAllListValues(JList<String> list) {
		DefaultListModel<String> dlm = (DefaultListModel<String>) list
				.getModel();
		List<String> ret = new ArrayList<String>();

		for (int i = 0; i < dlm.getSize(); i++) {
			ret.add(dlm.getElementAt(i));
		}

		return ret;
	}

	/**
	 * Get plot panel.
	 * 
	 * @return
	 */
	public JPanel getGraphicPane() {
		return graphicPane;
	}

	/**
	 * Set plot panel.
	 * 
	 * @param graphicPane
	 */
	public void setGraphicPane(JPanel graphicPane) {
		this.graphicPane = graphicPane;
	}

	/**
	 * Get the selected conditions in the list.
	 * 
	 * @return
	 */
	public JList<String> getListSelectedCond() {
		return listCond;
	}

	/**
	 * Set selected conditions in the list.
	 * 
	 * @param listSelectedCond
	 */
	public void setListSelectedCond(JList<String> listSelectedCond) {
		this.listCond = listSelectedCond;
	}

	/**
	 * Get selected data in the list.
	 * 
	 * @return
	 */
	public JList<String> getListAvailable() {
		return listData;
	}

	/**
	 * Set selected data in the list.
	 * 
	 * @param listAvailable
	 */
	public void setListAvailable(JList<String> listAvailable) {
		this.listData = listAvailable;
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
	 * Check exit.
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
	 * Get plot.
	 * 
	 * @return
	 */
	public Plot getGraphic() {
		return graphic;
	}

	/**
	 * Set plot.
	 * 
	 * @param graphic
	 */
	public void setGraphic(Plot graphic) {
		this.graphic = graphic;
	}
}
