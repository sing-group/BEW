package es.uvigo.ei.sing.bew.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.files.FileToData;

/**
 * This class allows data collection to be sent to an AIBench operation.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class ImportExpGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		List<File> temporaryFiles = new ArrayList<File>();
		String path = FileToData.loadXLSFile(temporaryFiles);

		if (!path.isEmpty()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			params.add(path);

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
