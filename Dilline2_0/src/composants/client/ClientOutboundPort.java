package composants.client;

import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class ClientOutboundPort extends AbstractOutboundPort implements RequestingCI, LookupCI{
	
	private static final long serialVersionUID = 1L;

	public ClientOutboundPort(String uri, AbstractComponent owner) throws Exception {
        super(uri, RequestingCI.class, owner);
    }

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		 return ((RequestingCI)this.getConnector()).execute(request);
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Partie Async
		
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
