package cvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import classes.GeographicalZone;
import classes.Position;
import classes.utils.NodeFactory;
//import composants.client.Client;
//import composants.noeud.Node;
import composants.register.Register;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import tests.requests.RequestTest;
import withplugin.composants.Client;
import withplugin.composants.Node;

public class DistributedCVM extends	AbstractDistributedCVM{
	
	//les uris des JVM
	protected static final String JVM1_URI ="jvm1";
	protected static final String JVM2_URI ="jvm2";
	protected static final String JVM3_URI ="jvm3";
	protected static final String JVM4_URI ="jvm4";
	protected static final String JVM5_URI ="jvm5";

	
	//nodes crees
	 Map<NodeInfoI, ArrayList<SensorDataI>> nodeInfos;
	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;
	
    public static final int nbRequestsPerClient = 1;

	
	public DistributedCVM(String[] args) throws Exception{
		super(args);
		
		this.nodeInfos = NodeFactory.createNodes(50, 30);
		//NodeFactory.displayNodes(nodeInfos);
	
	}
	
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{

		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {
			
			//Zone du client
			PositionI p1 = new Position(23, 25);
			PositionI p2 = new Position(23, 25);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);
			
			/** création du composant register           **/
	        AbstractComponent.createComponent(
					Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
																      REGISTER_REGISTRATION_INBOUND_PORT_URI});

//	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, RequestTest.createBooleanFloodingRequests(nbRequestsPerClient, true, AbstractCVM.getThisJVMURI() )});
			
			/** création des composants node           **/
			handleNodes(0, 10);
	        
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
			PositionI p1 = new Position(23, 25);
			PositionI p2 = new Position(23, 25);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);

//	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, RequestTest.createBooleanFloodingRequests(nbRequestsPerClient, true, AbstractCVM.getThisJVMURI())});
			

			/** création des composants node           **/
			handleNodes(10, 20);

		} else if (AbstractCVM.getThisJVMURI().equals(JVM3_URI)) {
			
//			//Zone du client
			PositionI p1 = new Position(23, 25);
			PositionI p2 = new Position(23, 25);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);


//	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, RequestTest.createBooleanFloodingRequests(nbRequestsPerClient, true, AbstractCVM.getThisJVMURI())});
			

			/** création des composants node           **/
			handleNodes(20, 30);

		} else if (AbstractCVM.getThisJVMURI().equals(JVM4_URI)) {
			
//			//Zone du client
			PositionI p1 = new Position(23, 25);
			PositionI p2 = new Position(23, 25);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);
	

//	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, RequestTest.createBooleanFloodingRequests(nbRequestsPerClient, true, AbstractCVM.getThisJVMURI())});
			

			/** création des composants node           **/
			handleNodes(30, 40);

		} else if (AbstractCVM.getThisJVMURI().equals(JVM5_URI)) {
			
//			//Zone du client
			PositionI p1 = new Position(23, 25);
			PositionI p2 = new Position(23, 25);
			GeographicalZoneI zone = new GeographicalZone(p1,p2);
		

//	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone, RequestTest.createBooleanFloodingRequests(nbRequestsPerClient, true, AbstractCVM.getThisJVMURI())});
			

			/** création des composants node           **/
			handleNodes(40, 50);

		}else {

			System.out.println("Unknown JVM URI... " + AbstractCVM.getThisJVMURI());

		}

		super.instantiateAndPublish();
	}
	
	private void handleNodes(int start, int end) {
	    List<Entry<NodeInfoI, ArrayList<SensorDataI>>> nodeList = new ArrayList<>(nodeInfos.entrySet());
	    System.out.println("Handling nodes from " + start + " to " + end);
	    nodeList.subList(start, end).forEach(entry -> {
	        System.out.println("Preparing to create node: " + entry.getKey().nodeIdentifier());
	        try {
	            AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{entry.getKey(), entry.getValue()});
	            System.out.println("Successfully created node: " + entry.getKey().nodeIdentifier());
	        } catch (Exception e) {
	            System.err.println("Error creating node: " + entry.getKey().nodeIdentifier());
	            e.printStackTrace();
	        }
	    });
	}



	public static void main(String[] args) {
		
		DistributedCVM dcvm;
		try {
			dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(500000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
