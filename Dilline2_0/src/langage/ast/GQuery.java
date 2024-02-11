package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
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
//		ProcessingNodeI processingNode = data.getProcessingNode();
//		if(cont instanceof IECont) {
//			QueryResultI res = new QueryResult();
//			SensorDataI sensorinfo = new SensorData(processingNode.getNodeIdentifier(), processingNode.)
//			(QueryResult) res).setpositiveSensorNodes(processingNode.getNodeIdentifier());
//			return res;
//
//			}
//		}
		return null;
	}




}
