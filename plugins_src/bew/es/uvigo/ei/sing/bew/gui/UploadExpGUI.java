package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.view.dialogs.UploadExpDialog;

/**
 * GUI class to call DownloadExp operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class UploadExpGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {

		UploadExpDialog ued = new UploadExpDialog();
		ued.setVisible(true);

		if (ued.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			params.add(ued.getSelectedExp());

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
