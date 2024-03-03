package composants.client;

import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class ClientLookupOutboundPort extends AbstractOutboundPort implements LookupCI{
	
	private static final long serialVersionUID = 1L;

	public ClientLookupOutboundPort(String uri, AbstractComponent owner) throws Exception {
        super(uri, LookupCI.class, owner);
    }

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.getConnector()).findByIdentifier(sensorNodeId);
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.getConnector()).findByZone(z);
	}
}
