package composants.client;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;



@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered = {RequestResultCI.class})


public class Client extends AbstractComponent {
	
	private static final long serialVersionUID = 1L;
	//private static final long serialVersionUID = 1L;
	protected ClientOutboundPort	outboundPort ;
	protected ClientInboundPort	inboundPort ;
	public static final String CLOP_URI = "clientOutbondPort";
	public static final String CLIP_URI = "clientInbondPort";
	//private NodeInfoI noeud;
	private RequestI request;
	
	protected Client(RequestI request) throws Exception{
			// the reflection inbound port URI is the URI of the component
			// no simple thread and one schedulable thread
			super(1, 0) ;
			// if the required interface is not declared in the annotation
			// on the component class, it can be added manually with the
			// following instruction:
			//this.addRequiredInterface(URIConsumerI.class) ;

			// create the port that exposes the required interface
			this.outboundPort = new ClientOutboundPort(CLOP_URI, this) ;
			this.inboundPort = new ClientInboundPort(CLIP_URI, this) ;
			// publish the port (an outbound port is always local)
			this.outboundPort.publishPort();
			this.inboundPort.publishPort();
			
			this.request = request;

//			if (AbstractCVM.isDistributed) {
//				this.getLogger().setDirectory(System.getProperty("user.dir")) ;
//			} else {
//				this.getLogger().setDirectory(System.getProperty("user.home")) ;
//			}
//			this.getTracer().setTitle("consumer") ;
//			this.getTracer().setRelativePosition(1, 1) ;
//
//			AbstractComponent.checkImplementationInvariant(this);
//			AbstractComponent.checkInvariant(this);
	}
	
	//Audit1
	@Override
	public void execute() throws Exception{
		this.logMessage("request sent");
		QueryResultI result = this.outboundPort.execute(request);
		if(result.isBooleanRequest()) {
			this.logMessage("request result = " + result.positiveSensorNodes());
		}
		else if(result.isGatherRequest()) {
			this.logMessage("request result = " + result.gatheredSensorsValues());
		}
		else {
			this.logMessage("result empty");
		}
	}
	
	
	@Override
    public void start() throws ComponentStartException
    {
        this.logMessage("starting client component.");
        super.start();
    }
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(CLOP_URI);
		//this.doPortDisconnection(CLIP_URI);
		this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.outboundPort.unpublishPort();
			this.inboundPort.unpublishPort();
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	

}
