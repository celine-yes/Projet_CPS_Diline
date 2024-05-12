package langage.ast;

import java.io.Serializable;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;
import langage.interfaces.ICont;
import langage.interfaces.QueryI;


public class BQuery implements QueryI, Serializable{
	
	private static final long serialVersionUID = 1L;
	private ICont cont;
	private IBexp bexp;
	
	
	public BQuery(ICont cont, IBexp bexp) {
		super();
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
	public QueryResultI eval(ExecutionStateI data) throws Exception{
		
	    QueryResultI result = new QueryResult();
	    ProcessingNodeI processingNode;
		processingNode = data.getProcessingNode();

		((QueryResult) result).setIsBoolean();   
		    
	    // Évaluation de l'expression booléenne pour le nœud actuel
	    if (bexp.eval(data)) {   
	        ((QueryResult) result).setpositiveSensorNodes(processingNode.getNodeIdentifier()); 
	    }
	    cont.eval(data);

	    return result;
	}
}
