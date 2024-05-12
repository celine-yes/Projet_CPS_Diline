package langage.ast;
import classes.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDirs;

/**
 * Represents a recursive list of directions within a sensor network query language.
 * This class allows for adding multiple directions to an execution state, supporting complex
 * directional queries. It utilizes a recursive composition to handle an extended list of
 * directions that can be applied sequentially during the evaluation process.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class RDirs implements IDirs{
	
	private static final long serialVersionUID = 1L;
	private Direction dir;
	private IDirs dirs;
	
	/**
     * Constructs an RDirs instance with a specific direction and subsequent directions.
     * 
     * @param dir the current direction to be added to the execution state.
     * @param dirs the next set of directions to be processed recursively.
     */
	public RDirs(Direction dir, IDirs dirs) {
		super();
		this.dir = dir;
		this.dirs = dirs;
	}
	
	/**
     * Gets the current direction.
     * 
     * @return the current direction to be added.
     */
	public Direction getDir() {
		return dir;
	}

	public IDirs getDirs() {
		return dirs;
	}
	
	/**
     * Evaluates the directions by adding the current direction to the execution state
     * and recursively evaluating any further directions.
     * 
     * @param data the execution state context which maintains the current state information.
     * @return null after adding all directions to the state.
     * @throws Exception if there is an error during the evaluation process.
     */
	@Override
	public Object eval(ExecutionStateI data) throws Exception{
		((ExecutionState) data).addDirections(dir);
		dirs.eval(data);
		return null;
	}
	
}
