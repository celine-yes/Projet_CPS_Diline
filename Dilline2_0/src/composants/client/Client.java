package composants.client;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import composants.connector.ClientNodeConnector;
import composants.connector.ClientRegisterConnector;
import composants.noeud.Node;
import composants.noeud.NodeSensorNodeP2POutboundPort;
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
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;


@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class,ClocksServerCI.class })
@OfferedInterfaces(offered = {RequestResultCI.class})

public class Client extends AbstractComponent {
	
	
	protected ClientRequestingOutboundPort	outboundPortRequesting ;
	protected ClientLookupOutboundPort	outboundPortLookup ;
	protected ClocksServerOutboundPort clockOutboundPort;
	protected ClientRequestResultInboundPort	inboundPortRequestResult ;
	
	protected GeographicalZoneI zone;
	protected RequestI request;
	protected Map<String, QueryResultI> requestResults;
	
	private static int timeBeforeShowingResult = 5;
	
	
	protected Client(String obPortRequesting, String obPortLookup,
				     String ibPortRequestResult,GeographicalZoneI zone, RequestI request) throws Exception{

			super(1, 1) ;

			this.outboundPortRequesting = new ClientRequestingOutboundPort(obPortRequesting, this) ;
			this.outboundPortLookup = new ClientLookupOutboundPort(obPortLookup, this) ;
			this.inboundPortRequestResult = new ClientRequestResultInboundPort(ibPortRequestResult, this) ;
			
			this.outboundPortRequesting.publishPort();
			this.outboundPortLookup.publishPort();
			this.inboundPortRequestResult.publishPort();
			
			this.zone = zone;
			this.request = request;
			this.requestResults = new HashMap<>();
	}
	
	public ConnectionInfoI findNodeToSend(GeographicalZoneI zone) {
		this.logMessage("Client: Finding Nodes in Geographical Zone... ");
		Set<ConnectionInfoI> zoneNodes = null;
		
		try {
			zoneNodes = this.outboundPortLookup.findByZone(zone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (ConnectionInfoI info: zoneNodes) {
			this.logMessage("Client: " + info.nodeIdentifier() + " found");
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
		this.logMessage("Client: Chose to send request to " + nodeSelected.nodeIdentifier());
		
		return nodeSelected;
	}
	
	public void sendRequestSync(ConnectionInfoI node,  RequestI request){
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
			this.logMessage("request result = " + result.gatheredSensorsValues()+"\n");
		}
		else {
			this.logMessage("result empty");
		}
	}
	
	public void sendRequestAsync(ConnectionInfoI node, RequestI request) throws Exception{
		//récupérer le inboundport du noeud sur lequel le client doit envoyer la requete
		BCM4JavaEndPointDescriptorI endpoint =(BCM4JavaEndPointDescriptorI) node.endPointInfo();
		String nodeInboundPort = endpoint.getInboundPortURI();
		
		//connection entre client et noeud choisi via RequestingCI
		
		this.doPortConnection(
				this.outboundPortRequesting.getPortURI(),
				nodeInboundPort,
				ClientNodeConnector.class.getCanonicalName());
		
		this.outboundPortRequesting.executeAsync(request);
			
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
		Instant i1 = ac.getStartInstant().plusSeconds(timeBeforeShowingResult);
		long d = ac.nanoDelayUntilInstant(i1);
				
		this.scheduleTask(
		o -> { 
			QueryResultI result = requestResults.get(request.requestURI());
			if(result.isBooleanRequest()) {
				this.logMessage("request result = " + result.positiveSensorNodes());
			}
			else if(result.isGatherRequest()) {
				this.logMessage("request result = " + result.gatheredSensorsValues()+"\n");
			}
			else {
				this.logMessage("result empty");
			}
		 },
		d, TimeUnit.NANOSECONDS);
		
		
	}
	
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		QueryResultI finalResult = requestResults.get(requestURI);
		 if (result.isBooleanRequest()) {
		        // Si la requête est de type Bquery
			 ArrayList<String> nodesPositives = finalResult.positiveSensorNodes();
			 for(String node : result.positiveSensorNodes()) {
				 if(!(nodesPositives.contains(node))) {
					 nodesPositives.add(node); 
				 }
			 }	      	
		 } else if (result.isGatherRequest()) {
		        // Si la requête est de type Gquery
			 ArrayList<SensorDataI> nodesgathered = finalResult.gatheredSensorsValues();
			 for(SensorDataI node : result.gatheredSensorsValues()) {
				 if(!(nodesgathered.contains(node))) {
					 nodesgathered.add(node); 
				 }
			 }	      	  
		 }
		 requestResults.put(requestURI, finalResult);
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
	public void execute() throws Exception{
		
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
		Instant i1 = ac.getStartInstant().plusSeconds(CVM.NB_NODES+1);
		Instant i2 = ac.getStartInstant().plusSeconds(CVM.NB_NODES + Node.cptDelay +1);
		
		long d1 = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes
		long d2 = ac.nanoDelayUntilInstant(i2);
		
		this.scheduleTask(
		o -> { 
			ConnectionInfoI nodeSelected = findNodeToSend(zone);
			try {
				sendRequestSync(nodeSelected, request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			this.scheduleTask(
//					b -> { 
//				        this.logMessage("Sending request to "+ nodeSelected.nodeIdentifier() + " after sensors update");
//				        sendRequestSync(nodeSelected);
//					 },
//			d2, TimeUnit.NANOSECONDS);
			
		 },
		d1, TimeUnit.NANOSECONDS);
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.outboundPortRequesting.getPortURI());
		this.doPortDisconnection(this.outboundPortLookup.getPortURI());
		this.doPortDisconnection(this.clockOutboundPort.getPortURI());
		
		this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.outboundPortRequesting.unpublishPort();
			this.outboundPortLookup.unpublishPort();
			this.clockOutboundPort.unpublishPort();
			this.inboundPortRequestResult.unpublishPort();
			
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	

}
