package composants.noeud;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

//permet de renvoyer les resultats des requetes asynchrone au composant client

public class RequestResultOutboundPort extends AbstractOutboundPort implements RequestResultCI{
	
	private static final long serialVersionUID = 1L;

	public RequestResultOutboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, RequestResultCI.class, owner) ;
		assert	uri != null && owner != null ;
	}

	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		((RequestResultCI)this.getConnector()).acceptRequestResult(requestURI, result);
	}
}