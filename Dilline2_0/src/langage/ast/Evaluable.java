package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Evaluable {
	public abstract Object eval();
	public abstract Object eval(ExecutionStateI data);
	
}
