package langage.ast;


import classes.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDirs;


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
	@Override
	public Object eval(ExecutionStateI data) {
		((ExecutionState) data).addDirections(dir);
		return null;
	}

	
}
