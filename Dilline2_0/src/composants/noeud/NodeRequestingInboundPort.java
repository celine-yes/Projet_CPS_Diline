package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class NodeRequestingInboundPort extends AbstractInboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	
	// TODO A revoir les constructeurs des ports
	public NodeRequestingInboundPort(String uri,ComponentI owner) throws Exception{
		
		// the implemented interface is statically known
		super(uri, RequestingCI.class, owner) ;

	}
	
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return this.getOwner().handleRequest(o -> ((Node)o).execute(request));
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
