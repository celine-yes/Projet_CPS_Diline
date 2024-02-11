package langage.ast;


import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICexp;
import langage.interfaces.ICexpBexp;

public class CexpBexp implements ICexpBexp{
	private ICexp cexp;

	public CexpBexp(ICexp cexp) {
		super();
		this.cexp = cexp;
	}

	public ICexp getCexp() {
		return cexp;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		
		return cexp.eval(data);
	}

}
