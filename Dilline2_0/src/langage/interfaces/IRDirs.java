package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public interface IRDirs extends IDirs{
	public Direction getDir();
	public IDirs getDirs();
}
