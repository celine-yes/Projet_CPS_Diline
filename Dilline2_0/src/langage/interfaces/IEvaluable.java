package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IEvaluable {
	public Object eval(ExecutionStateI data) throws Exception;

}