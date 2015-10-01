package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.view.dialogs.ManageMetaDialog;
import es.uvigo.ei.sing.bew.view.panels.CreateMetaValuePanel;

/**
 * GUI class to call CreateConditionValues operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ManageConditionValuesGUI implements InputGUI {
	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		// Open GUI dialog
		ManageMetaDialog ccd = new ManageMetaDialog(
				BewConstants.VALUESFILE, new CreateMetaValuePanel());
		ccd.setVisible(true);

		if (ccd.isExit()) {
			// Get values
			Map<String, List<String>> mapCondAndValues = ccd.getCondAndValues();

			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Add params into aibench list
			params.add(mapCondAndValues);

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
