package es.uvigo.ei.sing.bew.operations;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.dialogs.PlotDialog;

/**
 * This class is an AIBench operation for creating a plot.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Creates a new Plot in the selected Method.")
public class CreateGraphic {

	private Method method;
	private String type;

	/**
	 * Set method.
	 * 
	 * @param method
	 */
	@Port(direction = Direction.INPUT, name = "Set Method", order = 1)
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Set statistic type.
	 * 
	 * @param type
	 */
	@Port(direction = Direction.INPUT, name = "Set type", order = 2)
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Creates a new plot.
	 * 
	 * @param m
	 *            Method to add the plot.
	 */
	@Port(direction = Direction.INPUT, name = "Select Method", order = 3)
	public void selectExp(String function) {
		PlotDialog plotD = new PlotDialog(this.method, function);
		plotD.setVisible(true);

		if (plotD.isExit()) {
			// We add the graphic to our method
			this.method.addPlot(plotD.getGraphic());
		}
	}
}
