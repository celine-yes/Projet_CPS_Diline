package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class Position implements PositionI {
	
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public double distance(PositionI p) {
		return Math.sqrt(Math.pow(((Position) p).getX() - x, 2) + Math.pow(((Position) p).getY() - y, 2));
	}

	@Override
	public Direction directionFrom(PositionI p) {
		Position pos = (Position) p;
		if (pos.getX() > x && pos.getY() > y ) {
			return Direction.NE;
		}
		else if (pos.getX() < x && pos.getY() > y ) {
			return Direction.NW;
		}
		else if (pos.getX() < x && pos.getY() < y ) {
			return Direction.SW;
		}

		return Direction.SE;
	}

	@Override
	public boolean northOf(PositionI p) {
		return y < ((Position) p).getY();
	}

	@Override
	public boolean southOf(PositionI p) {
		return y > ((Position) p).getY();
	}

	@Override
	public boolean eastOf(PositionI p) {
		return x > ((Position) p).getX();
	}

	@Override
	public boolean westOf(PositionI p) {
		return x < ((Position) p).getX();
	}

}
