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
@Operation(description = "Manage BEW Methods.")
public class ManageMethodsOperation {

	private String methodName;

	/**
	 * Set Method name.
	 * 
	 * @param methodName
	 */
	@Port(direction = Direction.INPUT, name = "Set Method name", order = 1)
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Set Method units.
	 * 
	 * @param units
	 */
	@Port(direction = Direction.INPUT, name = "Set Method units", order = 2)
	public void setUnits(String units) {
		try {
			FunctionConstants.writeMethodsFile(methodName, units);

			ShowDialog.showInfo(
					I18n.get("conditionCreateOkTitle"),
					I18n.get("methodCreateOk") + methodName
							+ I18n.get("conditionCreateOk2"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowDialog.showError(I18n.get("methodCreateErrorTitle"),
					I18n.get("methodCreateError"));
			e.printStackTrace();
		}
	}
}
