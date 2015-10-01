package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import es.uvigo.ei.aibench.core.datatypes.annotation.Clipboard;
import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
@Datatype(structure = Structure.COMPLEX, namingMethod = "getName", renameable = false)
public class InterLabExperiment extends Observable implements IExperiment,
		Serializable {

	private static final long serialVersionUID = 1L;
	// Default name
	private String name = "";
	private String contact = "";
	private String organization = "";
	private String date = "";
	private String authors = "";
	private String publication = "";
	private String notes = "";
	private String bioID = "";

	// IntraExperiment and DataSeries
	private Map<Object, List<Object>> mapIntraExpsRows;
	// IntraExperiment and Color
	private Map<Object, Object> expColors;

	private Methods methods;
	private ConstantConditions constantCond;

	/**
	 * Default constructor.
	 * 
	 * @param interSetup
	 *            InterExperiment Setup.
	 */
	public InterLabExperiment(String[] interSetup) {
		super();
		
		this.mapIntraExpsRows = new HashMap<Object, List<Object>>();
		this.expColors = new HashMap<Object, Object>();
		this.methods = new Methods();
		this.constantCond = new ConstantConditions();

		setExpSetup(interSetup);
	}

	/**
	 * Empty constructor.
	 */
	public InterLabExperiment() {
		super();
		
		this.mapIntraExpsRows = new HashMap<Object, List<Object>>();
		this.expColors = new HashMap<Object, Object>();
		this.methods = new Methods();
		this.constantCond = new ConstantConditions();
	}

	/**
	 * Set methods.
	 * 
	 * @param methods
	 */
	public void setMetodo(Methods methods) {
		this.methods = methods;
	}

	/**
	 * Add a new method.
	 * 
	 * @param method
	 */
	public void addMetodo(Method method) {
		this.methods.addMethod(method);
	}

	/**
	 * Delete the input method.
	 * 
	 * @param method
	 */
	public void deleteMethod(Method method) {
		this.methods.getMetodos().remove(method);
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	@Clipboard(name = "Methods", order = 1)
	public Methods getMethods() {
		return this.methods;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Set name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Clipboard(name = "Constant Condition", order = 2)
	public ConstantConditions getConstantCondition() {
		// TODO Auto-generated method stub
		return constantCond;
	}

	/**
	 * 
	 * @param constantCond
	 */
	public void setConstantCondition(ConstantConditions constantCond) {
		// TODO Auto-generated method stub
		this.constantCond = constantCond;
	}

	@Override
	public String[] getExpSetup() {
		// TODO Auto-generated method stub
		String[] expSetup = new String[8];

		expSetup[0] = this.name;
		expSetup[1] = this.authors;
		expSetup[2] = this.organization;
		expSetup[3] = this.contact;
		expSetup[4] = this.date;
		expSetup[5] = this.publication;
		expSetup[6] = this.notes;
		expSetup[7] = this.bioID;

		return expSetup;
	}

	@Override
	public void setExpSetup(String[] setup) {
		this.name = setup[0];
		this.authors = setup[1];
		this.organization = setup[2];
		this.contact = setup[3];
		this.date = setup[4];
		this.publication = setup[5];
		this.notes = setup[6];
		this.bioID = setup[7];

		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public Map<Object, List<Object>> getMapIntraExpsAndRows() {
		return this.mapIntraExpsRows;
	}

	@Override
	public Map<Object, Object> getMapIntraExpsColors() {
		return this.expColors;
	}

	/**
	 * Get intraExperiments.
	 * 
	 * @return
	 */
	public List<IExperiment> getIntraExperiments() {
		List<IExperiment> ret = new ArrayList<IExperiment>();

		// Keys are Experiments
		for (Object exp : mapIntraExpsRows.keySet()) {
			ret.add((IExperiment) exp);
		}

		return ret;
	}

	/**
	 * Get the rows for the input experiment.
	 * 
	 * @param exp
	 *            Experiment to find rows.
	 * @return List<Integer> with the rows.
	 */
	public List<Integer> getIntraRows(IExperiment exp) {
		List<Integer> ret = new ArrayList<Integer>();

		for (Object row : mapIntraExpsRows.get(exp)) {
			ret.add((Integer) row);
		}

		return ret;
	}

	/**
	 * Set intraExperiments rows.
	 * 
	 * @param expRows
	 */
	public void setIntraExperiments(
			Map<Object, List<Object>> expRows) {
		mapIntraExpsRows = expRows;
	}

	/**
	 * Set intraExperiment colors.
	 * 
	 * @param intraColors
	 */
	public void setIntraColors(Map<Object, Object> intraColors) {
		expColors = intraColors;
	}

	@Override
	public String getBioID() {
		// TODO Auto-generated method stub
		return this.bioID;
	}

	@Override
	public void setBioID(String bioID) {
		// TODO Auto-generated method stub
		this.bioID = bioID;
	}
}
