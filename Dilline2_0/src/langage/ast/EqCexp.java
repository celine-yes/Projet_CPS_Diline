package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IEqCexp;
import langage.interfaces.IRand;

public class EqCexp implements IEqCexp{
	private IRand rand1;
	private IRand rand2;
	
	public EqCexp(IRand rand1, IRand rand2) {
		super();
		this.rand1 = rand1;
		this.rand2 = rand2;
	}
	
	public IRand getRand1() {
		return rand1;
	}

	public IRand getRand2() {
		return rand2;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		double value1 = ((Number) rand1.eval(data)).doubleValue();
        double value2 = ((Number) rand2.eval(data)).doubleValue();
        return value1 == value2;
	}


}