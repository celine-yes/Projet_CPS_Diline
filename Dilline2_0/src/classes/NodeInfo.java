package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/**
 * The class <code>NodeInfo</code> implements the methods
 * of the interface <code>NodeInfoI</code> extends from the interface <code>ConnectionInfo</code> 
 * including set methods.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

public class NodeInfo extends ConnectionInfo implements NodeInfoI {
	
	private static final long serialVersionUID = 1L;
	private PositionI nodePosition; 
	private BCM4JavaEndPointDescriptorI inboundP2P; 
	private double range;
	
	
	public NodeInfo(String nodeIdentifier, PositionI nodePosition, double range) {
		super(nodeIdentifier);
		this.nodePosition = new Position(((Position) nodePosition).getX(), ((Position) nodePosition).getY());
		this.inboundP2P = null;
		this.range = range;
	}
	
	public void setInboundPorts(BCM4JavaEndPointDescriptorI inboundRequesting, BCM4JavaEndPointDescriptorI inboundP2P) {
		super.setEndPointInfo(inboundRequesting);
		this.inboundP2P = inboundP2P;
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
