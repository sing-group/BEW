package es.uvigo.ei.sing.bew.sheets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import jxl.Sheet;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.SetupTable;
import es.uvigo.ei.sing.bew.tables.models.MyTableModel;
import es.uvigo.ei.sing.bew.view.panels.SetupPanel;

/**
 * This class shows the forms to create Conditions.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SetupSheetConfig extends JPanel implements IWizardStep,
		ISheetConfigurator {

	private static final long serialVersionUID = 1L;

	private SetupPanel setupPanel;
	private SetupTable setupTable;
	private Sheet sheet;

	private List<String> condNames;
	private List<String> condValues;
	private List<String> condUnits;

	private String sheetName;
	private String[] expSetup;
	private String bioID = "";

	private boolean isInter;

	/**
	 * Constructor that load the data from an entry Sheet. (Used by FileToData
	 * when load a XML file).
	 * 
	 * @param sheet
	 *            Sheet with the data.
	 * @param setup
	 *            Experiment Setup data.
	 */
	public SetupSheetConfig(Sheet sheet, String[] setup) {
		super();

		// Initializing variables
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<String>();
		this.condUnits = new ArrayList<String>();
		this.expSetup = new String[8];

		this.setupTable = new SetupTable(false);
		this.setupTable.setIsEditing(false);

		this.sheet = sheet;
		this.sheetName = sheet.getName();

		// Load the data from the sheet to the table
		this.setupTable.loadDataFromSheet(this.sheet);

		// Show table warnings during load if exists
		String toShow = this.setupTable.getErrors();
		if (!toShow.isEmpty())
			ShowDialog.showInfo("Something happened during load!", toShow);

		initialize();

		fillTextFields(setup);
	}

	/**
	 * Constructor to edit the Constant Conditions Sheet in the experiment.
	 * 
	 * @param name
	 *            Sheet name.
	 * @param data
	 *            Sheet data.
	 * @param expSetup
	 *            Experiment Setup data contained in the sheet.
	 */
	public SetupSheetConfig(String name, Object[][] data, String[] expSetup,
			boolean isInter) {
		super();

		// Initializing variables
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<String>();
		this.condUnits = new ArrayList<String>();
		this.expSetup = new String[8];

		this.setupTable = new SetupTable(isInter);
		this.setupTable.setIsEditing(true);

		// Don't need a Sheet here
		this.sheet = null;
		this.sheetName = name;

		// We only need the data to init the table
		this.setupTable.loadDataFromMatrix(data);

		this.isInter = isInter;

		initialize();

		fillTextFields(expSetup);
	}

	/**
	 * Default constructor. Used when we create an empty sheet from the Factory.
	 */
	public SetupSheetConfig() {
		super();

		// Initializing variables
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<String>();
		this.condUnits = new ArrayList<String>();
		this.expSetup = new String[8];

		this.setupTable = new SetupTable(false);

		// Default and constant name for the Sheet
		this.sheetName = I18n.get("conditionSheetName");

		initialize();
	}

	/**
	 * Constructor for adding Experiment Setup to the Sheet.
	 * 
	 * @param expSetup
	 *            Experiment Setup data.
	 */
	public SetupSheetConfig(String[] expSetup) {
		super();
		
		// Initializing variables
		this.condNames = new ArrayList<String>();
		this.condValues = new ArrayList<String>();
		this.condUnits = new ArrayList<String>();
		this.expSetup = new String[8];

		this.setupTable = new SetupTable(false);

		// Default and constant name for the Sheet
		this.sheetName = I18n.get("conditionSheetName");

		initialize();

		fillTextFields(expSetup);
	}

	/**
	 * Method to initialize the dialog.
	 */
	public void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder(null, I18n.get("experimentalSetup"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(350);
		add(splitPane);

		JPanel constantPane = new JPanel();
		constantPane.setBorder(new TitledBorder(null, I18n
				.get("constantConditions"), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		splitPane.setRightComponent(constantPane);
		GridBagLayout gblConstantPane = new GridBagLayout();
		gblConstantPane.columnWidths = new int[] { 200, 100 };
		gblConstantPane.rowHeights = new int[] { 75 };
		gblConstantPane.columnWeights = new double[] { 1.0, 0.0 };
		gblConstantPane.rowWeights = new double[] { 1.0 };
		constantPane.setLayout(gblConstantPane);

		JScrollPane scrollPane = new JScrollPane(this.setupTable);
		// Rows header
		JTable rowTable = new RowNumberTable(setupTable);

		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());

		GridBagConstraints gbcScrollPane = new GridBagConstraints();
		gbcScrollPane.fill = GridBagConstraints.BOTH;
		gbcScrollPane.insets = new Insets(0, 0, 0, 5);
		gbcScrollPane.gridx = 0;
		gbcScrollPane.gridy = 0;
		constantPane.add(scrollPane, gbcScrollPane);

		JPanel tableButtons = new JPanel();
		GridBagConstraints gbcTableButtons = new GridBagConstraints();
		gbcTableButtons.gridx = 1;
		gbcTableButtons.gridy = 0;
		constantPane.add(tableButtons, gbcTableButtons);
		tableButtons.setLayout(new GridLayout(0, 1, 0, 20));

		// Button to create a new Row in the conditionsTable
		JButton newFil = new JButton("Add condition");
		newFil.setToolTipText("Create a new condition for the experiment.");
		newFil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setupTable.insertBlankRow();
			}
		});
		tableButtons.add(newFil);

		// Button to delete the selected row in the conditionTable
		JButton deleteFil = new JButton(I18n.get("deleteCondition"));
		deleteFil.setToolTipText("Delete the selected condition of the experiment.");
		deleteFil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyTableModel model = (MyTableModel) setupTable.getModel();
				int[] rows = setupTable.getSelectedRows();
				for (int i = 0; i < rows.length; i++) {
					model.removeRow(rows[i] - i);
				}
			}
		});
		tableButtons.add(deleteFil);

		if (isInter) {
			newFil.setEnabled(false);
			deleteFil.setEnabled(false);
		}

		setupPanel = new SetupPanel();
		splitPane.setLeftComponent(setupPanel);
	}

	/**
	 * Method to fill the TextFields of the Sheet with the Experiment Setup
	 * data.
	 * 
	 * @param setup
	 *            String[8] with the Experiment Setup data.
	 */
	private void fillTextFields(String[] setup) {
		try {
			String name = setup[0];
			if (name.length() > BewConstants.NAME_LIMIT) {
				this.setupPanel.getFieldName().setText(
						name.substring(0, BewConstants.NAME_LIMIT));
			} else
				this.setupPanel.getFieldName().setText(name);

			if (setup[1].length() > BewConstants.AUTHORS_LIMIT) {
				this.setupPanel.getFieldAuthors().setText(
						setup[1].substring(0, BewConstants.AUTHORS_LIMIT));
			} else
				this.setupPanel.getFieldAuthors().setText(setup[1]);

			if (setup[2].length() > BewConstants.ORGANIZATION_LIMIT) {
				this.setupPanel.getFieldOrganization().setText(
						setup[2].substring(0, BewConstants.ORGANIZATION_LIMIT));
			} else
				this.setupPanel.getFieldOrganization().setText(setup[2]);

			this.setupPanel.getFieldContact().setText(setup[3]);
			this.setupPanel.getFieldDate().setText(setup[4]);
			this.setupPanel.getFieldPublication().setText(setup[5]);

			if (setup[6].length() > BewConstants.DESCRIPTION_LIMIT) {
				this.setupPanel.getFieldNotes().setText(
						setup[6].substring(0, BewConstants.DESCRIPTION_LIMIT));
			} else
				this.setupPanel.getFieldNotes().setText(setup[6]);

			this.bioID = setup[7];
		} catch (IndexOutOfBoundsException e) {

		}
	}

	/**
	 * Method to fill the ExperimentSetup Array with the values of each
	 * TextField.
	 */
	public void fillExpSetup() {
		String expName = this.setupPanel.getFieldName().getText().trim();

		expSetup[0] = expName;
		expSetup[1] = this.setupPanel.getFieldAuthors().getText().trim();
		expSetup[2] = this.setupPanel.getFieldOrganization().getText().trim();
		expSetup[3] = this.setupPanel.getFieldContact().getText().trim();
		expSetup[4] = this.setupPanel.getFieldDate().getText().trim();
		expSetup[5] = this.setupPanel.getFieldPublication().getText().trim();
		expSetup[6] = this.setupPanel.getFieldNotes().getText().trim();
		expSetup[7] = this.bioID;
	}

	@Override
	public boolean next() {
		// Cancel editing for the tables
		this.setupTable.cancelEditing();

		// Purge variables
		this.condNames.clear();
		this.condValues.clear();
		this.condUnits.clear();

		if (FunctionConstants.validateRequiredFields(this.setupPanel)) {
			MyTableModel mtm = this.setupTable.getModel();
			if (setupTable.validateTableContent()) {
				Object[][] data = setupTable.getTableData();
				for (int row = 0; row < mtm.getRowCount(); row++) {
					this.condNames.add(data[row][0].toString());
					this.condValues.add(data[row][1].toString());
					this.condUnits.add(data[row][2].toString());
				}
			} else {
				ShowDialog.showError(I18n.get("incorrectValuesTitle"),
						setupTable.getErrors());
				return false;
			}
			fillExpSetup();
			return true;
		} else {
			ShowDialog.showError(I18n.get("requiredFieldsMissingTitle"),
					I18n.get("requiredFieldsMissing"));
			return false;
		}
	}

	@Override
	public boolean validateStructure() {
		return false;
	}

	@Override
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Set sheet name.
	 * 
	 * @param sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setLastCol(int column) {
		// TODO Auto-generated method stub
	}

	@Override
	public Integer getNumConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	/**
	 * Get conditions table.
	 * 
	 * @return
	 */
	public JTable getTableConditions() {
		this.setupTable.cancelEditing();
		return setupTable;
	}

	/**
	 * Set condition names.
	 * 
	 * @param condNames
	 */
	public void setArrayConditionNames(List<String> condNames) {
		this.condNames = condNames;
	}

	/**
	 * Get condition values.
	 * 
	 * @return
	 */
	public List<String> getArrayConditionValues() {
		return condValues;
	}

	/**
	 * Set condition values.
	 * 
	 * @param condValues
	 */
	public void setArrayConditionValues(List<String> condValues) {
		this.condValues = condValues;
	}

	@Override
	public List<String> getConditionNames() {
		return this.condNames;
	}

	@Override
	public JTable getDataTable() {
		this.setupTable.cancelEditing();
		return setupTable;
	}

	/**
	 * Set experiment setup.
	 * 
	 * @param expSetup
	 */
	public void setExpSetup(String[] expSetup) {
		this.expSetup = expSetup.clone();
	}

	@Override
	public String[] getExpSetup() {
		fillExpSetup();
		return this.expSetup;
	}

	@Override
	public Object[][] getTableToObject() {
		if (setupTable.validateTableContent())
			return setupTable.getTableData();
		else
			return null;
	}

	@Override
	public String getExperimentName() {
		return this.setupPanel.getFieldName().getText().trim();
	}

	@Override
	public List<String> getConditionUnits() {
		// TODO Auto-generated method stub
		return this.condUnits;
	}
}
