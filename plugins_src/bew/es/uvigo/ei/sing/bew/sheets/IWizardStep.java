package es.uvigo.ei.sing.bew.sheets;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * Interface for the Sheets.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public interface IWizardStep {
	/**
	 * Validate the structure.
	 * 
	 * @return
	 */
	boolean next();

	/**
	 * Get the panel of the Sheet.
	 * 
	 * @return
	 */
	JPanel getPanel();

	/**
	 * Get condition names.
	 * 
	 * @return
	 */
	List<String> getConditionNames();

	/**
	 * Get condition units.
	 * 
	 * @return
	 */
	List<String> getConditionUnits();

	/**
	 * Get number of conditions.
	 * 
	 * @return
	 */
	Integer getNumConditions();

	/**
	 * Transform the data table into a static matrix.
	 * 
	 * @return Object[][] with the data.
	 */
	Object[][] getTableToObject();

	/**
	 * Get sheet name.
	 * 
	 * @return
	 */
	String getSheetName();

	/**
	 * Set sheet name.
	 * 
	 * @param name
	 *            String with the sheet name.
	 */
	void setSheetName(String name);

	/**
	 * Get sheet units.
	 * 
	 * @return
	 */
	String getUnits();

	/**
	 * Get data table.
	 * 
	 * @return
	 */
	JTable getDataTable();

	/**
	 * Set last column.
	 * 
	 * @param column
	 */
	void setLastCol(int column);

	/**
	 * Check if the sheet is a constant sheet.
	 * 
	 * @return
	 */
	boolean isConstant();

	/**
	 * Get experiment setup.
	 * 
	 * @return
	 */
	String[] getExpSetup();
}
