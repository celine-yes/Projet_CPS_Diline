package composants.client;

import java.util.Set;

import composants.connector.ClientNodeConnector;
import composants.connector.ClientRegisterConnector;
import composants.connector.NodeNodeConnector;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;


import classes.Position;
import classes.GeographicalZone;

@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered = {RequestResultCI.class})


public class Client extends AbstractComponent {
	
	protected ClientRequestingOutboundPort	outboundPortRequesting ;
	protected ClientLookupOutboundPort	outboundPortLookup ;
	protected ClientRequestResultInboundPort	inboundPortRequestResult ;
	private RequestI request;
	
	protected Client(String obPortRequesting, String obPortLookup,
				     String ibPortRequestResult, RequestI request) throws Exception{
			// the reflection inbound port URI is the URI of the component
			// no simple thread and one schedulable thread
			super(1, 0) ;

			this.outboundPortRequesting = new ClientRequestingOutboundPort(obPortRequesting, this) ;
			this.outboundPortLookup = new ClientLookupOutboundPort(obPortLookup, this) ;
			this.inboundPortRequestResult = new ClientRequestResultInboundPort(ibPortRequestResult, this) ;
			
			// publish the port (an outbound port is always local)
			this.outboundPortRequesting.publishPort();
			this.outboundPortLookup.publishPort();
			this.inboundPortRequestResult.publishPort();
			
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
		
		//connection entre client et register via LookupCI
		this.doPortConnection(
				this.outboundPortLookup.getPortURI(),
				CVM.REGISTER_LOOKUP_INBOUND_PORT_URI,
				ClientRegisterConnector.class.getCanonicalName());
		
		//appel de findByZone
		PositionI p1 = new Position(1.0,1.0);
		PositionI p2 = new Position(21,45);
		GeographicalZoneI zone = new GeographicalZone(p1,p2);
		this.logMessage("Client: Cherche Nodes dans Zone Geographique... ");
		Set<ConnectionInfoI> zoneNodes = this.outboundPortLookup.findByZone(zone);
		
		
		for (ConnectionInfoI info: zoneNodes) {
			this.logMessage("Client: Trouvé " + info.nodeIdentifier());
		}
		
		//prendre un noeud au hasart parmi celles trouvé dans la zone
		int n=(int)(Math.random() * zoneNodes.size());
		int i=0;
		ConnectionInfoI nodeSelected = null;
		for(ConnectionInfoI node : zoneNodes) {
            if(i == n) {
            	nodeSelected = node;
                break;
            }
            i++;
        }
		this.logMessage("Client: Choisi d'envoyer une requête à " + nodeSelected.nodeIdentifier());
		
		//récupérer le inboundport du noeud sur lequel le client doit envoyer la requete
		BCM4JavaEndPointDescriptorI endpoint =(BCM4JavaEndPointDescriptorI) nodeSelected.endPointInfo();
		String nodeInboundPort = endpoint.getInboundPortURI();
		
		//connection entre client et noeud choisi via RequestingCI
		this.doPortConnection(
				this.outboundPortRequesting.getPortURI(),
				nodeInboundPort,
				ClientNodeConnector.class.getCanonicalName());
		
		QueryResultI result = this.outboundPortRequesting.execute(request);
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
		//this.doPortDisconnection(this.outboundPortRequesting.getPortURI());
		//this.doPortDisconnection(CLIP_URI);
		this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.outboundPortRequesting.unpublishPort();
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	

}
