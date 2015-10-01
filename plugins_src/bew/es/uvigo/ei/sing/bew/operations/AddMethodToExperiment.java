package es.uvigo.ei.sing.bew.operations;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.sheets.AbstractMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.sheets.CreateMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.sheets.ImportMethodSheetConfigurator;
import es.uvigo.ei.sing.bew.view.dialogs.AddingElementDialog;

/**
 * This class is an AIBench operation for adding a new Method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Add a Method to an Experiment.")
public class AddMethodToExperiment {

	private Method method;

	/**
	 * 
	 * @param method
	 */
	@Port(direction = Direction.INPUT, name = "Set Method", order = 1)
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Set the intraExperiment. Select the type of Method to create and add it.
	 * 
	 * @param exp
	 *            intraExperiment.
	 */
	@Port(direction = Direction.INPUT, name = "Select Experiment", order = 2)
	public void selectExp(Experiment exp) {

		AddingElementDialog aed = null;
		AbstractMethodSheetConfigurator dsc;

		// If null, we have to create a new method
		if (method == null) {
			aed = new AddingElementDialog(new CreateMethodSheetConfigurator(),
					exp);
			aed.setVisible(true);

			dsc = (CreateMethodSheetConfigurator) aed.getSheet();
		}
		// Else, we copy a created method
		else {
			List<String> condNames = new ArrayList<String>();
			List<Integer> condValues = new ArrayList<Integer>();
			List<String> condUnits = new ArrayList<String>();

			for (Condition c : method.getArrayCondition().getElements()) {
				condNames.add(c.getName());
				condValues.add(c.getConditionValues().size());
				condUnits.add(c.getUnits());
			}

			// We introduce the default name
			aed = new AddingElementDialog(new ImportMethodSheetConfigurator(
					I18n.get("comboNameDefault"), method.getUnits(),
					FunctionConstants.dataToArray(method.conditionsToMatrix()),
					condNames, condValues, condUnits, condNames.size()), exp);
			aed.setVisible(true);

			dsc = (ImportMethodSheetConfigurator) aed.getSheet();
		}

		// If the user want to create the method (accept button)
		if (aed.isExit()) {
			List<String> conditionNames = dsc.getConditionNames();
			List<String> conditionUnits = dsc.getConditionUnits();

			String[] names = new String[conditionNames.size()];

			int i = 0;
			for (String name : conditionNames) {
				names[i] = name;
				i++;
			}

			exp.addMetodo(FunctionConstants.createMethod(dsc.getSheetName(),
					dsc.getUnits(), dsc.getTableToObject(), names,
					conditionUnits.toArray(), dsc.getNumConditions(), exp));
		}
	}
}
