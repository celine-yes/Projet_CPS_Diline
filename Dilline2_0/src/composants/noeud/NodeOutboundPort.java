package composants.noeud;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.examples.basic_cs.interfaces.URIConsumerCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class NodeOutboundPort extends AbstractOutboundPort implements RequestResultCI, RegistrationCI{
	private static final long serialVersionUID = 1L;

	public NodeOutboundPort(String uri,ComponentI owner) throws Exception{
			super(uri, URIConsumerCI.class, owner) ;
			assert	uri != null && owner != null ;
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
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
