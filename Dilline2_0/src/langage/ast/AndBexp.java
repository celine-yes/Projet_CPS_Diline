package langage.ast;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;

public class AndBexp implements IBexp, Serializable{
	private static final long serialVersionUID = 1L;
	
	private IBexp bexp1;
	private IBexp bexp2;
	
	public AndBexp(IBexp bexp1, IBexp bexp2) {
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
		boolean bexp1Result = (boolean) bexp1.eval(data);
        boolean bexp2Result = (boolean) bexp2.eval(data);
        return bexp1Result && bexp2Result;
	}

}
