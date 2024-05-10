package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBase;

public class ABase implements IBase{
	
	private static final long serialVersionUID = 1L;
	private PositionI position;

	public ABase(PositionI position) {
		super();
		this.position = position;
	}
	
	@Override
	public PositionI getPosition() {
		return position;
	}

	@Override
	public PositionI eval(ExecutionStateI data) {
		return position;
	}

}
