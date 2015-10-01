package es.uvigo.ei.sing.bew.operations;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.dialogs.CalculateStatisticDialog;

/**
 * This class is an AIBench operation for creating a statistic.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Calculate a Statistic function for a Method.")
public class CreateStatistic {

	private Method method;
	private String type;

	/**
	 * Set method.
	 * 
	 * @param m
	 */
	@Port(direction = Direction.INPUT, name = "Set Method", order = 1)
	public void setMethod(Method m) {
		this.method = m;
	}

	/**
	 * Set statistic type.
	 * 
	 * @param t
	 */
	@Port(direction = Direction.INPUT, name = "Set type", order = 2)
	public void setType(String t) {
		this.type = t;
	}

	/**
	 * Creates the statistic and add it to the Method.
	 * 
	 * @param function
	 *            Statistic function.
	 */
	@Port(direction = Direction.INPUT, name = "Calculate the statistic function to the selected Method", order = 3)
	public void calculateStatistic(String function) {
		CalculateStatisticDialog sgd = new CalculateStatisticDialog(
				this.method, function, this.type);
		sgd.setVisible(true);

		if (sgd.isExit()) {
			// We add the Statistic to the Method
			method.addStatistics(sgd.getStatistic());
		}
	}
}
