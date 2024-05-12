package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

/**
 * Implements {@link ConnectionInfoI}, providing a concrete representation of connection
 * information for a sensor node, including its unique identifier and endpoint information.
 * This class serves as a utility to facilitate network connection handling by storing
 * and providing access to essential connection details.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class ConnectionInfo implements ConnectionInfoI{
	
	private static final long serialVersionUID = 1L;
	private String identifiant;
	private BCM4JavaEndPointDescriptorI endPointInfo;
	
	public ConnectionInfo(String identifiant) {
		this.identifiant = identifiant;
		this.endPointInfo = null;
	}
	
	public ConnectionInfo(String nodeIdentifier, BCM4JavaEndPointDescriptorI endPointInfo2) {
		this.identifiant = nodeIdentifier;
		this.endPointInfo = endPointInfo2;
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
