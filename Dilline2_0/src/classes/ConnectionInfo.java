package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

public class ConnectionInfo implements ConnectionInfoI{
	
	private static final long serialVersionUID = 1L;
	private String identifiant;
	private BCM4JavaEndPointDescriptorI endPointInfo;
	
	public ConnectionInfo(String identifiant) {
		this.identifiant = identifiant;
		this.endPointInfo = null;
	}
	
	public void setEndPointInfo(BCM4JavaEndPointDescriptorI endPointInfo) {
		this.endPointInfo = endPointInfo;
	}
	
	@Override
	public String nodeIdentifier() {
		return identifiant;
	}

	@Override
	public EndPointDescriptorI endPointInfo() {
		return endPointInfo;
	}

}
