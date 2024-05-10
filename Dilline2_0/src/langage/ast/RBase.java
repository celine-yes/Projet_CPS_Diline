package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBase;

public class RBase implements IBase{
	
	private static final long serialVersionUID = 1L;
	private PositionI position;

	public RBase() {
		super();
	}
	
	@Override
	public PositionI getPosition() {
		return position;
	}

	@Override
	public PositionI eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		position = processingNode.getPosition();
		return position;
	}

}
