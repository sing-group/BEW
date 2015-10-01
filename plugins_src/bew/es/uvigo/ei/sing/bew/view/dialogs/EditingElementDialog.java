package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.sheets.ISheetConfigurator;
import es.uvigo.ei.sing.bew.sheets.ImportMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.sheets.SetupSheetConfig;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog for editing an element (Experiment or Method).
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class EditingElementDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private boolean exit;
	private IExperiment exp;
	private Method method;
	private ISheetConfigurator sheetConf;

	/**
	 * Constructor to edit a created Experiment Setup Sheet (Used by
	 * EditExpSetup).
	 * 
	 * @param name
	 *            Sheet name.
	 * @param data
	 *            Sheet data.
	 * @param expSetup
	 *            Experiment Setup data.
	 * @param exp
	 *            Experiment.
	 * @wbp.parser.constructor
	 */
	public EditingElementDialog(String name, Object[][] data,
			String[] expSetup, IExperiment exp, boolean isInter) {
		super();

		setTitle(I18n.get("editingExpSetup"));

		this.exp = exp;
		sheetConf = new SetupSheetConfig(name, data, expSetup, isInter);

		initialize();
		initButtons("editIntra");
	}

	/**
	 * Constructor to edit a created Method (Used by EditMethod).
	 * 
	 * @param name
	 *            Method name.
	 * @param units
	 *            Method units.
	 * @param data
	 *            Method data.
	 * @param condNames
	 *            Condition names.
	 * @param condValues
	 *            Condition values.
	 * @param condUnits
	 *            Condition units.
	 * @param method
	 *            Method.
	 */
	public EditingElementDialog(String name, String units, Object[][] data,
			List<String> condNames, List<Integer> condValues,
			List<String> condUnits, Method method) {
		super();

		setTitle(I18n.get("editingMethod"));

		this.method = method;
		sheetConf = new ImportMethodSheetConfigurator(name, units, data,
				condNames, condValues, condUnits, condNames.size());

		initialize();
		initButtons("editMethod");
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		// Dialog configuration
		this.setMinimumSize(new Dimension(800, 600));
		this.setMaximumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setAlwaysOnTop(false);
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(0, 0));

		getContentPane().add((Component) sheetConf, BorderLayout.CENTER);
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons(String help) {
		String[] buttonNames = { I18n.get("finalize"), I18n.get("validate"),
				I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/glass.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateSheet()) {
					exit = true;
					dispose();
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateSheet()) {
					ShowDialog.showInfo(I18n.get("correctStructureTitle"),
							I18n.get("correctStructure"));
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit = false;
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						help, this), BorderLayout.SOUTH);
	}

	/**
	 * Validates the Sheet in the dialog.
	 * 
	 * @return True if the sheet is valid, false otherwise.
	 */
	private boolean validateSheet() {
		// For editing experiment
		if (exp != null) {
			// Sheet is correct and the edited experiment has a unique name
			if (FunctionConstants.validateExperimentNames(exp,
					sheetConf.getExperimentName())) {
				if (sheetConf.next())
					return true;
			} else
				ShowDialog.showError(I18n.get("duplicateNameTitle"),
						I18n.get("duplicateName"));
		}
		// For editing method
		else if (method != null) {
			// Sheet is correct and the edited method has a unique name
			if (sheetConf.next()) {
				if (validateSheetName()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Validates sheet name in the dialog.
	 * 
	 * @return True if name is unique, false otherwise.
	 */
	private boolean validateSheetName() {
		String name = sheetConf.getSheetName();
		int index = 1;
		boolean repetition = true;
		List<Method> methods = method.getParent().getMethods().getMetodos();
		List<String> names = new ArrayList<String>();

		for (Method m : methods) {
			if (!method.equals(m)) {
				names.add(m.getName());
			}
		}

		while (repetition) {
			if (!names.contains(name)) {
				repetition = false;
			} else {
				name = sheetConf.getSheetName() + "_" + index;
				index++;
			}
		}

		sheetConf.setSheetName(name);

		return true;
	}

	/**
	 * Check exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.exit;
	}

	/**
	 * Get sheet.
	 * 
	 * @return
	 */
	public ISheetConfigurator getSheet() {
		return this.sheetConf;
	}
}
