package es.uvigo.ei.sing.bew.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.ConstantConditions;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.InterLabExperiment;
import es.uvigo.ei.sing.bew.view.dialogs.CompareExperimentsDialog;

/**
 * This class is an AIBench operation for comparing two or more
 * intraExperiments.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Compare two or more Experiments.")
public class CompareExperiments {

	private CompareExperimentsDialog ced;
	private Object[] intraExperiments;
	private Object[] methods;
	private Object[] colorList;

	/**
	 * Set intraExperiments.
	 * 
	 * @param intraExperiments
	 */
	@Port(direction = Direction.INPUT, name = "Set intra-experiments", order = 1)
	public void setIntraExperiments(Object[] intraExperiments) {
		this.intraExperiments = intraExperiments;
	}

	/**
	 * Set Methods of the intraExperiments.
	 * 
	 * @param methodList
	 */
	@Port(direction = Direction.INPUT, name = "Set methods", order = 2)
	public void setMethods(Object[] methodList) {
		this.methods = methodList;
	}

	/**
	 * Set colors of the intraExperiments.
	 * 
	 * @param colorList
	 */
	@Port(direction = Direction.INPUT, name = "Set colors", order = 3)
	public void setColors(Object[] colorList) {
		this.colorList = colorList;
	}

	/**
	 * Creates the comparison of the intraExperiment and create the
	 * interExperiment.
	 * 
	 * @return interExperiment
	 */
	@Port(direction = Direction.OUTPUT, name = "Create inter-experiment", order = 4)
	public InterLabExperiment createInterExperiment() {
		this.ced = new CompareExperimentsDialog(methods, intraExperiments,
				colorList);

		this.ced.setVisible(true);

		if (this.ced.isExit()) {
			String[] interExpSetup = ced.getInterExpSetup();

			// Create InterExperiment
			InterLabExperiment ile = new InterLabExperiment(interExpSetup);

			// Retrieve involved intraExperiments
			ile.setIntraExperiments(ced.getMapExpRows());
			ile.setIntraColors(ced.getMapExpColors());

			// Set the constantConditions for the InterExperiment
			ConstantConditions constantCond;
			Set<IExperiment> intraExperiments = ced.getIntraExperiments();	
			List<String> interConditions = new ArrayList<String>();
			List<String> interCondVal = new ArrayList<String>();
			List<String> interCondUnits = new ArrayList<String>();
			// Go over intraExperiments to take their constantConditions
			for (IExperiment experiment : intraExperiments) {
				constantCond = experiment.getConstantCondition();

				if (constantCond != null) {
					// Save intraExperiments conditions and values
					interConditions.addAll(constantCond.getConstantConditions());
					interCondVal.addAll(constantCond.getConstantValues());
					interCondUnits.addAll(constantCond.getConstantUnits());
				}
			}
			ile.setConstantCondition(new ConstantConditions("InterConstant", interConditions, interCondVal, interCondUnits));

			// Retrieve method information
			Object[][] mergeData = FunctionConstants.dataToArray(ced
					.getSelectedRows());
			List<Condition> condNames = ced.getConditionNames();
			String methodName = ced.getMethodName();
			String methodUnits = ced.getMethodUnits();
			int numCond = ced.getNumConditions();

			// Add method to InterExperiment
			ile.addMetodo(FunctionConstants.createInterMethod(methodName,
					methodUnits, mergeData, condNames, numCond, ile));
			
			return ile;
		}
		return null;
	}
}
