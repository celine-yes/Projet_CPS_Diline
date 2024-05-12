package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * The interface QueryI provides a common type for the abstract syntax tree nodes that appear at the root of the tree,
 * extends {@link fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI}
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 *
 */
public interface QueryI extends fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI{
	
	public ICont getCont();
	public QueryResultI eval(ExecutionStateI data) throws Exception;
}
