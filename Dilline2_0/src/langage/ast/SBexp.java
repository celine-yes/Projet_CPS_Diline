package langage.ast;


import exceptions.InvalidTypeException;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;

public class SBexp implements IBexp{
	
	private static final long serialVersionUID = 1L;
	private String sensorId;

	public SBexp(String sensorId) {
		super();
		this.sensorId = sensorId;

	}
	public String getSensorId() {
		return sensorId;
	}

	@Override
	public boolean eval(ExecutionStateI data) throws Exception {
		// valeur du capteur ayant identifiant= sensorId du noeud courant
		
		ProcessingNodeI processingNode = data.getProcessingNode();
		SensorDataI sensorData =  processingNode.getSensorData(sensorId);
		
		if (!(sensorData.getValue() instanceof Boolean)) {
			throw new InvalidTypeException("SBexp#eval: invalid type value, expected boolean");
		}
		return (boolean) sensorData.getValue();
		
		
	}


}
