package composants.client;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class ClientInboundPort extends AbstractInboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;

	public ClientInboundPort(String uri, AbstractComponent owner) throws Exception {
        super(uri, RequestingCI.class, owner);
    }

	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
