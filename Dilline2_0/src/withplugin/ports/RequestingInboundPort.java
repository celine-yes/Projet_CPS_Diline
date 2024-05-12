package withplugin.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import plugins.NodePlugin;


/** The {@code RequestingInboundPort} class provides an implementation for an inbound port
* that handles requests from client components to node components in a sensor network.
* It extends {@link AbstractInboundPort} and implements the {@link RequestingCI} component interface,
* providing a mechanism for receiving and processing requests asynchronously and synchronously.
*
* <p>This inbound port is designed to be attached to node components that are capable of processing
* sensor network requests. It leverages the node's {@link NodePlugin} to handle the actual request processing,
* ensuring that request processing logic is encapsulated within the plugin rather than the port itself.</p>
*
* @param owner the component that owns this port, which must implement {@link RequestingCI} and ideally be a node.
* @param threadPoolURI the identifier for the thread pool where tasks related to request processing will be executed.
* @param pluginURI the URI of the plugin where the actual request processing logic is defined.
*
* @throws Exception if there is an issue creating the port or if the configuration is incorrect.
* 
* @author Dilyara Babanazarova
* @author Céline Fan
*/
public class RequestingInboundPort extends AbstractInboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	//A revoir les constructeurs des ports
	public RequestingInboundPort(ComponentI owner, String threadPoolURI, String pluginURI) throws Exception{
		
		
		// the implemented interface is statically known
		super(RequestingCI.class, owner, pluginURI, null) ;
		this.threadPoolURI = threadPoolURI;

	}
	
    /**
     * Processes a synchronous request from a client component.
     *
     * @param request the request to process, conforming to {@link RequestI}.
     * @return the result of processing the request, encapsulated in a {@link QueryResultI} object.
     * @throws Exception if the request processing fails or encounters an error.
     */
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
	
    /**
     * Processes an asynchronous request from a client component.
     *
     * @param request the request to process, conforming to {@link RequestI}.
     * @throws Exception if the request processing fails or encounters an error.
     */
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
