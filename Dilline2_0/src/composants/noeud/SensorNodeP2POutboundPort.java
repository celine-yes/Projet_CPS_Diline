package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;


/**
 * <code>SensorNodeP2POutboundPort</code> extends {@link AbstractOutboundPort} and implements
 * {@link SensorNodeP2PCI}, enabling node components to interact with other node components
 * over the network. This outbound port facilitates managing peer-to-peer connections and handling
 * both synchronous and asynchronous requests in a distributed sensor network.
 *
 * <p>This outbound port serves as a communication gateway for a node to initiate connections,
 * request disconnections, and execute requests with its neighboring nodes, supporting dynamic
 * topology changes and distributed processing.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */


public class SensorNodeP2POutboundPort extends AbstractOutboundPort implements SensorNodeP2PCI{ 
	
	private static final long serialVersionUID = 1L;

	public SensorNodeP2POutboundPort(ComponentI owner) throws Exception{
		super(SensorNodeP2PCI.class, owner) ;
		assert	uri != null && owner != null ;
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Disconnection(neighbour);

		
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Connection(newNeighbour);

		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return ((SensorNodeP2PCI)this.getConnector()).execute(request);

	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).executeAsync(requestContinuation);
		
	}
}
