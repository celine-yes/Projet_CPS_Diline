package classes;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import langage.interfaces.IBquery;
import langage.interfaces.IGquery;

public class QueryResult implements QueryResultI {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> positiveSensorNodes = new ArrayList<String>();
	private ArrayList<SensorDataI> gatheredSensorsValues = new ArrayList<SensorDataI>();

	
	@Override
	public boolean isBooleanRequest() {
		return false;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		return positiveSensorNodes;
	}
	
	public void setpositiveSensorNodes(String id) {
		//audit1
		positiveSensorNodes.add(id);
	}
	
	public void setgatheredSensorsValues(SensorDataI sensorinfo) {
		//audit1
		gatheredSensorsValues.add(sensorinfo);
	}

	@Override
	public boolean isGatherRequest() {
		return false;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		return gatheredSensorsValues;
	}

}
