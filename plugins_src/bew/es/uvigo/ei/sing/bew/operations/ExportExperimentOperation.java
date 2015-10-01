package es.uvigo.ei.sing.bew.operations;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.files.DataToFile;
import es.uvigo.ei.sing.bew.model.Experiment;

/**
 * This class is an AIBench operation for saving an intraExperiment in XLS.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Save IntraExperiment in XLS.")
public class ExportExperimentOperation {

	/**
	 * Saves the Experiment in a file.
	 * 
	 * @param exp
	 *            IntraExperiment to save.
	 */
	@Port(direction = Direction.INPUT, name = "Save IntraExperiment", order = 1)
	public void saveExperiment(Experiment exp) {
		DataToFile.saveXLSData(exp);
	}
}
