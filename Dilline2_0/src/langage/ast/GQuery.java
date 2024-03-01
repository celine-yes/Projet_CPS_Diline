package langage.ast;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICont;
import langage.interfaces.IGather;
import langage.interfaces.IGquery;

public class GQuery implements IGquery{
	
	private ICont cont;
	private IGather gather;
	
	
	public GQuery(ICont cont, IGather gather) {
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
	public QueryResultI eval(ExecutionStateI data) {
		QueryResultI result = new QueryResult();
		
		((QueryResult) result).setIsGather();

		SensorDataI sd = (SensorDataI) gather.eval(data);
		((QueryResult) result).setgatheredSensorsValues(sd); 
		cont.eval(data);
		return result;
	}




}
