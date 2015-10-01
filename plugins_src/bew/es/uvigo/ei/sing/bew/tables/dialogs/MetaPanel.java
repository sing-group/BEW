package es.uvigo.ei.sing.bew.tables.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;

/**
 * Panel for metaconditions.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class MetaPanel extends JPanel implements ITablePanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane conditionsTabbed;

	/**
	 * Creates the panel.
	 */
	public MetaPanel() {
		super();
		init();
	}

	/**
	 * Initializes the panel.
	 */
	private void init() {
		setMinimumSize(new Dimension(640, 480));
		setLayout(new GridLayout(0, 1, 0, 0));

		conditionsTabbed = new JTabbedPane(JTabbedPane.TOP);
		prepareTabbedPane();

		add(conditionsTabbed);
	}

	/**
	 * Prepares the JTabbedPane inside the MetaPanel. One tab per Condition in
	 * the file.
	 */
	private void prepareTabbedPane() {
		List<String> conditions = FunctionConstants.loadConditionsFromFile();

		for (String condition : conditions) {
			// Exclude metacondition
			if (!condition.equals(BewConstants.METACONDITION)) {
				try {
					List<String> condValues = FunctionConstants
							.readValuesForCondition(condition);
					int typePanel = FunctionConstants.calculateTypePanel(
							condValues, condition);

					// Add ITablePanel to the tab content
					conditionsTabbed.addTab(condition,
							(Component) TablePanelFactory.createSpecificPanel(
									typePanel, condValues));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getFinalValue() {
		// TODO Auto-generated method stub
		int tabCount = conditionsTabbed.getTabCount();
		// int tabsWithValues = 0;
		String toRet = "";

		// Go over all the tabs
		for (int index = 0; index < tabCount; index++) {
			// All tabs are tablePanels
			ITablePanel tablePanel = (ITablePanel) conditionsTabbed
					.getComponentAt(index);

			String value = tablePanel.getFinalValue();
			if (value != null && !value.trim().isEmpty()) {
				if (!toRet.isEmpty())
					toRet = toRet.concat(BewConstants.AND + value);
				else
					toRet = tablePanel.getFinalValue();

				// tabsWithValues++;
			}
		}

		// Successful exit only when user select values from 2 different
		// conditions
		// if (tabsWithValues >= 1 || tabsWithValues == 0)
		return toRet;
		// else
		// return null;
	}

	@Override
	public void addValues(List<String> values) {
		// TODO Auto-generated method stub
	}

	@Override
	public Dimension getDimension() {
		// TODO Auto-generated method stub
		return getMinimumSize();
	}

	@Override
	public void resetValues() {
		// TODO Auto-generated method stub
		int tabCount = conditionsTabbed.getTabCount();
		if (tabCount > 0) {
			ITablePanel tablePanel = (ITablePanel) conditionsTabbed
					.getComponentAt(conditionsTabbed.getSelectedIndex());
			tablePanel.resetValues();
		}
	}

	@Override
	public void setFinalValue(String value) {
		// TODO Auto-generated method stub
	}
}
