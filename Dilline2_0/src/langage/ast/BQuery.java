package langage.ast;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;
import langage.interfaces.ICont;
import langage.interfaces.QueryI;


public class BQuery implements QueryI{
	
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
		
		ReadWriteLock rwLock = new ReentrantReadWriteLock();
		
	    QueryResultI result = new QueryResult();
	    
	    ProcessingNodeI processingNode;
		Lock readLock1 = rwLock.readLock();
		readLock1.lock();

		try {
			processingNode = data.getProcessingNode();

		} finally { 
		    readLock1.unlock();
		}
	    
		Lock writeLock1 = rwLock.writeLock();
		writeLock1.lock();

		try {
		    ((QueryResult) result).setIsBoolean();   
		    
		    // Évaluation de l'expression booléenne pour le nœud actuel
		    if (bexp.eval(data)) {   
		        ((QueryResult) result).setpositiveSensorNodes(processingNode.getNodeIdentifier()); 
		    }
		    cont.eval(data);

		}finally { 
		    writeLock1.unlock();
		}

	    return result;
	}
}
