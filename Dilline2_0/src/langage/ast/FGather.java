package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
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
	public Object eval(ExecutionStateI data) {
		// TODO Auto-generated method stub
		return null;
	}



}
