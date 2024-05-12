package classes;

import java.io.Serializable;
import java.time.Instant;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

/**
 * The class <code>SensorData</code> implements the methods
 * of the interface <code>SensorDataI</code> .
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

public class SensorData implements SensorDataI, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String sensorId;
	private Serializable value;
	private Instant timestamp;
	
	public SensorData(String nodeId, String sensorId, Serializable value ) {
		this.nodeId = nodeId;
		this.sensorId = sensorId;
		this.value = value;
		this.timestamp = Instant.now();
	}
	
	public SensorData(String nodeId, String sensorId, Serializable value, Instant timestamp ) {
		this.nodeId = nodeId;
		this.sensorId = sensorId;
		this.value = value;
		this.timestamp = timestamp;
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
		return (Class<? extends Serializable>) value.getClass();
	}

	@Override
	public Serializable getValue() {
		return (Serializable) value;
	}
	
	public void updateValue() {
		if (value instanceof Boolean) {
	        this.value = !(Boolean) value; // Inverse la valeur
	    }
	    // Vérifie si value est un Double
	    else if (value instanceof Double) {
	        this.value = (Double) value + 1; // Ajoute 1 à la valeur
	    }
		this.timestamp = Instant.now(); // Met à jour le timestamp à chaque modification de la valeur
	}

	@Override
	public Instant getTimestamp() {
		return timestamp;

	}
	
	@Override
	public String toString() {
		return "(" + nodeId + ", " + sensorId + ", " + value + ")";
	}
	
    public SensorData copy() {
        return new SensorData(this.nodeId, this.sensorId, this.value, this.timestamp);
    }

}
