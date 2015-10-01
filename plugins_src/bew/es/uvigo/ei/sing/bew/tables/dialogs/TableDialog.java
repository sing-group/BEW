package es.uvigo.ei.sing.bew.tables.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Popup dialog for each type of cell in the custom tables.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class TableDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private ITablePanel tablePanel;

	private int typePanel;
	private List<String> condValues;
	private String finalValue;
	private TableDialog me;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            Component parent.
	 * @param type
	 *            int type of the dialog.
	 * @param condValues
	 *            List with the Condition values.
	 */
	public TableDialog(Component parent, int type, List<String> condValues) {
		super();

		this.me = this;
		this.typePanel = type;
		this.condValues = condValues;

		init();
		initButtons();
	}

	/**
	 * Initializes the dialog.
	 * 
	 */
	private void init() {
		setTitle("Select Condition values");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(1, 0, 0, 0));

		tablePanel = TablePanelFactory.createSpecificPanel(typePanel,
				condValues);
		contentPanel.add((Component) tablePanel);

		Dimension tablePanelDim = tablePanel.getDimension();
		setMinimumSize(new Dimension(tablePanelDim.height + 25,
				tablePanelDim.width + 25));

		setLocationRelativeTo(null);
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "OK", I18n.get("cancel"), "Reset" };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png"),
				getClass().getResource("/img/glass.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finalValue = tablePanel.getFinalValue();

				if (finalValue != null) {
					// Only dispose when user selects a value
					dispose();
				} else {
					ShowDialog.showError(me, I18n.get("valueCreateCharsTitle"),
							I18n.get("valueCreateChars"));
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tablePanel.resetValues();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						"addCond", this), BorderLayout.SOUTH);
	}

	/**
	 * Adds values to the dialog.
	 * 
	 * @param values
	 *            Values to add.
	 */
	public void addValues(List<String> values) {
		this.tablePanel.addValues(values);
	}

	/**
	 * DEPRECATED.
	 * 
	 * @param row
	 */
	public void setTableRow(Integer row) {
		// this.tableRow = row;
	}

	/**
	 * Set selected value in the dialog.
	 * 
	 * @param value
	 *            Value to be selected.
	 */
	public void setFinalValue(String value) {
		this.tablePanel.setFinalValue(value);
	}

	/**
	 * Get selected value.
	 * 
	 * @return String with the value.
	 */
	public String getFinalValue() {
		return finalValue;
	}
}
