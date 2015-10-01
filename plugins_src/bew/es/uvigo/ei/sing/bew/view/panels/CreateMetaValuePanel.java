package es.uvigo.ei.sing.bew.view.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.view.components.IManageMeta;

/**
 * Custom dialog to create Condition values.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CreateMetaValuePanel extends JPanel implements IManageMeta {

	private static final long serialVersionUID = 1L;

	private ListsPanel listsPanel;
	private JTabbedPane tabbedPaneValues;

	// False == Numerical, True == Nominal
	private String condName;
	private Map<String, List<String>> condAndValues;

	/**
	 * Create the panel.
	 */
	public CreateMetaValuePanel() {
		super();

		condAndValues = new HashMap<String, List<String>>();

		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the dialog.
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		setMinimumSize(new Dimension(800, 600));
		setLayout(new BorderLayout(0, 0));
		{
			listsPanel = new ListsPanel("Available nominal Conditions",
					"Selected Conditions", true);
			listsPanel.setBorder(new TitledBorder(null, "Select Conditions",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));

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

			listsPanel.addStringsToList(getNominalConditions());

			add(listsPanel, BorderLayout.NORTH);
		}
		{
			tabbedPaneValues = new JTabbedPane(JTabbedPane.TOP);
			add(tabbedPaneValues, BorderLayout.CENTER);
		}
	}

	/**
	 * 
	 * @param name
	 */
	private void addNewTab(Object name) {
		// Always is a numericPanel in Factory (type 1 with null condValues)
		tabbedPaneValues.addTab(name.toString(),
				(Component) new AddMetaValuePanel());
	}

	/**
	 * 
	 * @param name
	 */
	private void deleteTab(Object name) {
		int index = tabbedPaneValues.indexOfTab(name.toString());
		if (tabbedPaneValues.getTabCount() > 0 && index != -1)
			tabbedPaneValues.removeTabAt(index);
		// Delete from map
		condAndValues.remove(name.toString());
	}

	/**
	 * Get nominal Conditions from the conditions file.
	 * 
	 * @return List<String> with nominal conditions names.
	 * @throws Exception
	 */
	private List<String> getNominalConditions() throws Exception {
		List<String> toRet = new ArrayList<String>();

		List<String> conditions = FunctionConstants.loadConditionsFromFile();
		for (String c : conditions) {
			// Onyl want nominal conditions
			if (FunctionConstants.readValuesForCondition(c).size() > 0) {
				toRet.add(c);
			}
		}

		return toRet;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean finish() {
		int index = 0;
		AddMetaValuePanel ccv;
		List<Object> selectedObjects = listsPanel.getAllSelectedValues();
		// User must select at least one value
		if (!selectedObjects.isEmpty()) {
			for (Object cond : selectedObjects) {
				// Get the tab of the selected cond
				ccv = (AddMetaValuePanel) tabbedPaneValues
						.getComponentAt(index);

				List<String> values = ccv.getCreatedValues();
				// If one value has incorrect characters return false
				if (values.isEmpty()) {
					ShowDialog.showError(I18n.get("reportingExpTitle"),
							I18n.get("valueCreateChars"));
					return false;
				}

				// Put cond name and values from tab
				this.condAndValues.put(cond.toString(), ccv.getCreatedValues());
				index++;
			}
			return true;
		} else {
			ShowDialog.showError(I18n.get("reportingExpTitle"),
					I18n.get("valueCreate"));
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return condName;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, List<String>> getCondAndValues() {
		return this.condAndValues;
	}

	@Override
	public List<String> getCondValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNominal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return "";
	}
}
