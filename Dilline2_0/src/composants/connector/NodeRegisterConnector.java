package composants.connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class NodeRegisterConnector extends AbstractConnector implements RegistrationCI{

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.offering).register(nodeInfo);
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
