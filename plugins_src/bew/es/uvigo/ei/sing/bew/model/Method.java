package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.aibench.core.datatypes.annotation.Clipboard;
import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.ListElements;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Datatype(structure = Structure.COMPLEX, namingMethod = "getName", renameable = false, removeMethod = "remove")
public class Method extends Observable implements Serializable {

	@Datatype(structure = Structure.LIST, renameable = false)
	public class AIBenchList<T> extends Observable implements Serializable {
		private static final long serialVersionUID = 1L;

		private List<T> delegate;

		/**
		 * Default constructor.
		 * 
		 * @param delegate
		 *            List with values.
		 */
		public AIBenchList(List<T> delegate) {
			this.delegate = delegate;
		}

		@ListElements
		public List<T> getElements() {
			return this.delegate;
		}

		/**
		 * Add all the values from the input list.
		 * 
		 * @param list
		 *            List with values.
		 */
		public void addAll(List<T> list) {
			this.delegate.addAll(list);

			this.setChanged();
			this.notifyObservers();
		}

		/**
		 * Clear all the current values.
		 */
		public void clear() {
			this.delegate.clear();

			this.setChanged();
			this.notifyObservers();
		}

		/**
		 * Add one element to the list.
		 * 
		 * @param element
		 *            Element to add.
		 */
		public void addElement(T element) {
			this.delegate.add(element);

			this.setChanged();
			this.notifyObservers();
		}
	}

	private static final long serialVersionUID = 1L;

	// Variables
	private String name;
	private String units;
	private IExperiment parent;
	private AIBenchList<DataSeries> dataSeries;
	private AIBenchList<Condition> arrayCondition;
	private AIBenchList<Plot> arrayPlots;
	private AIBenchList<Statistic> arrayStatistics;

	/**
	 * Default constructor.
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
	 *            Method experiment parent.
	 */
	public Method(String name, String units, List<DataSeries> dataSer,
			List<Condition> aCondition, IExperiment parent) {
		super();

		this.name = name;
		this.units = units;
		this.parent = parent;
		this.dataSeries = new AIBenchList<DataSeries>(
				new ArrayList<DataSeries>());
		this.arrayCondition = new AIBenchList<Condition>(
				new ArrayList<Condition>());
		this.arrayPlots = new AIBenchList<Plot>(new ArrayList<Plot>());
		this.arrayStatistics = new AIBenchList<Statistic>(
				new ArrayList<Statistic>());

		this.dataSeries.addAll(dataSer);
		this.arrayCondition.addAll(aCondition);
	}

	@Clipboard(name = "Data Series", order = 1)
	public AIBenchList<DataSeries> getDataSeries() {
		return this.dataSeries;
	}

	@Clipboard(name = "Conditions", order = 2)
	public AIBenchList<Condition> getArrayCondition() {
		return this.arrayCondition;
	}

	@Clipboard(name = "Plots", order = 3)
	public AIBenchList<Plot> getArrayPlots() {
		return this.arrayPlots;
	}

	@Clipboard(name = "Statistics", order = 4)
	public AIBenchList<Statistic> getArrayStatistics() {
		return this.arrayStatistics;
	}

	/**
	 * Get name.
	 * 
	 * @return
	 */
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

	/**
	 * Add a new plot.
	 * 
	 * @param plot
	 */
	public void addPlot(Plot plot) {
		this.arrayPlots.addElement(plot);
	}

	/**
	 * Add a new statistic.
	 * 
	 * @param statistic
	 */
	public void addStatistics(Statistic statistic) {
		this.arrayStatistics.addElement(statistic);
	}

	/**
	 * Method to convert all the data in this method into a matrix.
	 * 
	 * @return Dynamic matrix.
	 */
	public List<List<Object>> allDataToMatrix() {
		List<List<Object>> ret = new ArrayList<List<Object>>();

		for (DataSeries ds : dataSeries.getElements()) {
			List<Object> aux = new ArrayList<Object>();
			for (Object value : ds.getDataRow())
				aux.add(value);

			ret.add(aux);
		}

		return ret;
	}

	/**
	 * Method to convert only the condition values in this method table into a
	 * matrix.
	 * 
	 * @return Dynamic matrix.
	 */
	public List<List<Object>> conditionsToMatrix() {
		List<List<Object>> ret = new ArrayList<List<Object>>();

		for (DataSeries ds : dataSeries.getElements()) {
			ret.add(ds.getDataRowConditions());
		}

		return ret;
	}

	/**
	 * Method to edit the method. It clears the actual values and set the news.
	 * 
	 * @param method
	 *            New method to set.
	 */
	public void editMethod(Method method) {
		this.arrayCondition.clear();
		this.dataSeries.clear();

		this.name = method.getName();
		this.units = method.getUnits();
		this.arrayCondition.addAll(method.getArrayCondition().getElements());
		this.dataSeries.addAll(method.getDataSeries().getElements());

		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets the number of conditions in this Method.
	 * 
	 * @return int with the number of conditions.
	 */
	public int getNumConditions() {
		Set<String> set = new HashSet<String>();
		for (Condition c : arrayCondition.getElements()) {
			set.add(c.getName());
		}

		return set.size();
	}

	/**
	 * 
	 * @return
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * 
	 * @param units
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * 
	 * @return
	 */
	public IExperiment getParent() {
		return parent;
	}

	/**
	 * 
	 * @param parent
	 */
	public void setParent(Experiment parent) {
		this.parent = parent;
	}

	/**
	 * Removes the input plot from the list of plots.
	 * 
	 * @param plot
	 *            Plot to remove.
	 */
	public void removePlotFromArray(Plot plot) {
		this.arrayPlots.getElements().remove(plot);
	}

	/**
	 * Removes the input Statistic from the list of Statistics.
	 * 
	 * @param statistic
	 *            Statistic to remove.
	 */
	public void removeStatisticFromArray(Statistic statistic) {
		this.arrayStatistics.getElements().remove(statistic);
	}

	/**
	 * Method to remove the method from the system.
	 */
	public void remove() {
		List<ClipboardItem> items = Core.getInstance().getClipboard()
				.getItemsByClass(Method.class);
		ClipboardItem torem = null;
		for (ClipboardItem item : items) {
			if (item.getUserData().equals(this)) {
				torem = item;
				break;
			}
		}

		// If parent is IntraExperiment then return true, otherwise return false
		// (Inter cannot delete methods)
		this.parent.getMethods().getMetodos().remove(this);
		Core.getInstance().getClipboard().removeClipboardItem(torem);

		System.gc();
	}
}
