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
	    double dx = pos.getX() - x;
	    double dy = pos.getY() - y;

	    if (dx == 0 && dy == 0) {
	        return null; // no direction
	    }

	    if (dx == 0) {
	        // vertical alignment
	        if (dy > 0) {
	            return y > pos.getY() ? Direction.SW : Direction.NW;
	        } else {
	            return y > pos.getY() ? Direction.SE : Direction.NE;
	        }
	    } else if (dy == 0) {
	        // horizontal alignment
	        if (dx > 0) {
	            return x > pos.getX() ? Direction.NW : Direction.NE;
	        } else {
	            return x > pos.getX() ? Direction.SW : Direction.SE;
	        }
	    } else {
	        if (dx > 0) {
	            if (dy > 0) {
	                return Direction.NE; 
	            } else {
	                return Direction.SE; 
	            }
	        } else {
	            if (dy > 0) {
	                return Direction.NW; 
	            } else {
	                return Direction.SW;
	            }
	        }
	    }
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
