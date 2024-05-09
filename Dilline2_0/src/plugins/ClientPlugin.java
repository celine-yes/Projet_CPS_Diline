package plugins;

import java.util.Set;

import composants.client.LookupOutboundPort;
import composants.client.RequestingOutboundPort;
import composants.connector.ClientNodeConnector;
import composants.connector.ClientRegisterConnector;
import cvm.CVM;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class ClientPlugin extends AbstractPlugin implements RequestingCI, LookupCI {

	private static final long serialVersionUID = 1L;
	protected RequestingOutboundPort	outboundPortRequesting;
	protected LookupOutboundPort	outboundPortLookup;
	protected ConnectionInfoI nodeToConnect;
	
	@Override
	public void	installOn(ComponentI owner)
	throws Exception
	{
		super.installOn(owner);

	}
	
	@Override
	public void			initialise() throws Exception
	{	
		this.outboundPortRequesting = new RequestingOutboundPort(this.getOwner());
		this.outboundPortLookup = new LookupOutboundPort(this.getOwner()) ;
		
		this.outboundPortRequesting.publishPort();
		this.outboundPortLookup.publishPort();
		
		//connection entre client et register via LookupCI
  		this.getOwner().doPortConnection(
  				this.outboundPortLookup.getPortURI(),
  				CVM.REGISTER_LOOKUP_INBOUND_PORT_URI,
  				ClientRegisterConnector.class.getCanonicalName());
  		
  		super.initialise();
	}
	
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return this.outboundPortLookup.findByIdentifier(sensorNodeId);
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return this.outboundPortLookup.findByZone(z);
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		
		//récupérer le inboundport du noeud sur lequel le client doit envoyer la requete
		BCM4JavaEndPointDescriptorI endpoint =(BCM4JavaEndPointDescriptorI) nodeToConnect.endPointInfo();
		String nodeInboundPort = endpoint.getInboundPortURI();
		
		//connection entre client et noeud choisi via RequestingCI
		this.getOwner().doPortConnection(
				this.outboundPortRequesting.getPortURI(),
				nodeInboundPort,
				ClientNodeConnector.class.getCanonicalName());
		
		return this.outboundPortRequesting.execute(request);
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		
		//récupérer le inboundport du noeud sur lequel le client doit envoyer la requete
		BCM4JavaEndPointDescriptorI endpoint =(BCM4JavaEndPointDescriptorI) nodeToConnect.endPointInfo();
		String nodeInboundPort = endpoint.getInboundPortURI();
		
		//connection entre client et noeud choisi via RequestingCI
		this.getOwner().doPortConnection(
				this.outboundPortRequesting.getPortURI(),
				nodeInboundPort,
				ClientNodeConnector.class.getCanonicalName());
		
		this.outboundPortRequesting.executeAsync(request);
		
	}
	
	public RequestingCI getRequestingServicesReference() {
		return this.outboundPortRequesting;
	}
	
	
	public LookupCI getLookupServicesReference() {
		return this.outboundPortLookup;
	}
	
	public void setNodeToConnect(ConnectionInfoI node) {
		this.nodeToConnect = node;
	}
	
	@Override
	public void			finalise() throws Exception
	{
//		this.getOwner().doPortDisconnection(this.outboundPortRequesting.getPortURI());
//		this.getOwner().doPortDisconnection(this.outboundPortLookup.getPortURI());
//		
//		super.finalise();
	}
	
	@Override
	public void			uninstall() throws Exception
	{
//		this.outboundPortRequesting.unpublishPort();
//		this.outboundPortLookup.unpublishPort();
//		this.outboundPortRequesting.destroyPort();
//		this.outboundPortLookup.destroyPort();
//		this.removeRequiredInterface(RequestingCI.class);
//		this.removeRequiredInterface(LookupCI.class);
	}

}
