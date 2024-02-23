package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class NodeSensorNodeP2PInboundPort extends AbstractInboundPort implements SensorNodeP2PCI{ 
	
	public NodeSensorNodeP2PInboundPort(String uri,ComponentI owner) throws Exception{	
		// the implemented interface is statically known
		super(uri, SensorNodeP2PCI.class, owner) ;
	}
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
