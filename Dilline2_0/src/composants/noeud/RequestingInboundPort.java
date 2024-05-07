package composants.noeud;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;


/**
 * The class <code>RequestingInboundPort</code> implements an inbound
 * port which implements the offered interface <code>RequestingCI</code> so
 * that the provider can be called through this port.
 * This InboundPort allows client components to send requests to node components
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class RequestingInboundPort extends AbstractInboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	//A revoir les constructeurs des ports
	public RequestingInboundPort(ComponentI owner, String threadPoolURI) throws Exception{
		
		
		// the implemented interface is statically known
		super(RequestingCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;

	}
	
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Node)o).execute(request));
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Node)this.getTaskOwner()).executeAsync(request) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		
	}
	
}
