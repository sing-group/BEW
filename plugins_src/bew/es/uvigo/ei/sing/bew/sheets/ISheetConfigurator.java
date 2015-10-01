package es.uvigo.ei.sing.bew.sheets;

/**
 * Interface to divide the two types of ISheetConfigurator.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public interface ISheetConfigurator {
	/**
	 * Method to validate the structure of the Sheet.
	 * 
	 * @return True is correct, False otherwise.
	 */
	boolean validateStructure();

	/**
	 * Get sheet name.
	 * 
	 * @return
	 */
	String getSheetName();

	/**
	 * Set sheet name
	 * 
	 * @param name
	 *            String with the new name.
	 */
	void setSheetName(String name);

	/**
	 * Get experiment name.
	 * 
	 * @return
	 */
	String getExperimentName();

	/**
	 * Method to validate the structure of the sheet.
	 * 
	 * @return True is correct, False otherwise.
	 */
	boolean next();
}
