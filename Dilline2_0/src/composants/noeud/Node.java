package composants.noeud;

import java.util.ArrayList;
import java.util.Set;

import classes.ExecutionState;
import classes.ProcessingNode;
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

@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class})
public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	
	protected NodeRegistrationOutboundPort	outboundPortRegistration;
	
	/*
	 * On considérera que:
	 * index 0 corresponde à l'outboundport de voisin NE
	 * index 1 corresponde à l'outboundport de voisin NW
	 * index 2 corresponde à l'outboundport de voisin SE
	 * index 0 corresponde à l'outboundport de voisin NW
	 */
	protected ArrayList<NodeSensorNodeP2POutboundPort> listOutboundP2P;	
	
	
	protected NodeRequestingInboundPort	inboundPortRequesting ;
	protected NodeSensorNodeP2PInboundPort inboundPortP2P ;
	
	private NodeInfoI nodeInfo;
	private ExecutionStateI exState;
	private ArrayList<SensorDataI> capteurs;
	
	protected Node(String ibPortRequesting, String ibPortP2P, String obPortRegistration
			, NodeInfoI node, ArrayList<SensorDataI> sensors ) throws Exception{	
			// the reflection inbound port URI is the URI of the component
			super(1, 0) ;
			
			this.inboundPortRequesting = new NodeRequestingInboundPort(ibPortRequesting, this);
			this.inboundPortP2P = new NodeSensorNodeP2PInboundPort(ibPortP2P, this);
			this.outboundPortRegistration = new NodeRegistrationOutboundPort(obPortRegistration, this);
			this.listOutboundP2P= new ArrayList<NodeSensorNodeP2POutboundPort>();
			for(int i=0; i<4; i++) {
				listOutboundP2P.add(null);
			}
			
			this.inboundPortRequesting.publishPort();
			this.inboundPortP2P.publishPort();
			this.outboundPortRegistration.publishPort();

			
			
			this.capteurs = sensors;
			this.nodeInfo = node;
			ProcessingNodeI prcNode = new ProcessingNode(nodeInfo, capteurs);
			exState = new ExecutionState(prcNode);
		}
	
	//sert a reconnaitre le bon outboundport pour chaque voisin afin de faciliter la manipulation
	public void addListOBPortP2P(NodeSensorNodeP2POutboundPort port, NodeInfoI node, NodeInfoI neighbour) {
		PositionI posNode = node.nodePosition();
		PositionI posNeighbour = neighbour.nodePosition();
		Direction d = posNode.directionFrom(posNeighbour);
		
		switch (d) {
	    case NE:
	    	this.listOutboundP2P.add(0, port);
	        break;
	    case NW:
	    	this.listOutboundP2P.add(1, port);
	        break;
	    case SE:
	    	this.listOutboundP2P.add(2, port);
	    default:
	    	this.listOutboundP2P.add(3, port);
	}
		
	}
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		this.logMessage(this.nodeInfo.nodeIdentifier() + " is registering...");
		Set<NodeInfoI> neighbours = this.outboundPortRegistration.register(nodeInfo);

		if(this.outboundPortRegistration.registered(nodeInfo.nodeIdentifier())) {
			this.logMessage(nodeInfo.nodeIdentifier() + " registered!");
		}
		
		for (NodeInfoI neighbour: neighbours) {
			if(neighbour != null) {
				NodeSensorNodeP2POutboundPort outboundport = new NodeSensorNodeP2POutboundPort("OutP2PVoisin" + neighbour.nodeIdentifier(),this);
				outboundport.publishPort();
				addListOBPortP2P(outboundport, nodeInfo, neighbour);
				
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
		addListOBPortP2P(outboundport, nodeInfo, neighbour);
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		this.logMessage("processing request...");
		QueryI coderequest = (QueryI) request.getQueryCode();
		QueryResultI result = (QueryResultI) coderequest.eval(exState);
		
		this.logMessage("request processed !");
		return result;
		
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void start() throws ComponentStartException
    {
        this.logMessage("starting node component.");
        
        //node doit register auprès du registre
        try {
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
		this.register(nodeInfo) ;
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		//this.doPortDisconnection(outboundPort.getPortURI());
		//this.doPortDisconnection(inboundPort.getPortURI());
		this.logMessage("stopping node component.");
        this.printExecutionLogOnFile("node");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.inboundPortRequesting.unpublishPort();
			this.inboundPortP2P.unpublishPort();
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	
	

}
