package es.uvigo.ei.sing.bew.tables.renderer;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * Custom cell editor for custom tables. Used to put the comboBoxes.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CellComboEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboBox;

	/**
	 * 
	 * @param comboBox
	 */
	public CellComboEditor(final JComboBox<String> comboBox) {
		super(comboBox);
		this.comboBox = comboBox;
	}

	/**
	 * Adds a value to the comboBox.
	 * 
	 * @param value
	 *            Value to add.
	 */
	public void addComboValue(String value) {
		this.comboBox.addItem(value);
	}

	/**
	 * Deletes a value in the comboBox.
	 * 
	 * @param value
	 *            Value to delete.
	 */
	public void deleteComboValue(String value) {
		this.comboBox.removeItem(value);
	}

	/**
	 * Set default selected item.
	 * 
	 * @param item
	 *            Item to be selected
	 */
	public void setSelectItem(String item) {
		this.comboBox.setSelectedItem(item);
	}

	/**
	 * 
	 * @return
	 */
	public Object getSelectedItem() {
		return this.comboBox.getSelectedItem();
	}
}
