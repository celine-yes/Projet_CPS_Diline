package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IGather;


public class FGather implements IGather{
	
	private static final long serialVersionUID = 1L;
	private String sensorId;

	
	public FGather(String sensorId) {
		super();
		this.sensorId = sensorId;

	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public ArrayList<SensorDataI> eval(ExecutionStateI data) {
		ArrayList<SensorDataI> res = new ArrayList<SensorDataI>();
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensortrouve = processingNode.getSensorData(sensorId);
		if(sensortrouve != null) {
			res.add(sensortrouve);
		}
		return res;
	}
}
