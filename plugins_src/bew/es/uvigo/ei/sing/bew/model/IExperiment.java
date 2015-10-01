package es.uvigo.ei.sing.bew.model;

import java.util.List;
import java.util.Map;

/**
 * Interface for the two types of Experiments in the application (intra and
 * inter).
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public interface IExperiment {
	/**
	 * Get experiment name.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Get experiment methods.
	 * 
	 * @return
	 */
	Methods getMethods();

	/**
	 * Get experiment constant conditions.
	 * 
	 * @return
	 */
	ConstantConditions getConstantCondition();

	/**
	 * Get experiment setup.
	 * 
	 * @return
	 */
	String[] getExpSetup();

	/**
	 * Set experiment setup.
	 * 
	 * @param setup
	 *            String[7]
	 */
	void setExpSetup(String[] setup);

	/**
	 * Get intraExperiments with their rows.
	 * 
	 * @return
	 */
	Map<Object, List<Object>> getMapIntraExpsAndRows();

	/**
	 * Get intraExperiments with their colors.
	 * 
	 * @return
	 */
	Map<Object, Object> getMapIntraExpsColors();

	/**
	 * Get the biofomics id associated to the Experiment.
	 * 
	 * @return
	 */
	String getBioID();

	/**
	 * Set the biofomics id associated to the Experiment.
	 * 
	 * @param bioID
	 */
	void setBioID(String bioID);
}
