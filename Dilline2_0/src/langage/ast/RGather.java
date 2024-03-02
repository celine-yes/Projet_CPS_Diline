package langage.ast;

import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IEvaluable;
import langage.interfaces.IGather;
import langage.interfaces.IRgather;

public class RGather implements IRgather{
	private String sensorId;
	private IGather gather;
	
	public RGather(String sensorId, IGather gather) {
		super();
		this.sensorId = sensorId;
		this.gather = gather;
	}

	public String getSensorId() {
		return sensorId;
	}
	
	public IGather getGather() {
		return gather;
	}

	@Override
	public ArrayList<SensorDataI> eval(ExecutionStateI data) {
		ArrayList<SensorDataI> res = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> resSuite = new ArrayList<SensorDataI>();
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensortrouve = processingNode.getSensorData(sensorId);
		if(sensortrouve != null) {
			res.add(sensortrouve);
		}
		resSuite = (ArrayList<SensorDataI>) gather.eval(data);
		
		res.addAll(resSuite);
		
		return res;
	}

}
