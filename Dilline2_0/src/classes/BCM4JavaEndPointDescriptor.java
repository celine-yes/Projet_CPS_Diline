package classes;

import java.io.Serializable;

/**
 * The class <code>BCM4JavaEndPointDescriptor</code> implements the methods
 * of the interface <code>BCM4JavaEndPointDescriptorI</code>.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String inboundPortURI;
	private Class<? extends OfferedCI> offeredInterface;
	
	public BCM4JavaEndPointDescriptor(String port, Class<? extends OfferedCI> offeredInterface) {
		this.inboundPortURI = port;
		this.offeredInterface = offeredInterface;
	}

	@Override
	public String getInboundPortURI() {
		return inboundPortURI;
	}

	@Override
	public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
		if (inter == null) return false;
		return offeredInterface.isAssignableFrom(inter);
	
	}
}
