package langage.ast;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;
import langage.interfaces.IBquery;
import langage.interfaces.ICont;
import langage.interfaces.IECont;
import langage.interfaces.IFCont;


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
	    QueryResultI result = new QueryResult();
	    ProcessingNodeI processingNode = data.getProcessingNode();

	    // Évaluation de l'expression booléenne pour le nœud actuel
	    if ((boolean) bexp.eval(data)) {
	        ((QueryResult) result).setIsBoolean();
	        ((QueryResult) result).setpositiveSensorNodes(processingNode.getNodeIdentifier()); 
	    }
	    return result;
	}
}
