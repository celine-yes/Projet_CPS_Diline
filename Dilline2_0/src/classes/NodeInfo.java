package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class NodeInfo extends ConnectionInfo implements NodeInfoI {
	
	private static final long serialVersionUID = 1L;
	private PositionI nodePosition; 
	private BCM4JavaEndPointDescriptorI inboundP2P; 
	private double range;
	
	
	public NodeInfo(String nodeIdentifier, PositionI nodePosition, BCM4JavaEndPointDescriptorI inboundRequesting, BCM4JavaEndPointDescriptorI inboundP2P, double range) {
		super(nodeIdentifier, inboundRequesting);
		this.nodePosition = new Position(((Position) nodePosition).getX(), ((Position) nodePosition).getY());
		this.inboundP2P = inboundP2P;
		this.range = range;
}

	@Override
	public PositionI nodePosition() {
		return new Position(((Position) nodePosition).getX(), ((Position) nodePosition).getY());
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
