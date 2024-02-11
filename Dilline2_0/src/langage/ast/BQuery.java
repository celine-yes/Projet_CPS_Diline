package langage.ast;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;
import langage.interfaces.IBquery;
import langage.interfaces.ICont;
import langage.interfaces.IECont;


public class BQuery implements IBquery{
	
	private ICont cont;
	private IBexp bexp;
	
	
	public BQuery(ICont cont, IBexp bexp) {
		this.cont = cont;
		this.bexp = bexp;
	}


	public ICont getCont() {
		return cont;
	}


	public IBexp getBexp() {
		return bexp;
	}


	@Override
	public QueryResultI eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		if(cont instanceof IECont) {
			if((boolean) bexp.eval(data)) {
				QueryResultI res = new QueryResult();
				((QueryResult) res).setpositiveSensorNodes(processingNode.getNodeIdentifier());
				return res;
			}
		}
		// A compléter
		return null;
	}




}
