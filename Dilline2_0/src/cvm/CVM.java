package cvm;
import classes.BCM4JavaEndPointDescriptor;
import classes.NodeInfo;
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
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

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
		NodeInfoI node1 = new NodeInfo(
				"node1", 
				null, 
				new BCM4JavaEndPointDescriptor("node1client"), 
				new BCM4JavaEndPointDescriptor("node1voisin"),
				50.0);
		
		SensorDataI sensorNode1 = new SensorData("node1", "temperature", 51.0);
		
		//creation de composant client
		String clientURI = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {});
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
