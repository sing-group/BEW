package es.uvigo.ei.sing.bew.view.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
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
public class CreateMetaMethodPanel extends JPanel implements
		IManageMeta {

	private static final long serialVersionUID = 1L;

	private CustomTextField textFieldName;
	private CustomTextField textFieldUnits;

	private String methodName;
	private String methodUnits;

	/**
	 * Create the panel.
	 */
	public CreateMetaMethodPanel() {
		super();

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
			panelCondition.setBorder(new TitledBorder(null, "Introduce Method",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			add(panelCondition, BorderLayout.CENTER);
			GridBagLayout gblPanelCond = new GridBagLayout();
			gblPanelCond.columnWidths = new int[] { 50, 25, 30, 30 };
			gblPanelCond.rowHeights = new int[] { 25, 25 };
			gblPanelCond.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0 };
			gblPanelCond.rowWeights = new double[] { 0.0, 0.0 };
			panelCondition.setLayout(gblPanelCond);

			{
				JLabel lblCondition = new JLabel("Introduce Method name:");
				GridBagConstraints gbcLblCond = new GridBagConstraints();
				gbcLblCond.insets = new Insets(5, 0, 5, 5);
				gbcLblCond.gridx = 0;
				gbcLblCond.gridy = 0;
				panelCondition.add(lblCondition, gbcLblCond);

				textFieldName = new CustomTextField("");
				textFieldName.setPlaceholder(BewConstants.DEFAULT_VALUE);
				GridBagConstraints gbcTextFieldName = new GridBagConstraints();
				gbcTextFieldName.fill = GridBagConstraints.HORIZONTAL;
				gbcTextFieldName.gridwidth = 3;
				gbcTextFieldName.insets = new Insets(5, 0, 5, 5);
				gbcTextFieldName.gridx = 1;
				gbcTextFieldName.gridy = 0;
				panelCondition.add(textFieldName, gbcTextFieldName);
				textFieldName.setColumns(10);
			}
			{
				JLabel lblType = new JLabel("Introduce Method units:");
				GridBagConstraints gbcLblType = new GridBagConstraints();
				gbcLblType.insets = new Insets(0, 0, 0, 5);
				gbcLblType.gridx = 0;
				gbcLblType.gridy = 1;
				panelCondition.add(lblType, gbcLblType);

				textFieldUnits = new CustomTextField("");
				textFieldUnits.setPlaceholder("NaN");
				textFieldUnits.setColumns(10);
				GridBagConstraints gbcTextFieldUnits = new GridBagConstraints();
				gbcTextFieldUnits.gridwidth = 3;
				gbcTextFieldUnits.insets = new Insets(0, 0, 0, 5);
				gbcTextFieldUnits.fill = GridBagConstraints.HORIZONTAL;
				gbcTextFieldUnits.gridx = 1;
				gbcTextFieldUnits.gridy = 1;
				panelCondition.add(textFieldUnits, gbcTextFieldUnits);
			}
		}
	}

	/**
	 * Finishes the dialog if all is correct.
	 * 
	 * @return True if the dialog can finish, false otherwise.
	 */
	@Override
	public boolean finish() {
		this.methodName = textFieldName.getText().trim();
		this.methodUnits = textFieldUnits.getText().trim();

		if (methodUnits.isEmpty()) {
			this.methodUnits = "???";
		}

		// Method name is not empty
		if (!methodName.isEmpty()) {
			methodName = FunctionConstants.putAsterisks(methodName);
			return true;
		} else {
			ShowDialog.showError(I18n.get("methodCreateTitle"),
					I18n.get("methodCreate"));
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNominal() {
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return methodName;
	}

	/**
	 * 
	 * @return
	 */
	public String getUnits() {
		return methodUnits;
	}

	@Override
	public Map<String, List<String>> getCondAndValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCondValues() {
		// TODO Auto-generated method stub
		return null;
	}
}
