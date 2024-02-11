package langage.ast;


import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.ISBexp;

public class SBexp implements ISBexp{
	
	private String sensorId;

	public SBexp(String sensorId) {
		super();
		this.sensorId = sensorId;

	}
	public String getSensorId() {
		return sensorId;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		// valeur du capteur ayant identifiant= sensorId du noeud courant
		
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensorData =  processingNode.getSensorData(sensorId);
		
		return (boolean) sensorData.getValue();
		
		
	}


}
