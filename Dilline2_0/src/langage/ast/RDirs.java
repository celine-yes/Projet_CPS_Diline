package langage.ast;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IRDirs;

public class RDirs implements IRDirs{
	private Direction dir;
	private Set<Direction> dirs;
	
	public RDirs(Direction dir, Set<Direction> dirs) {
		super();
		this.dir = dir;
		this.dirs = dirs;
	}
	public Direction getDir() {
		return dir;
	}

	public Set<Direction> getDirs() {
		return dirs;
	}
	
	@Override
	public Object eval(ExecutionStateI data) {
		dirs.add(dir);
		return dirs;
	}
	
}
