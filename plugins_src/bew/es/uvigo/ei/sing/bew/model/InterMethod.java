package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
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
@Datatype(structure = Structure.COMPLEX, namingMethod = "getName", renameable = false, removable = false)
public class InterMethod extends Method implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor, calls to super.
	 * 
	 * @param name
	 *            Method name.
	 * @param units
	 *            Method units.
	 * @param dataSer
	 *            Method dataSeries.
	 * @param aCondition
	 *            Method Conditions.
	 * @param parent
	 *            Method parent (Experiment).
	 */
	public InterMethod(String name, String units, List<DataSeries> dataSer,
			List<Condition> aCondition, IExperiment parent) {
		// TODO Auto-generated constructor stub
		super(name, units, dataSer, aCondition, parent);
	}
}
