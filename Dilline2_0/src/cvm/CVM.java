package cvm;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import classes.GeographicalZone;
import classes.NodeInfo;
import classes.Position;
import classes.Request;
import classes.SensorData;
//import composants.client.Client;
//import composants.noeud.Node;
import composants.register.Register;

import withplugin.composants.Client;
import withplugin.composants.Node;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
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
import langage.ast.RDirs;
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
	
	public static final int NB_NODES = 5;
	public static int timeBeforeSendingRequest = NB_NODES+1;
	public static int timeBeforeShowingResult = timeBeforeSendingRequest + NB_NODES;
	public static int timeBeforeUpdatingSensorValue = timeBeforeShowingResult + 1;
	
	
	/** URIs of the components node.						*/
	protected String node1_uri;
	protected String node2_uri;
	protected String node3_uri;
	protected String node4_uri;
	protected String node5_uri;
	
	protected List<String> nodesURI = new ArrayList<String>();
	//Zone du client
	protected PositionI p1 = new Position(1,1);
	protected PositionI p2;
	
	/** URI of the component register.						*/
	protected String register_uri;
	/** URI of the component client.						*/
	protected String client_uri;
	public final static String clientID = "client";

	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;


	/** For ClocksServer						*/
	public static final String TEST_CLOCK_URI = "test-clock";
    public static final Instant START_INSTANT =
    Instant.parse("2024-01-31T09:00:00.00Z");
    protected static final long START_DELAY = 1000L;
    public static final double ACCELERATION_FACTOR = 100.0;
    public static final long unixEpochStartTimeInNanos =
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
    
    public static int number = 1;
    
	
	@Override
	public void deploy() throws Exception {
		
		//creerNoeuds(5);

		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);
        
        
        //creation de NodeInfo pour parametre de composant noeud
		String sensorId1 = "temperature";
		String sensorId2 = "fumee";
		
		double sensorValue1 = 30.0;
		boolean sensorValue12 = false;
		double sensorValue2 = 32.0;
		double sensorValue3 = 35.0;
		double sensorValue4 = 45.0;
		boolean sensorValue42 = true;
		double sensorValue5 = 33.0;
		boolean sensorValue52 = false;
		
		String nodeId1 = "node1";
		String nodeId2 = "node2";
		String nodeId3 = "node3";
		String nodeId4 = "node4";
		String nodeId5 = "node5";
	
		SensorDataI sensorNode1 = new SensorData(nodeId1, sensorId1, sensorValue1);
		SensorDataI sensorNode12 = new SensorData(nodeId1, sensorId2, sensorValue12);
		SensorDataI sensorNode2 = new SensorData(nodeId2, sensorId1, sensorValue2);
		SensorDataI sensorNode3 = new SensorData(nodeId3, sensorId1, sensorValue3);
		SensorDataI sensorNode4 = new SensorData(nodeId4, sensorId1, sensorValue4);
		SensorDataI sensorNode42 = new SensorData(nodeId4, sensorId2, sensorValue42);
		SensorDataI sensorNode5 = new SensorData(nodeId5, sensorId1, sensorValue5);
		SensorDataI sensorNode52 = new SensorData(nodeId5, sensorId2, sensorValue52);

		ArrayList<SensorDataI> sensorsNode1 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode2 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode3 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode4 = new ArrayList<SensorDataI>();
		ArrayList<SensorDataI> sensorsNode5 = new ArrayList<SensorDataI>();

		sensorsNode1.add(sensorNode1);
		sensorsNode1.add(sensorNode12);
		sensorsNode2.add(sensorNode2);
		sensorsNode3.add(sensorNode3);
		sensorsNode4.add(sensorNode4);
		sensorsNode4.add(sensorNode42);
		sensorsNode5.add(sensorNode5);
		sensorsNode5.add(sensorNode52);
//		
		PositionI positionNode1= new Position(5,5);
		PositionI positionNode2= new Position(20, 20);
		PositionI positionNode3= new Position(30, 15);
		PositionI positionNode4= new Position(35, 40);
		PositionI positionNode5= new Position(50, 0);
		
		//Zone du client
		PositionI p1 = new Position(0, 0);
		PositionI p2 = new Position(25, 30);
		GeographicalZoneI zone = new GeographicalZone(p1,p2);
		
		double range = 30.0;
		
		NodeInfoI node1 = new NodeInfo(
				nodeId1, 
				positionNode1,
				range);
		NodeInfoI node2 = new NodeInfo(
				nodeId2, 
				positionNode2,
				range);
		NodeInfoI node3 = new NodeInfo(
				nodeId3, 
				positionNode3,
				range);
		NodeInfoI node4 = new NodeInfo(
				nodeId4, 
				positionNode4,
				range);
		NodeInfoI node5 = new NodeInfo(
				nodeId5, 
				positionNode5,
				range);
		
		
		/**  création des requetes pour composant client    **/
		
		//Comparaisons
		IBexp bexp = new GeqCexp(
				new SRand(sensorId1),
				new CRand(29.0));
		
		//Continuations Inondations
		double distFcont = 40.0;
		IFCont fcont = new FCont(new ABase(positionNode1), distFcont);
		
		//Continuations Directionnelles
		int maxSauts = 3;

		IDirs direction2 = new RDirs(Direction.NE, new FDirs(Direction.SE));
		
		IDCont dcont = new DCont(direction2, maxSauts);
		
		//Requêtes booléennes
		QueryI bqueryE = new BQuery(new ECont(), bexp);
		RequestI requestBEcont = new Request("requestBEcont", bqueryE);
		
		QueryI bqueryF = new BQuery(fcont, bexp);
		RequestI requestBFcont = new Request("requestBFcont", bqueryF);
		
		QueryI bqueryD = new BQuery(dcont, bexp);
		RequestI requestBDcont = new Request("requestBDcont", bqueryD);
		
		//Requêtes de collectes
		RGather rg = new RGather("temperature",new FGather("fumee"));
		
		QueryI gqueryE = new GQuery(new ECont(),rg);
		RequestI requestGEcont = new Request("requestGEcont", gqueryE);
		
		QueryI gqueryF = new GQuery(fcont,rg);
		RequestI requestGFcont = new Request("requestGFcont", gqueryF);
		
		QueryI gqueryD = new GQuery(dcont,rg);
		RequestI requestGDcont = new Request("requestGDcont", gqueryD);
		
		ArrayList<RequestI> requetes = new ArrayList<>();
			requetes.add(requestBDcont);
		//requetes.add(requestBFcont);
		
		/** création du composant register           **/
        this.register_uri = AbstractComponent.createComponent(
				Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
															      REGISTER_REGISTRATION_INBOUND_PORT_URI});
        
        
        /** création du composant client           **/
		this.client_uri = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {zone,requetes});

		
//		/** création des composants nodes           **/
        this.node1_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {node1, sensorsNode1});
        
        this.node2_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {node2, sensorsNode2});
        
        this.node3_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {node3, sensorsNode3});
        
        this.node4_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {node4, sensorsNode4});
        
        this.node5_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {node5, sensorsNode5});
        
        /** création du composant clockServer         **/
        String clock = AbstractComponent.createComponent(
	        ClocksServer.class.getCanonicalName(),
	        new Object[]{
		        TEST_CLOCK_URI, // URI attribuée à l’horloge
		        unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
		        START_INSTANT, // instant de démarrage du scénario
		        ACCELERATION_FACTOR}); // facteur d’acccélération
		   
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