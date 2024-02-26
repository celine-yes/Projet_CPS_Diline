package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class NodeInfo implements NodeInfoI {
	
	private static final long serialVersionUID = 1L;
	private String nodeIdentifier;
	private PositionI nodePosition;
	private BCM4JavaEndPointDescriptorI inboundRequesting; 
	private BCM4JavaEndPointDescriptorI inboundP2P; 
	private double range;
	
	
	public NodeInfo(String nodeIdentifier, PositionI nodePosition, BCM4JavaEndPointDescriptorI inboundRequesting, BCM4JavaEndPointDescriptorI inboundP2P, double range) {
		super();
		this.nodeIdentifier = nodeIdentifier;
		this.nodePosition = nodePosition;
		this.inboundRequesting = inboundRequesting;
		this.inboundP2P = inboundP2P;
		this.range = range;
}

	@Override
	public String nodeIdentifier() {
		return nodeIdentifier;
	}

	@Override
	public EndPointDescriptorI endPointInfo() {
		return inboundRequesting;
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
		return inboundP2P;
	}

}
