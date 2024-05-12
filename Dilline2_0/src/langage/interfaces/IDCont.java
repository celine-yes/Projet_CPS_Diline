package langage.interfaces;

/**
 * Extends the {@code ICont} interface to provide specific functionalities for directional-based continuation
 * in sensor network queries.
 *
 * <p>This interface includes methods to access the directional settings and the maximum number of hops for the propagation.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IDCont extends ICont{
	
    /**
     * Retrieves the directions associated with this continuation.
     *
     * @return the directional settings of this continuation.
     */
	public IDirs getDirs();
	
	 /**
     * Retrieves the maximum number of hops allowed for the directional continuation.
     *
     * @return the maximum number of hops for the continuation.
     */
	public int getMaxSauts();
}
