package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class GeographicalZone implements GeographicalZoneI {
	
	private static final long serialVersionUID = 1L;
	// rectangle
	private PositionI p1; //coin sup gauche
	private PositionI p2; //coin inf droit
	
	public GeographicalZone(PositionI p1, PositionI p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
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
