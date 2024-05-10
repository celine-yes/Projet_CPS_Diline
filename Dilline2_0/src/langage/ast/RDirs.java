package langage.ast;
import classes.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDirs;

public class RDirs implements IDirs{
	
	private static final long serialVersionUID = 1L;
	private Direction dir;
	private IDirs dirs;
	
	public RDirs(Direction dir, IDirs dirs) {
		super();
		this.dir = dir;
		this.dirs = dirs;
	}
	public Direction getDir() {
		return dir;
	}

	public IDirs getDirs() {
		return dirs;
	}
	
	@Override
	public Object eval(ExecutionStateI data) throws Exception{
		((ExecutionState) data).addDirections(dir);
		dirs.eval(data);
		return null;
	}
	
}
