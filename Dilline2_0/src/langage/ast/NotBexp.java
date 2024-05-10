package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;

public class NotBexp implements IBexp{
	
	private static final long serialVersionUID = 1L;
	private IBexp bexp;

	public NotBexp(IBexp bexp) {
		super();
		this.bexp = bexp;

	}
	public IBexp getBexp() {
		return bexp;
	}
	

	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		boolean bexpResult = bexp.eval(data);

        return !bexpResult;
	}

}
