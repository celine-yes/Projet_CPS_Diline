package cvm;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import classes.BCM4JavaEndPointDescriptor;
import classes.ConnectionInfo;
import classes.GeographicalZone;
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
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
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


	/** URIs of the registration outbound port of nodes.						*/
	public final static String	NODE1_REGISTRATION_OUTBOUND_PORT_URI =
														"node1registrationobpURI" ;
	public final static String	NODE2_REGISTRATION_OUTBOUND_PORT_URI =
														"node2registrationobpURI" ;
	public final static String	NODE3_REGISTRATION_OUTBOUND_PORT_URI =
														"node3registrationobpURI" ;
	public final static String	NODE4_REGISTRATION_OUTBOUND_PORT_URI =
														"node4registrationobpURI" ;
	public final static String	NODE5_REGISTRATION_OUTBOUND_PORT_URI =
														"node5registrationobpURI" ;
	
	/** URIs of the SensorNodeP2P inboundport of nodes.						*/
	public final static String	NODE1_P2P_INBOUND_PORT_URI =
														"node1P2PibpURI" ;
	public final static String	NODE2_P2P_INBOUND_PORT_URI =
														"node2P2PibpURI" ;
	public final static String	NODE3_P2P_INBOUND_PORT_URI =
														"node3P2PibpURI" ;
	public final static String	NODE4_P2P_INBOUND_PORT_URI =
														"node4P2PibpURI" ;
	public final static String	NODE5_P2P_INBOUND_PORT_URI =
														"node5P2PibpURI" ;

	
	/** URIs of the requesting inboundport of nodes.						*/
	public final static String	NODE1_REQUESTING_INBOUND_PORT_URI =
														"node1RrequestingibpURI" ;
	public final static String	NODE2_REQUESTING_INBOUND_PORT_URI =
														"node2requestingibpURI" ;
	public final static String	NODE3_REQUESTING_INBOUND_PORT_URI =
														"node3requestingibpURI" ;
	public final static String	NODE4_REQUESTING_INBOUND_PORT_URI =
														"node4requestingibpURI" ;
	public final static String	NODE5_REQUESTING_INBOUND_PORT_URI =
														"node5requestingibpURI" ;
	
	/** URIs of the request result outboundport of nodes.						*/
	public final static String	NODE1_REQUESTR_OUTBOUND_PORT_URI =
														"node1requestrobpURI" ;
	public final static String	NODE2_REQUESTR_OUTBOUND_PORT_URI =
														"node2requestrobpURI" ;
	public final static String	NODE3_REQUESTR_OUTBOUND_PORT_URI =
														"node3requestrobpURI" ;
	public final static String	NODE4_REQUESTR_OUTBOUND_PORT_URI =
														"node4requestrobpURI" ;
	public final static String	NODE5_REQUESTR_OUTBOUND_PORT_URI =
														"node5requestrobpURI" ;
	
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
    
    public static int number = 1;
    public HashMap<NodeInfo, List<SensorData>> creerNoeuds(int nombreNoeuds) throws Exception {
    	
        // Liste des types de capteurs
        List<String> typesCapteurs = Arrays.asList(
            "fumee",
            "temperature",
            "vitesse_vent",
            "humidite",
            "pression_atm",
            "lumiere",
            "son"
        );
        
        // HashMap pour stocker les noeuds et les capteurs
        HashMap<NodeInfo, List<SensorData>> noeudsEtCapteurs = new HashMap<>();

        // Générateur de nombres aléatoires
        Random random = new Random();

        // Rayon du cercle
        int rayon = 50;

        // Boucle pour créer le nombre de noeuds spécifié
        for (int i = 0; i < nombreNoeuds; i++) {

            // Génération aléatoire de l'identifiant du noeud
            String nodeId = "node" + (i + 1);

            // Génération aléatoire de l'angle du noeud sur le cercle
            double angle = random.nextDouble() * 2 * Math.PI;

            // Calcul des coordonnées du noeud
            int x = (int) (rayon * Math.cos(angle) + rayon);
            int y = (int) (rayon * Math.sin(angle) + rayon);

            // Création de la position du noeud
            Position position = new Position(x, y);
            
            if(i == nombreNoeuds - 1) {
            	this.p2 = position;
            }

            // Génération aléatoire des capteurs du noeud
            List<SensorData> capteurs = new ArrayList<>();
            List<String> capteurIDs = new ArrayList<>();
            int nombreCapteurs = random.nextInt(3) + 1;
            for (int j = 0; j < nombreCapteurs; j++) {

                // Génération aléatoire du type de capteur
                String sensorId = typesCapteurs.get(random.nextInt(typesCapteurs.size()));
                while(capteurIDs.contains(sensorId)){
                	sensorId = typesCapteurs.get(random.nextInt(typesCapteurs.size()));
                }

                // Génération aléatoire de la valeur du capteur
                double sensorValue = random.nextDouble() * 100;

                // Ajout du capteur à la liste
                capteurs.add(new SensorData(nodeId, sensorId, sensorValue));
                capteurIDs.add(sensorId);
            }

            // Génération aléatoire des URIs
            String requestingibpURI = "node" + (i + 1) + "_requestingibpURI" ;
            String p2PibpURI = "node" + (i + 1) + "_P2PibpURI";
            String requestRobURI = "node" + (i + 1) + "_requestRobURI";
            String registrobURI = "node" + (i + 1) + "_registrobURI";
            

            // Création des BCM4JavaEndPointDescriptorI
            BCM4JavaEndPointDescriptorI inboundRequesting = new BCM4JavaEndPointDescriptor (requestingibpURI);
            BCM4JavaEndPointDescriptorI inboundP2P = new BCM4JavaEndPointDescriptor(p2PibpURI);

            // Création du noeud
            NodeInfo node = new NodeInfo(nodeId, position, inboundRequesting, inboundP2P, 50.0);
            
            

            // Ajout du noeud à la liste
            noeudsEtCapteurs.put(node, capteurs);
        
        
            /** création des composants nodes           **/
            String nodeUri = AbstractComponent.createComponent(
            				Node.class.getCanonicalName(), new Object [] {
            						requestingibpURI,
            						p2PibpURI,
            						requestRobURI,
            						registrobURI,
            						node,
            						capteurs}
            );
            
            //pour toggleTracing et toggleLogging apres;
            nodesURI.add(nodeUri);
        
        }
        // Affiche les noeuds créés
        for (NodeInfo noeud : noeudsEtCapteurs.keySet()) {

            // Affichage de l'identifiant du noeud
            System.out.println("Noeud " + noeud.nodeIdentifier() + ":");

            // Affichage de la position du noeud
            Position pos = (Position) noeud.nodePosition();
            System.out.println("  Position: ( " + pos.getX() + ", " + pos.getY() + " )");

            // Affichage des capteurs du noeud
            System.out.println("  Capteurs:");
            for (SensorData capteur : noeudsEtCapteurs.get(noeud)) {
                System.out.println("	" + capteur.getSensorIdentifier() + " : " + capteur.getValue() + " \n");
            }

        }

        return noeudsEtCapteurs;
    }


	
	@Override
	public void deploy() throws Exception {
		
		//creerNoeuds(5);

		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);
        
        
//        //creation de NodeInfo pour parametre de composant noeud
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
		PositionI positionNode2= new Position(20,20);
		PositionI positionNode3= new Position(30,15);
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
		NodeInfoI node5 = new NodeInfo(
				nodeId5, 
				positionNode5,
				new BCM4JavaEndPointDescriptor(NODE5_REQUESTING_INBOUND_PORT_URI), 
				new BCM4JavaEndPointDescriptor(NODE5_P2P_INBOUND_PORT_URI),
				range);
		
		ConnectionInfoI clientConnectionInfo = new ConnectionInfo(clientID, 
				new BCM4JavaEndPointDescriptor(CLIENT_REQUESTRESULT_INBOUND_PORT_URI));
		
		
		
		/**  création des requetes pour composant client    **/
		
		//Comparaisons
		IBexp bexp = new GeqCexp(
				new SRand(sensorId1),
				new CRand(29.0));
		
		//Continuations Inondations
		double distFcont = 50.0;
		IFCont fcont = new FCont(new ABase(positionNode1), distFcont);
		
		//Continuations Directionnelles
		int maxSauts = 2;
		IDirs direction1 = new FDirs(Direction.NE);
		IDirs direction2 = new RDirs(Direction.NE, new FDirs(Direction.SE));
		
		IDCont dcont = new DCont(direction2, maxSauts);
		
		//Requêtes booléennes
		QueryI bqueryE = new BQuery(new ECont(), bexp);
		RequestI requestBEcont = new Request("requestBEcont", bqueryE, clientConnectionInfo);
		
		QueryI bqueryF = new BQuery(fcont, bexp);
		RequestI requestBFcont = new Request("requestBFcont", bqueryF, clientConnectionInfo);
		
		QueryI bqueryD = new BQuery(dcont, bexp);
		RequestI requestBDcont = new Request("requestBDcont", bqueryD, clientConnectionInfo);
		
		//Requêtes de collectes
		RGather rg = new RGather("temperature",new FGather("fumee"));
		
		QueryI gqueryE = new GQuery(new ECont(),rg);
		RequestI requestGEcont = new Request("requestGEcont", gqueryE, clientConnectionInfo);
		
		QueryI gqueryF = new GQuery(fcont,rg);
		RequestI requestGFcont = new Request("requestGFcont", gqueryF, clientConnectionInfo);
		
		QueryI gqueryD = new GQuery(dcont,rg);
		RequestI requestGDcont = new Request("requestGDcont", gqueryD, clientConnectionInfo);
		
		
		/** création du composant register           **/
        this.register_uri = AbstractComponent.createComponent(
				Register.class.getCanonicalName(), new Object [] {REGISTER_LOOKUP_INBOUND_PORT_URI,
															      REGISTER_REGISTRATION_INBOUND_PORT_URI});
        
        
        /** création du composant client           **/
		this.client_uri = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {CLIENT_REQUESTING_OUTBOUND_PORT_URI,
																CLIENT_LOOKUP_OUTBOUND_PORT_URI, 
																CLIENT_REQUESTRESULT_INBOUND_PORT_URI, 
																zone,requestBDcont});

		
//		/** création des composants nodes           **/
        this.node1_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE1_REQUESTING_INBOUND_PORT_URI,
														      NODE1_P2P_INBOUND_PORT_URI,
														      NODE1_REQUESTR_OUTBOUND_PORT_URI,
														      NODE1_REGISTRATION_OUTBOUND_PORT_URI,
														      node1, sensorsNode1});
        
        this.node2_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE2_REQUESTING_INBOUND_PORT_URI,
														      NODE2_P2P_INBOUND_PORT_URI,
														      NODE2_REQUESTR_OUTBOUND_PORT_URI,
														      NODE2_REGISTRATION_OUTBOUND_PORT_URI,
														      node2, sensorsNode2});
        
        this.node3_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE3_REQUESTING_INBOUND_PORT_URI,
														      NODE3_P2P_INBOUND_PORT_URI,
														      NODE3_REQUESTR_OUTBOUND_PORT_URI,
														      NODE3_REGISTRATION_OUTBOUND_PORT_URI,
														      node3, sensorsNode3});
        
        this.node4_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE4_REQUESTING_INBOUND_PORT_URI,
														      NODE4_P2P_INBOUND_PORT_URI,
														      NODE4_REQUESTR_OUTBOUND_PORT_URI,
														      NODE4_REGISTRATION_OUTBOUND_PORT_URI,
														      node4, sensorsNode4});
        this.node5_uri = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {NODE5_REQUESTING_INBOUND_PORT_URI,
														      NODE5_P2P_INBOUND_PORT_URI,
														      NODE5_REQUESTR_OUTBOUND_PORT_URI,
														      NODE5_REGISTRATION_OUTBOUND_PORT_URI,
														      node5, sensorsNode5});
        
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