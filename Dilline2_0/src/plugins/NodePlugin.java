package plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import classes.BCM4JavaEndPointDescriptor;
import classes.ExecutionState;
import classes.NodeInfo;
import classes.QueryResult;
import classes.RequestContinuation;
import composants.connector.NodeNodeConnector;
import composants.connector.NodeRegisterConnector;
import composants.noeud.RegistrationOutboundPort;
import composants.noeud.RequestingInboundPort;
import composants.noeud.SensorNodeP2PInboundPort;
import composants.noeud.SensorNodeP2POutboundPort;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.QueryI;

public class NodePlugin extends AbstractPlugin implements RequestingCI, SensorNodeP2PCI, RegistrationCI{

	private static final long serialVersionUID = 1L;
	
	protected RegistrationOutboundPort	outboundPortRegistration;
	protected RequestingInboundPort	inboundPortRequesting ;
	protected SensorNodeP2PInboundPort inboundPortP2P ;
	protected Map<NodeInfoI, SensorNodeP2POutboundPort> neighbourPortMap;

	protected NodeInfoI nodeInfo;
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
	
	public NodePlugin(NodeInfoI nodeinfo) throws Exception {
		this.nodeInfo = nodeinfo;
		this.neighbourPortMap = new HashMap<>();
		this.rwLock = new ReentrantReadWriteLock();
		this.initialise();
		
	}

    public void installOn(ComponentI owner) throws Exception {
		assert	!owner.isInstalled(this.getPluginURI());
		
		// Add the interface
		this.addOfferedInterface(SensorNodeP2PCI.class);
		this.addOfferedInterface(RequestingCI.class);
		this.addRequiredInterface(RegistrationCI.class);
		
		this.outboundPortRegistration = new RegistrationOutboundPort(this.getOwner());
		this.inboundPortRequesting = new RequestingInboundPort(this.getOwner(), CLIENT_POOL_URI);
		this.inboundPortP2P = new SensorNodeP2PInboundPort(this.getOwner(), NODE_POOL_URI);
		
		this.inboundPortRequesting.publishPort();
		this.inboundPortP2P.publishPort();
		this.outboundPortRegistration.publishPort();
		
		BCM4JavaEndPointDescriptorI requestingEndPoint = new BCM4JavaEndPointDescriptor(inboundPortRequesting.getPortURI(), RequestingCI.class);
		BCM4JavaEndPointDescriptorI p2pEndPoint = new BCM4JavaEndPointDescriptor(inboundPortP2P.getPortURI(), SensorNodeP2PCI.class);
		((NodeInfo)(nodeInfo)).setInboundPorts(requestingEndPoint, p2pEndPoint);
		
        super.installOn(owner);
        
    }
	
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		
		this.getOwner().doPortDisconnection(
				((BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo()).getInboundPortURI()) ;
		this.logMessage(nodeInfo.nodeIdentifier() + " disconnected from " + neighbour.nodeIdentifier());
		
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.outboundPortRegistration.registered(nodeIdentifier);
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
	    Set<NodeInfoI> neighbours = this.outboundPortRegistration.register(nodeInfo);

	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
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
	    } finally {
	        writeLock.unlock();
	    }

	    return neighbours;
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		
	    SensorNodeP2POutboundPort outboundport = new SensorNodeP2POutboundPort(this.getOwner());
	    outboundport.publishPort();

	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
	        this.neighbourPortMap.put(newNeighbour, outboundport);

	        this.getOwner().doPortConnection(
	            outboundport.getPortURI(),
	            ((BCM4JavaEndPointDescriptorI) newNeighbour.p2pEndPointInfo()).getInboundPortURI(),
	            NodeNodeConnector.class.getCanonicalName());

	        this.logMessage(nodeInfo.nodeIdentifier() + " connected to " + newNeighbour.nodeIdentifier());
	    } finally {
	        writeLock.unlock();
	    }
		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		Lock writeLock = rwLock.writeLock();	
	    QueryI coderequest = (QueryI) requestContinuation.getQueryCode();
	    QueryResultI result = null;
	    ExecutionState executionState = null;
	    
		writeLock.lock();		 
		try {
			
		    //pour mettre a jour les valeurs d'execution state
			executionState = ((ExecutionState) requestContinuation.getExecutionState()).copy();
			this.logMessage("Result recu = " + executionState.getCurrentResult().positiveSensorNodes());
		    executionState.updateProcessingNode(prcNode);
    		this.logMessage("executionState copied");
		} finally {
		    writeLock.unlock();
		}
		
	    PositionI posNodeAct = nodeInfo.nodePosition();
	    PositionI posNeighbour = null;
	    Direction d = null;
	    ArrayList<NodeInfoI> neighboursToSend = new ArrayList<NodeInfoI>();
		 
	    writeLock.lock(); // Verrou d'écriture pour modifier executionState
	    try {
	        if (executionState.isDirectional()) {

	            // Si nous n'avons pas encore atteint le nombre maximum de sauts
	            if(!executionState.noMoreHops()) {
	                executionState.incrementHops();

	                result = (QueryResultI) coderequest.eval(executionState);
	                if (result == null)  this.logMessage("result is null");
	                // Ajout du résultat courant
	                executionState.addToCurrentResult(result);
	                this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());

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
	                this.logMessage("actualResult = " + executionState.getCurrentResult().positiveSensorNodes());

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
		
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {

		 

		
		
		
	}
	
	private ArrayList<QueryResultI> sendRequest(ArrayList<NodeInfoI> neighboursToSend, RequestContinuationI requestC){
	    ArrayList<QueryResultI> neighbourResults = new ArrayList<>();
	    
	    Lock readLock = rwLock.readLock();
	    readLock.lock();
	    try {
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
	    } finally {
	        readLock.unlock();
	    }
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
	    Lock writeLock = rwLock.writeLock();
	    writeLock.lock();
	    try {
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
	    } finally {
	        writeLock.unlock();
	    }
	}
	
	private void findNeighboursToSend(ArrayList<NodeInfoI> neighboursToSend, PositionI posNodeAct, ExecutionStateI exState) {
	    Lock readLock = rwLock.readLock();
	    readLock.lock();
	    try {
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
	    } finally {
	        readLock.unlock();
	    }
	}
	
	@Override
	public void			initialise() throws Exception
	{
		
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
	
	@Override
	public void			finalise() throws Exception
	{	
		this.neighbourPortMap.clear();
		this.neighbourPortMap = null;
		
		this.getOwner().doPortDisconnection(outboundPortRegistration.getPortURI());
		for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
			SensorNodeP2POutboundPort port = entry.getValue();
			this.getOwner().doPortDisconnection(port.getPortURI());
		}
	}
	
	@Override
	public void			uninstall() throws Exception
	{
		this.outboundPortRegistration.unpublishPort();
		this.outboundPortRegistration.destroyPort();
		for (Map.Entry<NodeInfoI, SensorNodeP2POutboundPort> entry : neighbourPortMap.entrySet()) {
			SensorNodeP2POutboundPort port = entry.getValue();
			port.unpublishPort();
			port.destroyPort();
		}
		this.inboundPortRequesting.unpublishPort();
		this.inboundPortRequesting.destroyPort();
		this.inboundPortP2P.unpublishPort();
		this.inboundPortP2P.destroyPort();
		this.removeOfferedInterface(RequestingCI.class);
		this.removeOfferedInterface(SensorNodeP2PCI.class);
		this.removeRequiredInterface(RegistrationCI.class);
	}

}
