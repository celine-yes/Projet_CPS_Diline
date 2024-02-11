package langage.ast;

import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IRgather;

public class RGather implements IRgather{
	private String sensorId;
	private List<Object> gather;
	
	public RGather(String sensorId, List<Object> gather) {
		super();
		this.sensorId = sensorId;
		this.gather = gather;
	}

	public String getSensorId() {
		return sensorId;
	}
	
	public List<Object> getGather() {
		return gather;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		//
		ProcessingNodeI node = data.getProcessingNode();
		SensorDataI capteurs = node.getSensorData(sensorId);
		gather.add(0, capteurs.getValue());
		return gather;
	}

}
