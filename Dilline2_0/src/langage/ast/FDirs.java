package langage.ast;


import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IFDirs;


public class FDirs implements IFDirs{
	private Dir dir;
	
	public FDirs(Dir dir) {
		super();
		this.dir = dir;
	}
	public Dir getDir() {
		return dir;
	}
	@Override
	public Object eval(ExecutionStateI data) {
		return dir;
	}

	
}
