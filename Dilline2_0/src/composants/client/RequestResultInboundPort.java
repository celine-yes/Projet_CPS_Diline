package composants.client;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class RequestResultInboundPort extends AbstractInboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;

	public RequestResultInboundPort(ComponentI owner, String threadPoolURI) throws Exception{	
		
		// the implemented interface is statically known
		super(RequestResultCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;
	}

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
