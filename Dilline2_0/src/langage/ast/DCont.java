package langage.ast;

import classes.ExecutionState;
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
	    // Si la continuation n'est pas encore définie, définissez les valeurs nécessaires dans l'état d'exécution
	    if (!data.isContinuationSet()) {
	        ((ExecutionState) data).setDirectional();	        
	        ((ExecutionState) data).setNbHops(maxSauts);
	        dirs.eval(data);
	    }
	    return null;
	}
}
