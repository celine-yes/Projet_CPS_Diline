package plugins;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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
//import composants.noeud.RequestingInboundPort;
//import composants.noeud.SensorNodeP2PInboundPort;
import composants.noeud.SensorNodeP2POutboundPort;

import withplugin.ports.SensorNodeP2PInboundPort;
import withplugin.ports.RequestingInboundPort;


import cvm.CVM;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
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

public class NodePlugin extends AbstractPlugin implements RequestingCI, SensorNodeP2PCI, RegistrationCI{

	private static final long serialVersionUID = 1L;
	
	protected RegistrationOutboundPort	outboundPortRegistration;
	protected ClocksServerOutboundPort clockOutboundPort;
	protected Map<NodeInfoI, SensorNodeP2POutboundPort> neighbourPortMap;
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

	
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				NODE_POOL_URI =
													"node pool URI";
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				CLIENT_POOL_URI =
													"client pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				NODE_POOL_SIZE = 3;
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				CLIENT_POOL_SIZE = 2;
	
	protected static final String NODE_PLUGIN_URI = 
												"nodePluginURI";
	
	public NodePlugin(NodeInfoI nodeinfo, ArrayList<SensorDataI> sensors ) throws Exception {
		this.nodeInfo = nodeinfo;
		this.neighbourPortMap = new HashMap<>();
		this.rwLockRequetes = new ReentrantReadWriteLock();
		this.rwLockCapteurs = new ReentrantReadWriteLock();
		
		this.capteurs = sensors.stream()
                .map(sensor -> ((SensorData)sensor).copy())
                .collect(Collectors.toCollection(ArrayList::new));
		
		prcNode = new ProcessingNode(nodeInfo, capteurs);
		requetesTraites = new ArrayList<>();
		
	}

    public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		// Add the interface		
		this.addOfferedInterface(SensorNodeP2PCI.class);
		this.addOfferedInterface(RequestingCI.class);
		this.addRequiredInterface(RegistrationCI.class);
		
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
		
        
        
    }
	
    
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
	    this.logMessage(this.nodeInfo.nodeIdentifier() + " is registering...");
	    Set<NodeInfoI> neighbours = this.outboundPortRegistration.register(nodeInfo);

//	    Lock writeLock = rwLock.writeLock();
//	    writeLock.lock();
//	    try {
	        if (this.outboundPortRegistration.registered(nodeInfo.nodeIdentifier())) {
	            this.logMessage(nodeInfo.nodeIdentifier() + " registered!");
	        }

	        for (NodeInfoI neighbour : neighbours) {
	            if (neighbour != null) {
	                System.out.println(neighbour.nodeIdentifier());
	                SensorNodeP2POutboundPort outboundport = new SensorNodeP2POutboundPort(this.getOwner());
	                outboundport.publishPort();
	                this.neighbourPortMap.put(neighbour, outboundport);

	                this.getOwner().doPortConnection(
	                    outboundport.getPortURI(),
	                    ((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
	                    NodeNodeConnector.class.getCanonicalName());

	                this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
	                outboundport.ask4Connection(nodeInfo);
	            }
	        }
//	    } finally {
//	        writeLock.unlock();
//	    }

	    return neighbours;
	}
	
	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
	    SensorNodeP2POutboundPort outboundport = new SensorNodeP2POutboundPort(this.getOwner());
	    outboundport.publishPort();

//	    Lock writeLock = rwLock.writeLock();
//	    writeLock.lock();
//	    try {
	        this.neighbourPortMap.put(neighbour, outboundport);

	        this.getOwner().doPortConnection(
	            outboundport.getPortURI(),
	            ((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
	            NodeNodeConnector.class.getCanonicalName());

	        this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
//	    } finally {
//	        writeLock.unlock();
//	    }
	}


	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().doPortDisconnection(
				((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI()) ;
		this.logMessage(nodeInfo.nodeIdentifier() + " disconnected from " + neighbour.nodeIdentifier());
	}
	

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) request.getQueryCode();
	    QueryResultI result = null;
	    PositionI posNodeAct = nodeInfo.nodePosition();
	    PositionI posNeighbour = null;
	    Direction d = null;
	    
		//evaluer la requete si le noeud n'a pas deja traite cette requete
	    Lock readLock = rwLockRequetes.readLock();
	    readLock.lock();
	    
	    try {
	    	if (requetesTraites.contains(request.requestURI())) {
				this.logMessage(nodeInfo.nodeIdentifier() + " : i have already processed this request!");
				return null;
			}
	    } finally {
	        readLock.unlock();
	    }
		
        ArrayList<NodeInfoI> neighboursToSend = new ArrayList<>();
        ArrayList<QueryResultI> neighbourResults = null;
   
	    Lock writeLock = rwLockRequetes.writeLock();
	    writeLock.lock();
	    try {
	    	
	    	requetesTraites.add(request.requestURI());
	    } finally {
	        writeLock.unlock();
	    }
	    
        // Update the processing node
         ExecutionState executionState = ((ExecutionState) request.getExecutionState()).copy();

        executionState.updateProcessingNode(prcNode);

        if (executionState.isDirectional()) {
        	if(!executionState.noMoreHops()) {
        		
	            executionState.incrementHops();
	            result = (QueryResultI) coderequest.eval(executionState);

	            for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	                NodeInfoI neighbour = entry.getKey();
	                posNeighbour = neighbour.nodePosition();
	                d = posNodeAct.directionFrom(posNeighbour);
	                
	                if (executionState.getDirections().contains(d)) {
	                    neighboursToSend.add(neighbour);
	                }
	            }
        	} else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : No more hops for me!");
            }
        } else if (executionState.isFlooding()) {
            // Si le nœud est dans maxDist
            if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {

                result = (QueryResultI) coderequest.eval(executionState);

	            for (NodeInfoI neighbour : neighbourPortMap.keySet()) {
	                neighboursToSend.add(neighbour);
	            }
            } else {
                this.logMessage(nodeInfo.nodeIdentifier() + " : I am not in maximal distance!");
            }
        }
	   
	    
        neighbourResults = sendRequest(neighboursToSend, request);
        result = ifIsNull(result, neighbourResults);
        mergeResults(result, neighbourResults);
	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	    return result;
	}


	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		
		ExecutionStateI exState = new ExecutionState(prcNode);
		QueryResultI result;
		
		//evaluer la requete sur le premier noeud
		result = (QueryResultI) coderequest.eval(exState);
		
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
		

		ArrayList <QueryResultI> neighbourResults = null;
		ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();
		PositionI posNodeAct = nodeInfo.nodePosition();
		
		findNeighboursToSend(neighboursToSend, posNodeAct, exState);
		
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
	
	private ArrayList<QueryResultI> sendRequest(ArrayList<NodeInfoI> neighboursToSend, RequestContinuationI requestC){
	    ArrayList<QueryResultI> neighbourResults = new ArrayList<>();
	    
//	    Lock readLock = rwLock.readLock();
//	    readLock.lock();
//	    try {
	        for (NodeInfoI neighbourToSend : neighboursToSend) {
	            SensorNodeP2POutboundPort port = neighbourPortMap.get(neighbourToSend);
	            if (port != null) {
	                try {
	                    QueryResultI neighbourResult = requestC.isAsynchronous() ? null : port.execute(requestC);
	                    if (requestC.isAsynchronous()) {
	                        port.executeAsync(requestC);
	                    } else {
	                        neighbourResults.add(neighbourResult);
	                    }
	                    this.logMessage(nodeInfo.nodeIdentifier() + " : Request sent to neighbour " + neighbourToSend.nodeIdentifier());
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
//	    } finally {
//	        readLock.unlock();
//	    }
	    return neighbourResults;
	}

	
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
	
	
	private void mergeResults(QueryResultI result, ArrayList<QueryResultI> neighbourResults) {
//	    Lock writeLock = rwLock.writeLock();
//	    writeLock.lock();
//	    try {
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
//	    } finally {
//	        writeLock.unlock();
//	    }
	}
	
	private void findNeighboursToSend(ArrayList<NodeInfoI> neighboursToSend, PositionI posNodeAct, ExecutionStateI exState) {
//	    Lock readLock = rwLock.readLock();
//	    readLock.lock();
//	    try {
	        if (exState.isDirectional()) {
	            for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	                NodeInfoI neighbour = entry.getKey();
	                PositionI posNeighbour = neighbour.nodePosition();
	                Direction d = posNodeAct.directionFrom(posNeighbour);
	                if (exState.getDirections().contains(d)) {
	                    neighboursToSend.add(neighbour);
	                }
	            }
	        } else if (exState.isFlooding()) {
	            neighbourPortMap.keySet().forEach(neighboursToSend::add);
	        }
//	    } finally {
//	        readLock.unlock();
//	    }
	}

	private void sendResultToClient(RequestI request, ExecutionStateI exState) throws Exception {
	    ConnectionInfoI clientConnInfo = request.clientConnectionInfo();
	    String clientInboundPort = ((BCM4JavaEndPointDescriptor)clientConnInfo.endPointInfo()).getInboundPortURI();
	    
	    // Node must send the result to the client
	    this.getOwner().doPortConnection(
	        this.outboundPortRequestR.getPortURI(),
	        clientInboundPort,
	        NodeClientConnector.class.getCanonicalName()
	    );
	    outboundPortRequestR.acceptRequestResult(request.requestURI(), exState.getCurrentResult());
	    this.logMessage(nodeInfo.nodeIdentifier() + " connected to client to send the result");
	}
	
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
//	        if (result == null)  {
//	        	this.logMessage("result is null");
//	        }else {
//	        	this.logMessage("resultat = " + result.isBooleanRequest());
//	        	this.logMessage("resultat = " + result.positiveSensorNodes());
//	        }
        
        exState.addToCurrentResult(result);
        //this.logMessage("actualResult= " + exState.getCurrentResult().positiveSensorNodes());
	   
		

		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			//node doit envoyer le resultat au client
			sendResultToClient(request, exState);	
		}
		
		ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>(); 
		PositionI posNodeAct = nodeInfo.nodePosition();
		
		findNeighboursToSend(neighboursToSend, posNodeAct, exState);
		
		if (neighboursToSend.isEmpty()) {
			this.logMessage("no neighbours to send the request");
			//node doit envoyer le resultat au client
			sendResultToClient(request, exState);
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
	

	
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) requestContinuation.getQueryCode();
	    QueryResultI result = null;
	    ExecutionState executionState = null;
		executionState = ((ExecutionState) requestContinuation.getExecutionState()).copy();
		Lock readLock = rwLockRequetes.readLock();
		readLock.lock();
		 
		try {
			//evaluer la requete si le noeud n'a pas deja traite cette requete
			if (requetesTraites.contains(requestContinuation.requestURI())) {
				this.logMessage(nodeInfo.nodeIdentifier() + " : i have already processed this request!");
				sendResultToClient(requestContinuation, executionState);	
				return;
			}
		} finally {
		    readLock.unlock();
		}
		
		Lock writeLock = rwLockRequetes.writeLock();	

	    
		writeLock.lock();		 
		try {
			//ajout d'uri de la requete actuel a l'ensemble des requetes traitees
			requetesTraites.add(requestContinuation.requestURI());
		} finally {
		    writeLock.unlock();
		}
	    //pour mettre a jour les valeurs d'execution state
		
		//this.logMessage("Result recu = " + executionState.getCurrentResult().positiveSensorNodes());
	    executionState.updateProcessingNode(prcNode);
		//this.logMessage("executionState copied");
		
		
	    PositionI posNodeAct = nodeInfo.nodePosition();
	    PositionI posNeighbour = null;
	    Direction d = null;
	    ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();
		 
	    writeLock.lock(); // Verrou d'écriture pour modifier executionState
	    try {
	        if (executionState.isDirectional()) {

	            // Si nous n'avons pas encore atteint le nombre maximum de sauts
	            if(!executionState.noMoreHops()) {
	            	//this.logMessage(executionState.);
	                executionState.incrementHops();
	               

	                result = (QueryResultI) coderequest.eval(executionState);
	                //if (result == null)  this.logMessage("result is null");
	                // Ajout du résultat courant
	                executionState.addToCurrentResult(result);
	                //this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());

	                // Trouver les voisins dans les bonnes directions
	                for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	                    NodeInfoI neighbour = entry.getKey();
	                    posNeighbour = neighbour.nodePosition();
	                    d = posNodeAct.directionFrom(posNeighbour);

	                    if(executionState.getDirections().contains(d)) {
	                        neighboursToSend.add(neighbour);
	                    }
	                }
	            } else {
	                this.logMessage(nodeInfo.nodeIdentifier() + " : No more hops for me!");
	            }

	        } else if (executionState.isFlooding()) {
	            // Si le nœud est dans maxDist
	            if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {

	                result = (QueryResultI) coderequest.eval(executionState);

	                // Ajout du résultat courant
	                executionState.addToCurrentResult(result);
	                //this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());

		            for (NodeInfoI neighbour : neighbourPortMap.keySet()) {
		                neighboursToSend.add(neighbour);
		            }
	            } else {
	                this.logMessage(nodeInfo.nodeIdentifier() + " : I am not in maximal distance!");
	            }
	        }
	    } finally {
	        writeLock.unlock();
	    }

		if (neighboursToSend.isEmpty()) {
			
			this.logMessage("no neighbours to send the request");
			//node doit envoyer le resultat au client
			sendResultToClient(requestContinuation, executionState);
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
	
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.outboundPortRegistration.registered(nodeIdentifier);
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.outboundPortRegistration.findNewNeighbour(nodeInfo, d);

	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.outboundPortRegistration.unregister(nodeIdentifier);		
	}
    
	public NodeInfoI getNodeInfo() {
		return this.nodeInfo;
	}
    
    
	@Override
	public void			initialise() throws Exception
	{
		
		//connection entre node et clock
		//System.out.println("-----------------------------" + this.getOwner());
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

	
	public void			executePlugin() throws Exception{
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
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
