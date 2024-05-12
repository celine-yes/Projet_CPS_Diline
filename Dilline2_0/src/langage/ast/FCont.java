package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBase;
import langage.interfaces.IFCont;

import classes.ExecutionState;

/**
 * Implements {@code IFCont} to provide functionalities for flood-based continuation
 * within the domain-specific language of sensor network queries.
 *
 * <p>This class encapsulates the logic for evaluating whether a continuation should
 * propagate as a flooding operation based on the specified maximum distance and the base position.</p>

 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class FCont implements IFCont{
	
	private static final long serialVersionUID = 1L;
	private IBase base;
	private double maxDist;
	
	//Continuation inondation
	public FCont(IBase base, double maxDist) {
		super();
		this.base = base;
		this.maxDist = maxDist;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IBase getBase() {
		return base;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getMaxDist() {
		return maxDist;
	}
	
	/**
	 * Evaluates the continuation's conditions and applies flooding settings to the provided execution state.
	 *
	 * @param data the execution state to be modified based on the continuation's logic.
	 * @return null, indicating no direct output from this evaluation.
	 */
	@Override
	public Object eval(ExecutionStateI data) {
	    if (!data.isContinuationSet()) {
	        ((ExecutionState) data).setFlooding();
	        ((ExecutionState) data).setMaxDist(maxDist);
	        
	        PositionI position = base.eval(data);
	        ((ExecutionState) data).setBase(position);
	    }
	    return null;
	}
	
}
