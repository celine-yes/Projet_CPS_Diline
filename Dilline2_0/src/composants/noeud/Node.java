package composants.noeud;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import classes.ExecutionState;
import classes.ProcessingNode;
import classes.QueryResult;
import classes.RequestContinuation;
import classes.SensorData;
import composants.connector.NodeNodeConnector;
import composants.connector.NodeRegisterConnector;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
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


@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class, ClocksServerCI.class})
public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	
	protected NodeRegistrationOutboundPort	outboundPortRegistration;
	protected ClocksServerOutboundPort clockOutboundPort;
	protected Map<NodeInfoI, NodeSensorNodeP2POutboundPort> neighbourPortMap;
	
	protected NodeRequestingInboundPort	inboundPortRequesting ;
	protected NodeSensorNodeP2PInboundPort inboundPortP2P ;
	
	
	private NodeInfoI nodeInfo;
	private ProcessingNodeI prcNode;
	private ExecutionStateI exState;
	private ArrayList<SensorDataI> capteurs;
	public static int cptDelay = 0;
	
	public Node(String ibPortRequesting, String ibPortP2P, String obPortRegistration
			, NodeInfoI node, ArrayList<SensorDataI> sensors ) throws Exception{	
			super(1, 1) ;
			
			this.inboundPortRequesting = new NodeRequestingInboundPort(ibPortRequesting, this);
			this.inboundPortP2P = new NodeSensorNodeP2PInboundPort(ibPortP2P, this);
			this.outboundPortRegistration = new NodeRegistrationOutboundPort(obPortRegistration, this);
			this.neighbourPortMap = new HashMap<>();
			
			this.inboundPortRequesting.publishPort();
			this.inboundPortP2P.publishPort();
			this.outboundPortRegistration.publishPort();

			this.capteurs = sensors;
			this.nodeInfo = node;
			prcNode = new ProcessingNode(nodeInfo, capteurs);
			exState = new ExecutionState(prcNode);
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
	    
	    Set<String> noeudsTraite = executionState.getNoeudsTraite();
	    ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();
	    //les resultats des voisins
	    ArrayList <QueryResultI> neighbourResults = null;

	    if (executionState.isDirectional()) {
	        
	        // Si nous n'avons pas encore atteint le nombre maximum de sauts
	        if(! executionState.noMoreHops()) {
	        	
	        	executionState.incrementHops();
	        	 //evaluation de la requete sur le noeud actuel
	    	    result = (QueryResultI) coderequest.eval(request.getExecutionState());
	    	    
	    		//Envoyer la requête à ses voisins dans les bonnes directions
	    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
    		        posNeighbour = neighbour.nodePosition();
    				d = posNodeAct.directionFrom(posNeighbour);
    				
    				if(executionState.getDirections().contains(d)) {
    					if(! noeudsTraite.contains(neighbour.nodeIdentifier())) {
    						 executionState.addNoeudTraite(neighbour.nodeIdentifier());
    						 neighboursToSend.add(neighbour);
    					
    					}		
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
	    	        
					if(! noeudsTraite.contains(neighbour.nodeIdentifier())) {
					    executionState.addNoeudTraite(neighbour.nodeIdentifier());
					    neighboursToSend.add(neighbour);
					}
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
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		
		this.logMessage(nodeInfo.nodeIdentifier() + " : processing request sent by client...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		QueryResultI result;
		
		//evaluer la requete sur le premier noeud
		result = (QueryResultI) coderequest.eval(exState);
		
		//cas de continuation vide
		if(!exState.isContinuationSet()) {
			return result;
		}
		
	    //ajout d'identifiant du noeud actuel a l'ensemble des noeuds traite
		((ExecutionState) exState).addNoeudTraite(nodeInfo.nodeIdentifier());
		
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
		    	    
    		//Envoyer la requête à ses voisins dans les bonnes directions
    	    for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
    	        NodeInfoI neighbour = entry.getKey();
		        PositionI posNeighbour = neighbour.nodePosition();
				Direction d = posNodeAct.directionFrom(posNeighbour);
				
				if(exState.getDirections().contains(d)) {
					((ExecutionState) exState).addNoeudTraite(neighbour.nodeIdentifier());
					neighboursToSend.add(neighbour);
				}
    	    }
    	 }else {
			 for (Map.Entry<NodeInfoI, NodeSensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
	    	        NodeInfoI neighbour = entry.getKey();
	    	        ((ExecutionState) exState).addNoeudTraite(neighbour.nodeIdentifier());
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
	    ExecutionState executionState = (ExecutionState) requestC.getExecutionState();
	    Set<String> noeudsTraite = executionState.getNoeudsTraite();
	    
		for (NodeInfoI neighbourToSend : neighboursToSend ) {
	        NodeSensorNodeP2POutboundPort port = neighbourPortMap.get(neighbourToSend);
	        for(String nodeTraite : noeudsTraite) {
    	        //this.logMessage("Noeuds déjà Traités : " + nodeTraite);
	        }

			QueryResultI neighbourResult = null;
			try {
				neighbourResult = port.execute(requestC);
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
		// TODO Auto-generated method stub
		
	}
	
	public void updateSensors() {
		for (SensorDataI capteur : capteurs) {
			((SensorData) capteur).updateValue();
		}
		prcNode = new ProcessingNode(nodeInfo, capteurs);
		exState = new ExecutionState(prcNode);
		this.logMessage(nodeInfo.nodeIdentifier() + " : Sensors value updated ------------");
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
	
	@Override
	public void			execute() throws Exception
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
