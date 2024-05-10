package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IRand;

public class SRand implements IRand{
	
	private static final long serialVersionUID = 1L;
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
		System.out.println("sensorId............................" + sensorId);
		ProcessingNodeI processingNode = data.getProcessingNode();
		System.out.println("processingNode......................" + processingNode.getNodeIdentifier());
		System.out.println("processingNode.getSensorData(sensorId)....." + processingNode.getSensorData(sensorId));
		System.out.println("processingNode.getSensorData(sensorId).getValue()..." + processingNode.getSensorData(sensorId).getValue());
		return processingNode.getSensorData(sensorId).getValue();
	}


}
