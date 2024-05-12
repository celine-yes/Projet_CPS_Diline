package langage.ast;


import classes.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDirs;

/**
 * Represents a directional component within the domain-specific language for sensor networks.
 * This class encapsulates a single direction, which can be added to the execution state,
 * typically used in network operations where directional constraints are relevant.
 *
 * <p>{@code FDirs} serves as a simple wrapper for the {@link Direction} enum, providing a mechanism
 * to apply a specific directional constraint to the execution state, aiding in the propagation
 * of requests or data within the defined network topology.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class FDirs implements IDirs{
	
	private static final long serialVersionUID = 1L;
	private Direction dir;
	
	public FDirs(Direction dir) {
		super();
		this.dir = dir;
	}
	public Direction getDir() {
		return dir;
	}
	
	 /**
     * Evaluates this direction within the context of the given execution state,
     * typically by adding this direction to a set of directions in the state
     * to control the flow or scope of data propagation.
     *
     * @param data the execution state which will be modified to include this direction.
     * @return returns {@code null} as it primarily performs an operation on the state.
     */
	@Override
	public Object eval(ExecutionStateI data) {
		((ExecutionState) data).addDirections(dir);
		return null;
	}

	
}
