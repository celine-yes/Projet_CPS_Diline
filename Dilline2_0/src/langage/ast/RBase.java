package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IRBase;

public class RBase implements IRBase{
	private String thisPosition;

	public RBase(String position) {
		super();
		this.thisPosition = position;
	}

	public String getPosition() {
		return thisPosition;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		return processingNode.getPosition();
	}


}
