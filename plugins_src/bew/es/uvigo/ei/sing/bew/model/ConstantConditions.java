package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Datatype(structure = Structure.SIMPLE, renameable = false, namingMethod = "getClipName")
public class ConstantConditions extends Observable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> constantCond;
	private List<String> constantValues;
	private List<String> constantUnits;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            ConstantCondition name.
	 * @param cond
	 *            Conditions.
	 * @param condVal
	 *            Condition values.
	 * @param condUnits
	 *            Condition units.
	 */
	public ConstantConditions(String name, List<String> cond,
			List<String> condVal, List<String> condUnits) {
		super();

		this.constantCond = new ArrayList<String>();
		this.constantValues = new ArrayList<String>();
		this.constantUnits = new ArrayList<String>();
		this.name = name;
		this.constantCond.addAll(cond);
		this.constantValues.addAll(condVal);
		this.constantUnits.addAll(condUnits);
	}

	/**
	 * Empty constructor.
	 */
	public ConstantConditions() {
		super();

		this.constantCond = new ArrayList<String>();
		this.constantValues = new ArrayList<String>();
		this.constantUnits = new ArrayList<String>();
	}

	/**
	 * Get the name.
	 * 
	 * @return
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
	 * 
	 * @return
	 */
	public List<String> getConstantConditions() {
		return constantCond;
	}

	/**
	 * 
	 * @param constantCond
	 */
	public void setConstantConditions(List<String> constantCond) {
		this.constantCond = constantCond;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getConstantValues() {
		return constantValues;
	}

	/**
	 * 
	 * @param constantValues
	 */
	public void setConstantValues(List<String> constantValues) {
		this.constantValues = constantValues;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getConstantUnits() {
		return constantUnits;
	}

	/**
	 * 
	 * @param constantUnits
	 */
	public void setConstantUnits(List<String> constantUnits) {
		this.constantUnits = constantUnits;
	}

	/**
	 * Transform the conditions and condition values into a dynamic matrix.
	 * 
	 * @return Dynamic matrix.
	 */
	public List<List<Object>> dataToMatrix() {
		List<List<Object>> ret = new ArrayList<List<Object>>();

		for (int i = 0; i < constantCond.size(); i++) {
			List<Object> aux = new ArrayList<Object>();
			aux.add(constantCond.get(i));
			aux.add(constantValues.get(i));
			aux.add(constantUnits.get(i));
			ret.add(aux);
		}

		return ret;
	}

	/**
	 * Method to edit the constant conditions. It clears the actual conditions
	 * and condition values and set the news.
	 * 
	 * @param constantCond
	 *            New constant conditions.
	 */
	public void editConstantCondition(ConstantConditions constantCond) {
		this.constantCond.clear();
		this.constantValues.clear();
		this.constantUnits.clear();

		this.name = constantCond.getName();
		this.constantCond.addAll(constantCond.getConstantConditions());
		this.constantValues.addAll(constantCond.getConstantValues());
		this.constantUnits.addAll(constantCond.getConstantUnits());

		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Get name.
	 * 
	 * @return
	 */
	public String getClipName() {
		return "Constant Condition";
	}
}
