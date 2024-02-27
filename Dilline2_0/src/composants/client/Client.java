package composants.client;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import composants.connector.ClientNodeConnector;
import composants.connector.ClientRegisterConnector;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;


import classes.Position;
import classes.GeographicalZone;

@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class,ClocksServerCI.class })
@OfferedInterfaces(offered = {RequestResultCI.class})


public class Client extends AbstractComponent {
	
	protected ClientRequestingOutboundPort	outboundPortRequesting ;
	protected ClientLookupOutboundPort	outboundPortLookup ;
	protected ClientRequestResultInboundPort	inboundPortRequestResult ;
	private RequestI request;
	private ClocksServerOutboundPort clockOutboundPort;
	
	protected Client(String obPortRequesting, String obPortLookup,
				     String ibPortRequestResult, RequestI request) throws Exception{
			// the reflection inbound port URI is the URI of the component
			// no simple thread and one schedulable thread
			super(1, 1) ;

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
	

	@Override
	public void execute() throws Exception{
		
		//appel de findByZone
		PositionI p1 = new Position(1,1);
		PositionI p2 = new Position(21,45);
		GeographicalZoneI zone = new GeographicalZone(p1,p2);
		


				
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		
		// toujours faire waitUntilStart avant d’utiliser l’horloge pour
		// calculer des moments et instants
		ac.waitUntilStart();
		
		Instant i0 = Instant.parse("2024-01-31T09:00:00.00Z");
		Instant i1 = i0.plusSeconds(3);
		
		long d = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes
		final AbstractComponent c = this;
		this.scheduleTask(
		o -> { 
			ConnectionInfoI nodeSelected = findNodeToSend(zone);
			sendRequest(nodeSelected);
		 },
		d, TimeUnit.NANOSECONDS);
	
	}
	
	public ConnectionInfoI findNodeToSend(GeographicalZoneI zone) {
		this.logMessage("Client: Cherche Nodes dans Zone Geographique... ");
		Set<ConnectionInfoI> zoneNodes = null;
		
		try {
			zoneNodes = this.outboundPortLookup.findByZone(zone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (ConnectionInfoI info: zoneNodes) {
			this.logMessage("Client: Trouvé " + info.nodeIdentifier());
		}
		
		//prendre un noeud au hasard parmi celles trouvé dans la zone
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
		
		return nodeSelected;
	}
	
	public void sendRequest(ConnectionInfoI node){
		//récupérer le inboundport du noeud sur lequel le client doit envoyer la requete
		BCM4JavaEndPointDescriptorI endpoint =(BCM4JavaEndPointDescriptorI) node.endPointInfo();
		String nodeInboundPort = endpoint.getInboundPortURI();
		QueryResultI result = null;
		
		//connection entre client et noeud choisi via RequestingCI
		try {
			this.doPortConnection(
					this.outboundPortRequesting.getPortURI(),
					nodeInboundPort,
					ClientNodeConnector.class.getCanonicalName());
			
			result = this.outboundPortRequesting.execute(request);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
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
        
      
      try {
    	  //connection entre client et clock
    	  this.clockOutboundPort = new ClocksServerOutboundPort(this);
    	  this.clockOutboundPort.publishPort();
          this.doPortConnection(
        		  this.clockOutboundPort.getPortURI(),
    			  ClocksServer.STANDARD_INBOUNDPORT_URI,
    			  ClocksServerConnector.class.getCanonicalName());
          
          
        //connection entre client et register via LookupCI
  		this.doPortConnection(
  				this.outboundPortLookup.getPortURI(),
  				CVM.REGISTER_LOOKUP_INBOUND_PORT_URI,
  				ClientRegisterConnector.class.getCanonicalName());
  		
      }catch(Exception e) {
    	  System.out.println(e);
      }
      
		
    }
	
	@Override
	public synchronized void finalise() throws Exception {
		//this.doPortDisconnection(this.outboundPortRequesting.getPortURI());
		//this.doPortDisconnection(CLIP_URI);
		this.doPortDisconnection(this.clockOutboundPort.getPortURI());
		this.clockOutboundPort.unpublishPort();
		this.clockOutboundPort.destroyPort();
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
