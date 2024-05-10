package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IGather;

public class RGather implements IGather{
	
	private static final long serialVersionUID = 1L;
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
	public ArrayList<SensorDataI> eval(ExecutionStateI data) throws Exception{
		ArrayList<SensorDataI> res = new ArrayList<SensorDataI>();
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensortrouve = processingNode.getSensorData(sensorId);
		if(sensortrouve != null) {
			res.add(sensortrouve);
		}
		ArrayList<SensorDataI>resSuite = (ArrayList<SensorDataI>) gather.eval(data);
		
		res.addAll(resSuite);
		
		return res;
	}

}
