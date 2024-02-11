package composants.register;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class RegisterInboundPort extends AbstractInboundPort implements LookupCI, RegistrationCI {

	public RegisterInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
		
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
