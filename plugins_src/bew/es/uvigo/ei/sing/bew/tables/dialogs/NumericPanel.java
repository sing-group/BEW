package es.uvigo.ei.sing.bew.tables.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;

/**
 * Custom panel for Numerical Conditions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class NumericPanel extends JPanel implements ITablePanel {

	private static final long serialVersionUID = 1L;

	private CustomTextField fieldValue;

	/**
	 * Create the panel.
	 */
	public NumericPanel() {
		super();
		init();
	}

	/**
	 * Initializes the panel.
	 */
	private void init() {
		setMinimumSize(new Dimension(105, 375));
		{
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 100, 100 };
			gridBagLayout.rowHeights = new int[] { 20, 0 };
			gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
			gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			setLayout(gridBagLayout);
			fieldValue = new CustomTextField("");
			fieldValue.setPlaceholder(BewConstants.DEFAULT_VALUE);
			{
				JLabel lblInfo = new JLabel("Introduce a value or concentration:");
				GridBagConstraints gbcLblInfo = new GridBagConstraints();
				gbcLblInfo.insets = new Insets(0, 0, 0, 5);
				gbcLblInfo.gridx = 0;
				gbcLblInfo.gridy = 0;
				add(lblInfo, gbcLblInfo);
			}
			GridBagConstraints gbcFieldValue = new GridBagConstraints();
			gbcFieldValue.fill = GridBagConstraints.HORIZONTAL;
			gbcFieldValue.gridx = 1;
			gbcFieldValue.gridy = 0;
			add(fieldValue, gbcFieldValue);
			fieldValue.setColumns(10);
		}
	}

	@Override
	public String getFinalValue() {
		// Original border, successful exit
		String toRet = fieldValue.getText().trim();
		if (!FunctionConstants.simbolValidationInCond(toRet))
			return null;
		else
			return toRet;
	}

	@Override
	public void addValues(List<String> values) {
		// TODO Auto-generated method stub
	}

	@Override
	public Dimension getDimension() {
		// TODO Auto-generated method stub
		return getMinimumSize();
	}

	@Override
	public void resetValues() {
		// TODO Auto-generated method stub
		this.fieldValue.setText("");
	}

	@Override
	public void setFinalValue(String value) {
		String newValue = value;
		if (newValue.startsWith("*") && newValue.endsWith("*")) {
			newValue = newValue.substring(1, newValue.length() - 1);
		}
		this.fieldValue.setText(newValue);
	}
}