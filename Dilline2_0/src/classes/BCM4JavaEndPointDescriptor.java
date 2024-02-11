package classes;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI {
	
	private String inboundPortURI;
	
	public BCM4JavaEndPointDescriptor(String port) {
		this.inboundPortURI = port;
	}

	@Override
	public String getInboundPortURI() {
		// TODO Auto-generated method stub
		return inboundPortURI;
	}

	@Override
	public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
		// TODO Auto-generated method stub
		return false;
	}

}
