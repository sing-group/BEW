package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Datatype(structure = Structure.SIMPLE, removable = false, renameable = false, namingMethod = "getName")
public class Condition implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<Object> conditionValues;
	private String units;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            Condition name.
	 * @param condVal
	 *            Condition values.
	 * @param units
	 *            Condition units.
	 */
	public Condition(String name, List<Object> condVal, String units) {
		this.conditionValues = new LinkedList<Object>();

		this.name = name;
		this.conditionValues.addAll(condVal);
		this.units = units;
	}

	/**
	 * Get the name.
	 * 
	 * @return String name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get Condition values.
	 * 
	 * @return LinkedList<String> with the Condition values.
	 */
	public List<Object> getConditionValues() {
		return this.conditionValues;
	}

	/**
	 * Set the Condition values.
	 * 
	 * @param conditionValues
	 */
	public void setConditionValues(List<Object> conditionValues) {
		this.conditionValues = conditionValues;
	}

	/**
	 * Get units.
	 * 
	 * @return units.
	 */
	public String getUnits() {
		return this.units;
	}

	/**
	 * Set units.
	 * 
	 * @param units
	 *            Input units.
	 */
	public void setUnits(String units) {
		this.units = units;
	}
}
