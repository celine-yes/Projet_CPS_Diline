package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

/**
 * <code>RequestResultOutboundPort</code> extends {@link AbstractOutboundPort} and implements
 * {@link RequestResultCI}, enabling node components to send asynchronous request results back to
 * the client component.
 *
 * <p>This outbound port is typically used in distributed sensor networks where nodes process
 * data asynchronously and must report the results back to a central or initiating client. It supports
 * efficient, decoupled communication between distributed components.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class RequestResultOutboundPort extends AbstractOutboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;
	
    /**
     * Constructs a <code>RequestResultOutboundPort</code> associated with the given owner.
     *
     * @param owner the component that owns this port, typically a node component.
     * @throws Exception if the port cannot be created due to an internal error.
     */
	public RequestResultOutboundPort(ComponentI owner) throws Exception{
		super(RequestResultCI.class, owner) ;
	}
	
    /**
     * Sends the result of an asynchronous request to the client that initiated the request.
     *
     * @param requestURI the URI of the request for which results are being sent.
     * @param result the result of the request, encapsulated within a {@link QueryResultI}.
     * @throws Exception if there is an error in transmitting the result to the client.
     */
	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		((RequestResultCI)this.getConnector()).acceptRequestResult(requestURI, result);
	}
}
