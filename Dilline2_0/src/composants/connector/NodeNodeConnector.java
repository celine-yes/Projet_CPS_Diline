package composants.connector;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

/**
 * The {@code NodeNodeConnector} class extends {@link AbstractConnector} and implements
 * {@link SensorNodeP2PCI}. It provides functionality for sensor nodes to interact directly with
 * each other, handling connections, disconnections, and the execution of requests in both
 * synchronous and asynchronous modes.
 *
 * <p>This connector supports the peer-to-peer interactions necessary for managing the distributed
 * sensor network environment, where nodes need to dynamically connect and disconnect as well as
 * process requests that require collaborative efforts among multiple nodes.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class NodeNodeConnector extends AbstractConnector implements SensorNodeP2PCI{
	
    /**
     * Requests a disconnection from a neighbor node. This method allows a node to
     * safely disconnect from its neighbor, ensuring any state related to the connection
     * is cleanly updated.
     *
     * @param neighbour the node information of the neighbor from which to disconnect
     * @throws Exception if there is an issue executing the disconnection
     */
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		((SensorNodeP2PCI)this.offering).ask4Disconnection(neighbour);

	}
	
    /**
     * Requests a new connection to a prospective neighbor node. This method enables dynamic
     * network topology changes by allowing nodes to connect to new neighbors as needed.
     *
     * @param newNeighbour the node information of the new neighbor to connect to
     * @throws Exception if there is an issue executing the connection
     */
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		((SensorNodeP2PCI)this.offering).ask4Connection(newNeighbour);
	}
	
    /**
     * Executes a request in a synchronous manner, expecting a direct response from the
     * node receiving the request.
     *
     * @param request the continuation of the request to be processed
     * @return the result of the request execution
     * @throws Exception if there is an error during the request execution
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return ((SensorNodeP2PCI)this.offering).execute(request);
	}
	
    /**
     * Initiates the asynchronous execution of a request.
     *
     * @param requestContinuation the continuation of the request to be processed asynchronously
     * @throws Exception if there is an error initiating the asynchronous processing
     */
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		((SensorNodeP2PCI)this.offering).executeAsync(requestContinuation);
		
	}


}
