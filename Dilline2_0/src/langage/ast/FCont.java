package langage.ast;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
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
	public QueryResultI eval(ExecutionStateI data) {
		if (!data.isContinuationSet()) {
			((ExecutionState) data).setFlooding();
			((ExecutionState) data).setMaxDist(maxDist);
			((ExecutionState) data).setBase(base);		
		}
		ProcessingNodeI node = data.getProcessingNode();		
		
		return null;
		
	}

	


	
}
