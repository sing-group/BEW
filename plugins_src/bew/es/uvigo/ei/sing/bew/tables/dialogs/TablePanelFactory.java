package es.uvigo.ei.sing.bew.tables.dialogs;

import java.util.List;

/**
 * Factory to create the table cells dialogs according to the input type.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class TablePanelFactory {

	/**
	 * Creates the ITablePanel according to the input type and add all the input
	 * values inside it.
	 * 
	 * @param typePanel
	 *            Type panel: 0 = condition with values, 1 = condition without
	 *            values, 2 = metacondition, 3 =a ntimicrobial.
	 * @param condValues
	 *            Condition values to add.
	 * @return ITablePanel object.
	 */
	public static ITablePanel createSpecificPanel(int typePanel,
			List<String> condValues) {
		ITablePanel tablePanel = null;

		// 0: Condition with values
		// 1: Condition with no values
		// 2: MetaCondition
		// 3: Antimicrobial
		switch (typePanel) {
		case 0:
			tablePanel = new NominalPanel(false);
			tablePanel.addValues(condValues);
			break;
		case 1:
			tablePanel = new NumericPanel();
			break;
		case 2:
			tablePanel = new MetaPanel();
			break;
		case 3:
			tablePanel = new NominalPanel(true);
			tablePanel.addValues(condValues);
			break;
		}

		return tablePanel;
	}

}
