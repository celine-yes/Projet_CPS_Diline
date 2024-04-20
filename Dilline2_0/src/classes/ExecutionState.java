package classes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionState implements ExecutionStateI{
	
	private static final long serialVersionUID = 1L;
	private ProcessingNodeI processingNode;
	private boolean directional = false;
	private boolean flooding= false;
	private double maxDist;
	private PositionI base;
	private Set<Direction> directions = new HashSet<>();
	private AtomicInteger compteur_hops = new AtomicInteger(0);
	private int nb_hops;
	private QueryResultI finalResult = new QueryResult();
	
	public ExecutionState(ProcessingNodeI processingNode) {
		this.processingNode = processingNode;
	}

	@Override
	public ProcessingNodeI getProcessingNode() {
		return processingNode;
	}

	@Override
	public void updateProcessingNode(ProcessingNodeI pn) {
		this.processingNode = pn;
	}

	@Override
	public boolean isDirectional() {
		return directional;
	}
	
	public void setDirectional() {
		directional = true;
	}

	@Override
	public Set<Direction> getDirections() {
		return new HashSet<>(directions);
	}
	
	public void addDirections(Direction direction) {
		directions.add(direction);
	}
	
	@Override
	public boolean noMoreHops() {
	    return nb_hops == compteur_hops.get();
	}

	@Override
	public void incrementHops() {
	    compteur_hops.incrementAndGet();
	}
	
	public void setNbHops(int nb) {
		nb_hops = nb;
	}

	@Override
	public boolean isFlooding() {
		return flooding;
	}
	
	public void setFlooding() {
		flooding = true;
	}
	
	public void setMaxDist(double maxDist) {
		this.maxDist = maxDist;
	}
	
	public void setBase(PositionI base) {
		this.base = base;
	}
	
	@Override
	public boolean withinMaximalDistance(PositionI p) {
		return p.distance(base) < maxDist;
	}

	@Override
	public boolean isContinuationSet() {
		return flooding || directional;
	}
	
	@Override
	public QueryResultI getCurrentResult() {
		return finalResult;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
	    if (result.isBooleanRequest()) {
	    	System.out.println("rsult is boolean");
	    	
	        // Si la requête est de type Bquery
	        finalResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
	        
	        if (! (finalResult.isBooleanRequest())){
	        	((QueryResult) finalResult).setIsBoolean();
	        }
	        
	    } else if (result.isGatherRequest()) {
	        // Si la requête est de type Gquery
	    	finalResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());  
	    	
	        if (! (finalResult.isGatherRequest())){
	        	((QueryResult) finalResult).setIsGather();
	        }
	    }
	}
	
	public ExecutionState copy() {
	    ExecutionState copieState = new ExecutionState(this.processingNode);
	    copieState.directional = this.directional;
	    copieState.flooding = this.flooding;
	    copieState.maxDist = this.maxDist;
	    copieState.base = this.base;
	    copieState.directions = new HashSet<>(this.directions);
	    copieState.compteur_hops = this.compteur_hops; //not a copy, supposed to be shared
	    copieState.nb_hops = this.nb_hops;
	    copieState.finalResult = ((QueryResult) this.finalResult).copy();

	    return copieState;
	}
}
