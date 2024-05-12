package composants.noeud;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;


/**
 * <code>RequestingInboundPort</code> is designed to handle requests sent by client components to
 * node components in a sensor network environment. This port facilitates both synchronous and
 * asynchronous execution of requests.
 *
 * <p>This class implements the {@link RequestingCI} interface, allowing it to receive and process
 * requests directed towards node components. By managing a dedicated thread pool for request processing,
 * this port ensures that request handling is efficient and responsive.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class RequestingInboundPort extends AbstractInboundPort implements RequestingCI{
	
	private static final long serialVersionUID = 1L;
	protected final String		threadPoolURI;
	
	public RequestingInboundPort(ComponentI owner, String threadPoolURI) throws Exception{
		
		
		// the implemented interface is statically known
		super(RequestingCI.class, owner) ;
		this.threadPoolURI = threadPoolURI;

	}
	
	/**
	 * Executes a request synchronously and returns a result.
	 *
	 * @param request the request to be executed.
	 * @return the result of the request execution.
	 * @throws Exception if there is an issue in executing the request.
	 */
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return this.getOwner().handleRequest(
				threadPoolURI,
				o -> ((Node)o).execute(request));
	}

	/**
	 * Initiates the asynchronous execution of a request.
	 *
	 * @param request the request to be executed asynchronously.
	 * @throws Exception if there is an issue scheduling the asynchronous task.
	 */
	@Override
	public void executeAsync(RequestI request) throws Exception {
		this.getOwner().runTask(
				threadPoolURI,
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Node)this.getTaskOwner()).executeAsync(request) ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}
				}) ;
		
	}
	
}
