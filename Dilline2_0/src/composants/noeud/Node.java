package composants.noeud;

import java.util.HashMap;

import classes.ExecutionState;
import classes.ProcessingNode;
import composants.client.ClientOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.basic_cs.components.URIProvider;
import fr.sorbonne_u.components.examples.basic_cs.interfaces.URIProviderCI;
import fr.sorbonne_u.components.examples.basic_cs.ports.URIProviderInboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import langage.interfaces.QueryI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.*;

@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class})
public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	
	public static final String NIP_URI = "n1";
	protected NodeOutboundPort	outboundPort ;
	protected NodeInboundPort	inboundPort ;
	private NodeInfoI nodeInfo;
	private String uriPrefix;
	private ExecutionStateI exState;
	
	protected Node(String uriPrefix, String inboundPortURI, String outboundPortURI, NodeInfoI node) throws Exception{	
			// the reflection inbound port URI is the URI of the component
			super(uriPrefix, 1, 0) ;
			
			this.inboundPort = new NodeInboundPort(inboundPortURI, this);
			this.outboundPort = new NodeOutboundPort(outboundPortURI, this);
			this.inboundPort.publishPort();
			this.outboundPort.publishPort();
			
			//
			this.nodeInfo = node;
			ProcessingNodeI prcNode = new ProcessingNode(nodeInfo);
			exState = new ExecutionState(prcNode);
			
			

//			assert	uriPrefix != null :
//						new PreconditionException("uri can't be null!");
//			assert	inboundPortURI != null && outboundPortURI != null:
//						new PreconditionException("PortURI can't be null!");
//
//			this.uriPrefix = uriPrefix ;
//
//			// if the offered interface is not declared in an annotation on
//			// the component class, it can be added manually with the
//			// following instruction:
//			//this.addOfferedInterface(URIProviderI.class) ;
//
//			// create the port that exposes the offered interface with the
//			// given URI to ease the connection from client components.
//
//
//			if (AbstractCVM.isDistributed) {
//				this.getLogger().setDirectory(System.getProperty("user.dir"));
//			} else {
//				this.getLogger().setDirectory(System.getProperty("user.home"));
//			}
//			this.getTracer().setTitle("provider");
//			this.getTracer().setRelativePosition(1, 0);
//
//			URIProvider.checkInvariant(this) ;
//			AbstractComponent.checkImplementationInvariant(this);
//			AbstractComponent.checkInvariant(this);
//			assert	this.uriPrefix.equals(uriPrefix) :
//						new PostconditionException("The URI prefix has not "
//													+ "been initialised!");
//			assert	this.isPortExisting(inboundPortURI) :
//						new PostconditionException("The component must have a "
//								+ "port with URI " + inboundPortURI);
//			assert	this.findPortFromURI(inboundPortURI).
//						getImplementedInterface().equals(URIProviderCI.class) :
//						new PostconditionException("The component must have a "
//								+ "port with implemented interface URIProviderI");
//			assert	this.findPortFromURI(inboundPortURI).isPublished() :
//						new PostconditionException("The component must have a "
//								+ "port published with URI " + inboundPortURI);
		}



	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		
		
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		// TODO Auto-generated method stub
		
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
		QueryI coderequest = (QueryI) request.getQueryCode();
		return (QueryResultI) coderequest.eval(exState);
		
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void start() throws ComponentStartException
    {
        this.logMessage("starting node component.");
        super.start();
    }
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(outboundPort.getPortURI());
		this.doPortDisconnection(inboundPort.getPortURI());
		this.logMessage("stopping node component.");
        this.printExecutionLogOnFile("node");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.inboundPort.unpublishPort();
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
	
	

}
