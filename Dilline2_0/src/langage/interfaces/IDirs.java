package langage.interfaces;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

/**
 * Represents directional information in sensor network queries.
 * This interface extends {@link IEvaluable} to provide methods specific to handling directions
 * in queries, which can be crucial for defining how queries propagate through the network.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public interface IDirs extends IEvaluable{
	
	/**
     * Retrieves the specific direction associated with this directional data.
     * Directions are typically used in sensor network queries to specify how information
     * should flow or be restricted within the network topology.
     *
     * @return the direction as an enum {@link Direction}
     */
	public Direction getDir();
}
