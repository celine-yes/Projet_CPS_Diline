package langage.interfaces;

/**
 * Extends the {@code ICont} interface to provide specific functionalities for flood-based continuation
 * in sensor network queries.
 *
 * <p>This interface includes methods to access the base component and the maximum distance for the flood propagation.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IFCont extends ICont{
	
	/**
     * Retrieves the initial base component associated with this continuation.
     *
     * @return the base component of the continuation.
     */
	public IBase getBase();
	 /**
     * Retrieves the maximum distance for the flood propagation.
     *
     * @return the maximum allowable distance for flood continuation.
     */
	public double getMaxDist();
}
