package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IBase extends Serializable{ 
	public PositionI eval(ExecutionStateI data);
	public PositionI getPosition();
}
