package es.uvigo.ei.sing.bew.tables.dialogs;

import java.awt.Dimension;
import java.util.List;

/**
 * Interface for the table dialogs used in editable cells.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public interface ITablePanel {

	/**
	 * Get final value selected in the dialog.
	 * 
	 * @return String with the selected value.
	 */
	public String getFinalValue();

	/**
	 * Add values to the dialog.
	 * 
	 * @param values
	 *            Values to add.
	 */
	public void addValues(List<String> values);

	/**
	 * Reset the values to the default.
	 */
	public void resetValues();

	/**
	 * Get the dimension of the dialog.
	 * 
	 * @return Dialog Dimension.
	 */
	public Dimension getDimension();

	/**
	 * Set the selected value in the dialog.
	 * 
	 * @param value
	 *            Selected value.
	 */
	public void setFinalValue(String value);
}
