package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.dialogs.CopyMethodDialog;

/**
 * GUI class to call Add Method operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class AddMethodGUI implements InputGUI {
	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Open GUI Dialog
		CopyMethodDialog cmd = new CopyMethodDialog();
		cmd.setVisible(true);

		if (cmd.isExit()) {
			// Obtain info
			Method selectedMethod = cmd.getSelectedMethod();
			IExperiment selExp = cmd.getSelectedExp();

			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Add into aibench list
			params.add(selectedMethod);
			params.add(selExp);

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
