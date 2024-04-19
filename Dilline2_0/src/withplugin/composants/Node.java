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
import composants.noeud.RequestResultOutboundPort;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.AddPlugin;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import langage.interfaces.QueryI;
import plugins.NodePlugin;


@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class, ClocksServerCI.class})

public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	
	
	protected ClocksServerOutboundPort clockOutboundPort;
	
	protected ArrayList<String> requetesTraites;
	protected RequestResultOutboundPort outboundPortRequestR;
	
	protected NodeInfoI nodeInfo;
	protected ProcessingNodeI prcNode;
	protected ArrayList<SensorDataI> capteurs;
	public static int cptDelay = 0;
	
	protected ReadWriteLock rwLock;
	protected NodePlugin plugin;
	

	
	protected static final String NODE_PLUGIN_URI = 
												"nodePluginURI";
	
	protected Node(NodeInfoI node, ArrayList<SensorDataI> sensors ) throws Exception{	
			super(1, 1) ;
			
			plugin= new NodePlugin(node);
			plugin.setPluginURI(NODE_PLUGIN_URI);
			
			this.outboundPortRequestR = new RequestResultOutboundPort(this);
			this.outboundPortRequestR.publishPort();
			
			this.nodeInfo = node;
			
			this.capteurs = sensors.stream()
                    .map(sensor -> ((SensorData)sensor).copy())
                    .collect(Collectors.toCollection(ArrayList::new));
			
			prcNode = new ProcessingNode(nodeInfo, capteurs);
			requetesTraites = new ArrayList<>();
			
			this.rwLock = new ReentrantReadWriteLock();
			this.initialise();
		}
	
	
	protected Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
	    this.logMessage(this.nodeInfo.nodeIdentifier() + " is registering...");
	    return this.plugin.register(nodeInfo);
	}
	
	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		this.plugin.ask4Connection(neighbour);
	}


	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.plugin.ask4Disconnection(neighbour);
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
	    Lock readLock = rwLock.readLock();
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
   
	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
	    	
	    	requetesTraites.add(request.requestURI());
	    	
	        // Update the processing node
	        ExecutionState executionState = (ExecutionState) request.getExecutionState();
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
	    } finally {
	        writeLock.unlock();
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
		
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		 
		try {
			//evaluer la requete sur le premier noeud
			result = (QueryResultI) coderequest.eval(exState);
			
			requetesTraites.add(request.requestURI());
			
			//cas de continuation vide
			if(!exState.isContinuationSet()) {
				return result;
			}
		} finally {
		    writeLock.unlock();
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
	


	


	private void sendResultToClient(RequestI request, ExecutionStateI exState) throws Exception {
	    ConnectionInfoI clientConnInfo = request.clientConnectionInfo();
	    String clientInboundPort = ((BCM4JavaEndPointDescriptor)clientConnInfo.endPointInfo()).getInboundPortURI();
	    
	    // Node must send the result to the client
	    this.doPortConnection(
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
	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
	    	requetesTraites.add(request.requestURI());
	        result = (QueryResultI) coderequest.eval(exState);
	        if (result == null)  {
	        	this.logMessage("result is null");
	        }else {
	        	this.logMessage("resultat = " + result.isBooleanRequest());
	        	this.logMessage("resultat = " + result.positiveSensorNodes());
	        }
	        
	        exState.addToCurrentResult(result);
	        this.logMessage("actualResult= " + exState.getCurrentResult().positiveSensorNodes());
	    } finally {
	        writeLock.unlock();
	    }
		

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
		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			//evaluer la requete si le noeud n'a pas deja traite cette requete
			if (requetesTraites.contains(requestContinuation.requestURI())) {
				this.logMessage(nodeInfo.nodeIdentifier() + " : i have already processed this request!");
				return;
			}
		} finally {
		    readLock.unlock();
		}
		
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();		 
		try {
			//ajout d'uri de la requete actuel a l'ensemble des requetes traitees
			requetesTraites.add(requestContinuation.requestURI());
			
		} finally {
		    writeLock.unlock();
		}
		this.plugin.executeAsync(requestContinuation);
	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	   
	}
	
	
	public void updateSensors() {

	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
	        // Updating each sensor's value
	        for (SensorDataI capteur : capteurs) {
	            ((SensorData) capteur).updateValue();
	        }
	        prcNode = new ProcessingNode(nodeInfo, capteurs); 
	        this.logMessage(nodeInfo.nodeIdentifier() + " : Sensors value updated ------------");
	    } finally {
	       
	        writeLock.unlock();
	    }
	}
	
	@Override
    public void start() throws ComponentStartException
    {
        this.logMessage("starting node component.");
        
        
        try {
        	
          //connection entre node et clock
      	  this.clockOutboundPort = new ClocksServerOutboundPort(this);
      	  this.clockOutboundPort.publishPort();
            this.doPortConnection(
          		  this.clockOutboundPort.getPortURI(),
      			  ClocksServer.STANDARD_INBOUNDPORT_URI,
      			  ClocksServerConnector.class.getCanonicalName());
            
		} catch (Exception e) {
			e.printStackTrace();
		}

        super.start();
    }
	
	protected void		initialise() throws Exception
	{	
		this.getTracer().setTitle("Node " + nodeInfo.nodeIdentifier()) ;
		this.getTracer().setRelativePosition(CVM.number/5 + 1, CVM.number % 5) ;
		CVM.number++;
		this.toggleTracing() ;
		this.toggleLogging();

	}
	
	@Override
	public void	execute() throws Exception
	{
		super.execute() ;
		
		//Install the plug-in 
		this.installPlugin(plugin);
				
		AcceleratedClock ac = this.clockOutboundPort.getClock(CVM.TEST_CLOCK_URI);
		ac.waitUntilStart();
		Instant i1 = ac.getStartInstant().plusSeconds(Node.cptDelay++);
		long dRegister = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes		
		
		Instant i2 = ac.getStartInstant().plusSeconds(CVM.NB_NODES + Node.cptDelay);
		long dUpdateSensors = ac.nanoDelayUntilInstant(i2); // délai en nanosecondes		
		
		this.scheduleTask(
				o -> { 
					try {
						this.register(nodeInfo) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					this.scheduleTask(
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
	public synchronized void finalise() throws Exception {
		
		this.doPortDisconnection(clockOutboundPort.getPortURI());
		this.doPortDisconnection(outboundPortRequestR.getPortURI());
		this.logMessage("stopping node component.");
        this.printExecutionLogOnFile("node");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			
			this.clockOutboundPort.unpublishPort();
			this.clockOutboundPort.destroyPort();
			this.outboundPortRequestR.unpublishPort();
			this.outboundPortRequestR.destroyPort();
			
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}	

}
