package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class NodeInfo implements NodeInfoI {
	
	private static final long serialVersionUID = 1L;
	private String nodeIdentifier;
	private PositionI nodePosition;
	private BCM4JavaEndPointDescriptorI uri_client; 
	private BCM4JavaEndPointDescriptorI uri_voisin; 
	private double range;
	
	public NodeInfo(String nodeIdentifier, PositionI nodePosition, BCM4JavaEndPointDescriptorI uri_client, BCM4JavaEndPointDescriptorI uri_voisin, double range) {
		super();
		this.nodeIdentifier = nodeIdentifier;
		this.nodePosition = nodePosition;
		this.uri_client = uri_client;
		this.uri_voisin = uri_voisin;
		this.range = range;
}

	@Override
	public String nodeIdentifier() {
		return nodeIdentifier;
	}

	@Override
	public EndPointDescriptorI endPointInfo() {
		return uri_client;
	}

	@Override
	public PositionI nodePosition() {
		return nodePosition;
	}

	@Override
	public double nodeRange() {
		return range;
	}

	@Override
	public EndPointDescriptorI p2pEndPointInfo() {
		return uri_voisin;
	}

}
