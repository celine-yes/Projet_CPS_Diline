package composants.connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

/**
 * The {@code NodeRegisterConnector} class extends {@link AbstractConnector} and implements
 * {@link RegistrationCI}. It facilitates interaction with the sensor network registry, supporting
 * operations such as checking registration status, registering new nodes, finding potential new
 * neighbors, and unregistering nodes.
 *
 * <p>This connector handles the administrative tasks required to manage the dynamic nature of
 * sensor networks, where nodes may frequently join or leave the network.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class NodeRegisterConnector extends AbstractConnector implements RegistrationCI{
	
    /**
     * Checks if the node with the given identifier is already registered in the network.
     *
     * @param nodeIdentifier the identifier of the node to check
     * @return true if the node is already registered; false otherwise
     * @throws Exception if there is an issue accessing the registration status
     */
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return ((RegistrationCI)this.offering).registered(nodeIdentifier);
	}
	
    /**
     * Registers a new node in the network and returns a set of potential neighbors based on
     * the node's information and network topology.
     *
     * @param nodeInfo the information about the node to be registered
     * @return a set of node information for potential neighbors
     * @throws Exception if there is an issue with registering the node
     */
	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.offering).register(nodeInfo);
	}
	
    /**
     * Finds a new neighbor for the node in a specified direction.
     *
     * @param nodeInfo the node seeking a new neighbor
     * @param d the direction in which the neighbor is sought
     * @return the node information of a potential new neighbor or null if no suitable neighbor is found
     * @throws Exception if there is an issue finding a new neighbor
     */
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return ((RegistrationCI)this.offering).findNewNeighbour(nodeInfo, d);

	}
	
    /**
     * Unregisters a node from the network using its identifier.
     *
     * @param nodeIdentifier the identifier of the node to be unregistered
     * @throws Exception if there is an issue with unregistering the node
     */
	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.offering).unregister(nodeIdentifier);
		
	}

}
