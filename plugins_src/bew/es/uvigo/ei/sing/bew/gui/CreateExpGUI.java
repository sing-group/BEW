package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.view.dialogs.WizardDialog;

/**
 * This class allows data collection to be sent to an AIBench operation.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class CreateExpGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Begin the Wizard empty to start creating the methods to our
		// experiment
		WizardDialog myWizard = new WizardDialog();
		
		// Starting wizard
		myWizard.setVisible(true);

		if (myWizard.isExit()) {
			List<IWizardStep> sheetConf = myWizard.getSteps();

			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Variables for AIBench
			String[] methodNames = new String[sheetConf.size()];
			String[] methodUnits = new String[sheetConf.size()];
			Integer[] numConditions = new Integer[sheetConf.size()];
			String[][] conditionNames = new String[sheetConf.size()][];
			String[][] condUnits = new String[sheetConf.size()][];
			LinkedList<Object[][]> data = new LinkedList<Object[][]>();
			String[] expSetup = new String[8];

			FunctionConstants.setAIBenchValues(sheetConf, methodNames,
					methodUnits, numConditions, conditionNames, data, expSetup, condUnits);

			params.add(methodNames);
			params.add(methodUnits);
			params.add(numConditions);
			params.add(conditionNames);
			params.add(data);
			params.add(expSetup);
			params.add(condUnits);
			
			ParamSpec[] aibenchParams = es.uvigo.ei.aibench.core.CoreUtils
					.createParams(params);

			System.err.println(aibenchParams);
			arg0.paramsIntroduced(aibenchParams);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValidationError(Throwable arg0) {
		// TODO Auto-generated method stub

	}

}
