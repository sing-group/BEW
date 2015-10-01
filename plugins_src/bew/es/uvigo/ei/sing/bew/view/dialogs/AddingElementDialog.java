package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.sheets.ISheetConfigurator;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog for adding an element.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class AddingElementDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private ISheetConfigurator sheetConf;
	private Experiment experiment;
	private boolean exit = false;

	/**
	 * Default constructor.
	 * 
	 * @param sheetConf
	 *            ISheetConfigurator with the data.
	 * @param exp
	 *            IntraExperiment.
	 */
	public AddingElementDialog(ISheetConfigurator sheetConf, Experiment exp) {
		super();

		this.experiment = exp;
		this.sheetConf = sheetConf;

		initialize();
		initButtons();

		this.setLocationRelativeTo(null);
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		// Dialog configuration
		setTitle(I18n.get("newMethodInExp") + " (" + experiment.getName() + ")");
		this.setMinimumSize(new Dimension(800, 600));
		this.setMaximumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(false);

		Container contentPane = this.getContentPane();
		getContentPane().setLayout(new BorderLayout(0, 0));

		contentPane.add((Component) sheetConf, BorderLayout.CENTER);
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { I18n.get("finalize"), I18n.get("validate"),
				I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/glass.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (validateSheet()) {
					exit = true;
					dispose();
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateSheet()) {
					JOptionPane.showMessageDialog(null,
							I18n.get("correctStructure"),
							I18n.get("correctStructureTitle"),
							JOptionPane.INFORMATION_MESSAGE);
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
						"addMethod", this), BorderLayout.SOUTH);
	}

	/**
	 * Method to validate the sheet.
	 * 
	 * @return True if the sheet is valid, false otherwise.
	 */
	private boolean validateSheet() {
		boolean toRet = false;
		if (sheetConf.next()) {
			if (validateSheetName())
				toRet = true;
		} else
			toRet = false;

		return toRet;
	}

	/**
	 * Method to validate the sheet name.
	 * 
	 * @return True if the name is valid, false otherwise.
	 */
	private boolean validateSheetName() {
		String name = sheetConf.getSheetName();
		int index = 1;
		boolean repetition = true;
		List<Method> methods = experiment.getMethods().getMetodos();
		List<String> names = new ArrayList<String>();

		for (Method m : methods) {
			names.add(m.getName());
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
	 * Get exit.
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
