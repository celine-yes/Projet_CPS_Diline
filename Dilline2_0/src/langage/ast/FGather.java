package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IGather;


/**
 * Represents one gather strategy in a sensor network query language.
 * This class is designed to collect sensor data from a specified sensor without any further
 * recursive gathering, making it a terminal node in a gather strategy chain.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class FGather implements IGather{
	
	private static final long serialVersionUID = 1L;
	private String sensorId; // Sensor ID from which to gather data

	
	public FGather(String sensorId) {
		super();
		this.sensorId = sensorId;

	}

	public String getSensorId() {
		return sensorId;
	}
	
	/**
     * Evaluates the gather strategy by collecting sensor data from the specified sensor ID.
     * This method is terminal and does not invoke further gather strategies.
     *
     * @param data the current execution state of the query processing.
     * @return an array list of sensor data from the specified sensor.
     */
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
