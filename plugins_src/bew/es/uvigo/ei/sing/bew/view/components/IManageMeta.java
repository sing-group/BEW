package es.uvigo.ei.sing.bew.view.components;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public interface IManageMeta {
	/**
	 * Method to finish the dialog.
	 * 
	 * @return
	 */
	boolean finish();

	/**
	 * Method to check if the condition is nominal.
	 * 
	 * @return
	 */
	boolean isNominal();

	/**
	 * Method to get the name.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Method to get the units.
	 * 
	 * @return
	 */
	String getUnits();

	/**
	 * Method to get condition and values created in the dialog.
	 * 
	 * @return
	 */
	Map<String, List<String>> getCondAndValues();

	/**
	 * Method to get values created in the dialog.
	 * 
	 * @return
	 */
	List<String> getCondValues();
}
