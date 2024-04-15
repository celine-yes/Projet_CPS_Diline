package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IBexp{
	public boolean eval(ExecutionStateI data) throws Exception;
}
