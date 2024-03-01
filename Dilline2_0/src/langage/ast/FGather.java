package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IFgather;


public class FGather implements IFgather{
	private String sensorId;

	
	public FGather(String sensorId) {
		super();
		this.sensorId = sensorId;

	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public SensorDataI eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensortrouve = processingNode.getSensorData(sensorId);
		
		return sensortrouve;
	}
}
