package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface QueryI extends fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI{
	
	public ICont getCont();
	public QueryResultI eval(ExecutionStateI data) throws Exception;
}
