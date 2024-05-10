package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IBexp extends Serializable{
	public boolean eval(ExecutionStateI data) throws Exception;
}
