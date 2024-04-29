package withplugin.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import plugins.NodePlugin;

//permet aux composants clients d'envoyer des requêtes aux noeuds du réseau

public class RequestingInboundPort extends AbstractInboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	//A revoir les constructeurs des ports
	public RequestingInboundPort(ComponentI owner, String threadPoolURI, String pluginURI) throws Exception{
		
		
		// the implemented interface is statically known
		super(RequestingCI.class, owner, pluginURI, null) ;
		this.threadPoolURI = threadPoolURI;

	}
	
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
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
	public void executeAsync(RequestI request) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask(this.getPluginURI()) {
					@Override
					public void run() {
						try {
							((NodePlugin)this.getTaskProviderReference()).executeAsync(request) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		
	}
	
}
