package langage.ast;

import classes.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDCont;
import langage.interfaces.IDirs;

/**
 * Implements {@code IDCont} to provide functionalities for directional continuation
 * within the domain-specific language of sensor network queries.
 *
 * <p>This class encapsulates the logic for evaluating whether a continuation should
 * propagate directionally based on the specified maximum number of hops and directional settings.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class DCont implements IDCont{
	
	private static final long serialVersionUID = 1L;
	private IDirs dirs;
	private int maxSauts;
	
	/**
	 * Constructs a new {@code DCont} with the specified directional settings and maximum hops.
	 *
	 * @param dirs the directional settings component for this continuation.
	 * @param maxSauts the maximum number of hops for directional propagation.
	 */
	public DCont(IDirs dirs, int maxSauts) {
		super();
		this.dirs = dirs;
		this.maxSauts = maxSauts;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IDirs getDirs() {
		return dirs;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxSauts() {
		return maxSauts;
	}
	
	/**
	 * Evaluates the continuation's conditions and applies directional settings to the provided execution state.
	 *
	 * @param data the execution state to be modified based on the continuation's logic.
	 * @return null, indicating no direct output from this evaluation.
	 * @throws Exception if there are errors during the evaluation process.
	 */
	@Override
	public Object eval(ExecutionStateI data) throws Exception{
	    // Si la continuation n'est pas encore définie, définissez les valeurs nécessaires dans l'état d'exécution
	    if (!data.isContinuationSet()) {
	        ((ExecutionState) data).setDirectional();	        
	        ((ExecutionState) data).setNbHops(maxSauts);
	        dirs.eval(data);
	    }
	    return null;
	}
}
