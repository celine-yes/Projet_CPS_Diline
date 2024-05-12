package langage.interfaces;

/**
 * Defines the gather functionality for queries in a sensor network.
 * This interface extends {@link IEvaluable} to provide methods specific to data gathering from sensors.
 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IGather extends IEvaluable{
	
	/**
     * Retrieves the identifier of the sensor from which data needs to be gathered.
     * This identifier is used to locate and query the sensor within the network.
     *
     * @return the sensor identifier as a {@link String}
     */
	public String getSensorId();
}
