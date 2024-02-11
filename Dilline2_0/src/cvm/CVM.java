package cvm;
import composants.client.Client;
import composants.connector.ClientNodeConnector;
import composants.noeud.Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {
		super();
	}
	
	@Override
	public void deploy() throws Exception {
		
		
		//creation de composant client
		String clientURI = AbstractComponent.createComponent(
				Client.class.getCanonicalName(), new Object [] {});
		//creation de composant noeud
		String noeudURI = AbstractComponent.createComponent(
				Node.class.getCanonicalName(), new Object [] {});
		
		this.toggleTracing(clientURI);
		this.toggleTracing(noeudURI);

		//connection des composants
		this.doPortConnection(
				clientURI, 
				Client.CLOP_URI, 
				Node.NIP_URI, 
				ClientNodeConnector.class.getCanonicalName());
		super.deploy();
	}

	public static void main(String[] args) {
		System.out.println("dans main");
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(2500L);
			Thread.sleep(10000L);
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
