package withplugin.composants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import classes.BCM4JavaEndPointDescriptor;
import classes.ConnectionInfo;
import classes.QueryResult;
import classes.Request;
import composants.client.RequestResultInboundPort;
import composants.noeud.Node;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import plugins.ClientPlugin;


@RequiredInterfaces(required = {ClocksServerCI.class})
@OfferedInterfaces(offered = {RequestResultCI.class})

public class Client extends AbstractComponent {
	
	protected ReadWriteLock rwLock;

	protected ClocksServerOutboundPort clockOutboundPort;
	protected RequestResultInboundPort	inboundPortRequestResult ;
	
	protected GeographicalZoneI zone;
	protected RequestI request;
	protected Map<String, QueryResultI> requestResults;
	protected AcceleratedClock ac;
	protected ConnectionInfoI clientConnectionInfo;
	protected ClientPlugin plugin;
	
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String ACCEPT_POOL_URI =
													"node pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int ACCEPT_POOL_SIZE = 5;
	
	protected static final String CLIENT_PLUGIN_URI = 
			"clientPluginURI";
	
	private static int timeBeforeShowingResult = 10;
	private static int cptClient = 1;
	
	
	protected Client(GeographicalZoneI zone, RequestI request) throws Exception{

			super(1, 1) ;
			
			plugin = new ClientPlugin();
			plugin.setPluginURI(CLIENT_PLUGIN_URI);

			this.inboundPortRequestResult = new RequestResultInboundPort(this, ACCEPT_POOL_URI) ;
			this.inboundPortRequestResult.publishPort();
			
			this.zone = zone;
			this.request = request;
			this.requestResults = new HashMap<>();
			
			this.clientConnectionInfo = new ConnectionInfo("client"+ cptClient++);
			((ConnectionInfo)(clientConnectionInfo)).setEndPointInfo(
					new BCM4JavaEndPointDescriptor(inboundPortRequestResult.getPortURI(), RequestResultCI.class));
			
			this.rwLock = new ReentrantReadWriteLock();
			this.initialise();
	}
	
	public ConnectionInfoI findNodeToSend(GeographicalZoneI zone) {
		this.logMessage("Client: Finding Nodes in Geographical Zone... ");
		Set<ConnectionInfoI> zoneNodes = null;
		
		try {
			zoneNodes = plugin.findByZone(zone);
		} catch (Exception e) {
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
		QueryResultI result = null;
		
		//initialise le connectionInfo du client dans la requête
		((Request)(request)).setConnectionInfo(clientConnectionInfo);
		
		try {
			//set les infos du noeud pour se connecter au bon noeud
			this.plugin.setNodeToConnect(node);
			result = this.plugin.execute(request);
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

		
		//initialise le connectionInfo du client dans la requête
		((Request)(request)).setConnectionInfo(clientConnectionInfo);
		
		//set les infos du noeud pour se connecter au bon noeud
		this.plugin.setNodeToConnect(node);
		this.plugin.executeAsync(request);
			
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
	    Lock writeLock = rwLock.writeLock(); // Verrou d'écriture pour les opérations de mise à jour de requestResults
	    this.logMessage("passe dans acceptRequestResult");
	    QueryResultI finalResult;

	    writeLock.lock();
	    try {
	        finalResult = requestResults.get(requestURI);

	    if (finalResult == null) {
	    	requestResults.put(requestURI, result);
	        return;
	    }
	    
	    // Fusion des résultats en fonction du type de requête
	    if (result.isBooleanRequest()) {
	        this.logMessage("result's value before acceptRequestResult : " + finalResult.positiveSensorNodes());

            for (String node : result.positiveSensorNodes()) {
                if (!finalResult.positiveSensorNodes().contains(node)) {
                    ((QueryResult) finalResult).setpositiveSensorNodes(node);
                }
            }

	        this.logMessage("result's value after acceptRequestResult : " + finalResult.positiveSensorNodes());

	    } else if (result.isGatherRequest()) {
	    	this.logMessage("result's value before acceptRequestResult : " + finalResult.gatheredSensorsValues());

            ArrayList<SensorDataI> gatheredNodes = finalResult.gatheredSensorsValues();
            for (SensorDataI node : result.gatheredSensorsValues()) {
                if (!gatheredNodes.contains(node)) {
                    gatheredNodes.add(node);
                }
            }
            ((QueryResult) finalResult).setgatheredSensorsValues(gatheredNodes);
            this.logMessage("result's value after acceptRequestResult : " + finalResult.gatheredSensorsValues());

	    }
	    requestResults.put(requestURI, finalResult);
	    } finally {
	        writeLock.unlock();
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
  		
      }catch(Exception e) {
    	  System.out.println(e);
      }
      
    }
	
	protected void		initialise() throws Exception
	{	this.getTracer().setTitle("Client ") ;
		this.getTracer().setRelativePosition(CVM.number/5 + 1, CVM.number % 5) ;
		CVM.number++;
		this.toggleTracing() ;
		this.toggleLogging();

		this.createNewExecutorService(ACCEPT_POOL_URI,
									  ACCEPT_POOL_SIZE,
									  false);
	}
	
	@Override
	public void execute() throws Exception{
		
		super.execute();
		
		//Install the plug-in 
		this.installPlugin(plugin);
		
		this.ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
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
			
			this.scheduleTask(
					b -> { 
				        this.logMessage("Sending request to "+ nodeSelected.nodeIdentifier() + " after sensors update");
				        sendRequestSync(nodeSelected, request);
					 },
			d2, TimeUnit.NANOSECONDS);
			
		 },
		d1, TimeUnit.NANOSECONDS);
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.clockOutboundPort.getPortURI());
		
		this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.clockOutboundPort.unpublishPort();
			this.inboundPortRequestResult.unpublishPort();
			
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	

}
