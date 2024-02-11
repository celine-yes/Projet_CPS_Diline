package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IABase;

public class ABase implements IABase{
	private String position;

	public ABase(String position) {
		super();
		this.position = position;
	}

	public String getPosition() {
		return position;
	}


	@Override
	public Object eval(ExecutionStateI data) {
		return position;
	}


}
