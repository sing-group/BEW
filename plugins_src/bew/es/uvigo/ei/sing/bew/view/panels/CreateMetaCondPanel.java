package es.uvigo.ei.sing.bew.view.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;
import es.uvigo.ei.sing.bew.view.components.IManageMeta;

/**
 * Dialog to create Conditions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CreateMetaCondPanel extends JPanel implements IManageMeta {

	private static final long serialVersionUID = 1L;

	private AddMetaValuePanel ccv;
	private CustomTextField fieldCond;

	// False == Numerical, True == Nominal
	private boolean isNom;
	private String condName;

	/**
	 * Create the panel.
	 */
	public CreateMetaCondPanel() {
		super();

		this.ccv = new AddMetaValuePanel();

		init();
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setMinimumSize(new Dimension(640, 480));
		setLayout(new BorderLayout(0, 0));
		{
			JPanel panelCondition = new JPanel();
			panelCondition.setBorder(new TitledBorder(null,
					"Introduce Condition", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			add(panelCondition, BorderLayout.NORTH);
			GridBagLayout gblPanelCond = new GridBagLayout();
			gblPanelCond.columnWidths = new int[] { 50, 25, 0, 0, 25 };
			gblPanelCond.rowHeights = new int[] { 25, 25 };
			gblPanelCond.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0 };
			gblPanelCond.rowWeights = new double[] { 0.0, 0.0 };
			panelCondition.setLayout(gblPanelCond);

			{
				JLabel lblCondition = new JLabel("Introduce Condition name:");
				GridBagConstraints gbcLblCondition = new GridBagConstraints();
				gbcLblCondition.insets = new Insets(5, 0, 5, 5);
				gbcLblCondition.gridx = 0;
				gbcLblCondition.gridy = 0;
				panelCondition.add(lblCondition, gbcLblCondition);

				fieldCond = new CustomTextField("");
				fieldCond.setToolTipText("Introduce the name of the condition.");
				fieldCond.setPlaceholder(BewConstants.DEFAULT_VALUE);
				GridBagConstraints gbcTextFieldCond = new GridBagConstraints();
				gbcTextFieldCond.fill = GridBagConstraints.HORIZONTAL;
				gbcTextFieldCond.gridwidth = 3;
				gbcTextFieldCond.insets = new Insets(5, 0, 5, 0);
				gbcTextFieldCond.gridx = 1;
				gbcTextFieldCond.gridy = 0;
				panelCondition.add(fieldCond, gbcTextFieldCond);
				fieldCond.setColumns(10);
			}
			{
				JLabel lblType = new JLabel("Set Condition type:");
				GridBagConstraints gbcLblType = new GridBagConstraints();
				gbcLblType.insets = new Insets(0, 0, 0, 5);
				gbcLblType.gridx = 0;
				gbcLblType.gridy = 1;
				panelCondition.add(lblType, gbcLblType);

				JRadioButton rdbtnNominal = new JRadioButton("Nominal");
				rdbtnNominal.setToolTipText("Check this for a nominal condition (condition with fixed values).");
				rdbtnNominal.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						ccv.setVisible(true);
						isNom = true;
					}
				});
				GridBagConstraints gbcRdbtnNominal = new GridBagConstraints();
				gbcRdbtnNominal.insets = new Insets(0, 0, 0, 5);
				gbcRdbtnNominal.gridx = 1;
				gbcRdbtnNominal.gridy = 1;
				panelCondition.add(rdbtnNominal, gbcRdbtnNominal);

				JRadioButton rdbtnNumerical = new JRadioButton("Numerical");
				rdbtnNumerical.setToolTipText("Check this for numerical condition (condition with ranged values).");
				rdbtnNumerical.setSelected(true);
				rdbtnNumerical.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						ccv.setVisible(false);
						isNom = false;
					}
				});
				GridBagConstraints gbcRdbtnNum = new GridBagConstraints();
				gbcRdbtnNum.gridx = 3;
				gbcRdbtnNum.gridy = 1;
				panelCondition.add(rdbtnNumerical, gbcRdbtnNum);

				ButtonGroup bg = new ButtonGroup();
				bg.add(rdbtnNominal);
				bg.add(rdbtnNumerical);
			}
		}
		{
			add(ccv, BorderLayout.CENTER);
			ccv.setVisible(false);
		}
	}

	/**
	 * Finishes the dialog if all is correct.
	 * 
	 * @return True if the dialog can finish, false otherwise.
	 */
	@Override
	public boolean finish() {
		this.condName = fieldCond.getText().trim();
		// Condition name is not empty and has valid values
		if (!condName.isEmpty()
				&& FunctionConstants.simbolValidationInCond(condName)) {
			// If Condition is nominal it must has at least one Condition value
			if (isNom && getCondValues().size() > 0) {
				// this.condName = "*" + condName + "*";
				condName = FunctionConstants.putAsterisks(condName);
				return true;
			} else if (!isNom) {
				// this.condName = "*" + condName + "*";
				condName = FunctionConstants.putAsterisks(condName);
				return true;
			} else {
				ShowDialog.showError(I18n.get("conditionCreateTitle"),
						I18n.get("conditionValueCreate"));
			}
		} else {
			ShowDialog.showError(I18n.get("conditionCreateTitle"),
					I18n.get("conditionCreate"));
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNominal() {
		return isNom;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return condName;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getCondValues() {
		return ccv.getCreatedValues();
	}

	@Override
	public Map<String, List<String>> getCondAndValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return "";
	}
}
