package composants.register;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

/**
 * The class <code>LookupInboundPort</code> implements an inbound
 * port which implements the offered interface <code>LookupCI</code> so
 * that the provider can be called through this port.
 * This InboundPort allows the register component to receive a request from a client 
 * component to search through the nodes network and extract one node so the client component
 * can send its request to.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class LookupInboundPort extends AbstractInboundPort implements LookupCI {

	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;

	public LookupInboundPort(String uri,ComponentI owner, String threadPoolURI) throws Exception {
		super(uri, LookupCI.class, owner);
		this.threadPoolURI = threadPoolURI;
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Register)o).findByIdentifier(sensorNodeId));
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Register)o).findByZone(z));
	}

}
