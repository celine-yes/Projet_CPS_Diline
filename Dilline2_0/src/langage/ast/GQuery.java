package langage.ast;

import java.util.ArrayList;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICont;
import langage.interfaces.IGather;
import langage.interfaces.QueryI;

/**
 * Represents a gather query in a sensor network query language.
 * This query type is used to collect data from sensors across the network based on specified conditions and continuations.
 * It manages the aggregation of sensor data according to the gather strategy defined by the {@code gather} field.
 *
 * <p>This class also handles the continuation of queries across the network using the provided continuation strategy
 * in the {@code cont} field, allowing for extended and complex data gathering operations.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class GQuery implements QueryI{
	
	private static final long serialVersionUID = 1L;
	private ICont cont; 
	private IGather gather;
	
	/**
     * Constructs a GQuery with specified continuation and data gathering strategies.
     * 
     * @param cont   the continuation strategy to manage how the query propagates through the network.
     * @param gather the data gathering strategy to specify how data is collected from the network.
     */
	public GQuery(ICont cont, IGather gather) {
		super();
		this.cont = cont;
		this.gather = gather;
	}


	public ICont getCont() {
		return cont;
	}


	public IGather getGather() {
		return gather;
	}

	
	/**
     * Evaluates this query within the given execution state context. This method processes the query by
     * gathering data according to the specified strategy and manages query propagation using the specified continuation.
     *
     * @param data the current execution state of the query processing.
     * @return the result of the query as an aggregated collection of sensor data.
     * @throws Exception if there is an error during the query evaluation process.
     */
	@Override
	public QueryResultI eval(ExecutionStateI data) throws Exception{
		
	    QueryResultI result = new QueryResult();
	    
		((QueryResult) result).setIsGather();

		@SuppressWarnings("unchecked")
		ArrayList<SensorDataI> sd = (ArrayList<SensorDataI>) gather.eval(data);
		((QueryResult) result).setgatheredSensorsValues(sd); 
		cont.eval(data);

		return result;
	}


}
