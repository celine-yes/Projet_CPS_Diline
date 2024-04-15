package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;

public class OrBexp implements IBexp{
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
	public boolean eval(ExecutionStateI data) throws Exception{
		boolean bexp1Result = bexp1.eval(data);
        boolean bexp2Result = bexp2.eval(data);
        return bexp1Result || bexp2Result;
	}

}
