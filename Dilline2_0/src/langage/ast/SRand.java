package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
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
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensor = processingNode.getSensorData(sensorId);
		if (sensor != null) {
			return processingNode.getSensorData(sensorId).getValue();
		}
		return null;
	}
}
