package langage.ast;


import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IFDirs;


public class FDirs implements IFDirs{
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
		return dir;
	}

	
}
