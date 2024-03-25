package composants.noeud;

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
import classes.ExecutionState;
import classes.ProcessingNode;
import classes.QueryResult;
import classes.RequestContinuation;
import classes.SensorData;
import composants.connector.NodeClientConnector;
import composants.connector.NodeNodeConnector;
import composants.connector.NodeRegisterConnector;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
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
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI;
import langage.interfaces.QueryI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.*;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import classes.RequestContinuation;


@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class, ClocksServerCI.class})
public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	
	protected NodeRegistrationOutboundPort	outboundPortRegistration;
	protected ClocksServerOutboundPort clockOutboundPort;
	protected Map<NodeInfoI, NodeSensorNodeP2POutboundPort> neighbourPortMap;
	protected ArrayList<String> requetesTraites;
	
	protected NodeRequestingInboundPort	inboundPortRequesting ;
	protected NodeSensorNodeP2PInboundPort inboundPortP2P ;
	protected NodeRequestResultOutboundPort outboundPortRequestR;
	
	protected NodeInfoI nodeInfo;
	protected ProcessingNodeI prcNode;
	protected ArrayList<SensorDataI> capteurs;
	public static int cptDelay = 0;
	
	protected ReadWriteLock rwLock;
	
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				NODE_POOL_URI =
													"node pool URI";
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				CLIENT_POOL_URI =
													"client pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				NODE_POOL_SIZE = 5;
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				CLIENT_POOL_SIZE = 2;
	
	protected Node(String ibPortRequesting, String ibPortP2P, String outboundPortRequestR, String obPortRegistration
			, NodeInfoI node, ArrayList<SensorDataI> sensors ) throws Exception{	
			super(1, 1) ;
			
			this.inboundPortRequesting = new NodeRequestingInboundPort(ibPortRequesting, this, CLIENT_POOL_URI);
			this.inboundPortP2P = new NodeSensorNodeP2PInboundPort(ibPortP2P, this, NODE_POOL_URI);
			this.outboundPortRequestR = new NodeRequestResultOutboundPort(outboundPortRequestR, this);
			this.outboundPortRegistration = new NodeRegistrationOutboundPort(obPortRegistration, this);
			this.neighbourPortMap = new HashMap<>();
			
			this.inboundPortRequesting.publishPort();
			this.inboundPortP2P.publishPort();
			this.outboundPortRegistration.publishPort();
			this.outboundPortRequestR.publishPort();

			this.capteurs = sensors;
			this.nodeInfo = node;
			prcNode = new ProcessingNode(nodeInfo, capteurs);
			requetesTraites = new ArrayList<String>();
			
			this.rwLock = new ReentrantReadWriteLock();
			this.initialise();
		}
	
	
    public void addPortToNeighbourMap(NodeInfoI neighbour, NodeSensorNodeP2POutboundPort port) {
        this.neighbourPortMap.put(neighbour, port);
    }
    
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		this.logMessage(this.nodeInfo.nodeIdentifier() + " is registering...");
		Set<NodeInfoI> neighbours  = this.outboundPortRegistration.register(nodeInfo);

		if(this.outboundPortRegistration.registered(nodeInfo.nodeIdentifier())) {
			this.logMessage(nodeInfo.nodeIdentifier() + " registered!");
		}
		
		for (NodeInfoI neighbour: neighbours) {
			if(neighbour != null) {
				System.out.println(neighbour.nodeIdentifier());
				NodeSensorNodeP2POutboundPort outboundport = new NodeSensorNodeP2POutboundPort("OutP2PVoisin" + neighbour.nodeIdentifier(),this);
				outboundport.publishPort();				
				addPortToNeighbourMap(neighbour, outboundport);	
				
				this.doPortConnection(
						outboundport.getPortURI(),
						((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
						NodeNodeConnector.class.getCanonicalName()) ;
				
				this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
				outboundport.ask4Connection(nodeInfo);
			}
		}

		return neighbours;
	}
	
	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		NodeSensorNodeP2POutboundPort outboundport = new NodeSensorNodeP2POutboundPort("OutP2PVoisin" + neighbour.nodeIdentifier(),this);
		outboundport.publishPort();	
		addPortToNeighbourMap(neighbour, outboundport);
		
		this.doPortConnection(
				outboundport.getPortURI(),
				((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI(),
				NodeNodeConnector.class.getCanonicalName()) ;
		
		this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.doPortDisconnection(
				((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI()) ;
		this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + neighbour.nodeIdentifier());
	}
	

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) request.getQueryCode();
	    QueryResultI result = null;

	    PositionI posNodeAct = nodeInfo.nodePosition();
	    PositionI posNeighbour = null;
	    Direction d = null;
	    
	    //pour mettre a jour les valeurs d'execution state
	    ExecutionState executionState = (ExecutionState) request.getExecutionState();
	    executionState.updateProcessingNode(prcNode);
	    
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
		

	    ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();
	    //les resultats des voisins
	    ArrayList <QueryResultI> neighbourResults = null;
	    
	    if (executionState.isDirectional()) {
	        
	        // Si nous n'avons pas encore atteint le nombre maximum de sauts
	        if(! executionState.noMoreHops()) {
	        	
	        	executionState.incrementHops();
	        	 //evaluation de la requete sur le noeud actuel
	    	    result = (QueryResultI) coderequest.eval(request.getExecutionState());
	    	    
	    	    //Trouver les voisins dans les bonnes directions
	    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
    		        posNeighbour = neighbour.nodePosition();
    				d = posNodeAct.directionFrom(posNeighbour);
    				
    				if(executionState.getDirections().contains(d)) {
    					 neighboursToSend.add(neighbour);
    				}		
	    	    }	
	        }else {this.logMessage(nodeInfo.nodeIdentifier() + " : no more hops for me!");}
	        
	    } else if (executionState.isFlooding()) {
	    	//si le noued est dans maxDist
	    	if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {
	    		
	        	 //evaluation de la requete sur le noeud actuel
	    	    result = (QueryResultI) coderequest.eval(request.getExecutionState());
	    		
	    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
					neighboursToSend.add(neighbour);
	    	    } 
	    	}else {this.logMessage(nodeInfo.nodeIdentifier() + " : i am not in maximal distance!");}
	    }
	    
	    neighbourResults = sendRequest(neighboursToSend, request);
	    result = ifIsNull(result, neighbourResults);
	    //fusionner tous les resultats 
	    mergeResults(result,neighbourResults);

	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	    return result;
	}


	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		QueryResultI result;
		
		ExecutionStateI exState = new ExecutionState(prcNode);
		
		//evaluer la requete sur le premier noeud
		result = (QueryResultI) coderequest.eval(exState);
		
		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			return result;
		}
		
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		 
		try {
			requetesTraites.add(request.requestURI());
		} finally {
		    writeLock.unlock();
		}
		
		
		//construire RequestContinuationI pour passer la requete aux noeuds voisins
		RequestContinuationI requestCont = new RequestContinuation(
				request.requestURI(),
				request.getQueryCode(),
				request.clientConnectionInfo(),
				exState);
		
		ArrayList <QueryResultI> neighbourResults = null;
		ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>(); 
		
		if (exState.isDirectional()) {
			PositionI posNodeAct = nodeInfo.nodePosition();
		    	    
			//Trouver les voisins dans les bonnes directions
    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
    	        NodeInfoI neighbour = entry.getKey();
		        PositionI posNeighbour = neighbour.nodePosition();
				Direction d = posNodeAct.directionFrom(posNeighbour);
				
				if(exState.getDirections().contains(d)) {
					neighboursToSend.add(neighbour);
				}
    	    }
    	 }else {
			 for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
	    	        neighboursToSend.add(neighbour);
					
	    	 }
    	}
		
		neighbourResults = sendRequest(neighboursToSend, requestCont);
	    //fusionner tous les resultats 
	    mergeResults(result,neighbourResults);
	    
		this.logMessage(nodeInfo.nodeIdentifier() + " : request processed !");
		return result;
	}
	
	public ArrayList<QueryResultI> sendRequest(ArrayList<NodeInfoI> neighboursToSend,RequestContinuationI requestC){
		
	    ArrayList <QueryResultI> neighbourResults = new ArrayList<QueryResultI>();
	    
		for (NodeInfoI neighbourToSend : neighboursToSend ) {
	        NodeSensorNodeP2POutboundPort port = neighbourPortMap.get(neighbourToSend);
			QueryResultI neighbourResult = null;
			try {
				if (requestC.isAsynchronous()) {
					port.executeAsync(requestC);
				}
				else {
					neighbourResult = port.execute(requestC);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.logMessage(nodeInfo.nodeIdentifier() + " : Request sent to neighbour " + neighbourToSend.nodeIdentifier());
			neighbourResults.add(neighbourResult);
	       
	    }
		return neighbourResults;
	}
	
	public QueryResultI ifIsNull(QueryResultI result, ArrayList<QueryResultI> neighbourResults) {
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
	
	
	public void mergeResults(QueryResultI result, ArrayList<QueryResultI> neighbourResults) {
		
	    if (result.isBooleanRequest()) {
	        // Si la requête est de type Bquery
	        for (QueryResultI neighbourResult : neighbourResults) {
	        	if (neighbourResult != null) {
	        		result.positiveSensorNodes().addAll(neighbourResult.positiveSensorNodes());
	        	}
	        }
	    } else if (result.isGatherRequest()) {
	        // Si la requête est de type Gquery
	        for (QueryResultI neighbourResult : neighbourResults) {
	        	if (neighbourResult != null) {
	        		result.gatheredSensorsValues().addAll(neighbourResult.gatheredSensorsValues());
	        	}
	        }
	    }
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		QueryResultI result;
		
		ExecutionStateI exState = new ExecutionState(prcNode);
		//les infos du client 
		ConnectionInfoI clientConnInfo = request.clientConnectionInfo();
		String clientInboundPort = ((BCM4JavaEndPointDescriptor)clientConnInfo.endPointInfo()).getInboundPortURI();
		
		//evaluer la requete sur le premier noeud
		result = (QueryResultI) coderequest.eval(exState);
		// ajout du resultat courant
		exState.addToCurrentResult(result);
		this.logMessage("actualResult= " + exState.getCurrentResult().positiveSensorNodes());
		
		//ajout d'uri de la requete actuel a l'ensemble des requetes traitees
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		 
		try {
			requetesTraites.add(request.requestURI());
		} finally {
		    writeLock.unlock();
		}
		
		
		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			//node doit envoyer le resultat au client
			this.doPortConnection(
					this.outboundPortRequestR.getPortURI(),
					clientInboundPort,
					NodeClientConnector.class.getCanonicalName());
			outboundPortRequestR.acceptRequestResult(request.requestURI(), exState.getCurrentResult());
			this.logMessage(nodeInfo.nodeIdentifier() + " connected to client to send the result ");	
			
		}
		
		//construire RequestContinuationI pour passer la requete aux noeuds voisins
		RequestContinuationI requestCont = new RequestContinuation(
				request.requestURI(),
				request.getQueryCode(),
				request.clientConnectionInfo(),
				exState);
		((RequestContinuation) requestCont).setAsynchronous();
		ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>(); 
		
		if (exState.isDirectional()) {
			PositionI posNodeAct = nodeInfo.nodePosition();
		    	    
			//Trouver les voisins dans les bonnes directions
    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
    	        NodeInfoI neighbour = entry.getKey();
		        PositionI posNeighbour = neighbour.nodePosition();
				Direction d = posNodeAct.directionFrom(posNeighbour);
				
				if(exState.getDirections().contains(d)) {
					neighboursToSend.add(neighbour);
				}
    	    }
    	 }else {
			 for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
	    	        neighboursToSend.add(neighbour);
					
	    	 }
    	}
		if (neighboursToSend.size() == 0) {
			this.logMessage("no neighbours to send the request");
			//node doit envoyer le resultat au client
			this.doPortConnection(
					this.outboundPortRequestR.getPortURI(),
					clientInboundPort,
					NodeClientConnector.class.getCanonicalName()) ;
			outboundPortRequestR.acceptRequestResult(request.requestURI(), exState.getCurrentResult());
			this.logMessage(nodeInfo.nodeIdentifier() + " connected to client to send the result");		
		}
		else{
			sendRequest(neighboursToSend, requestCont);
		}		
	}
	
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {

		this.logMessage(nodeInfo.nodeIdentifier() + " : processing continuation request...");
	    QueryI coderequest = (QueryI) requestContinuation.getQueryCode();
	    QueryResultI result = null;
	    
	    //pour mettre a jour les valeurs d'execution state
	    ExecutionState executionState = (ExecutionState) requestContinuation.getExecutionState();
	    executionState.updateProcessingNode(prcNode);

		//les infos du client 
		ConnectionInfoI clientConnInfo = requestContinuation.clientConnectionInfo();
		String clientInboundPort = ((BCM4JavaEndPointDescriptor)clientConnInfo.endPointInfo()).getInboundPortURI();
		
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

	    PositionI posNodeAct = nodeInfo.nodePosition();
	    PositionI posNeighbour = null;
	    Direction d = null;
	    ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();

	    if (executionState.isDirectional()) {
	    	this.logMessage("executionState.isDirectional()");
	        
	        // Si nous n'avons pas encore atteint le nombre maximum de sauts
	        if(! executionState.noMoreHops()) {	
	        	executionState.incrementHops();
	    		result = (QueryResultI) coderequest.eval(executionState);
	    		// ajout du resultat courant
	    		executionState.addToCurrentResult(result);
	    		this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());
	    	    
	    		//Trouver les voisins dans les bonnes directions
	    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
    		        posNeighbour = neighbour.nodePosition();
    				d = posNodeAct.directionFrom(posNeighbour);
    				
    				if(executionState.getDirections().contains(d)) {
    					neighboursToSend.add(neighbour);
    				}
	    	    }	
	        }else {this.logMessage(nodeInfo.nodeIdentifier() + " : no more hops for me!");}
	        
	        }else if (executionState.isFlooding()) {
	    	//si le noued est dans maxDist
	    	if (executionState.withinMaximalDistance(nodeInfo.nodePosition())) {
	    		
	    		result = (QueryResultI) coderequest.eval(executionState);
	    		// ajout du resultat courant
	    		executionState.addToCurrentResult(result);
	    		this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());

	    		
	    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
					neighboursToSend.add(neighbour);
	    	    } 
	    	}else {
	    		this.logMessage(nodeInfo.nodeIdentifier() + " : i am not in maximal distance!");
	    	}
	    }
	    if (neighboursToSend.size() == 0) {
	    	this.logMessage("no neighbours to send the request");
			//node doit envoyer le resultat au client
			this.doPortConnection(
					this.outboundPortRequestR.getPortURI(),
					clientInboundPort,
					NodeClientConnector.class.getCanonicalName()) ;
			outboundPortRequestR.acceptRequestResult(requestContinuation.requestURI(), executionState.getCurrentResult());
			this.logMessage(nodeInfo.nodeIdentifier() + " connected to client to send the result ! ");		
		}
		else{
			sendRequest(neighboursToSend, requestContinuation);
		}	

	    this.logMessage(nodeInfo.nodeIdentifier() + " : continuation request processed !");
	   
	}
	
	
	public void updateSensors() {
		for (SensorDataI capteur : capteurs) {
			((SensorData) capteur).updateValue();
		}
		prcNode = new ProcessingNode(nodeInfo, capteurs);
		this.logMessage("\n" + nodeInfo.nodeIdentifier() + " : Sensors value updated ------------");
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
            
            
        	//node doit register auprès du registre
			this.doPortConnection(
					this.outboundPortRegistration.getPortURI(),
					CVM.REGISTER_REGISTRATION_INBOUND_PORT_URI,
					NodeRegisterConnector.class.getCanonicalName()) ;
			this.logMessage(nodeInfo.nodeIdentifier() + " connected to register");
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		
		this.createNewExecutorService(NODE_POOL_URI,
									  NODE_POOL_SIZE,
									  false);
		this.createNewExecutorService(CLIENT_POOL_URI,
				  				CLIENT_POOL_SIZE,
				  				false);

	}
	
	@Override
	public void	execute() throws Exception
	{
		super.execute() ;
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
		this.doPortDisconnection(outboundPortRegistration.getPortURI());
		this.doPortDisconnection(clockOutboundPort.getPortURI());
		for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
			NodeSensorNodeP2POutboundPort port = entry.getValue();
			this.doPortDisconnection(port.getPortURI());
		}

		this.logMessage("stopping node component.");
        this.printExecutionLogOnFile("node");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.outboundPortRegistration.unpublishPort();
			this.clockOutboundPort.unpublishPort();
			for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
				NodeSensorNodeP2POutboundPort port = entry.getValue();
				port.unpublishPort();
			}
			this.inboundPortRequesting.unpublishPort();
			this.inboundPortP2P.unpublishPort();
			
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}	

}
