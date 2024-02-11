package classes;

import java.io.Serializable;
import java.time.Instant;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class SensorData implements SensorDataI {
	
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String sensorId;
	private Serializable value;
	
	public SensorData(String nodeId, String sensorId, Serializable value ) {
		this.nodeId = nodeId;
		this.sensorId = sensorId;
		this.value = value;
	}
	

	@Override
	public String getNodeIdentifier() {
		return nodeId;
	}

	@Override
	public String getSensorIdentifier() {
		return sensorId;
	}

	@Override
	public Class<? extends Serializable> getType() {
		return value.getClass();
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public Instant getTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

}
