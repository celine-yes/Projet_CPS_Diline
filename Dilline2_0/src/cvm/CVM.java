package cvm;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import classes.GeographicalZone;
import classes.Position;
import classes.utils.NodeFactory;
//import composants.client.Client;
//import composants.noeud.Node;
import composants.register.Register;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import tests.requests.RequestTest;
import withplugin.composants.Client;
import withplugin.composants.Node;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {
		super();
	}
	
//	public static final int NB_NODES = 10;
//	public static int timeBeforeSendingRequest = NB_NODES+1000;
//	public static int timeBeforeShowingResult = timeBeforeSendingRequest + NB_NODES;
//	public static int timeBeforeUpdatingSensorValue = timeBeforeShowingResult + 1;
	
	public static final int NB_NODES = 50;
	public static int timeBeforeSendingRequest = NB_NODES+500;
	public static int timeBeforeShowingResultAsync = timeBeforeSendingRequest + 2;
	public static int timeBeforeUpdatingSensorValue = timeBeforeShowingResultAsync + 1;

	
	/** URI of the registration inbound port of the register.						*/
	public final static String	REGISTER_REGISTRATION_INBOUND_PORT_URI = 
			                                            "registerregistrationibpURI" ;
	/** URI of the lookup inbound port of the register.						*/
	public final static String	REGISTER_LOOKUP_INBOUND_PORT_URI = 
			                                            "registerlookupibpURI" ;


//	/** For ClocksServer						*/
//	public static final String TEST_CLOCK_URI = "test-clock";
//    public static final Instant START_INSTANT =
//    Instant.parse("2024-01-31T09:00:00.00Z");
//    protected static final long START_DELAY = 5000L;
//    public static final double ACCELERATION_FACTOR = 100.0;
//    public static final long unixEpochStartTimeInNanos =
//            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
    
	/** For ClocksServer						*/
	public static final String TEST_CLOCK_URI = "test-clock";
    public static final Instant START_INSTANT =
    Instant.parse("2024-01-31T09:00:00.00Z");
    protected static final long START_DELAY = 7000L;
    public static final double ACCELERATION_FACTOR = 100.;
    public static final long unixEpochStartTimeInNanos =
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
    
    public static int number = 1; //pour affichage
    public static final int nbRequestsPerClient = 5;
	
	@Override
	public void deploy() throws Exception {
		
		//Zone du client
		PositionI p1 = new Position(23, 25);
		PositionI p2 = new Position(23, 25);
		GeographicalZoneI zone = new GeographicalZone(p1,p2);
		
		/** création du composant register           **/
        AbstractComponent.createComponent(
				Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
															      REGISTER_REGISTRATION_INBOUND_PORT_URI});
        
        /** création du composant client           **/
        AbstractComponent.createComponent(
                Client.class.getCanonicalName(), new Object [] {zone,RequestTest.createBooleanDirectionalRequests(nbRequestsPerClient, true)});
        AbstractComponent.createComponent(
                Client.class.getCanonicalName(), new Object [] {zone,RequestTest.createBooleanDirectionalRequests(nbRequestsPerClient, true)});
        AbstractComponent.createComponent(
                Client.class.getCanonicalName(), new Object [] {zone,RequestTest.createBooleanDirectionalRequests(nbRequestsPerClient, true)});
        AbstractComponent.createComponent(
                Client.class.getCanonicalName(), new Object [] {zone,RequestTest.createBooleanDirectionalRequests(nbRequestsPerClient, true)});
        AbstractComponent.createComponent(
                Client.class.getCanonicalName(), new Object [] {zone,RequestTest.createBooleanDirectionalRequests(nbRequestsPerClient, true)});

		
		/** création des composants nodes           **/
		
		Map<NodeInfoI, ArrayList<SensorDataI>> nodeInfos = NodeFactory.createNodes(CVM.NB_NODES, 7);
		NodeFactory.displayNodes(nodeInfos);  
		nodeInfos.entrySet().stream()
        .forEach(entry -> {
			try {
				AbstractComponent.createComponent(
						Node.class.getCanonicalName(), new Object [] {entry.getKey(), entry.getValue()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
        
        /** création du composant clockServer         **/
        AbstractComponent.createComponent(
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
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(100000L);
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}