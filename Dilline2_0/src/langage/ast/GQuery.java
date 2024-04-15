package langage.ast;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICont;
import langage.interfaces.IGather;
import langage.interfaces.QueryI;

public class GQuery implements QueryI{
	
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

		ReadWriteLock rwLock = new ReentrantReadWriteLock();
		
	    QueryResultI result = new QueryResult();
	    
		Lock writeLock1 = rwLock.writeLock();
		writeLock1.lock();

		try {
			((QueryResult) result).setIsGather();

			ArrayList<SensorDataI> sd = (ArrayList<SensorDataI>) gather.eval(data);
			((QueryResult) result).setgatheredSensorsValues(sd); 
			cont.eval(data);

		} finally { 
		    writeLock1.unlock();
		}

		return result;
	}




}
