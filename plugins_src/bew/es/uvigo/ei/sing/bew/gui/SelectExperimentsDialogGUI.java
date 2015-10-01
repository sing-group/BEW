package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.view.dialogs.SelectExperimentsDialog;

/**
 * This class allows data collection to be sent to an AIBench operation.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectExperimentsDialogGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Begin the SelectStatisticDialog
		SelectExperimentsDialog sed = new SelectExperimentsDialog();

		// Starting wizard
		sed.setVisible(true);

		if (sed.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Variables for AIBench
			Object[] intraExperimentList = sed.getSelectedIntraExperiments();
			Object[] methodList = sed.getSelectedMethods();
			Object[] experimentColors = sed.getExperimentColors();

			params.add(intraExperimentList);
			params.add(methodList);
			params.add(experimentColors);

			ParamSpec[] aibenchParams = es.uvigo.ei.aibench.core.CoreUtils
					.createParams(params);

			System.err.println(aibenchParams);
			arg0.paramsIntroduced(aibenchParams);
		}
	}

	@Override
	public void onValidationError(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
