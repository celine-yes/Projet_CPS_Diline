package langage.interfaces;

/**
 * Represents a boolean query in the sensor network query language.
 * It encapsulates a boolean expression and a continuation for processing the query.
 * This interface extends {@link QueryI} to provide specific functionalities for boolean queries.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IBquery extends QueryI{
	
	 /**
     * Retrieves the boolean expression associated with this query.
     * This expression determines the boolean logic to be applied on sensor data.
     *
     * @return the boolean expression as {@link IBexp}
     */
	public IBexp getBexp();
	
	/**
     * Retrieves the continuation part of the query that dictates subsequent query processing.
     * This defines how the query results are processed or propagated within the network.
     *
     * @return the continuation as {@link ICont}
     */
	public ICont getCont();
	
}

