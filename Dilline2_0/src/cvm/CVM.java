package cvm;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import classes.BCM4JavaEndPointDescriptor;
import classes.ConnectionInfo;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

import classes.NodeInfo;
import classes.Position;
import classes.Request;
import classes.SensorData;
import composants.client.Client;
import composants.noeud.Node;
import composants.register.Register;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.*;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import langage.ast.ABase;
import langage.ast.BQuery;
import langage.ast.CRand;
import langage.ast.DCont;
import langage.ast.ECont;
import langage.ast.FCont;
import langage.ast.FDirs;
import langage.ast.FGather;
import langage.ast.GQuery;
import langage.ast.GeqCexp;
import langage.ast.RGather;
import langage.ast.SRand;
import langage.interfaces.IBexp;
import langage.interfaces.IDCont;
import langage.interfaces.IDirs;
import langage.interfaces.IFCont;
import langage.interfaces.QueryI;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {
		super();
	}
	
	
	/** URI of the component node of the first node.						*/
	protected String node1_uri;
	/** URI of the component node of the second node.						*/
	protected String node2_uri;
	/** URI of the component node of the third node.						*/
	protected String node3_uri;
	/** URI of the component node of the fourth node.						*/
	protected String node4_uri;
	
	/** URI of the component node of the register.						*/
	protected String register_uri;
	/** URI of the component node of the client.						*/
	protected String client_uri;
	/** URI of the component node of the client.						*/
	public final static String clientID = "client";


	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE1_REGISTRATION_OUTBOUND_PORT_URI =
														"node1registrationobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE2_REGISTRATION_OUTBOUND_PORT_URI =
														"node2registrationobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE3_REGISTRATION_OUTBOUND_PORT_URI =
														"node3registrationobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE4_REGISTRATION_OUTBOUND_PORT_URI =
														"node4registrationobpURI" ;
	
	
	//techniquement, on a 4 outboundPort P2P par noeud
	/** URI of the SensorNodeP2P outbound port of the first node.						*/
	public final static String	NODE1_P2P_OUTBOUND_PORT_URI =
														"node1P2PobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE2_P2P_OUTBOUND_PORT_URI =
														"node2P2PobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE3_P2P_OUTBOUND_PORT_URI =
														"node3P2PobpURI" ;
	/** URI of the registration outbound port of the first node.						*/
	public final static String	NODE4_P2P_OUTBOUND_PORT_URI =
														"node4P2PobpURI" ;
	
	//techniquement, on a 4 inboundPort P2P par noeud
	/** URI of the SensorNodeP2P inbound port of the first node.						*/
	public final static String	NODE1_P2P_INBOUND_PORT_URI =
														"node1P2PibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE2_P2P_INBOUND_PORT_URI =
														"node2P2PibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE3_P2P_INBOUND_PORT_URI =
														"node3P2PibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE4_P2P_INBOUND_PORT_URI =
														"node4P2PibpURI" ;
	
	
	/** URI of the SensorNodeP2P inbound port of the first node.						*/
	public final static String	NODE1_REQUESTING_INBOUND_PORT_URI =
														"node1RrequestingibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE2_REQUESTING_INBOUND_PORT_URI =
														"node2requestingibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE3_REQUESTING_INBOUND_PORT_URI =
														"node3requestingibpURI" ;
	/** URI of the registration inbound port of the first node.						*/
	public final static String	NODE4_REQUESTING_INBOUND_PORT_URI =
														"node4requestingibpURI" ;
	
	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;
	
	
	/** URI of the requesting outbound port of the client.						*/
	public final static String	CLIENT_REQUESTING_OUTBOUND_PORT_URI = 
			                                            "clientrequestingobpURI" ;
	/** URI of the lookup outbound port of the client.						*/
	public final static String	CLIENT_LOOKUP_OUTBOUND_PORT_URI = 
			                                            "clientlookupobpURI" ;
	/** URI of the request result inbound port of the client.						*/
	public final static String	CLIENT_REQUESTRESULT_INBOUND_PORT_URI = 
            											"clientrequestresultibpURI" ;
	
	
	/** For ClocksServer						*/
	public static final String TEST_CLOCK_URI = "test-clock";
    public static final Instant START_INSTANT =
    Instant.parse("2024-01-31T09:00:00.00Z");
    protected static final long START_DELAY = 3000L;
    public static final double ACCELERATION_FACTOR = 100.0;
    public static final long unixEpochStartTimeInNanos =
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
    
    public static final int NB_NODES = 4;

	
	@Override
	public void deploy() throws Exception {

		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);
        
        
//creation de NodeInfo pour parametre de composant noeud
		String sensorId = "temperature";
		double sensorValue1 = 50.0;
		double sensorValue2 = 40.0;
		double sensorValue3 = 45.0;
		String nodeId1 = "node1";
		String nodeId2 = "node2";
		String nodeId3 = "node3";
		String nodeId4 = "node4";
		double range = 35.0;
		
		SensorDataI sensorNode1 = new SensorData(nodeId1, sensorId, sensorValue1);
		SensorDataI sensorNode2 = new SensorData(nodeId2, sensorId, sensorValue2);
		SensorDataI sensorNode3 = new SensorData(nodeId3, sensorId, sensorValue3);
		SensorDataI sensorNode4 = new SensorData(nodeId4, sensorId, sensorValue2);
		
		ArrayList<SensorDataI> sensorsNode1 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode2 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode3 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode4 = new ArrayList<SensorDataI>();
		
		sensorsNode1.add(sensorNode1);
		sensorsNode2.add(sensorNode2);
		sensorsNode3.add(sensorNode3);
		sensorsNode4.add(sensorNode4);
		
		PositionI positionNode1= new Position(10,10);
		PositionI positionNode2= new Position(20,40);
		PositionI positionNode3= new Position(30,45);
		PositionI positionNode4= new Position(70,20);
		
		NodeInfoI node1 = new NodeInfo(
				nodeId1, 
				positionNode1,
				new BCM4JavaEndPointDescriptor(NODE1_REQUESTING_INBOUND_PORT_URI), 
				new BCM4JavaEndPointDescriptor(NODE1_P2P_INBOUND_PORT_URI),
				range);
		NodeInfoI node2 = new NodeInfo(
				nodeId2, 
				positionNode2,
				new BCM4JavaEndPointDescriptor(NODE2_REQUESTING_INBOUND_PORT_URI), 
				new BCM4JavaEndPointDescriptor(NODE2_P2P_INBOUND_PORT_URI),
				range);
		NodeInfoI node3 = new NodeInfo(
				nodeId3, 
				positionNode3,
				new BCM4JavaEndPointDescriptor(NODE3_REQUESTING_INBOUND_PORT_URI), 
				new BCM4JavaEndPointDescriptor(NODE3_P2P_INBOUND_PORT_URI),
				range);
		NodeInfoI node4 = new NodeInfo(
				nodeId4, 
				positionNode4,
				new BCM4JavaEndPointDescriptor(NODE4_REQUESTING_INBOUND_PORT_URI), 
				new BCM4JavaEndPointDescriptor(NODE4_P2P_INBOUND_PORT_URI),
				range);
		
		ConnectionInfoI clientConnectionInfo = new ConnectionInfo(clientID, 
				new BCM4JavaEndPointDescriptor(CLIENT_REQUESTRESULT_INBOUND_PORT_URI));
		
		// création des requetes pour composant client
		IBexp bexp = new GeqCexp(
				new SRand(sensorId),
				new CRand(50.0));
		QueryI query1 = new BQuery(new ECont(), bexp);
		RequestI request1 = new Request("requete1", query1, clientConnectionInfo);
		
		double distFcont = 60.0;
		IFCont fcont12 = new FCont(new ABase(positionNode1), distFcont);
		QueryI query12 = new BQuery(fcont12, bexp);
		RequestI request12 = new Request("requete12", query12, clientConnectionInfo);
		
		IDirs directions = new FDirs(Direction.NE);
		int maxSauts = 2;
		IDCont dcont13 = new DCont(directions, maxSauts);
		QueryI query13 = new BQuery(dcont13, bexp);
		RequestI request13 = new Request("requete13", query13, clientConnectionInfo);
		
		
		FGather fg =  new FGather("temperature");
		QueryI query2 = new GQuery(fcont12,fg);
		RequestI request2 = new Request("requete2", query2, clientConnectionInfo);
		
		List gather = new ArrayList<>();
		gather.add(nodeId1);
		gather.add(nodeId2);
		RGather rg = new RGather("temperature",gather);
		QueryI query3 = new GQuery(fcont12,rg);
		RequestI request23 = new Request("requete2", query3, clientConnectionInfo);
		
		//création du composant register
        this.register_uri = AbstractComponent.createComponent(
				Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
															      REGISTER_REGISTRATION_INBOUND_PORT_URI});
        
        
        //creation de composant client
		this.client_uri = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {CLIENT_REQUESTING_OUTBOUND_PORT_URI,
																CLIENT_LOOKUP_OUTBOUND_PORT_URI, 
																CLIENT_REQUESTRESULT_INBOUND_PORT_URI, 
																request23});

		
        //création des composants noeuds
        this.node1_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE1_REQUESTING_INBOUND_PORT_URI,
														      NODE1_P2P_INBOUND_PORT_URI,
														      NODE1_REGISTRATION_OUTBOUND_PORT_URI,
														      node1, sensorsNode1});
        
        this.node2_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE2_REQUESTING_INBOUND_PORT_URI,
														      NODE2_P2P_INBOUND_PORT_URI,
														      NODE2_REGISTRATION_OUTBOUND_PORT_URI,
														      node2, sensorsNode2});
        
        this.node3_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE3_REQUESTING_INBOUND_PORT_URI,
														      NODE3_P2P_INBOUND_PORT_URI,
														      NODE3_REGISTRATION_OUTBOUND_PORT_URI,
														      node3, sensorsNode3});
        
        this.node4_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE4_REQUESTING_INBOUND_PORT_URI,
														      NODE4_P2P_INBOUND_PORT_URI,
														      NODE4_REGISTRATION_OUTBOUND_PORT_URI,
														      node4, sensorsNode4});
        
        // Création du composant ClocksServer
        
        String clock = AbstractComponent.createComponent(
	        ClocksServer.class.getCanonicalName(),
	        new Object[]{
		        TEST_CLOCK_URI, // URI attribuée à l’horloge
		        unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
		        START_INSTANT, // instant de démarrage du scénario
		        ACCELERATION_FACTOR}); // facteur d’acccélération
        
        // création des différents clocks de ClocksServer
    
        this.toggleTracing(register_uri);
        this.toggleTracing(node1_uri);
		this.toggleTracing(node2_uri);
        this.toggleTracing(node3_uri);
        this.toggleTracing(node4_uri);
		this.toggleTracing(client_uri);
		//this.toggleTracing(clock);
        
        this.toggleLogging(register_uri);
		this.toggleLogging(node1_uri);
		this.toggleLogging(node2_uri);
		this.toggleLogging(node3_uri);
		this.toggleLogging(node4_uri);
		//this.toggleLogging(clock);
		
        
        super.deploy();      
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(2500L);
			Thread.sleep(100000L);
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}