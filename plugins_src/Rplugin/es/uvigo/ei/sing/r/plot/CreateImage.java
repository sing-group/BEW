package es.uvigo.ei.sing.r.plot;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;

@Operation(description="creates a image from a base64 encoding")
public class CreateImage {

	@Port(direction=Direction.BOTH, name = "bytes in base 64", order=1)
	public RImage setBytes(String bytes){
		return new RImage(bytes);
	}
}
