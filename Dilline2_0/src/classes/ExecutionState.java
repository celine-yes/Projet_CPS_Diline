package classes;

import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBase;

public class ExecutionState implements ExecutionStateI{
	
	private static final long serialVersionUID = 1L;
	private ProcessingNodeI processingNode;
	private boolean directional = false;
	private boolean flooding= false;
	private double maxDist;
	private IBase base;
	private Set<Direction> directions = new HashSet<>();
	private int compteur_hops=0;
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
		return directions;
	}
	
	public void addDirections(Direction direction) {
		directions.add(direction);
	}
	
	@Override
	public boolean noMoreHops() {
		return nb_hops == compteur_hops;
	}

	@Override
	public void incrementHops() {
		compteur_hops++;
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
	
	public void setBase(IBase base) {
		this.base = base;
	}
	
	@Override
	public boolean withinMaximalDistance(PositionI p) {
		return p.distance(base.getPosition()) < maxDist;
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
	        // Si la requête est de type Bquery
	        finalResult.positiveSensorNodes().addAll(result.positiveSensorNodes());      	
	    } else if (result.isGatherRequest()) {
	        // Si la requête est de type Gquery
	    	finalResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());  
	    }
	}
}
