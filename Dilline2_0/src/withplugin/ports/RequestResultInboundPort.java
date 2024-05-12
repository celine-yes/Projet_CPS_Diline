package withplugin.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import withplugin.composants.Client;

/**
 * The {@code RequestResultInboundPort} class provides an implementation for an inbound port
 * that handles results of requests, typically from sensor nodes to a client component in a sensor network.
 * It extends {@link AbstractInboundPort} and implements the {@link RequestResultCI} component interface,
 * providing a mechanism for receiving query results asynchronously.
 *
 * <p>This inbound port is designed to be attached to components that need to process the results
 * of asynchronous requests sent to sensor nodes. It delegates the handling of incoming results
 * to the component's specific implementation, ensuring that results are processed in the proper
 * context of the component's state and logic.</p>
 * @param owner the component that owns this port, expected to be of type {@link Client}
 *              or another component that can handle {@link QueryResultI} objects.
 * @param threadPoolURI the identifier for the thread pool where tasks will be executed,
 *                      allowing for result handling to be offloaded from the main thread.
 *
 * @throws Exception if there is an issue creating the port or setting up its configuration.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class RequestResultInboundPort extends AbstractInboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;

	public RequestResultInboundPort(ComponentI owner, String threadPoolURI) throws Exception{	
		
		// the implemented interface is statically known
		super(RequestResultCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;
	}
	
	   /**
     * Accepts and processes the result of a request identified by its URI. The method schedules
     * the processing of the result on the component's thread pool, ensuring that the component's
     * main thread remains responsive.
     *
     * @param requestURI the unique identifier of the request for which the result is reported.
     * @param result the result of the request, encapsulated in a {@link QueryResultI} object.
     * @throws Exception if there is an error in processing the result or scheduling the task.
     */
	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Client)this.getTaskOwner()).acceptRequestResult(requestURI, result) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		
	}
	
}
