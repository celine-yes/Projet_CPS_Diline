package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/**
 * The class <code>GeographicalZone</code> implements the methods
 * of the interface <code>GeographicalZoneI</code>, representing a rectangular geographical zone
 * defined by two corners: upper-left (p1) and lower-right (p2).
 * This class checks whether a given position is within the defined rectangle.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class GeographicalZone implements GeographicalZoneI {
	
	private static final long serialVersionUID = 1L;
	// rectangle
	private PositionI p1; // Upper-left corner
	private PositionI p2; // Lower-right corner
	
	public GeographicalZone(PositionI p1, PositionI p2) {
		super();
		Position point1 = (Position)p1;
		Position point2 = (Position)p2;
		this.p1 = new Position(point1.getX(), point1.getY());
		this.p2 = new Position(point2.getX(), point2.getY());
	}

	@Override
	public boolean in(PositionI p) {
		Position pos = (Position) p;
		Position pos1 = (Position) p1;
		Position pos2 = (Position) p2;

		return pos.getX() >= pos1.getX() && pos.getX() <= pos2.getX() && 
				pos.getY() >= pos1.getY() && pos.getY() <= pos2.getY();
	}

}
