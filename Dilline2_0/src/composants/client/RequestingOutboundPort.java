package composants.client;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;


/**
 * The class <code>RequestingOutboundPort</code> implements an outbound
 * port which implements the required interface <code>RequestingCI</code> so
 * that it can call its providers through this port.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class RequestingOutboundPort extends AbstractOutboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	
	/**
     * Constructs a {@code RequestingOutboundPort} with the specified owner.
     * 
     * @param owner the component that owns this port; should not be {@code null}
     * @throws Exception if the port cannot be properly initialized
     */
	public RequestingOutboundPort(ComponentI owner) throws Exception {
        super(RequestingCI.class, owner);
    }
	
	/**
     * Executes a synchronous request through this port. This method forwards the request
     * to the connected service provider and awaits a response, encapsulated as a {@code QueryResultI}.
     *
     * @param request the {@code RequestI} to be executed
     * @return a {@code QueryResultI} containing the result of the request
     * @throws Exception if there is an issue executing the request or processing the result
     */
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		 return ((RequestingCI)this.getConnector()).execute(request);
	}
	
	/**
     * Executes an asynchronous request through this port. Unlike {@code execute}, 
     * this method does not wait for a response.
     *
     * @param request the {@code RequestI} to be executed asynchronously
     * @throws Exception if there is an issue sending the request
     */
	@Override
	public void executeAsync(RequestI request) throws Exception {
		((RequestingCI)this.getConnector()).executeAsync(request);
		
	}
}
