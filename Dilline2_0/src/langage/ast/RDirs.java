package langage.ast;

import java.util.List;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IRDirs;

public class RDirs implements IRDirs{
	private Dir dir;
	private List<Dir> dirs;
	
	public RDirs(Dir dir, List<Dir> dirs) {
		super();
		this.dir = dir;
		this.dirs = dirs;
	}
	public Dir getDir() {
		return dir;
	}

	public List<Dir> getDirs() {
		return dirs;
	}
	
	@Override
	public Object eval(ExecutionStateI data) {
		dirs.add(0, dir);
		return dirs;
	}
	
}
