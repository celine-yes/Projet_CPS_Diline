package classes;

import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionState implements ExecutionStateI{
	
	private static final long serialVersionUID = 1L;
	private ProcessingNodeI processingNode;
	private boolean directional = false;
	private Set<Direction> directions = null;
	private int compteur_hops;
	private int nb_hops;
	
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
	public QueryResultI getCurrentResult() {
		// ne pas définir tt de suite pour la deuxieme partie asynchrone
		return null;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
		// ne pas définir tt de suite pour la deuxieme partie asynchrone
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
	
	public void setDirections(Set<Direction> directions) {
		if (this.directions == null) {
			this.directions = new HashSet<Direction>();
			this.directions = directions;
		}
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean withinMaximalDistance(PositionI p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isContinuationSet() {
		// TODO Auto-generated method stub
		return false;
	}

}
