package composants.noeud;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;


/**
 * <code>SensorNodeP2PInboundPort</code> extends {@link AbstractInboundPort} and implements
 * {@link SensorNodeP2PCI}, enabling node components to manage neighborhood connections and
 * facilitate distributed request execution among sensor nodes. This port supports operations such
 * as connection and disconnection requests between nodes, and handling synchronous and asynchronous
 * request executions.
 *
 * <p>This inbound port is crucial for implementing the peer-to-peer (P2P) functionalities of a sensor
 * network, allowing sensor nodes to dynamically adjust their network topology and share workload in
 * processing requests.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class SensorNodeP2PInboundPort extends AbstractInboundPort implements SensorNodeP2PCI{ 
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	public SensorNodeP2PInboundPort(ComponentI owner, String threadPoolURI) throws Exception{	
		
		// the implemented interface is statically known
		super(SensorNodeP2PCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;
	}
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
	            o -> {
	                ((Node) o).ask4Disconnection(neighbour);
	                return null;
	            }
	        );	
	}
	
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
	            o -> {
	                ((Node) o).ask4Connection(newNeighbour);
	                return null;
	            }
	        );	
	}
	
    /**
     * Executes a given request synchronously and returns the result.
     * This method handles direct request processing, providing immediate results back to the requester.
     *
     * @param request the continuation request to execute.
     * @return the result of the request execution.
     * @throws Exception if an error occurs during the execution of the request.
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Node)o).execute(request)
				);
	}
	
    /**
     * Executes a given request asynchronously.
     * This method schedules the request for asynchronous processing, allowing the node to handle
     * other tasks concurrently.
     *
     * @param requestContinuation the continuation request to execute asynchronously.
     * @throws Exception if an error occurs during the execution of the request.
     */
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Node)this.getTaskOwner()).executeAsync(requestContinuation) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		}
}
