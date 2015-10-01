package es.uvigo.ei.sing.bew.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;

/**
 * This class is an AIBench operation for comparing two or more
 * intraExperiments.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Creates new Condition values.")
public class ManageConditionValuesOperation {

	private String errors = "";

	/**
	 * Set Condition name.
	 * 
	 * @param intraExperiments
	 */
	@Port(direction = Direction.INPUT, name = "Set map with conditions and values", order = 1)
	public void setCondName(Map<String, List<String>> mapCondAndValues) {
		Set<String> conditions = mapCondAndValues.keySet();
		List<String> values = new ArrayList<String>();

		// Go over each condition
		for (String cond : conditions) {
			values = mapCondAndValues.get(cond);
			try {
				// Write values in file
				FunctionConstants.writeCondValuesFile(cond, values.toArray());
			} catch (Exception e) {
				errors = "- Unable to create all the values ​​for the Condition: "
						+ cond + ".\n";
				e.printStackTrace();
			}
		}

		if (errors.isEmpty()) {
			ShowDialog.showInfo(I18n.get("valueCreateOkTitle"),
					I18n.get("valueCreateOk"));
		} else {
			ShowDialog.showError(I18n.get("valueCreateErrorTitle"),
					I18n.get("valueCreateError") + errors);
		}
	}
}
