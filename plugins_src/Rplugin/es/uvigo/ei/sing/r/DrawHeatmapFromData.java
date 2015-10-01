package es.uvigo.ei.sing.r;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.datatypes.data.Data;

@Operation(description="Draws the heapmap of a data using numerical variables and R behind")
public class DrawHeatmapFromData {

	private Data data;

	@Port(direction=Direction.INPUT, name="data", order=1)
	public void setData(Data d){
		this.data = d;
	}
	
	
}	
