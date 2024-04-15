package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IBase{ 
	public PositionI eval(ExecutionStateI data);
	public PositionI getPosition();
}
