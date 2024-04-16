package classes;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> positiveSensorNodes = new ArrayList<String>();
	private ArrayList<SensorDataI> gatheredSensorsValues = new ArrayList<SensorDataI>();
	private boolean isBoolean = false;
	private boolean isGather = false;

	
	@Override
	public boolean isBooleanRequest() {
		return isBoolean;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		return new ArrayList<>(positiveSensorNodes);
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
		return new ArrayList<>(gatheredSensorsValues);
	}
	
    public QueryResult copie() {
        QueryResult newCopy = new QueryResult();

        // Copy the lists
        newCopy.positiveSensorNodes = new ArrayList<>(this.positiveSensorNodes);
        newCopy.gatheredSensorsValues = new ArrayList<>(this.gatheredSensorsValues);

        newCopy.isBoolean = this.isBoolean;
        newCopy.isGather = this.isGather;

        return newCopy;
    }

}
