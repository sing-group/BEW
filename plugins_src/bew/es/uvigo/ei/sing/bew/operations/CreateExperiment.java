package es.uvigo.ei.sing.bew.operations;

import java.util.List;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Experiment;

/**
 * This class is an AIBench operation for creating an intraExperiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Create a new IntraExperiment.")
public class CreateExperiment {

	private String[] methodNames;
	private String[] methodUnits;
	private Integer[] numConditions;
	private String[][] conditionNames;
	private List<Object[][]> data;
	private String[] expSetup;
	private String[][] condUnits;

	/**
	 * Set methodNames.
	 * 
	 * @param methodNames
	 */
	@Port(direction = Direction.INPUT, name = "method name", order = 1)
	public void setNames(String[] methodNames) {
		this.methodNames = methodNames;
	}

	/**
	 * Set methodUnits.
	 * 
	 * @param methodUnits
	 */
	@Port(direction = Direction.INPUT, name = "method units", order = 2)
	public void setUnits(String[] methodUnits) {
		this.methodUnits = methodUnits;
	}

	/**
	 * Set number of Conditions.
	 * 
	 * @param numConditions
	 */
	@Port(direction = Direction.INPUT, name = "num conditions", order = 3)
	public void setNumconditions(Integer[] numConditions) {
		this.numConditions = numConditions;
	}

	/**
	 * Set Condition names.
	 * 
	 * @param names
	 */
	@Port(direction = Direction.INPUT, name = "condition names", order = 4)
	public void setConditionNames(String[][] names) {
		this.conditionNames = names;
	}

	/**
	 * Set methods data.
	 * 
	 * @param dataMatrix
	 */
	@Port(direction = Direction.INPUT, name = "data", order = 5)
	public void setData(List<Object[][]> dataMatrix) {
		this.data = dataMatrix;
	}

	/**
	 * Set experiment setup.
	 * 
	 * @param expSetup
	 */
	@Port(direction = Direction.INPUT, name = "exp setup", order = 6)
	public void setExpSetup(String[] expSetup) {
		this.expSetup = expSetup.clone();
	}

	/**
	 * Set experiment setup.
	 * 
	 * @param expSetup
	 */
	@Port(direction = Direction.INPUT, name = "Condition units", order = 7)
	public void setCondUnits(String[][] condUnits) {
		this.condUnits = condUnits.clone();
	}

	/**
	 * Create the intraExperiment and add the new methods to it.
	 * 
	 * @return
	 */
	@Port(direction = Direction.OUTPUT, order = 8)
	public Experiment importExperiment() {
		Experiment exp = new Experiment();

		for (int i = 0; i < methodNames.length; i++) {
			// The sheet with this name will be the ConstantConditionSheet
			if (methodNames[i].equals(I18n.get("conditionSheetName"))) {
				exp.addConstantCondition(FunctionConstants.createConstant(
						methodNames[i], data.get(i)));
				exp.setExpSetup(expSetup);
			}

			else
				exp.addMetodo(FunctionConstants.createMethod(methodNames[i],
						methodUnits[i], data.get(i), conditionNames[i],
						condUnits[i], numConditions[i], exp));
		}

		return exp;
	}
}