package langage.ast;


import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;
import langage.interfaces.ICexp;

public class CexpBexp implements IBexp{
	private static final long serialVersionUID = 1L;
	
	private ICexp cexp;

	public CexpBexp(ICexp cexp) {
		super();
		this.cexp = cexp;
	}

	public ICexp getCexp() {
		return cexp;
	}

	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		return cexp.eval(data);
	}

}
