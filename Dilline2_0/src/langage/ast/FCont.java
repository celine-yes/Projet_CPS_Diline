package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBase;
import langage.interfaces.IFCont;
import classes.ExecutionState;

public class FCont implements IFCont{
	private IBase base;
	private double maxDist;
	
	//Continuation inondation
	public FCont(IBase base, double maxDist) {
		super();
		this.base = base;
		this.maxDist = maxDist;
	}
	
	public IBase getBase() {
		return base;
	}

	public double getMaxDist() {
		return maxDist;
	}
	

	@Override
	public Object eval(ExecutionStateI data) {
	    if (!data.isContinuationSet()) {
	        ((ExecutionState) data).setFlooding();
	        ((ExecutionState) data).setMaxDist(maxDist);
	        
	        PositionI position = base.eval(data);
	        ((ExecutionState) data).setBase(position);
	    }
	    return null;
	}
	
}
