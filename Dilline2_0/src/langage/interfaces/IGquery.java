package langage.interfaces;

/**
 * Represents a gather query in the sensor network query language.
 * It is used for collecting data from specified sensors within the network.
 * This interface extends {@link QueryI} to focus on data collection functionalities.

 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IGquery extends QueryI{
	
    /**
     * Retrieves the gather component of the query, which specifies the details
     * about how sensor data should be collected.
     *
     * @return the gather component as {@link IGather}
     */
	public IGather getGather();
}
