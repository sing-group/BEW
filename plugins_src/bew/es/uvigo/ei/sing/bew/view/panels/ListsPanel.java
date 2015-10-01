package es.uvigo.ei.sing.bew.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.tables.models.SortedListModel;
import es.uvigo.ei.sing.bew.tables.renderer.ListStripeRenderer;

/**
 * Custom panel to show two JLists.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ListsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList selectedList;
	private JList availableList;
	private boolean isSorted;

	/**
	 * Default constructor. Only two different models for these lists
	 * (DefaultListModel and SortedListModel)
	 * 
	 * @param availableLabel
	 *            Label for left list.
	 * @param selectedLabel
	 *            Label for right list.
	 * @param sortedLists
	 *            True if the list have an order, false otherwise.
	 */
	public ListsPanel(String availableLabel, String selectedLabel,
			boolean sortedLists) {
		super();

		init(availableLabel, selectedLabel);

		// True = Lists with sorted model
		if (sortedLists) {
			selectedList.setModel(new SortedListModel());
			availableList.setModel(new SortedListModel());

			isSorted = sortedLists;
		}
	}

	/**
	 * Initializes the dialog.
	 * 
	 * @param availableLabel
	 * @param selectedLabel
	 */
	private void init(String availableLabel, String selectedLabel) {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {800, 800};
		gridBagLayout.rowHeights = new int[] { 25, 100 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
		setLayout(gridBagLayout);
		{
			// Selected list
			selectedList = new JList(new DefaultListModel());
			selectedList.setCellRenderer(new ListStripeRenderer());
			JScrollPane selectedScroll = new JScrollPane(selectedList);
			GridBagConstraints gbcSelectedScroll = new GridBagConstraints();
			gbcSelectedScroll.fill = GridBagConstraints.BOTH;
			gbcSelectedScroll.gridx = 1;
			gbcSelectedScroll.gridy = 1;
			add(selectedScroll, gbcSelectedScroll);

			selectedList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						Object selectedValue = selectedList.getSelectedValue();
						if (selectedValue != null) {
							selectedToAvailable(selectedValue);
						}
						clearSelections();
					}
				}
			});
		}
		{
			// Available list
			availableList = new JList(new DefaultListModel());
			availableList.setCellRenderer(new ListStripeRenderer());
			JScrollPane availableScroll = new JScrollPane(availableList);
			GridBagConstraints gbcAvailableScroll = new GridBagConstraints();
			gbcAvailableScroll.fill = GridBagConstraints.BOTH;
			gbcAvailableScroll.insets = new Insets(0, 0, 0, 5);
			gbcAvailableScroll.gridx = 0;
			gbcAvailableScroll.gridy = 1;
			add(availableScroll, gbcAvailableScroll);

			availableList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						Object selectedValue = (Object) availableList
								.getSelectedValue();
						if (selectedValue != null) {
							availableToSelected(selectedValue);
						}
						clearSelections();
					}
				}
			});
		}
		{
			// Labels
			JLabel lblAvailable = new JLabel(availableLabel);
			GridBagConstraints gbcLblAvailable = new GridBagConstraints();
			gbcLblAvailable.insets = new Insets(0, 0, 5, 5);
			gbcLblAvailable.gridx = 0;
			gbcLblAvailable.gridy = 0;
			add(lblAvailable, gbcLblAvailable);

			JLabel lblSelected = new JLabel(selectedLabel);
			GridBagConstraints gbcLblSelected = new GridBagConstraints();
			gbcLblSelected.insets = new Insets(0, 0, 5, 0);
			gbcLblSelected.gridx = 1;
			gbcLblSelected.gridy = 0;
			add(lblSelected, gbcLblSelected);
		}
	}

	/**
	 * Puts the input value in the right list.
	 * 
	 * @param value
	 *            Value.
	 */
	public void availableToSelected(Object value) {
		if (isSorted) {
			SortedListModel selectedModel = (SortedListModel) selectedList
					.getModel();
			SortedListModel availableModel = (SortedListModel) availableList
					.getModel();

			selectedModel.addElement(value);
			availableModel.removeElement(value);
		} else {
			DefaultListModel selectedModel = (DefaultListModel) selectedList
					.getModel();
			DefaultListModel availableModel = (DefaultListModel) availableList
					.getModel();

			selectedModel.addElement(value);
			availableModel.removeElement(value);
		}
	}

	/**
	 * Puts the input value in the left list.
	 * 
	 * @param value
	 *            Value.
	 */
	public void selectedToAvailable(Object value) {
		if (isSorted) {
			SortedListModel selectedModel = (SortedListModel) selectedList
					.getModel();
			SortedListModel availableModel = (SortedListModel) availableList
					.getModel();

			availableModel.addElement(value);
			selectedModel.removeElement(value);
		} else {
			DefaultListModel selectedModel = (DefaultListModel) selectedList
					.getModel();
			DefaultListModel availableModel = (DefaultListModel) availableList
					.getModel();

			availableModel.addElement(value);
			selectedModel.removeElement(value);
		}
	}

	/**
	 * Adds Strings to the left list.
	 * 
	 * @param values
	 *            Values to add.
	 */
	public void addStringsToList(List<String> values) {
		if (isSorted) {
			SortedListModel availableModel = (SortedListModel) availableList
					.getModel();

			for (String value : values)
				availableModel.addElement(value);
		} else {
			DefaultListModel availableModel = (DefaultListModel) availableList
					.getModel();

			for (String value : values)
				availableModel.addElement(value);
		}
	}

	/**
	 * Add ClipboardItems to left list.
	 * 
	 * @param values
	 *            Values to add.
	 */
	public void addClipboardItemsToList(List<ClipboardItem> values) {
		if (isSorted) {
			SortedListModel availableModel = (SortedListModel) availableList
					.getModel();

			for (ClipboardItem value : values)
				availableModel.addElement(value);
		} else {
			DefaultListModel availableModel = (DefaultListModel) availableList
					.getModel();

			for (ClipboardItem value : values)
				availableModel.addElement(value);
		}
	}

	/**
	 * 
	 * Gets all selected values in the right list.
	 * 
	 * @return List<Object> with the selected values.
	 */
	public List<Object> getAllSelectedValues() {
		List<Object> selectedValues = new ArrayList<Object>();

		if (isSorted) {
			SortedListModel model = (SortedListModel) selectedList.getModel();
			int index = model.getSize();

			for (int i = 0; i < index; i++)
				selectedValues.add(model.getElementAt(i));
		} else {
			DefaultListModel model = (DefaultListModel) selectedList.getModel();
			int index = model.getSize();

			for (int i = 0; i < index; i++)
				selectedValues.add(model.getElementAt(i));
		}

		return selectedValues;
	}

	/**
	 * Adds the input listener to the left list.
	 * 
	 * @param listener
	 */
	public void addListenerToAvailableList(ListSelectionListener listener) {
		availableList.addListSelectionListener(listener);
	}

	/**
	 * Adds the input listener to the right list.
	 * 
	 * @param listener
	 */
	public void addListenerToSelectedList(ListSelectionListener listener) {
		selectedList.addListSelectionListener(listener);
	}

	/**
	 * Clears selection in both lists.
	 */
	public void clearSelections() {
		selectedList.clearSelection();
		availableList.clearSelection();
	}

	/**
	 * Removes all the values in the right list.
	 */
	public void removeSelectedValues() {
		List<Object> selectedValues = getAllSelectedValues();

		for (Object value : selectedValues) {
			selectedToAvailable(value);
		}
	}

	/**
	 * Deletes all values in both lists.
	 */
	public void deleteAllValues() {
		DefaultListModel selectedListModel = (DefaultListModel) selectedList
				.getModel();
		selectedListModel.removeAllElements();

		DefaultListModel availableModel = (DefaultListModel) availableList
				.getModel();
		availableModel.removeAllElements();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public boolean isInSelList(Object value) {
		if (isSorted) {
			SortedListModel selModel = (SortedListModel) selectedList
					.getModel();

			return selModel.contains(value);

		} else {
			DefaultListModel selectedListModel = (DefaultListModel) selectedList
					.getModel();

			return selectedListModel.contains(value);
		}
	}

	public JList getSelectedList() {
		return selectedList;
	}

	public JList getAvailableList() {
		return availableList;
	}
}
