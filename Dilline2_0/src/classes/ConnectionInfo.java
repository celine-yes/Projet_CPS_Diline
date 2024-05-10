package classes;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

/**
 * The class <code>ConnectionInfo</code> implements the methods
 * of the interface <code>ConnectionInfoI</code> including set methods.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

public class ConnectionInfo implements ConnectionInfoI, Serializable{
	
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
