package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.view.dialogs.UpdateMetadataDialog;

/**
 * GUI class to call UpdateMetadata operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class UpdateMetadataGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {

		UpdateMetadataDialog umd = new UpdateMetadataDialog();
		umd.setVisible(true);

		if (umd.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			params.add(umd.isConditions());
			params.add(umd.isValues());
			params.add(umd.isMethods());

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
