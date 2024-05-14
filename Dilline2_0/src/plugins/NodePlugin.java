package plugins;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import classes.BCM4JavaEndPointDescriptor;
import classes.ExecutionState;
import classes.NodeInfo;
import classes.ProcessingNode;
import classes.QueryResult;
import classes.RequestContinuation;
import classes.SensorData;
import composants.connector.NodeClientConnector;
import composants.connector.NodeNodeConnector;
import composants.connector.NodeRegisterConnector;
import composants.noeud.RegistrationOutboundPort;
import composants.noeud.RequestResultOutboundPort;
import composants.noeud.SensorNodeP2POutboundPort;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import langage.interfaces.QueryI;
import withplugin.ports.RequestingInboundPort;
import withplugin.ports.SensorNodeP2PInboundPort;




/**
 * The {@code NodePlugin} class extends {@code AbstractPlugin} and implements the
 * {@code RequestingCI}, {@code SensorNodeP2PCI}, and {@code RegistrationCI} interfaces
 * to facilitate sensor network operations, registration, and request handling in a distributed system.
 * This class manages node information, sensor data, and network connectivity with other nodes
 * for executing distributed queries and processing sensor data efficiently.
 * 
 * <p>The class supports both synchronous and asynchronous request processing, handling the registration
 * and deregistration of nodes, and managing connections based on geographical direction and proximity.
 * It also updates and manages sensors and their data to ensure that the state of each node is current.
 * The plugin architecture allows it to be dynamically attached to and detached from components.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class NodePlugin extends AbstractPlugin implements RequestingCI, SensorNodeP2PCI, RegistrationCI{

	private static final long serialVersionUID = 1L;
	
	protected RegistrationOutboundPort	outboundPortRegistration;
	protected ClocksServerOutboundPort clockOutboundPort;
	protected Map<Direction, NodeInfoI> neighbourNodeInfo;
	protected Map<Direction, SensorNodeP2POutboundPort> neighbourPortMap;
	protected ArrayList<String> requetesTraites;
	
	protected RequestingInboundPort	inboundPortRequesting ;
	protected SensorNodeP2PInboundPort inboundPortP2P ;
	protected RequestResultOutboundPort outboundPortRequestR;
	
	protected NodeInfoI nodeInfo;
	protected ProcessingNodeI prcNode;
	protected ArrayList<SensorDataI> capteurs;
	public static int cptDelay = 0;
	
	protected ReadWriteLock rwLockRequetes;
	protected ReadWriteLock rwLockCapteurs;

	
	public static final String				NODE_POOL_URI =
													"node pool URI";
	public static final String				CLIENT_POOL_URI =
													"client pool URI";
														
	protected static final int				NODE_POOL_SIZE = 3;
														
	protected static final int				CLIENT_POOL_SIZE = 2;
	
	protected static final String NODE_PLUGIN_URI = 
												"nodePluginURI";
	
	
	/**
     * Constructs a NodePlugin instance with specified node information and sensor data.
     * Initializes network topology management structures and locks for thread safety.
     *
     * @param nodeinfo the node information for the current node
     * @param sensors the sensors associated with the node
     * @throws Exception if there is an error during initialization
     */
	
	public NodePlugin(NodeInfoI nodeinfo, ArrayList<SensorDataI> sensors ) throws Exception {
		this.nodeInfo = nodeinfo;
		this.neighbourPortMap = new LinkedHashMap<>();
		this.neighbourNodeInfo = new LinkedHashMap<>(); //pour gerer le probleme de plus proche voisin 
		this.rwLockRequetes = new ReentrantReadWriteLock();
		this.rwLockCapteurs = new ReentrantReadWriteLock();
		
		this.capteurs = sensors.stream()
                .map(sensor -> ((SensorData)sensor).copy())
                .collect(Collectors.toCollection(ArrayList::new));
		
		prcNode = new ProcessingNode(nodeInfo, capteurs);
		requetesTraites = new ArrayList<>();
		
	}
	
	/**
     * Installs this plugin on the specified component. This method ensures that necessary interfaces are added
     * and ports are created and published.
     *
     * @param owner the component on which this plugin is being installed
     * @throws Exception if an error occurs during the installation process
     */

    public void installOn(ComponentI owner) throws Exception {
		
    	super.installOn(owner); 
    	
		// Add the interface and create ports	
    	this.addRequiredInterface(RegistrationCI.class);
    	this.addRequiredInterface(SensorNodeP2PCI.class);
		this.addOfferedInterface(SensorNodeP2PCI.class);
		this.addOfferedInterface(RequestingCI.class);
    }
    
    
    /**
     * Initializes the plugin, setting up necessary port connections for registration and
     * network communications, and preparing executor services for handling requests.
     *
     * @throws Exception if initialization fails due to configuration errors or connection issues
     */
    
	@Override
	public void			initialise() throws Exception
	{
		//Initialisation des ports
		this.outboundPortRegistration = new RegistrationOutboundPort(this.getOwner());
		this.inboundPortRequesting = new RequestingInboundPort(this.getOwner(), CLIENT_POOL_URI, NODE_PLUGIN_URI);
		this.outboundPortRequestR = new RequestResultOutboundPort(this.getOwner());
		this.inboundPortP2P = new SensorNodeP2PInboundPort(this.getOwner(), NODE_POOL_URI, NODE_PLUGIN_URI);
		this.inboundPortRequesting.publishPort();
		this.inboundPortP2P.publishPort();
		this.outboundPortRegistration.publishPort();
		this.outboundPortRequestR.publishPort();
		
		BCM4JavaEndPointDescriptorI requestingEndPoint = new BCM4JavaEndPointDescriptor(inboundPortRequesting.getPortURI(), RequestingCI.class);
		BCM4JavaEndPointDescriptorI p2pEndPoint = new BCM4JavaEndPointDescriptor(inboundPortP2P.getPortURI(), SensorNodeP2PCI.class);
		((NodeInfo)(nodeInfo)).setInboundPorts(requestingEndPoint, p2pEndPoint);

	    //connection entre node et clock
		this.clockOutboundPort = new ClocksServerOutboundPort(this.getOwner());
		this.clockOutboundPort.publishPort();
	    this.getOwner().doPortConnection(
	    		  this.clockOutboundPort.getPortURI(),
				  ClocksServer.STANDARD_INBOUNDPORT_URI,
				  ClocksServerConnector.class.getCanonicalName());
          
		
    	//node doit register auprès du registre
		this.getOwner().doPortConnection(
				this.outboundPortRegistration.getPortURI(),
				CVM.REGISTER_REGISTRATION_INBOUND_PORT_URI,
				NodeRegisterConnector.class.getCanonicalName()) ;
		this.logMessage(nodeInfo.nodeIdentifier() + " connected to register");
  		
		this.createNewExecutorService(NODE_POOL_URI,
				  NODE_POOL_SIZE,
				  false);
		this.createNewExecutorService(CLIENT_POOL_URI,
			CLIENT_POOL_SIZE,
			false);
		
		super.initialise();
	}
	
    /**
     * Registers the node with the network, establishing connections to neighbor nodes based on the
     * calculated direction and proximity, and updates the internal state to reflect these connections.
     *
     * @param nodeInfo the node information to be registered
     * @return a set of {@code NodeInfoI} objects representing the neighbors of this node
     * @throws Exception if registration fails
     */
	
	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		
		//enregistrement dans le registre
	    this.logMessage(this.nodeInfo.nodeIdentifier() + " is registering...");
	    Set<NodeInfoI> neighbours = this.outboundPortRegistration.register(nodeInfo);

	    if (this.outboundPortRegistration.registered(nodeInfo.nodeIdentifier())) {
	        this.logMessage(nodeInfo.nodeIdentifier() + " registered!");
	    }
	    
	    //recherche des voisins
	    for (NodeInfoI neighbour : neighbours) {
	        if (neighbour != null) {
	            Direction direction = nodeInfo.nodePosition().directionFrom(neighbour.nodePosition());
	            if (direction != null && !this.neighbourPortMap.containsKey(direction)) {
	                SensorNodeP2POutboundPort outboundPort = new SensorNodeP2POutboundPort(this.getOwner());
	                outboundPort.publishPort();
	                this.neighbourPortMap.put(direction, outboundPort);
	                this.neighbourNodeInfo.put(direction, neighbour);
	                
	                this.logMessage("Connecting to " + neighbour.nodeIdentifier() + " via " + direction);
	                this.getOwner().doPortConnection(
	                    outboundPort.getPortURI(),
	                    ((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
	                    NodeNodeConnector.class.getCanonicalName());
	                this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
	                outboundPort.ask4Connection(nodeInfo);
	            }
	        }
	    }
	    
	    ((ProcessingNode) prcNode).setNeighbours(new HashSet<>(neighbourNodeInfo.values()));
	    return neighbours;
	}
	
    /**
     * Initiates a connection request to the specified neighbor. This method will determine the
     * direction to the neighbor and attempt to establish a connection if the neighbor is closer than the current one.
     *
     * @param neighbour the node information of the neighbor to connect to
     * @throws Exception if the connection attempt fails
     */
	
	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		
	    Direction direction = this.nodeInfo.nodePosition().directionFrom(neighbour.nodePosition());
	    if (direction == null) {
	        this.logMessage("No valid direction found to connect to " + neighbour.nodeIdentifier());
	        return;
	    }

	    NodeInfoI currentNeighbour = this.neighbourNodeInfo.get(direction);
	    if (currentNeighbour != null) {
	        double currentDistance = this.nodeInfo.nodePosition().distance(currentNeighbour.nodePosition());
	        double newDistance = this.nodeInfo.nodePosition().distance(neighbour.nodePosition());
	        
	        // Check if the new neighbor is closer than the current one
	        if (newDistance >= currentDistance) {
	            return;
	        } 
	    }

	    // No existing neighbor or new neighbor is closer, establish connection
	    SensorNodeP2POutboundPort newOutboundPort = new SensorNodeP2POutboundPort(this.getOwner());
	    newOutboundPort.publishPort();
	    this.neighbourPortMap.put(direction, newOutboundPort); // Update to the new neighbor
	    this.neighbourNodeInfo.put(direction, neighbour); // Update to the new neighbor

	    this.getOwner().doPortConnection(
	        newOutboundPort.getPortURI(),
	        ((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
	        NodeNodeConnector.class.getCanonicalName());
	    
	    ((ProcessingNode) prcNode).setNeighbours(new HashSet<>(neighbourNodeInfo.values()));
	    this.logMessage(nodeInfo.nodeIdentifier() + "changing currentNeighbour; connected to " + neighbour.nodeIdentifier() + " via " + direction);
	}

	
	

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().doPortDisconnection(
				((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI()) ;
		this.logMessage(nodeInfo.nodeIdentifier() + " disconnected from " + neighbour.nodeIdentifier());
	}
	
    /**
     * Processes a given request received by node components synchronously , evaluates the request using the node's
     * sensors and  forwards it to other nodes based on the request's nature (flooding or directional).
     *
     * @param request the request to process
     * @return the result of the query execution
     * @throws Exception if there is an error in processing the request
     */

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) request.getQueryCode();
	    QueryResultI result = null;
	    
	    Lock readLock = rwLockRequetes.readLock();
	    readLock.lock();
	    
	    try {
	    	//vérifie si le noeud n'a pas déjà évaluer cette requête
	    	if (requetesTraites.contains(request.requestURI())) {
				this.logMessage(nodeInfo.nodeIdentifier() + " : i have already processed this request!");
				return null;
			}
	    } finally {
	        readLock.unlock();
	    }
   
	    Lock writeLock = rwLockRequetes.writeLock();
	    writeLock.lock();
	    try {
	    	
	    	requetesTraites.add(request.requestURI());
	    } finally {
	        writeLock.unlock();
	    }
	    
	    ArrayList<QueryResultI> neighbourResults = new ArrayList<>();
	    
	    Map<NodeInfoI, SensorNodeP2POutboundPort> neighboursToSend = new LinkedHashMap<>();

        // Update the processing node
        ExecutionState executionState = ((ExecutionState) request.getExecutionState()).copy();
        executionState.updateProcessingNode(new ProcessingNode (nodeInfo, capteurs));

        if (executionState.isDirectional()) {
        	if(!executionState.noMoreHops()) {
        		
	            executionState.incrementHops();
	            result = (QueryResultI) coderequest.eval(executionState);
	    		this.logMessage(nodeInfo.nodeIdentifier() + " nbHops = " + executionState.getCptHops());
	    		
	    		//envoyer la requete selon les directions
	    		neighboursToSend = fingNeighboursDirectional(executionState);

        	} else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : No more hops for me!");
            }
        } else if (executionState.isFlooding()) {
            // Si le nœud est dans maxDist
            if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {

                result = (QueryResultI) coderequest.eval(executionState);
                
                neighboursToSend = fingNeighboursFlooding();

            } else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : I am not in maximal distance!");
            }
        }
	   
	    
        neighbourResults = sendRequest(neighboursToSend, new RequestContinuation ( 
        													request.requestURI(), 
        													request.getQueryCode(), 
        													request.clientConnectionInfo(), 
        													executionState));
        result = ifIsNull(result, neighbourResults);
        mergeResults(result, neighbourResults);
	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	    return result;
	}
	
	
    /**
     * Processes a given request received by client components synchronously , evaluates the request using the node's
     * sensors and  forwards it to other nodes based on the request's nature (flooding or directional or empty).
     *
     * @param request the request to process
     * @return the result of the query execution
     * @throws Exception if there is an error in processing the request
     */

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		
		ExecutionStateI exState = new ExecutionState(new ProcessingNode(nodeInfo, capteurs));
		
		//evaluer la requete sur le premier noeud
		QueryResultI result = (QueryResultI) coderequest.eval(exState);
		
		Lock writeLock = rwLockRequetes.writeLock();
		writeLock.lock();
		try {	
			requetesTraites.add(request.requestURI());
		} finally {
		    writeLock.unlock();
		}
		
		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			return result;
		}
		

		ArrayList <QueryResultI> neighbourResults = new ArrayList<>();
		Map<NodeInfoI, SensorNodeP2POutboundPort> neighboursToSend = new LinkedHashMap<>();
		
		if (exState.isDirectional()) {
			neighboursToSend = fingNeighboursDirectional(exState);
        } else if (exState.isFlooding()) {
        	neighboursToSend = fingNeighboursFlooding();
        }
		
	    neighbourResults = sendRequest(neighboursToSend, new RequestContinuation(
	            request.requestURI(),
	            request.getQueryCode(),
	            request.clientConnectionInfo(),
	            exState));
	    
	    //fusionner tous les resultats 
	    mergeResults(result,neighbourResults);
	    
		this.logMessage(nodeInfo.nodeIdentifier() + " : request processed !");
		return result;
	}
	
	
	
	/**
	 * Identifies neighboring nodes to which requests should be forwarded based on the directional strategy defined in the execution state.
	 *
	 * @param executionState The execution state that contains directions for forwarding requests.
	 * @return A map of {@code NodeInfoI} to {@code SensorNodeP2POutboundPort}, representing the neighbors to forward requests to.
	 */
	
	private Map<NodeInfoI, SensorNodeP2POutboundPort> fingNeighboursDirectional(ExecutionStateI executionState) {
		Map<NodeInfoI, SensorNodeP2POutboundPort> voisins = new HashMap<>();
	    for (Direction d : Direction.values()) {
	        if (executionState.getDirections().contains(d) && neighbourPortMap.containsKey(d)) {
	            SensorNodeP2POutboundPort port = neighbourPortMap.get(d);
	            if (port != null) {
					voisins.put(neighbourNodeInfo.get(d), port);   	
	            }
	        }
	    }
	    return voisins;
	}
	
	/**
	 * Identifies all neighboring nodes for a flooding strategy regardless of direction.
	 *
	 * @return A map of {@code NodeInfoI} to {@code SensorNodeP2POutboundPort} representing all neighboring nodes.
	 */

	private Map<NodeInfoI, SensorNodeP2POutboundPort> fingNeighboursFlooding() {
		Map<NodeInfoI, SensorNodeP2POutboundPort> voisins = new HashMap<>();
	    for (Direction d : neighbourPortMap.keySet()) {
            SensorNodeP2POutboundPort port = neighbourPortMap.get(d);
            if (port != null) {
				voisins.put(neighbourNodeInfo.get(d), port);   	
            }
	    }
	    return voisins;
	}
	
	
	/**
	 * Sends a request to the specified neighbors and collects their responses.
	 *
	 * @param neighboursToSend The map of neighbors and their respective outbound ports.
	 * @param requestC The continuation request to be sent.
	 * @return A list of query results from the neighbors.
	 */
	private ArrayList<QueryResultI> sendRequest(Map<NodeInfoI, SensorNodeP2POutboundPort> neighboursToSend, RequestContinuationI requestC){
	    ArrayList<QueryResultI> neighbourResults = new ArrayList<>();
	  
	    for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighboursToSend.entrySet()) {
	    	NodeInfoI neighbour = entry.getKey();
	    	SensorNodeP2POutboundPort port = entry.getValue();
            if (port != null) {
                try {
                    QueryResultI neighbourResult = requestC.isAsynchronous() ? null : port.execute(requestC);
                    if (requestC.isAsynchronous()) {
                        port.executeAsync(requestC);
                    } else {
                        neighbourResults.add(neighbourResult);
                    }
                    this.logMessage(nodeInfo.nodeIdentifier() + " : Request sent to neighbour " + neighbour.nodeIdentifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return neighbourResults;
	}

	
	/**
	 * Creates a default query result if the initial result is null, setting the type from neighbor results.
	 *
	 * @param result The initial result which might be null.
	 * @param neighbourResults Results received from neighbors to possibly integrate.
	 * @return A non-null query result either being the original or a new composite result.
	 */
	private QueryResultI ifIsNull(QueryResultI result, ArrayList<QueryResultI> neighbourResults) {
		if (result == null) {
			result = new QueryResult();
			for (QueryResultI neighbourResult : neighbourResults) {
	        	if (neighbourResult != null) {
	        		if (neighbourResult.isBooleanRequest()) {
	        			((QueryResult) result).setIsBoolean();
	        		}else {
	        			((QueryResult) result).setIsGather();
	        		}
	        	}
	        }
		}
		return result;
	}
	
	/**
	 * Merges results from various sources into a single cumulative result based on the type of the query (Boolean or Gather).
	 *
	 * @param result The main result into which to merge the neighbor results.
	 * @param neighbourResults The results from neighbors that need to be merged into the main result.
	 */
	private void mergeResults(QueryResultI result, ArrayList<QueryResultI> neighbourResults) {
		
        if (result.isBooleanRequest()) {
            for (QueryResultI neighbourResult : neighbourResults) {
                if (neighbourResult != null) {
                    result.positiveSensorNodes().addAll(neighbourResult.positiveSensorNodes());
                }
            }
        } else if (result.isGatherRequest()) {
            for (QueryResultI neighbourResult : neighbourResults) {
                if (neighbourResult != null) {
                    result.gatheredSensorsValues().addAll(neighbourResult.gatheredSensorsValues());
                }
            }
        }
	}
	
	/**
	 * Sends the result of a processed request back to the client.
	 *
	 * @param request The request containing the client connection information.
	 * @param exState The execution state containing the current result to be sent.
	 * @throws Exception If there is an error during the port connection or result sending.
	 */
	
	private void sendResultToClient(RequestI request, ExecutionStateI exState) throws Exception {
	    ConnectionInfoI clientConnInfo = request.clientConnectionInfo();
	    String clientInboundPort = ((BCM4JavaEndPointDescriptor)clientConnInfo.endPointInfo()).getInboundPortURI();
	    
	    this.logMessage("dans sendResultToClient");
		this.getOwner().doPortConnection(
			        this.outboundPortRequestR.getPortURI(),
			        clientInboundPort,
			        NodeClientConnector.class.getCanonicalName()
		);
		this.logMessage("apres connection ");

	    outboundPortRequestR.acceptRequestResult(request.requestURI(), exState.getCurrentResult());
	    this.logMessage(nodeInfo.nodeIdentifier() + " connected to client to send the result");
	}
	
	
    /**
     * Processes a given request received by client components asynchronously , evaluates the request using the node's
     * sensors and  forwards it to other nodes based on the request's nature (flooding or directional or empty).
     *
     * @param request the request to process
     * @return the result of the query execution
     * @throws Exception if there is an error in processing the request
     */
	
	@Override
	public void executeAsync(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		
		ExecutionStateI exState = new ExecutionState(prcNode);				

	    QueryResultI result;
	    Lock writeLock = rwLockRequetes.writeLock();
	    writeLock.lock();
	    try {
	    	requetesTraites.add(request.requestURI());
	    } finally {
	        writeLock.unlock();
	    }
        result = (QueryResultI) coderequest.eval(exState);
        
        exState.addToCurrentResult(result);	   
		

		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			
		    Lock writeLock2 = rwLockRequetes.writeLock();
		    writeLock2.lock();
			//node doit envoyer le resultat au client
		    try {
		    	sendResultToClient(request, exState);
		    } finally {
		        writeLock2.unlock();
		    }
		}
		
		Map<NodeInfoI, SensorNodeP2POutboundPort> neighboursToSend = new LinkedHashMap<>(); 
		
		if (exState.isDirectional()) {
			neighboursToSend = fingNeighboursDirectional(exState);
        } else if (exState.isFlooding()) {
        	neighboursToSend = fingNeighboursFlooding();
        }

		if (neighboursToSend.isEmpty()) {
			this.logMessage("no neighbours to send the request");
			
		    Lock writeLock2 = rwLockRequetes.writeLock();
		    writeLock2.lock();
		    try {
		    	sendResultToClient(request, exState);
		    } finally {
		        writeLock2.unlock();
		    }
		}
		else{	
			//construire RequestContinuationI pour passer la requete aux noeuds voisins
			RequestContinuationI requestCont = new RequestContinuation(
	                request.requestURI(),
	                request.getQueryCode(),
	                request.clientConnectionInfo(),
	                exState
	            );
            ((RequestContinuation) requestCont).setAsynchronous();
            sendRequest(neighboursToSend, requestCont);
		}		
	}
	
	
    /**
     * Processes a given request received by node components asynchronously , evaluates the request using the node's
     * sensors and  forwards it to other nodes based on the request's nature (flooding or directional or empty).
     *
     * @param request the request to process
     * @return the result of the query execution
     * @throws Exception if there is an error in processing the request
     */
	
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) requestContinuation.getQueryCode();
	    QueryResultI result = null;
	    ExecutionState executionState = null;
		executionState = ((ExecutionState) requestContinuation.getExecutionState()).copy();
		
	    Lock writeLock = rwLockRequetes.writeLock();
	    writeLock.lock();
		 
		try {
			//evaluer la requete si le noeud n'a pas deja traite cette requete
			if (requetesTraites.contains(requestContinuation.requestURI())) {
				this.logMessage(nodeInfo.nodeIdentifier() + " : i have already processed this request!");

				//node doit envoyer le resultat au client
			    sendResultToClient(requestContinuation, executionState);
				return;
			}
		} finally {
			writeLock.unlock();
		}
		
		Lock writeLock1 = rwLockRequetes.writeLock();	

	    
		writeLock1.lock();		 
		try {
			//ajout d'uri de la requete actuel a l'ensemble des requetes traitees
			requetesTraites.add(requestContinuation.requestURI());
		} finally {
		    writeLock1.unlock();
		}
		
	    //pour mettre a jour les valeurs d'execution state
		executionState.updateProcessingNode(prcNode);

		
		
		Map<NodeInfoI, SensorNodeP2POutboundPort> neighboursToSend = new LinkedHashMap<>();
		 
        if (executionState.isDirectional()) {

            // Si nous n'avons pas encore atteint le nombre maximum de sauts
            if(!executionState.noMoreHops()) {
                executionState.incrementHops();

                result = (QueryResultI) coderequest.eval(executionState);

                // Ajout du résultat courant
                executionState.addToCurrentResult(result);

                // Trouver les voisins dans les bonnes directions
                neighboursToSend = fingNeighboursDirectional(executionState);

            } else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : No more hops for me!");
            }

        } else if (executionState.isFlooding()) {
            // Si le nœud est dans maxDist
            if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {

                result = (QueryResultI) coderequest.eval(executionState);

                // Ajout du résultat courant
                executionState.addToCurrentResult(result);

                neighboursToSend = fingNeighboursFlooding();
            } else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : I am not in maximal distance!");
            }
        }

		if (neighboursToSend.isEmpty()) {
			
			this.logMessage("no neighbours to send the request");
			
		    Lock writeLock2 = rwLockRequetes.writeLock();
		    writeLock2.lock();
			//node doit envoyer le resultat au client
		    try {
				//node doit envoyer le resultat au client
				sendResultToClient(requestContinuation, executionState);
		    } finally {
		        writeLock2.unlock();
		    }

		}else {
			
			//creation d'une nouvelle requete avec un nouveau executionstate
			RequestContinuationI newReq = new RequestContinuation(
					requestContinuation.requestURI(),
					requestContinuation.getQueryCode(),
					requestContinuation.clientConnectionInfo(),
					executionState);
			if(requestContinuation.isAsynchronous()) {
				((RequestContinuation) newReq).setAsynchronous();
			}
			sendRequest(neighboursToSend, newReq);
		}	

	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	   
	}
	
	/**
	 * Updates the sensor data on this node, potentially recalculating any data driven by sensor inputs.
	 * This method also resets the list of processed requests to allow for fresh processing in a new operational cycle.
	 */
	
	public void updateSensors() {

	    Lock writeLock = rwLockCapteurs.writeLock();
	    writeLock.lock();
	    try {
	        // Updating each sensor's value
	        for (SensorDataI capteur : capteurs) {
	            ((SensorData) capteur).updateValue();
	        }
	        ((ProcessingNode) prcNode).updateSensorinfo(capteurs);
	        requetesTraites = new ArrayList<>();
	        for (SensorDataI capteur : capteurs) {
	        	this.logMessage("New sensor value : "+ capteur.getValue());
	        }
	        this.logMessage(nodeInfo.nodeIdentifier() + " : Sensors value updated ------------");
	    } finally {
	       
	        writeLock.unlock();
	    }
	}
	
	/**
	 * Checks if the specified node identifier is registered in the network.
	 *
	 * @param nodeIdentifier The identifier of the node to check.
	 * @return true if the node is registered; false otherwise.
	 * @throws Exception If the registration check cannot be performed.
	 */
	
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.outboundPortRegistration.registered(nodeIdentifier);
	}
	
	/**
	 * Unregisters a node identified by the specified identifier from the network.
	 *
	 * @param nodeIdentifier The identifier of the node to unregister.
	 * @throws Exception If the node cannot be unregistered.
	 */
	
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.outboundPortRegistration.findNewNeighbour(nodeInfo, d);

	}
	
	/**
	 * Unregisters a node identified by the specified identifier from the network.
	 *
	 * @param nodeIdentifier The identifier of the node to unregister.
	 * @throws Exception If the node cannot be unregistered.
	 */
	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.outboundPortRegistration.unregister(nodeIdentifier);		
	}
    
	/**
	 * Retrieves the node information associated with this plugin instance.
	 *
	 * @return The current node information.
	 */
	public NodeInfoI getNodeInfo() {
		return this.nodeInfo;
	}

	/**
	 * Schedules the registration task of this node upon plugin execution. This method uses an accelerated clock
	 * to time the registration task appropriately within a simulated time framework.
	 *
	 * @throws Exception If there are issues executing the plugin logic, particularly with scheduling tasks.
	 */
	public void			executePlugin() throws Exception{
		
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
		NodePlugin.cptDelay+=10;
		Instant i1 = ac.getStartInstant().plusSeconds(NodePlugin.cptDelay++);
		long dRegister = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes		
		
        Instant i2 = ac.getStartInstant().plusSeconds(CVM.NB_NODES + NodePlugin.cptDelay);
        long dUpdateSensors = ac.nanoDelayUntilInstant(i2); // délai en nanosecondes
		
		this.getOwner().scheduleTask(
				o -> { 
					try {
						this.register(nodeInfo) ;
					} catch (Exception e) {
						e.printStackTrace();
					}					
				this.getOwner().scheduleTask(
                      b -> { 
                          try {
                              this.updateSensors() ;
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      },
              dUpdateSensors, TimeUnit.NANOSECONDS);

          },
		dRegister, TimeUnit.NANOSECONDS);
		
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
//		this.getOwner().doPortDisconnection(outboundPortRegistration.getPortURI());
//		
//		for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
//			SensorNodeP2POutboundPort port = entry.getValue();
//			this.getOwner().doPortDisconnection(port.getPortURI());
//		}
//		
//		this.getOwner().doPortDisconnection(clockOutboundPort.getPortURI());
//		this.getOwner().doPortDisconnection(outboundPortRequestR.getPortURI());
//		
//		super.finalise();
		
	}
	
	@Override
	public void			uninstall() throws Exception
	{
//		this.outboundPortRegistration.unpublishPort();
//		this.outboundPortRegistration.destroyPort();
//		this.clockOutboundPort.unpublishPort();
//		this.clockOutboundPort.destroyPort();
//		this.outboundPortRequestR.unpublishPort();
//		this.outboundPortRequestR.destroyPort();
//
//		for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
//			SensorNodeP2POutboundPort port = entry.getValue();
//			port.unpublishPort();
//			port.destroyPort();
//		}
//		this.neighbourPortMap.clear();
//		this.neighbourPortMap = null;
//		
//		this.inboundPortRequesting.unpublishPort();
//		this.inboundPortRequesting.destroyPort();
//		this.inboundPortP2P.unpublishPort();
//		this.inboundPortP2P.destroyPort();
//		this.removeOfferedInterface(RequestingCI.class);
//		this.removeOfferedInterface(SensorNodeP2PCI.class);
//		this.removeRequiredInterface(RegistrationCI.class);
	}

}