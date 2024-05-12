package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IGather;

/**
 * Represents a recursive gather strategy in a sensor network query language.
 * This class is designed to collect sensor data starting from a specified sensor and
 * potentially extending the gathering operation through additional recursive gather strategies.
 *
 * <p>This strategy allows for complex and chained data gathering from multiple sensors
 * potentially spread across different nodes in the network.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class RGather implements IGather{
	
	private static final long serialVersionUID = 1L;
	private String sensorId;
	private IGather gather;
	
	/**
     * Constructs an RGather instance with a specific sensor ID and an additional gather strategy.
     * 
     * @param sensorId the sensor identifier from which to start gathering data.
     * @param gather the next gather strategy to be applied recursively.
     */
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

	/**
     * Evaluates the gather strategy by collecting sensor data from the specified sensor ID
     * and then applying the subsequent gather strategy.
     *
     * @param data the current execution state of the query processing.
     * @return an array list of sensor data starting from the specified sensor.
     * @throws Exception if there is an error during the gather process.
     */
	@Override
	public ArrayList<SensorDataI> eval(ExecutionStateI data) throws Exception{
		ArrayList<SensorDataI> res = new ArrayList<SensorDataI>();
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensortrouve = processingNode.getSensorData(sensorId);
		if(sensortrouve != null) {
			res.add(sensortrouve);
		}
		@SuppressWarnings("unchecked")
		ArrayList<SensorDataI>resSuite = (ArrayList<SensorDataI>) gather.eval(data);
		
		res.addAll(resSuite);
		
		return res;
	}

}
