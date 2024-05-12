package classes.utils;

import java.util.List;

import classes.Request;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import langage.ast.ABase;
import langage.ast.BQuery;
import langage.ast.CRand;
import langage.ast.DCont;
import langage.ast.ECont;
import langage.ast.FCont;
import langage.ast.FDirs;
import langage.ast.FGather;
import langage.ast.GQuery;
import langage.ast.GeqCexp;
import langage.ast.RBase;
import langage.ast.RDirs;
import langage.ast.RGather;
import langage.ast.SRand;
import langage.interfaces.IBase;
import langage.interfaces.IBexp;
import langage.interfaces.ICont;
import langage.interfaces.IDCont;
import langage.interfaces.IDirs;
import langage.interfaces.IFCont;
import langage.interfaces.IGather;
import langage.interfaces.QueryI;


/**
 * The {@code RequestFactory} class provides static methods for creating different types of requests for sensor networks.
 * Each method in this class facilitates the creation of a request with specific characteristics, including boolean requests
 * and gather requests, which may include various continuation strategies such as empty, flooding, or directional continuations.
 *
 * <p> This class is designed to simplify the creation of request objects that can be used to interact with sensor nodes
 * within a network based on specific conditions and directives. The requests created by this factory are used to query
 * sensor nodes for data under specified conditions and may direct how the data is to be propagated through the network.
 *
 * <p> Usage of this class involves calling static methods that return instances of {@link RequestI}, each configured with
 * a unique query based on the provided parameters.
 *
 * <p> Key features include:
 * <ul>
 * <li>Methods to create boolean and gather requests with empty continuations.
 * <li>Methods to create boolean and gather requests with flooding continuations.
 * <li>Methods to create boolean and gather requests with directional continuations.
 * </ul>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class RequestFactory {
	
	/**
     * A counter used to generate unique request URIs incrementally.
     */
	public static int compteur = 0;
	
	
	/**
     * Converts a list of direction strings to a {@link IDirs} object representing a series of directions.
     * 
     * @param directionStrings a list of direction names that need to be converted into {@link IDirs}
     * @return an {@link IDirs} object representing the specified directions
     */
	private static IDirs createDirsFromList(List<String> directionStrings) {
        IDirs dirs = null;
        for (String dir : directionStrings) {
            Direction direction = Direction.valueOf(dir); 
            if (dirs == null) {
                dirs = new FDirs(direction);
            } else {
                dirs = new RDirs(direction, dirs);
            }
        }
        return dirs;
    }
	
	
	private static IGather createGatherChainFromSensorIds(List<String> sensorIds) {
        IGather gather = new FGather(sensorIds.get(sensorIds.size() - 1));
        for (int i = sensorIds.size() - 2; i >= 0; i--) {
            gather = new RGather(sensorIds.get(i), gather);
        }    
        return gather;
    }
	

	/** -------------------------------------------------------------------------------------------------------------------------------------- **/
    
	 /**
     * Creates a boolean request with an empty continuation.
     *
     * @param sensorId the sensor identifier
     * @param value the threshold value for comparison
     * @return a {@link RequestI} configured with a boolean expression and an empty continuation
     */    
	public static RequestI createBooleanRequestWithECont(String sensorId, double value) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(value));
        ICont econt = new ECont();
        QueryI query = new BQuery(econt, bexp);
        return new Request("request" + (compteur++), query);
    }
	
	
    
	/**
     * Creates a gather request with an empty continuation.
     *
     * @param rgather the gather request configurationRGather
     * @return a {@link RequestI} configured with a gather query and an empty continuation
     */
	public static RequestI createGatherRequestWithECont(List<String> sensorIds) {
        ICont econt = new ECont();
        IGather rgather = createGatherChainFromSensorIds(sensorIds);
        QueryI query = new GQuery(econt, rgather);
        return new Request("request" + (compteur++), query);
    }

    
	/** -------------------------------------------------------------------------------------------------------------------------------------- **/

    /**
     * Creates a boolean flooding request using specific base
     *
     * @param sensorId the sensor identifier
     * @param value the threshold value for comparison
     * @param base the base of flooding defined , either ABase or RBase
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a boolean condition
     */ 
    private static RequestI createFloodingBooleanRequest(String sensorId, double value, IBase base, double dist) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(value));
        IFCont fcont = new FCont(base, dist);
        QueryI query = new BQuery(fcont, bexp);
        return new Request("request" + (compteur++), query);
    }
    
    
    /**
     * Creates a boolean flooding request using an anchor-based (absolute) base.
     *
     * @param sensorId the sensor identifier
     * @param value the threshold value for comparison
     * @param position the position representing the base of flooding
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a boolean condition
     */
    public static RequestI createBooleanFloodingRequestWithABase(String sensorId, double value, PositionI position, double dist) {
        IBase base = new ABase(position);
        return createFloodingBooleanRequest(sensorId, value, base, dist);
    }
    
    
    /**
     * Creates a boolean flooding request using a relative base.
     *
     * @param sensorId the sensor identifier
     * @param value the threshold value for comparison
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a boolean condition
     */
    public static RequestI createBooleanFloodingRequestWithRBase(String sensorId, double value, double dist) {
        IBase base = new RBase();
        return createFloodingBooleanRequest(sensorId, value, base, dist);
    }
    
    /**
     * Creates a gather flooding request using specific base
     *
     * @param rgather the gather request configuration
     * @param base the base of flooding defined , either ABase or RBase
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a gather condition
     */    
    private static RequestI createFloodingGatherRequest(List<String> sensorIds, IBase base, double dist) {
        IFCont fcont = new FCont(base, dist);
        IGather rgather = createGatherChainFromSensorIds(sensorIds);
        QueryI query = new GQuery(fcont, rgather);
        return new Request("request" + (compteur++), query);
    }
    
    /**
     * Creates a gather flooding request using anchor-based (absolute) base
     *
     * @param rgather the gather request configuration
     * @param position the position representing the base of flooding
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a gather condition
     */
    public static RequestI createGatherFloodingRequestWithABase(List<String> sensorIds, PositionI position, double dist) {
        IBase base = new ABase(position);
        return createFloodingGatherRequest(sensorIds, base, dist);
    }
    
    /**
     * Creates a gather flooding request using a relative base.
     *
     * @param rgather the gather request configuration
     * @param dist the distance for the flooding range
     * @return a {@link RequestI} configured for flooding based on a gather condition
     */
    public static RequestI createGatherFloodingRequestWithRBase(List<String> sensorIds, double dist) {
        IBase base = new RBase();
        return createFloodingGatherRequest(sensorIds, base, dist);
    }
    
    
	/** -------------------------------------------------------------------------------------------------------------------------------------- **/


    /**
     * Creates a boolean request with a directional continuation.
     *
     * @param sensorId the sensor identifier
     * @param threshold the threshold value for the sensor reading
     * @param directions a list of directions specifying the propagation path
     * @param maxHops the maximum number of hops for the request propagation
     * @return a {@link RequestI} configured with a boolean expression and directional continuation
     */
    public static RequestI createBooleanRequestWithDCont(String sensorId, double threshold, List<String> directions, int maxHops) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(threshold));
        IDirs dirs = createDirsFromList(directions);
        IDCont dcont = new DCont(dirs, maxHops);
        QueryI query = new BQuery(dcont, bexp);
        return new Request("request" + (compteur++), query);
    }

    /**
     * Creates a gather request with a directional continuation.
     *
     * @param sensorId the sensor identifier
     * @param threshold the threshold value for the sensor reading
     * @param directions a list of directions specifying the propagation path
     * @param maxHops the maximum number of hops for the request propagation
     * @return a {@link RequestI} configured with a boolean expression and directional continuation
     */
    public static RequestI createGatherRequestWithDCont(List<String> sensorIds, List<String> directions, int maxHops) {
        IDirs dirs = createDirsFromList(directions);
        IDCont dcont = new DCont(dirs, maxHops);
        IGather rgather = createGatherChainFromSensorIds(sensorIds);
        QueryI query = new GQuery(dcont, rgather);
        return new Request("request" + (compteur++), query);
    }
}