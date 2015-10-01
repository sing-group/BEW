package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory. The name is plural because the users ask it.
 * 
 * @author Gael Pérez Rodríguez.
 */
@Datatype(structure = Structure.SIMPLE, removable = false, renameable = false, namingMethod = "getName")
public class DataSeries implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private Map<Condition, Object> mapCS;
	private List<Object> measurements;
	// Exact values in each row. Including white spaces
	private List<Object> dataRow;
	private Method parent;

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            ID for the dataSerie.
	 * @param condVal
	 *            Map, key=condition, value=condition value
	 * @param measur
	 *            List of measurements
	 * @param dataRow
	 *            List with all the data for this dataSerie (with no blanks).
	 */
	public DataSeries(int id, Map<Condition, Object> condVal,
			List<Object> measur, List<Object> dataRow) {
		this.id = id;

		this.mapCS = new LinkedHashMap<Condition, Object>();
		this.measurements = new ArrayList<Object>();
		this.dataRow = new ArrayList<Object>();

		this.mapCS.putAll(condVal);
		this.measurements.addAll(measur);
		this.dataRow.addAll(dataRow);
	}

	/**
	 * Get conditions and condition values.
	 * 
	 * @return
	 */
	public Map<Condition, Object> getMapCS() {
		return this.mapCS;
	}

	/**
	 * Set conditions and condition values.
	 * 
	 * @param mapCS
	 */
	public void setMapCS(Map<Condition, Object> mapCS) {
		this.mapCS = mapCS;
	}

	/**
	 * Get measurements.
	 * 
	 * @return
	 */
	public List<Object> getMeasurements() {
		return this.measurements;
	}

	/**
	 * Set measurements.
	 * 
	 * @param measurements
	 */
	public void setMeasurements(List<Object> measurements) {
		this.measurements = measurements;
	}

	/**
	 * Obtain the total size of the dataSerie.
	 * 
	 * @return Integer with the size.
	 */
	public Integer getTotalSize() {
		return this.mapCS.values().size() + this.measurements.size();
	}

	/**
	 * Obtain the conditions size of the dataSerie.
	 * 
	 * @return Integer with the conditions size.
	 */
	public Integer getConditionSize() {
		return this.mapCS.values().size();
	}

	/**
	 * Obtain the measurements size of the dataSerie.
	 * 
	 * @return Integer with the measurements size.
	 */
	public Integer getMeasurementsSize() {
		return this.measurements.size();
	}

	/**
	 * Get condition values.
	 * 
	 * @return Collection<String> with condition values.
	 */
	public Collection<Object> getConditionValues() {
		return mapCS.values();
	}

	/**
	 * Get dataRow.
	 * 
	 * @return
	 */
	public List<Object> getDataRow() {
		return dataRow;
	}

	/**
	 * Get the conditions inside the dataRow.
	 * 
	 * @return List<Object> with the conditions.
	 */
	public List<Object> getDataRowConditions() {
		List<Object> ret = new ArrayList<Object>();

		for (int i = 0; i < getConditionSize(); i++) {
			ret.add(dataRow.get(i));
		}

		return ret;
	}

	/**
	 * Set dataRow.
	 * 
	 * @param dataRow
	 */
	public void setDataRow(List<Object> dataRow) {
		this.dataRow = dataRow;
	}

	/**
	 * Get ID.
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Set parent.
	 * 
	 * @param parent
	 */
	public void setParent(Method parent) {
		this.parent = parent;
	}

	/**
	 * Get parent.
	 * 
	 * @return
	 */
	public Method getParent() {
		return this.parent;
	}

	/**
	 * Search in the DataSerie the conditionValue associated to a Condition.
	 * 
	 * @param cond
	 *            The condition to search the value inside.
	 * @param condValue
	 *            The value we want find.
	 * @return True if we find the conditionValue for this condition, otherwise
	 *         return else.
	 */
	public boolean existsCondition(Condition cond, String condValue) {
		boolean toRet = false;
		if (this.mapCS.containsKey(cond)
				&& this.mapCS.get(cond).equals(condValue)) {
			toRet = true;
		}

		return toRet;
	}
	
	/**
	 * Get name.
	 * 
	 * @return
	 */
	public String getName() {
		return "Data Series "+id;
	}
}
