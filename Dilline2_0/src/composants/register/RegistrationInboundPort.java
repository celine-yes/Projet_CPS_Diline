package composants.register;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


/**
 * The class <code>RegistrationInboundPort</code> implements an inbound
 * port which implements the offered interface <code>RegistrationCI</code> so
 * that the provider can be called through this port.
 * This InboundPort allows the register component to receive a request from a node 
 * component to register or unregister from the network.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;

	public RegistrationInboundPort(String uri,ComponentI owner, String threadPoolURI) throws Exception {
		
		super(uri, RegistrationCI.class, owner);
		this.threadPoolURI = threadPoolURI;
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Register)o).registered(nodeIdentifier));

	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Register)o).register(nodeInfo));
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Register)o).findNewNeighbour(nodeInfo, d));
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
	            o -> {
	                ((Register) o).unregister(nodeIdentifier);
	                return null;
	            }
	        );	
	}
}
