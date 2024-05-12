package withplugin.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import plugins.NodePlugin;

//permet de gérer les connexions de voisinage entre les noeuds

/**
 * The {@code SensorNodeP2PInboundPort} class extends {@link AbstractInboundPort} to provide
 * an implementation that handles peer-to-peer (P2P) interactions among sensor nodes in a sensor network.
 * This class implements the {@link SensorNodeP2PCI} interface, allowing it to handle connection requests,
 * disconnection notifications, and execution of requests both synchronously and asynchronously.
 *
 * <p>This inbound port is primarily used for managing network topology and propagating sensor data
 * or control messages across the network. It interacts with a {@link NodePlugin}, which encapsulates
 * the logic required to interact with other nodes in the network.</p>
 *
 * @param owner the component that owns this port; should be capable of handling sensor network P2P interactions.
 * @param threadPoolURI the identifier for the thread pool where network interaction tasks are executed.
 * @param pluginURI the URI of the plugin where the network interaction logic is defined.
 *
 * @throws Exception if there is an issue creating the port or if the initialization parameters are incorrect.
  * @author Dilyara Babanazarova
 * @author Celine Fan
 */
public class SensorNodeP2PInboundPort extends AbstractInboundPort implements SensorNodeP2PCI{ 
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	public SensorNodeP2PInboundPort(ComponentI owner, String threadPoolURI, String pluginURI) throws Exception{	
		
		// the implemented interface is statically known
		super(SensorNodeP2PCI.class, owner, pluginURI, null) ;
		this.threadPoolURI = threadPoolURI;
	}
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
				new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
					@Override
					public Void call() throws Exception{
						((NodePlugin) this.getServiceProviderReference()).ask4Disconnection(neighbour);
						return null;
					}
				});
	}
	
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
				threadPoolURI,
				new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
					@Override
					public Void call() throws Exception{
						((NodePlugin) this.getServiceProviderReference()).ask4Connection(newNeighbour);
						return null;
					}
				});
	}
	
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				new AbstractComponent.AbstractService<QueryResultI>(this.getPluginURI()) {
					@Override
					public QueryResultI call() throws Exception{
						return ((NodePlugin) this.getServiceProviderReference()).execute(request);
					}
				});
	}
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask(this.getPluginURI()) {
					@Override
					public void run() {
						try {
							((NodePlugin)this.getTaskProviderReference()).executeAsync(requestContinuation) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		}
}
