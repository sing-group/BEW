package es.uvigo.ei.sing.bew.tables.models;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * Custom ListModel to sort the content of a JList.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class SortedListModel extends AbstractListModel {
	// Define a SortedSet
	private SortedSet model;

	/**
	 * Default constructor.
	 */
	public SortedListModel() {
		super();
		// Create a TreeSet
		// Store it in SortedSet variable
		model = new TreeSet();
	}

	/**
	 * 
	 */
	public int getSize() {
		return model.size();
	}

	/**
	 * 
	 */
	public Object getElementAt(int index) {
		return model.toArray()[index];
	}

	/**
	 * 
	 * @param element
	 */
	public void addElement(Object element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	/**
	 * 
	 * @param elements
	 */
	public void addAll(Object elements[]) {
		model.addAll(Arrays.asList(elements));
		fireContentsChanged(this, 0, getSize());
	}

	/**
	 * 
	 */
	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean contains(Object element) {
		return model.contains(element);
	}

	/**
	 * 
	 * @return
	 */
	public Object firstElement() {
		// Return the appropriate element
		return model.first();
	}

	/**
	 * 
	 * @return
	 */
	public Iterator iterator() {
		return model.iterator();
	}

	/**
	 * 
	 * @return
	 */
	public Object lastElement() {
		return model.last();
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}
