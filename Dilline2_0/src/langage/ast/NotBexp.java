package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;
import langage.interfaces.INotBexp;

public class NotBexp implements INotBexp{
	private IBexp bexp;

	public NotBexp(IBexp bexp) {
		super();
		this.bexp = bexp;

	}
	public IBexp getBexp() {
		return bexp;
	}
	

	@Override
	public Object eval(ExecutionStateI data) {
		boolean bexpResult = (boolean) bexp.eval(data);

        return !bexpResult;
	}

}
