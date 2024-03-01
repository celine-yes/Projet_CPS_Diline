package langage.ast;

import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IRgather;

public class RGather implements IRgather{
	private String sensorId;
	private List<String> gather;
	
	public RGather(String sensorId, List<String> gather) {
		super();
		this.sensorId = sensorId;
		this.gather = gather;
	}

	public String getSensorId() {
		return sensorId;
	}
	
	public List<String> getGather() {
		return gather;
	}

	@Override
	public SensorDataI eval(ExecutionStateI data) {
		//
		ProcessingNodeI processingNode = data.getProcessingNode();
		String nodeId = processingNode.getNodeIdentifier();
		SensorDataI sensortrouve = null;
		if(gather.contains(nodeId)) {
			sensortrouve = processingNode.getSensorData(sensorId);
		}
		return sensortrouve;
	}

}
