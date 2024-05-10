package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IEvaluable extends Serializable {
	public Object eval(ExecutionStateI data) throws Exception;

}