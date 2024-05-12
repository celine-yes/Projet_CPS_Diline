package langage.ast;

import java.io.Serializable;
import java.util.ArrayList;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICont;
import langage.interfaces.IGather;
import langage.interfaces.QueryI;

public class GQuery implements QueryI, Serializable{
	
	private static final long serialVersionUID = 1L;
	private ICont cont;
	private IGather gather;
	
	
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


	@Override
	public QueryResultI eval(ExecutionStateI data) throws Exception{
		
	    QueryResultI result = new QueryResult();
	    
		((QueryResult) result).setIsGather();

		ArrayList<SensorDataI> sd = (ArrayList<SensorDataI>) gather.eval(data);
		((QueryResult) result).setgatheredSensorsValues(sd); 
		cont.eval(data);

		return result;
	}




}
