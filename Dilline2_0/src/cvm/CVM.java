package cvm;
import classes.BCM4JavaEndPointDescriptor;
import classes.NodeInfo;
import classes.Request;
import classes.SensorData;
import composants.client.Client;
import composants.connector.ClientNodeConnector;
import composants.noeud.Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.*;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import langage.ast.BQuery;
import langage.ast.CRand;
import langage.ast.ECont;
import langage.ast.FGather;
import langage.ast.GQuery;
import langage.ast.GeqCexp;
import langage.ast.SRand;
import langage.interfaces.IBexp;
import langage.interfaces.QueryI;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {
		super();
	}
	
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
        double sensorValue = 51.0;
        String nodeId = "node1";
        
        
		NodeInfoI node1 = new NodeInfo(
				nodeId, 
				null, 
				new BCM4JavaEndPointDescriptor("node1client"), 
				new BCM4JavaEndPointDescriptor("node1voisin"),
				50.0);
		
		SensorDataI sensorNode1 = new SensorData(nodeId, sensorId, sensorValue);
		
		// création des requetes pour composant client
		IBexp bexp = new GeqCexp(
				new SRand(sensorId),
				new CRand(50.0));
		QueryI query1 = new BQuery(new ECont(), bexp);
		RequestI request1 = new Request("requete1", query1);
		
		
		FGather rg =  new FGather("temperature");
		QueryI query2 = new GQuery(new ECont(),rg);
		RequestI request2 = new Request("requete2", query2);
		
		
		//creation de composant client
		String clientURI = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {request2});
		System.out.println("composant client est cree");
		
		
		//creation de composant noeud
		String noeudURI = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {"node1in", "node1out", node1, sensorNode1});
		System.out.println("composant node est cree");
		
		this.toggleTracing(clientURI);
		this.toggleTracing(noeudURI);
		this.toggleLogging(clientURI);
		this.toggleLogging(noeudURI);

		//connection des composants
		this.doPortConnection(
				clientURI, 
				Client.CLOP_URI, 
				"node1in", 
				ClientNodeConnector.class.getCanonicalName());
		super.deploy();
	}

	public static void main(String[] args) {
		System.out.println("dans main");
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