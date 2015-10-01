package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.ListElements;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class keeps all the methods for an experiment.
 * 
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Datatype(structure = Structure.LIST, renameable = false, namingMethod = "getName")
public class Methods extends Observable implements Serializable {

	private static final long serialVersionUID = 1L;

	List<Method> methods;

	/**
	 * Default constructor.
	 * 
	 * @param methods
	 *            List with methods.
	 */
	public Methods(List<Method> methods) {
		// TODO Auto-generated constructor stub.
		super();

		this.methods = new ArrayList<Method>();
		this.methods.addAll(methods);
	}

	/**
	 * Empty constructor.
	 */
	public Methods() {
		// TODO Auto-generated constructor stub
		super();

		this.methods = new ArrayList<Method>();
	}

	/**
	 * Add a new method.
	 * 
	 * @param methods
	 */
	public void addMethod(Method methods) {
		this.methods.add(methods);
		this.setChanged();
		this.notifyObservers();
	}

	@ListElements
	public List<Method> getMetodos() {
		return this.methods;
	}

	/**
	 * Get name.
	 * 
	 * @return
	 */
	public String getName() {
		return "Methods";
	}
}
