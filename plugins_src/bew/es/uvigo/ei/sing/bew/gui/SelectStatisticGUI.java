package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.dialogs.SelectTreeDialog;

/**
 * This class allows data collection to be sent to an AIBench operation.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectStatisticGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Begin the SelectTreeDialog
		SelectTreeDialog ssd = new SelectTreeDialog(false);

		// Starting wizard
		ssd.setVisible(true);

		if (ssd.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Variables for AIBench
			Method method = ssd.getSelectedMethod();
			String type = ssd.getSelectedType().toString();
			String function = ssd.getSelectedFunction().toString();

			params.add(method);
			params.add(type);
			params.add(function);

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
