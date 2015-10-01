package es.uvigo.ei.sing.bew.operations;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.FileToData;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.view.dialogs.WizardDialog;

/**
 * This class is an AIBench operation for importing an intraExperiment in XLS.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Import an IntraExperiment.")
public class ImportExperiment {

	private String path;

	/**
	 * Set Experiment path.
	 * 
	 * @param path
	 */
	@Port(direction = Direction.INPUT, name = "Experiment path", order = 1)
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Import the experiment and create it.
	 * 
	 * @return IntraExperiment.
	 */
	@Port(direction = Direction.OUTPUT, order = 2)
	public IExperiment importExperiment() {
		IExperiment experiment = null;

		try {
			// XLS need a Wizard to configure sheets
			List<IWizardStep> steps = new ArrayList<IWizardStep>();

			steps = FileToData.xlsToData(path);

			WizardDialog myWizard = new WizardDialog(steps);
			myWizard.setVisible(true);

			if (myWizard.isExit()) {
				experiment = FunctionConstants.importXlsExperiment(steps);
			}
		} catch (Exception e) {
			ShowDialog.showError(I18n.get("errorLoadTitle"),
					I18n.get("errorLoad"));
		}

		return experiment;
	}
}