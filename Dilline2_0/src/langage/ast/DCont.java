package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IDCont;
import langage.interfaces.IDirs;

public class DCont implements IDCont{
	private IDirs dirs;
	private int maxSauts;
	
	public DCont(IDirs dirs, int maxSauts) {
		super();
		this.dirs = dirs;
		this.maxSauts = maxSauts;
	}
	
	public IDirs getDirs() {
		return dirs;
	}

	public int getMaxSauts() {
		return maxSauts;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
