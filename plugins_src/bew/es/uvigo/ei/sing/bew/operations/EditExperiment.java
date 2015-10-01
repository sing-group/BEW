package es.uvigo.ei.sing.bew.operations;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.sheets.SetupSheetConfig;
import es.uvigo.ei.sing.bew.view.dialogs.EditingElementDialog;

/**
 * This class is an AIBench operation for editing an Experiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Edit a created Experiment.")
public class EditExperiment {

	/**
	 * Modify input experiment and save the information in it.
	 * 
	 * @param exp
	 *            Experiment to modify.
	 */
	@Port(direction = Direction.INPUT, name = "Select Experiment", order = 1)
	public void setExp(IExperiment exp) {
		boolean isInter;
		if(exp.getMapIntraExpsColors() != null){
			isInter = true;
		}else{
			isInter = false;
		}
		
		EditingElementDialog eed = new EditingElementDialog(exp
				.getConstantCondition().getName(),
				FunctionConstants.dataToArray(exp.getConstantCondition()
						.dataToMatrix()), exp.getExpSetup(), exp, isInter);
		eed.setVisible(true);
		
		if (eed.isExit()) {
			SetupSheetConfig csc = (SetupSheetConfig) eed.getSheet();

			ConstantConditions aux = FunctionConstants.createConstant(
					csc.getSheetName(), csc.getTableToObject());
			exp.getConstantCondition().editConstantCondition(aux);
			exp.setExpSetup(csc.getExpSetup());
		}

	}
}