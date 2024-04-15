package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IRand;

public class SRand implements IRand{
	private String sensorId;

	public SRand(String sensorId) {
		super();
		this.sensorId = sensorId;
	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		return processingNode.getSensorData(sensorId).getValue();
	}


}
