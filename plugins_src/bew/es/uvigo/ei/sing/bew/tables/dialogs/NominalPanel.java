package es.uvigo.ei.sing.bew.tables.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.tables.models.SortedListModel;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;
import es.uvigo.ei.sing.bew.view.panels.ListsPanel;

/**
 * Custom panel for Nominal Conditions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class NominalPanel extends JPanel implements ITablePanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane concTabbedPane;
	private JPanel antiPanel;
	private JTextPane textPane;
	private ListsPanel listsPanel;
	private CustomTextField filterField;

	// Variable to validate if the value after = is mandatory (true = mandatory,
	// false = optional)
	private boolean isAnti;
	private List<String> listValues;
	private JPanel headerPanel;
	private JLabel filterLabel;
	private JSplitPane splitPane;

	/**
	 * Creates the panel.
	 */
	public NominalPanel(boolean isAnti) {
		super();

		this.isAnti = isAnti;
		this.listValues = new ArrayList<String>();

		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the panel.
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		setLayout(new GridLayout(1, 1, 0, 0));
		setMinimumSize(new Dimension(640, 480));

		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.25);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		antiPanel = new JPanel();
		// add(antiPanel);
		antiPanel.setLayout(new BorderLayout(0, 0));
		{
			{
				listsPanel = new ListsPanel("Available values",
						"Selected values", true);
				ListSelectionListener availableListener = new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						if (!e.getValueIsAdjusting()) {
							JList list = (JList) e.getSource();
							Object selectedValue = list.getSelectedValue();
							if (selectedValue != null) {
								listsPanel.availableToSelected(selectedValue);
								addNewTab(selectedValue);
							}
							listsPanel.clearSelections();
						}
					}
				};

				ListSelectionListener selectedListener = new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						if (!e.getValueIsAdjusting()) {
							JList list = (JList) e.getSource();
							Object selectedValue = list.getSelectedValue();
							if (selectedValue != null) {
								listsPanel.selectedToAvailable(selectedValue);
								deleteTab(selectedValue);
							}
							listsPanel.clearSelections();
						}
					}
				};

				// Add new listeners
				listsPanel.addListenerToAvailableList(availableListener);
				listsPanel.addListenerToSelectedList(selectedListener);
			}

			{
				headerPanel = new JPanel();
				antiPanel.add(headerPanel, BorderLayout.NORTH);
				headerPanel.setLayout(new BorderLayout(5, 10));
				{
					textPane = new JTextPane();
					headerPanel.add(textPane, BorderLayout.NORTH);
					textPane.setText("Select the condition values you want to use from the available list. Then, put their concentration in the field below.");
					textPane.setEditable(false);
				}
				{
					filterLabel = new JLabel("Write here to filter results:");
					headerPanel.add(filterLabel, BorderLayout.WEST);
				}
				filterField = new CustomTextField("");
				filterField.setPlaceholder("Candida...");
				headerPanel.add(filterField, BorderLayout.CENTER);
				filterField.setColumns(10);

				// Listener to filter the values in the list dynamically
				filterField.getDocument().addDocumentListener(
						new DocumentListener() {
							public void insertUpdate(DocumentEvent e) {
								updateList();
							}

							public void removeUpdate(DocumentEvent e) {
								updateList();
							}

							public void changedUpdate(DocumentEvent e) {
								updateList();
							}

							private void updateList() {
								String input = filterField.getText().trim()
										.toLowerCase();
								// Get list model
								SortedListModel model = (SortedListModel) listsPanel
										.getAvailableList().getModel();

								if (!input.isEmpty()) {
									// Clear model values
									model.clear();

									// Add values that the user is writing in
									// the field
									for (Object item : listValues) {
										if (item.toString().toLowerCase()
												.startsWith(input)
												&& !listsPanel
														.isInSelList(item)) {
											model.addElement(item);
										}
									}
								} else {
									// If user delete all the text, show all the
									// values
									for (Object item : listValues) {
										if (!listsPanel.isInSelList(item)) {
											model.addElement(item);
										}
									}
								}
							}
						});
			}

			antiPanel.add(listsPanel, BorderLayout.CENTER);

			splitPane.setLeftComponent(antiPanel);
		}
		{
			concTabbedPane = new JTabbedPane(JTabbedPane.TOP);

			splitPane.setRightComponent(concTabbedPane);
		}

	}

	/**
	 * Adds new tab to the panel with the input value as the name of the tab.
	 * 
	 * @param name
	 *            Tab name.
	 */
	private void addNewTab(Object name) {
		// Always is a numericPanel in Factory (type 1 with null condValues)
		concTabbedPane.addTab(name.toString(),
				(Component) TablePanelFactory.createSpecificPanel(1, null));
	}

	/**
	 * Adds new tab to the panel with the input name as the name of the tab and
	 * the value as the value of the panel.
	 * 
	 * @param name
	 *            Tab name.
	 * @param value
	 *            Tab value.
	 */
	private void addNewTabWithValue(Object name, String value) {
		// Always is a numericPanel in Factory (type 1 with null condValues)
		ITablePanel tablePanel = TablePanelFactory.createSpecificPanel(1, null);
		tablePanel.setFinalValue(value);
		concTabbedPane.addTab(name.toString(), (Component) tablePanel);
	}

	/**
	 * Deletes tab according to the input name.
	 * 
	 * @param name
	 *            Tab name to delete.
	 */
	private void deleteTab(Object name) {
		int index = concTabbedPane.indexOfTab(name.toString());
		if (concTabbedPane.getTabCount() > 0 && index != -1)
			concTabbedPane.removeTabAt(index);
	}

	@Override
	public String getFinalValue() {
		int index = concTabbedPane.getTabCount();
		int concCovered = 0;
		String toRet = "";

		ITablePanel tablePanel;
		String tabName = "";
		for (int i = 0; i < index; i++) {
			// All tabs are tablePanels
			tablePanel = (ITablePanel) concTabbedPane.getComponentAt(i);
			tabName = concTabbedPane.getTitleAt(i);

			String finalValue = tablePanel.getFinalValue();
			if (finalValue != null && !finalValue.isEmpty()) {
				// Ex: anti1=0.5_and_anti2=1...
				if (toRet.isEmpty()) {
					toRet = toRet.concat(tabName + "="
							+ tablePanel.getFinalValue());
				} else {
					toRet = toRet.concat(BewConstants.AND + tabName + "="
							+ tablePanel.getFinalValue());
				}
				concCovered++;
			} else {
				// For Antimicrobial is mandatory so we put NaN by default
				if (isAnti) {
					// Ex: anti1=0.5_and_anti2=1...
					// if (toRet.isEmpty()) {
					// toRet = toRet.concat(tabName + "=NaN");
					// } else {
					// toRet = toRet.concat(BewConstants.AND + tabName
					// + "=NaN");
					// }
				}
				// Other nominal conditions is optional
				else {
					// Ex: anti1=0.5_and_anti2=1...
					if (toRet.isEmpty()) {
						toRet = toRet.concat(tabName);
					} else {
						toRet = toRet.concat(BewConstants.AND + tabName);
					}
				}
			}
		}
		if (concCovered == index || !isAnti) {
			return toRet;
		} else {
			// You must covered all concentrations
			return null;
		}
	}

	@Override
	public void addValues(List<String> values) {
		// Save list values
		this.listValues.addAll(values);

		listsPanel.addStringsToList(values);
	}

	@Override
	public void resetValues() {
		// TODO Auto-generated method stub
		List<Object> values = listsPanel.getAllSelectedValues();

		for (Object value : values) {
			deleteTab(value);
		}
		listsPanel.removeSelectedValues();
	}

	@Override
	public Dimension getDimension() {
		// TODO Auto-generated method stub
		return getMinimumSize();
	}

	@Override
	public void setFinalValue(String value) {
		// Ex: [Anti1=0.25]...
		String[] splittedValue = value.split(BewConstants.AND);

		for (String val : splittedValue) {
			// [Anti1], [0.25]
			String[] separateCond = val.split("\\=");
			listsPanel.availableToSelected(separateCond[0]);
			if (separateCond.length > 1)
				addNewTabWithValue(separateCond[0], separateCond[1]);
			else
				addNewTabWithValue(separateCond[0], "");
		}
	}
}
