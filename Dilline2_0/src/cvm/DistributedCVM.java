package cvm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import classes.GeographicalZone;
import classes.Position;
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
import withplugin.composants.Client;
import withplugin.composants.Node;

public class DistributedCVM extends	AbstractDistributedCVM{
	
	//les uris des JVM
	protected static final String JVM1_URI ="jvm1";
	protected static final String JVM2_URI ="jvm2";
	protected static final String JVM3_URI ="jvm3";
	protected static final String JVM4_URI ="jvm4";
	protected static final String JVM5_URI ="jvm5";
	
	/** For ClocksServer						*/
	public static final String TEST_CLOCK_URI = "test-clock";
    public static final Instant START_INSTANT =
    Instant.parse("2024-01-31T09:00:00.00Z");
    protected static final long START_DELAY = 1000L;
    public static final double ACCELERATION_FACTOR = 100.0;
    public static final long unixEpochStartTimeInNanos =
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
	
	
	//nodes crees
	 List<NodeInfoI> nodeInfos;
	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;
	
	
	public DistributedCVM(String[] args) throws Exception{
		super(args);
		
		//creation des NodeInfoI
		nodeInfos = NodeFactory.createNodes(5);
	
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
			
	        /** création du composant clockServer         **/
	        String clock = AbstractComponent.createComponent(
		        ClocksServer.class.getCanonicalName(),
		        new Object[]{
			        TEST_CLOCK_URI, // URI attribuée à l’horloge
			        unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
			        START_INSTANT, // instant de démarrage du scénario
			        ACCELERATION_FACTOR}); // facteur d’acccélération

	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, requetes});
			
			/** création du composant register           **/
	        AbstractComponent.createComponent(
					Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
																      REGISTER_REGISTRATION_INBOUND_PORT_URI});
	        
	        //5 premiers composants node
	        for(int i = 0; i<5 ; i++) {
	        	
	        	NodeInfoI node = nodeInfos.get(i);
	        	ArrayList<SensorDataI> sensors = NodeFactory.createSensorsForNode(node.nodeIdentifier());
	        	
	        	/** création du composant node           **/
	        	AbstractComponent.createComponent(
	    				Node.class.getCanonicalName(), new Object [] {node, sensors});
	        }


		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {
			
			//Zone du client
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
	
	
//	@Override
//	public void interconnect() throws Exception {
//		
//		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {
//
//	        /** création du composant client           **/
//			AbstractComponent.createComponent(
//					Client.class.getCanonicalName(), new Object [] {zone,requetes});
//
//
//		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {
//
//	        /** création du composant client           **/
//			AbstractComponent.createComponent(
//					Client.class.getCanonicalName(), new Object [] {zone,requetes});
//
//		} else {
//
//			System.out.println("Unknown JVM URI... " + AbstractCVM.getThisJVMURI());
//
//		}
//		super.interconnect();
//	}


	public static void main(String[] args) {
		
		DistributedCVM dcvm;
		try {
			dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(10000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}