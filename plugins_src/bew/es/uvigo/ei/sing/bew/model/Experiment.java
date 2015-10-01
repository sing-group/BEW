package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
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
public class Experiment extends Observable implements IExperiment,
		Serializable {

	private static final long serialVersionUID = 1L;

	// Default name
	private String experimentName = "";
	private String authors = "";
	private String organization = "";
	private String contact = "";
	private String date = "";
	private String publication = "";
	private String notes = "";
	private String bioID = "";

	private Methods methods;
	private ConstantConditions constantCondition;

	/**
	 * Default constructor.
	 * 
	 * @param methods
	 *            Methods for this experiment.
	 * @param constantCond
	 *            Constant conditions.
	 */
	public Experiment(Methods methods, ConstantConditions constantCond) {
		// TODO Auto-generated constructor stub
		super();

		this.methods = methods;
		this.constantCondition = constantCond;
	}

	/**
	 * Empty constructor.
	 */
	public Experiment() {
		// TODO Auto-generated constructor stub
		super();

		this.methods = new Methods();
	}

	@Override
	@Clipboard(name = "Methods", order = 1)
	public Methods getMethods() {
		return this.methods;
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
	 * Get method by input name.
	 * 
	 * @param name
	 *            Name of the method.
	 * @return Method.
	 */
	public Method getMethodByName(String name) {
		for (Method met : this.methods.getMetodos()) {
			if (name.equals(met.getName()))
				return met;
		}
		return null;
	}

	/**
	 * Add a method.
	 * 
	 * @param method
	 *            Method to add.
	 */
	public void addMetodo(Method method) {
		this.methods.addMethod(method);
	}

	/**
	 * Delete the input method.
	 * 
	 * @param method
	 *            Method to delete.
	 */
	public void deleteMethod(Method method) {
		this.methods.getMetodos().remove(method);
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	@Clipboard(name = "Constant Condition", order = 2)
	public ConstantConditions getConstantCondition() {
		return this.constantCondition;
	}

	/**
	 * Set constant conditions.
	 * 
	 * @param constantCond
	 */
	public void addConstantCondition(ConstantConditions constantCond) {
		this.constantCondition = constantCond;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Delete constant conditions.
	 */
	public void deleteConstant() {
		this.constantCondition = null;
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String getName() {
		return this.experimentName;
	}

	/**
	 * Set name.
	 * 
	 * @param experimentName
	 */
	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	/**
	 * Get authors.
	 * 
	 * @return
	 */
	public String getAuthors() {
		return authors;
	}

	/**
	 * Set authors.
	 * 
	 * @param authors
	 */
	public void setAuthors(String authors) {
		this.authors = authors;
	}

	/**
	 * Get organization.
	 * 
	 * @return
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Set organization.
	 * 
	 * @param organization
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * Get contact.
	 * 
	 * @return
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Set contact.
	 * 
	 * @param contact
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * Get date.
	 * 
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Set date.
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Get publication.
	 * 
	 * @return
	 */
	public String getPublication() {
		return publication;
	}

	/**
	 * Set publication.
	 * 
	 * @param publication
	 */
	public void setPublication(String publication) {
		this.publication = publication;
	}

	/**
	 * Get notes.
	 * 
	 * @return
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Set notes.
	 * 
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public void setExpSetup(String[] expSetup) {
		this.experimentName = expSetup[0];
		this.authors = expSetup[1];
		this.organization = expSetup[2];
		this.contact = expSetup[3];
		this.date = expSetup[4];
		this.publication = expSetup[5];
		this.notes = expSetup[6];
		this.bioID = expSetup[7];

		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String[] getExpSetup() {
		String[] expSetup = new String[8];

		expSetup[0] = this.experimentName;
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
	public Map<Object, List<Object>> getMapIntraExpsAndRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Object, Object> getMapIntraExpsColors() {
		// TODO Auto-generated method stub
		return null;
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
