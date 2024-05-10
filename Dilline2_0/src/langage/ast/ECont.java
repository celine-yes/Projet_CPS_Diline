package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IECont;

public class ECont implements IECont{

	private static final long serialVersionUID = 1L;

	@Override
	public Object eval(ExecutionStateI data) {
		return new ArrayList<String>();
	}
	
}
