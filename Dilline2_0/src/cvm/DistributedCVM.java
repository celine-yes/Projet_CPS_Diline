package cvm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import classes.GeographicalZone;
import classes.NodeInfo;
import classes.Position;
import classes.SensorData;
import classes.utils.NodeFactory;
import classes.utils.RequestFactory;
import composants.register.Register;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import composants.client.Client;
import composants.noeud.Node;

public class DistributedCVM extends	AbstractDistributedCVM{
	
	//les uris des JVM
	protected static final String JVM1_URI ="jvm1";
	protected static final String JVM2_URI ="jvm2";
	protected static final String JVM3_URI ="jvm3";
	protected static final String JVM4_URI ="jvm4";
	protected static final String JVM5_URI ="jvm5";

	
	//nodes crees
	 List<NodeInfoI> nodeInfos = NodeFactory.createNodes(8);
	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;
	
	
	public DistributedCVM(String[] args) throws Exception{
		super(args);
	
	}
	
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {
			
			//Zone du client
			PositionI p1 = new Position(0, 0);
			PositionI p2 = new Position(25, 30);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);
			
			//les requetes du client
			RequestI requestBDcont = RequestFactory.createBooleanRequestWithDCont(
															"temperature", 
															29.0, 
															Arrays.asList("NE", "SE"),
															3);
			ArrayList<RequestI> requetes = new ArrayList<>();
			requetes.add(requestBDcont);
			
			/** création du composant register           **/
	        AbstractComponent.createComponent(
					Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
																      REGISTER_REGISTRATION_INBOUND_PORT_URI});

	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, requetes});
			
//			//creation de NodeInfo pour parametre de composant noeud
//			String sensorId1 = "temperature";
//			String sensorId2 = "fumee";
//			
//			double sensorValue1 = 30.0;
//			boolean sensorValue12 = false;
//			double sensorValue2 = 32.0;
//			double sensorValue3 = 35.0;
//			double sensorValue4 = 45.0;
//			boolean sensorValue42 = true;
//			double sensorValue5 = 33.0;
//			boolean sensorValue52 = false;
//			
//			String nodeId1 = "node1";
//			String nodeId2 = "node2";
//			String nodeId3 = "node3";
//			String nodeId4 = "node4";
//			String nodeId5 = "node5";
//		
//			SensorDataI sensorNode1 = new SensorData(nodeId1, sensorId1, sensorValue1);
//			SensorDataI sensorNode12 = new SensorData(nodeId1, sensorId2, sensorValue12);
//			SensorDataI sensorNode2 = new SensorData(nodeId2, sensorId1, sensorValue2);
//			SensorDataI sensorNode3 = new SensorData(nodeId3, sensorId1, sensorValue3);
//			SensorDataI sensorNode4 = new SensorData(nodeId4, sensorId1, sensorValue4);
//			SensorDataI sensorNode42 = new SensorData(nodeId4, sensorId2, sensorValue42);
//			SensorDataI sensorNode5 = new SensorData(nodeId5, sensorId1, sensorValue5);
//			SensorDataI sensorNode52 = new SensorData(nodeId5, sensorId2, sensorValue52);
//
//			ArrayList<SensorDataI> sensorsNode1 = new ArrayList<SensorDataI>();
//			ArrayList<SensorDataI> sensorsNode2 = new ArrayList<SensorDataI>();
//			ArrayList<SensorDataI> sensorsNode3 = new ArrayList<SensorDataI>();
//			ArrayList<SensorDataI> sensorsNode4 = new ArrayList<SensorDataI>();
//			ArrayList<SensorDataI> sensorsNode5 = new ArrayList<SensorDataI>();
//
//			sensorsNode1.add(sensorNode1);
//			sensorsNode1.add(sensorNode12);
//			sensorsNode2.add(sensorNode2);
//			sensorsNode3.add(sensorNode3);
//			sensorsNode4.add(sensorNode4);
//			sensorsNode4.add(sensorNode42);
//			sensorsNode5.add(sensorNode5);
//			sensorsNode5.add(sensorNode52);
////			
//			PositionI positionNode1= new Position(5,5);
//			PositionI positionNode2= new Position(20, 20);
//			PositionI positionNode3= new Position(30, 15);
//			PositionI positionNode4= new Position(35, 40);
//			PositionI positionNode5= new Position(50, 0);
//		
//			
//			double range = 30.0;
//			
//			NodeInfoI node1 = new NodeInfo(
//					nodeId1, 
//					positionNode1,
//					range);
//			NodeInfoI node2 = new NodeInfo(
//					nodeId2, 
//					positionNode2,
//					range);
//			NodeInfoI node3 = new NodeInfo(
//					nodeId3, 
//					positionNode3,
//					range);
//			NodeInfoI node4 = new NodeInfo(
//					nodeId4, 
//					positionNode4,
//					range);
//			NodeInfoI node5 = new NodeInfo(
//					nodeId5, 
//					positionNode5,
//					range);
	        
	        //5 premiers composants node
	        for(int i = 0; i<5 ; i++) {
	        	
	        	NodeInfoI node = nodeInfos.get(i);
	        	ArrayList<SensorDataI> sensors = NodeFactory.createSensorsForNode(node.nodeIdentifier());
	        	
	        	/** création du composant node           **/
	        	AbstractComponent.createComponent(
	    				Node.class.getCanonicalName(), new Object [] {node, sensors});
	        }
	        
////			/** création des composants nodes           **/
//			AbstractComponent.createComponent(
//					Node.class.getCanonicalName(), new Object [] {node1, sensorsNode1});
//	        
//	        AbstractComponent.createComponent(
//					Node.class.getCanonicalName(), new Object [] {node2, sensorsNode2});
//	        
//	        AbstractComponent.createComponent(
//					Node.class.getCanonicalName(), new Object [] {node3, sensorsNode3});
//	        
//	        AbstractComponent.createComponent(
//					Node.class.getCanonicalName(), new Object [] {node4, sensorsNode4});
//	        
//	        AbstractComponent.createComponent(
//					Node.class.getCanonicalName(), new Object [] {node5, sensorsNode5});
	        
	        /** création du composant clockServer         **/
	        AbstractComponent.createComponent(
		        ClocksServer.class.getCanonicalName(),
		        new Object[]{
			        CVM.TEST_CLOCK_URI, // URI attribuée à l’horloge
			        CVM.unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
			        CVM.START_INSTANT, // instant de démarrage du scénario
			        CVM.ACCELERATION_FACTOR}); // facteur d’acccélération


		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {
			
//			//Zone du client
			PositionI p1 = new Position(25, 30);
			PositionI p2 = new Position(40, 30);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);
			
			//les requetes du client
			RequestI requestBFcont = RequestFactory.createBooleanFloodingRequestWithABase(
															"temperature", 
															29.0,
															new Position(5,5),
															40.0);
			
			ArrayList<RequestI> requetes = new ArrayList<>();
			requetes.add(requestBFcont);

	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, requetes});
			

			
			//5 derniers composants node
	        for(int i = 5; i<nodeInfos.size() ; i++) {
	        	
	        	NodeInfoI node = nodeInfos.get(i);
	        	ArrayList<SensorDataI> sensors = NodeFactory.createSensorsForNode(node.nodeIdentifier());
	        	
	        	/** création du composant node           **/
	        	AbstractComponent.createComponent(
	    				Node.class.getCanonicalName(), new Object [] {node, sensors});
	        }

		} else {

			System.out.println("Unknown JVM URI... " + AbstractCVM.getThisJVMURI());

		}

		super.instantiateAndPublish();
	}


	public static void main(String[] args) {
		
		DistributedCVM dcvm;
		try {
			dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(2500L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
