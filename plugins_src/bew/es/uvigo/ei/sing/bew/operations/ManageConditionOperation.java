package es.uvigo.ei.sing.bew.operations;

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
@Operation(description = "Create a new Condition.")
public class ManageConditionOperation {

	private String condName;
	private boolean isNominal;

	/**
	 * Set Condition name.
	 * 
	 * @param intraExperiments
	 */
	@Port(direction = Direction.INPUT, name = "Set Condition name", order = 1)
	public void setCondName(String condName) {
		this.condName = condName;
	}

	/**
	 * Set Condition type.
	 * 
	 * @param intraExperiments
	 */
	@Port(direction = Direction.INPUT, name = "Set Condition type", order = 2)
	public void setCondType(boolean isNominal) {
		this.isNominal = isNominal;
	}

	/**
	 * Set Condition values.
	 * 
	 * @param intraExperiments
	 */
	@Port(direction = Direction.INPUT, name = "Set Condition values", order = 3)
	public void setCondValues(Object[] condValues) {
		try {
			FunctionConstants.writeCondFile(condName.trim());
			if (isNominal)
				FunctionConstants.writeCondValuesFile(condName.trim(),
						condValues);

			ShowDialog.showInfo(
					I18n.get("conditionCreateOkTitle"),
					I18n.get("conditionCreateOk") + condName
							+ I18n.get("conditionCreateOk2"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowDialog.showError(I18n.get("conditionCreateErrorTitle"),
					I18n.get("conditionCreateError"));
			e.printStackTrace();
		}
	}
}
