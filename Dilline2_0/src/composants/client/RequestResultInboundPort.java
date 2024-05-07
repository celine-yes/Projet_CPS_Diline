package composants.client;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

/**
 * The class <code>RequestResultInboundPort</code> implements an inbound
 * port which implements the offered interface <code>RequestResultCI</code> so
 * that the provider can be called through this port.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class RequestResultInboundPort extends AbstractInboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;
	
	/**
     * The URI of the thread pool used for processing the incoming requests.
     */
	protected final String		threadPoolURI;
	
	/**
     * Constructs a {@code RequestResultInboundPort} with the specified owner and thread pool URI.
     * 
     * @param owner the component that owns this port; should not be {@code null}
     * @param threadPoolURI the URI of the thread pool to be used for processing the incoming requests
     * @throws Exception if the port cannot be properly initialized
     */
	public RequestResultInboundPort(ComponentI owner, String threadPoolURI) throws Exception{	
		
		// the implemented interface is statically known
		super(RequestResultCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;
	}
	
	/**
     * Processes the result of a request received through this port. This method schedules the processing
     * of the request result to be executed in a specific thread pool, ensuring that the handling of the result
     * does not interfere with the main operations of the component.
     *
     * @param requestURI the URI of the request for which the result is received
     * @param result the result of the request, encapsulated as a {@code QueryResultI}
     * @throws Exception if there is an issue in processing the result or scheduling the task
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
