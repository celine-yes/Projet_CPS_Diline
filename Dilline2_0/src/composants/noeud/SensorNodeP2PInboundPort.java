package composants.noeud;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

//permet de gérer les connexions de voisinage entre les noeuds

public class SensorNodeP2PInboundPort extends AbstractInboundPort implements SensorNodeP2PCI{ 
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	public SensorNodeP2PInboundPort(ComponentI owner, String threadPoolURI) throws Exception{	
		
		// the implemented interface is statically known
		super(SensorNodeP2PCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;
	}
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
	            o -> {
	                ((Node) o).ask4Disconnection(neighbour);
	                return null;
	            }
	        );	
	}
	
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
	            o -> {
	                ((Node) o).ask4Connection(newNeighbour);
	                return null;
	            }
	        );	
	}
	
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Node)o).execute(request)
				);
	}
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Node)this.getTaskOwner()).executeAsync(requestContinuation) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		}
}
