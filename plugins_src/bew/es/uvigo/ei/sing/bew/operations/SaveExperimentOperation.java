package es.uvigo.ei.sing.bew.operations;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.files.DataToFile;
import es.uvigo.ei.sing.bew.model.IExperiment;

/**
 * This class is an AIBench operation for saving an Experiment in XML.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Save Experiment in XML.")
public class SaveExperimentOperation {

	/**
	 * Saves the Experiment in a file.
	 * 
	 * @param exp
	 *            IntraExperiment to save.
	 */
	@Port(direction = Direction.INPUT, name = "Save Experiment", order = 1)
	public void saveExperiment(IExperiment exp) {
		DataToFile.saveXMLData(exp);
	}
}
