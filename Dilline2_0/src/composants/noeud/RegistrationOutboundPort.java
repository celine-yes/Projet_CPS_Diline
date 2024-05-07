package composants.noeud;

import java.util.Set;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

/**
 * The class <code>RegistrationOutboundPort</code> implements an outbound
 * port which implements the required interface <code>RegistrationCI</code> so
 * that it can call its providers through this port.
 * This outbound port allows new nodes to register or unregister calling the register component.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

//permet aux nouveaux noeuds de s'enregistrer auprès du registre du réseau

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
