package composants.client;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class ClientRequestResultInboundPort extends AbstractInboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;

	public ClientRequestResultInboundPort(String uri,ComponentI owner) throws Exception{	
		// the implemented interface is statically known
		super(uri, RequestResultCI.class, owner) ;
	}

	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
