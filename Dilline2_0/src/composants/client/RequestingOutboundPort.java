package composants.client;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class RequestingOutboundPort extends AbstractOutboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;

	public RequestingOutboundPort(AbstractComponent owner) throws Exception {
        super(RequestingCI.class, owner);
    }

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		 return ((RequestingCI)this.getConnector()).execute(request);
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		((RequestingCI)this.getConnector()).executeAsync(request);
		
	}

	
}
