package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.view.dialogs.ManageMetaDialog;
import es.uvigo.ei.sing.bew.view.panels.CreateMetaMethodPanel;

/**
 * GUI class to call CreateCondition operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ManageMethodsGUI implements InputGUI {
	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Open GUI dialog
		ManageMetaDialog mmd = new ManageMetaDialog(BewConstants.METHODFILE,
				new CreateMetaMethodPanel());
		mmd.setVisible(true);

		if (mmd.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Add params into aibench list
			params.add(mmd.getName());
			params.add(mmd.getUnits());

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
