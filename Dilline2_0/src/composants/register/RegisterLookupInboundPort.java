package composants.register;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class RegisterLookupInboundPort extends AbstractInboundPort implements LookupCI {

	private static final long serialVersionUID = 1L;

	public RegisterLookupInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, LookupCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return this.getOwner().handleRequest(o -> ((Register)o).findByIdentifier(sensorNodeId));
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return this.getOwner().handleRequest(o -> ((Register)o).findByZone(z));
	}

}
