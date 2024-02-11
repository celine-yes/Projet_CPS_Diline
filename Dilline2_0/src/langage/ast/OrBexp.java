package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;
import langage.interfaces.IOrBexp;

public class OrBexp implements IOrBexp{
	private IBexp bexp1;
	private IBexp bexp2;
	
	public OrBexp(IBexp bexp1, IBexp bexp2) {
		super();
		this.bexp1 = bexp1;
		this.bexp2 = bexp2;
	}
	public IBexp getBexp1() {
		return bexp1;
	}

	public IBexp getBexp2() {
		return bexp2;
	}
	

	@Override
	public Object eval(ExecutionStateI data) {
		boolean bexp1Result = (boolean) bexp1.eval(data);
        boolean bexp2Result = (boolean) bexp2.eval(data);
        return bexp1Result || bexp2Result;
	}

}
