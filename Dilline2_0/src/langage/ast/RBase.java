package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBase;

public class RBase implements IBase{
	private PositionI thisPosition;

	public RBase(PositionI position) {
		super();
		this.thisPosition = position;
	}

	public PositionI getPosition() {
		return thisPosition;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		return processingNode.getPosition();
	}


}
