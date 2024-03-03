package composants.register;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class RegisterRegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {
	
	private static final long serialVersionUID = 1L;

	public RegisterRegistrationInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.getOwner().handleRequest(o -> ((Register)o).registered(nodeIdentifier));

	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return this.getOwner().handleRequest(o -> ((Register)o).register(nodeInfo));
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
}
