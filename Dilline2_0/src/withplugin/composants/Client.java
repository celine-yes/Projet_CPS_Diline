package withplugin.composants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
import withplugin.ports.RequestResultInboundPort;

/**
 * The {@code Client} class extends {@link AbstractComponent} to provide sensor network interaction capabilities.
 * It is designed to manage sensor data requests and handle the results asynchronously or synchronously, depending on the request type.
 *
 * <p>This component uses {@link ClientPlugin} to abstract the complexity of network communications, including:
 * - Looking up nodes within a specified geographical zone.
 * - Sending requests directly to sensor nodes.
 *
 * <p>It handles network-related tasks such as establishing connections with sensor nodes,
 * managing threading for asynchronous operations, and maintaining a registry of node information.</p>
 *
 * <p>Attributes:</p>
 * <ul>
 *     <li>{@code zone} - The geographical zone where the client operates.</li>
 *     <li>{@code requests} - A list of requests the client will process.</li>
 *     <li>{@code requestResults} - A map storing results of the requests keyed by request identifiers.</li>
 *     <li>{@code ac} - An instance of {@link AcceleratedClock} used for timing and scheduling tasks.</li>
 *     <li>{@code clientConnectionInfo} - Information about the client's connection, used in network communications.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>Components inheriting this class can directly initiate sensor data requests.</li>
 *     <li>This component can dynamically find nodes within a specific geographical zone for targeted data querying.</li>
 *     <li>Handles asynchronous results through a callback mechanism, allowing results to be processed as they arrive.</li>
 * </ul>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */


@RequiredInterfaces(required = {ClocksServerCI.class})
@OfferedInterfaces(offered = {RequestResultCI.class})

public class Client extends AbstractComponent {
	
	protected ReadWriteLock rwLock;

	protected ClocksServerOutboundPort clockOutboundPort;
	protected RequestResultInboundPort	inboundPortRequestResult ;
	
	protected GeographicalZoneI zone;
	protected ArrayList<RequestI> requests;
	protected Map<String, QueryResultI> requestResults;
	protected AcceleratedClock ac;
	protected ConnectionInfoI clientConnectionInfo;
	protected ClientPlugin plugin;
	
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String ACCEPT_POOL_URI =
													"node pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int ACCEPT_POOL_SIZE = 10;
	
	protected static final String CLIENT_PLUGIN_URI = 
			"clientPluginURI";
	
	private int timeBeforeShowingResultAsync = CVM.timeBeforeSendingRequest + CVM.NB_NODES + 20;
	private static int cptClient = 1;
	
	//pour test de performance
	protected ArrayList<String> requestFinished = new ArrayList<String>();

	
    /**
     * Constructs a {@code Client} with a specific geographical zone and a list of requests.
     * It initializes communication ports and sets up necessary configurations for network interactions.
     *
     * @param zone the geographical zone within which the client operates
     * @param requests a list of requests to be sent to sensor nodes
     * @throws Exception if there is an error in setting up the client
     */
	protected Client(GeographicalZoneI zone, ArrayList<RequestI> requests) throws Exception{

			super(1, 1) ;
			
			plugin = new ClientPlugin();
			plugin.setPluginURI(CLIENT_PLUGIN_URI);

			this.inboundPortRequestResult = new RequestResultInboundPort(this, ACCEPT_POOL_URI) ;
			this.inboundPortRequestResult.publishPort();
			
			this.zone = zone;
			this.requests = requests;
			this.requestResults = new HashMap<>();
			
			cptClient++;
			this.clientConnectionInfo = new ConnectionInfo("client"+ cptClient);
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
			this.logMessage("done finding");
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
			
			// Obtenir le temps de début en nanosecondes selon l'horloge système
	        long startTime = System.nanoTime();
	        
			result = this.plugin.execute(request);
			
			// Obtenir le temps de fin en nanosecondes selon l'horloge système
	        long endTime = System.nanoTime();
	        
	        // Calculer le temps d'exécution en nanosecondes et le convertir en secondes
	        long durationInNanos = endTime - startTime;
	        double durationInSeconds = durationInNanos / 1_000_000_000.0;
	        double acceleratedTime = durationInSeconds * CVM.ACCELERATION_FACTOR;
	        
	        // Initialiser le BufferedWriter pour écrire dans le fichier
	        BufferedWriter writer = new BufferedWriter(new FileWriter("./execution_times_directionalC.txt", true)); // 'true' pour append
	        writer.write(acceleratedTime + " s\n");
	        if (writer != null) {
	            try {
	                writer.flush();
	                writer.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        //System.out.println("durée = " + acceleratedTime);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(result.isBooleanRequest()) {
			this.logMessage("Request result = " + result.positiveSensorNodes());
		}
		else if(result.isGatherRequest()) {
			this.logMessage("Request result = " + result.gatheredSensorsValues()+"\n");
		}
		else {
			this.logMessage("Result is empty");
		}
	}
	
	public void sendRequestAsync(ConnectionInfoI node, RequestI request) throws Exception{

		    
		//initialise le connectionInfo du client dans la requête
		((Request)(request)).setConnectionInfo(clientConnectionInfo);
		
		//set les infos du noeud pour se connecter au bon noeud
		this.plugin.setNodeToConnect(node);
        logMessage(request.requestURI() + " sent at " + Instant.now().toString());
        
        
        ((Request) request).setStartTime();
		this.plugin.executeAsync(request);
		
		Instant i1 = ac.getStartInstant().plusSeconds(timeBeforeShowingResultAsync);
		timeBeforeShowingResultAsync += timeBeforeShowingResultAsync + 1;
		
		long d = ac.nanoDelayUntilInstant(i1);
				
		this.scheduleTask(
		o -> { 
			QueryResultI result = requestResults.get(request.requestURI());
			if(result.isBooleanRequest()) {
				this.logMessage(request.requestURI() + "Request result = " + result.positiveSensorNodes() + "__size = " + result.positiveSensorNodes().size());
			}
			else if(result.isGatherRequest()) {
				this.logMessage(request.requestURI() + "Request result = " + result.gatheredSensorsValues()+ "__size = " + result.positiveSensorNodes().size());
			}
			else {
				this.logMessage("Request result is empty");
			}
		 },
		d, TimeUnit.NANOSECONDS);
		
	}
	
	private RequestI findRequestByUri(String uri) {
        for (RequestI request : requests) {
            if (request.requestURI().equals(uri)) {
                return request;
            }
        }
        return null;  // Retourne null si aucune requête ne correspond
    }

	
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
	    Lock writeLock = rwLock.writeLock(); // Verrou d'écriture pour les opérations de mise à jour de requestResults
	    QueryResultI finalResult;
	    
	    writeLock.lock();
	    try {
		    
	        finalResult = requestResults.get(requestURI);

		    if (finalResult == null) {
		    	requestResults.put(requestURI, result);
		        return;
		    }
		    
		    //pour récupérer le temps
		    RequestI request = findRequestByUri(requestURI);
		    
		    
		    // Fusion des résultats en fonction du type de requête
		    if (result.isBooleanRequest()) {
		    	
	            for (String node : result.positiveSensorNodes()) {
	                if (!finalResult.positiveSensorNodes().contains(node)) {
	                    ((QueryResult) finalResult).setpositiveSensorNodes(node);
	                }
	            }
	            

		    } else if (result.isGatherRequest()) {
	
	            ArrayList<SensorDataI> gatheredNodes = finalResult.gatheredSensorsValues();
	            for (SensorDataI node : result.gatheredSensorsValues()) {
	                if (!gatheredNodes.contains(node)) {
	                    gatheredNodes.add(node);
	                }
	            }
	            ((QueryResult) finalResult).setgatheredSensorsValues(gatheredNodes);
	        	
	
		    }
	    
	
	        if(finalResult.positiveSensorNodes().size() == 50 || finalResult.gatheredSensorsValues().size() == 50) {
	        	if(!requestFinished.contains(requestURI)) {
	        		this.logMessage(requestURI + " finished at" + Instant.now());
		    		requestFinished.add(requestURI);
		    		
		    		long endTime = System.nanoTime();
			        
			        // Calculer le temps d'exécution en nanosecondes et le convertir en secondes
			        long durationInNanos = endTime - ((Request) request).getStartTime();
			        double durationInSeconds = durationInNanos / 1_000_000_000.0;
			        double acceleratedTime = durationInSeconds * CVM.ACCELERATION_FACTOR;
			        
			        // Initialiser le BufferedWriter pour écrire dans le fichier
			        BufferedWriter writer = new BufferedWriter(new FileWriter("./execution_times_flooding_async.txt", true)); // 'true' pour append
			        writer.write(acceleratedTime + " s\n");
			        if (writer != null) {
			            try {
			                writer.flush();
			                writer.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			        
			        //System.out.println(requestURI + "durée = " + acceleratedTime);
	        	}
	        }
	        	
	        requestResults.put(requestURI, finalResult);
	        
	    } finally {
	        writeLock.unlock();
	    }
	}
	

	public void sendRequests(ArrayList<RequestI> requests) throws Exception {
	    if (requests.isEmpty()) return; // Condition d'arrêt si la liste des requêtes est vide
	    
	    
	    // Initialisation du premier délai pour les requêtes synchrones
	    Instant i1 = ac.getStartInstant().plusSeconds(CVM.timeBeforeSendingRequest);
		CVM.timeBeforeSendingRequest += CVM.timeBeforeUpdatingSensorValue + 1;

		long delay = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes
		//Si c'est asynchrone, on envoie toutes les requêtes sans délai
	    if(requests.get(0).isAsynchronous()) {
			this.scheduleTask(o -> {
				for(RequestI request: requests) {
			    	ConnectionInfoI nodeSelected = findNodeToSend(zone);
			    	try {
						sendRequestAsync(nodeSelected, request);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }, delay, TimeUnit.NANOSECONDS);
	    }else {
		    // Méthode récursive pour planifier chaque requête après la complétion de la précédente pour les requetes synchrones
			scheduleNextSynchronousRequest(requests, 0, delay);
	    }

	}

	private void scheduleNextSynchronousRequest(ArrayList<RequestI> requests, int currentIndex, long delay) {
	    if (currentIndex >= requests.size()) return; // Arrête la récursion si on a traité toutes les requêtes

	    this.scheduleTask(o -> {
	        try {
	            ConnectionInfoI nodeSelected = findNodeToSend(zone); // Trouver le noeud pour envoyer la requête
	            RequestI request = requests.get(currentIndex);
	            sendRequestSync(nodeSelected, request); // Envoyer la requête de manière asynchrone
	            logMessage("Synchronous Request " + request.toString() + " sent to " + nodeSelected.nodeIdentifier() + " at " + Instant.now().toString());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        // Planifier la prochaine requête avec un nouveau délai
	        scheduleNextSynchronousRequest(requests, currentIndex + 1, delay);
	    }, delay, TimeUnit.NANOSECONDS);
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
		
		assert this.isInstalled(CLIENT_PLUGIN_URI);
		assert this.getPlugin(CLIENT_PLUGIN_URI) == this.plugin;
		
		this.ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
	
		sendRequests(requests);
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
