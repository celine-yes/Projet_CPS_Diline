package langage.interfaces;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public interface IRDirs extends IDirs{
	public Direction getDir();
	public Set<Direction> getDirs();
}
