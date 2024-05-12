package composants.noeud;

import java.util.Set;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

/**
 * <code>RegistrationOutboundPort</code> extends {@link AbstractOutboundPort} and implements
 * {@link RegistrationCI} to enable sensor nodes to communicate with a registration component.
 * This port facilitates operations like node registration, unregistration, and neighborhood searches
 * through method invocations on connected registration service providers.
 *
 * <p><strong>Usage:</strong> This port is typically used by sensor node components to dynamically manage
 * their participation in the sensor network by registering, unregistering, or finding potential neighbors.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class RegistrationOutboundPort extends AbstractOutboundPort implements RegistrationCI{
	private static final long serialVersionUID = 1L;

	public RegistrationOutboundPort(ComponentI owner) throws Exception{
			super(RegistrationCI.class, owner) ;
		}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return ((RegistrationCI)this.getConnector()).registered(nodeIdentifier);
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.getConnector()).register(nodeInfo);
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return ((RegistrationCI)this.getConnector()).findNewNeighbour(nodeInfo, d);

	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.getConnector()).unregister(nodeIdentifier);
		
	}

}
