package es.uvigo.ei.sing.bew.operations;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.sheets.ImportMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.view.dialogs.EditingElementDialog;

/**
 * This class is an AIBench operation for editing a Method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Edit a created Method.")
public class EditMethod {

	/**
	 * Modify the input Method and saves the information in it.
	 * 
	 * @param method
	 *            Method to modify.
	 */
	@Port(direction = Direction.INPUT, name = "Select Method", order = 1)
	public void setExp(Method method) {
		if (method != null) {

			// Get conditions values and names
			List<String> condNames = new ArrayList<String>();
			List<Integer> condValues = new ArrayList<Integer>();
			List<String> condUnits = new ArrayList<String>();

			List<Condition> auxList = method.getArrayCondition().getElements();
			for (int i = 0; i < auxList.size(); i++) {
				condNames.add(auxList.get(i).getName());
				condValues.add(auxList.get(i).getConditionValues().size());
				condUnits.add(auxList.get(i).getUnits());
			}
			
			EditingElementDialog eed = new EditingElementDialog(method.getName(),
					method.getUnits(), FunctionConstants.dataToArray(method
							.allDataToMatrix()), condNames, condValues,
					condUnits, method);
			eed.setVisible(true);

			// If the user finalize...
			if (eed.isExit()) {
				ImportMethodSheetConfigurator dsc = (ImportMethodSheetConfigurator) eed
						.getSheet();
				List<String> conditionNames = dsc.getConditionNames();
				List<String> conditionUnits = dsc.getConditionUnits();

				String[] names = new String[conditionNames.size()];

				int i = 0;
				for (String name : conditionNames) {
					names[i] = name;
					i++;
				}
				// Create the new method taking the data that the user wrote
				Method aux = FunctionConstants.createMethod(dsc.getSheetName(),
						dsc.getUnits(), dsc.getTableToObject(), names,
						conditionUnits.toArray(), dsc.getNumConditions(),
						method.getParent());
				method.editMethod(aux);
			}
		}
	}
}