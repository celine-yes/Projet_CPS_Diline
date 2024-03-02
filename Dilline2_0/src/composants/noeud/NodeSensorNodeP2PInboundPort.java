package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

//permet de gérer les connexions de voisinage entre les noeuds

public class NodeSensorNodeP2PInboundPort extends AbstractInboundPort implements SensorNodeP2PCI{ 
	
	private static final long serialVersionUID = 1L;
	
	public NodeSensorNodeP2PInboundPort(String uri,ComponentI owner) throws Exception{	
		// the implemented interface is statically known
		super(uri, SensorNodeP2PCI.class, owner) ;
	}
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
	            o -> {
	                ((Node) o).ask4Disconnection(neighbour);
	                return null;
	            }
	        );	
	}
	
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
	            o -> {
	                ((Node) o).ask4Connection(newNeighbour);
	                return null;
	            }
	        );	
	}
	
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((Node)o).execute(request)
				);
	}
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
