package es.uvigo.ei.sing.bew.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;
import es.uvigo.ei.sing.bew.view.dialogs.DownloadExpDialog;

/**
 * GUI class to call DownloadExp operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class DownloadExpGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {

		DownloadExpDialog ded = new DownloadExpDialog();
		ded.setVisible(true);

		if (ded.isExit()) {
			// Call file chooser
			CustomFileChooser cfc = new CustomFileChooser();

			// Set only directories
			cfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// We open a save dialog
			int retrieval = cfc.showSaveDialog(null);

			if (retrieval == JFileChooser.APPROVE_OPTION) {
				// List with AIBench params
				List<Object> params = new LinkedList<Object>();

				params.add(ded.getDownloadFile());
				params.add(cfc.getSelectedFile().getAbsolutePath());

				ParamSpec[] aibenchParams = es.uvigo.ei.aibench.core.CoreUtils
						.createParams(params);

				System.err.println(aibenchParams);
				arg0.paramsIntroduced(aibenchParams);
			}
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
