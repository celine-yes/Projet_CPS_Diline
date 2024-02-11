package langage.ast;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.ICont;
import langage.interfaces.IECont;
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
		ProcessingNodeI processingNode = data.getProcessingNode();
		if(cont instanceof IECont) {
			SensorDataI sensordata = (SensorDataI) gather.eval(data);
			System.out.println(".............sensordata =" +sensordata);
			QueryResultI res = new QueryResult();
			
			if(sensordata != null) {
				((QueryResult) res).setgatheredSensorsValues(sensordata);
			}
			
			return res;
		}
		
		return null;
	}




}
