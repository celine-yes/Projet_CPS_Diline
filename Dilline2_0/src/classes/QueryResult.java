package classes;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;


/**
 * Implements the {@link QueryResultI} interface to provide mechanisms for storing and
 * querying results of two types of queries in a sensor network: boolean queries and gather queries.
 * This class allows storing either a list of sensor node identifiers that satisfied a boolean condition
 * or a list of sensor data collected from various nodes.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class QueryResult implements QueryResultI {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> positiveSensorNodes = new ArrayList<>();
	private ArrayList<SensorDataI> gatheredSensorsValues = new ArrayList<>();
	private boolean isBoolean = false;
	private boolean isGather = false;

	
	@Override
	public boolean isBooleanRequest() {
		return isBoolean;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		return positiveSensorNodes;
	}
	
	//pour les requetes Bquery
	public void setpositiveSensorNodes(String id) {
		positiveSensorNodes.add(id);
	}
	
	public void setIsBoolean() {
		isBoolean = true;
	}
	
	public void setIsGather() {
		isGather = true;
	}
	
	//pour les requetes Gquery
	public void setgatheredSensorsValues(ArrayList<SensorDataI> sensorsinfo) {
		this.gatheredSensorsValues = new ArrayList<>(sensorsinfo);
	}

	@Override
	public boolean isGatherRequest() {
		return isGather;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		return gatheredSensorsValues;
	}
	
    public QueryResult copy() {
        QueryResult newCopy = new QueryResult();

        // Copy the lists
        newCopy.positiveSensorNodes = new ArrayList<>(this.positiveSensorNodes);
        newCopy.gatheredSensorsValues = new ArrayList<>(this.gatheredSensorsValues);

        newCopy.isBoolean = this.isBoolean;
        newCopy.isGather = this.isGather;

        return newCopy;
    }

}
