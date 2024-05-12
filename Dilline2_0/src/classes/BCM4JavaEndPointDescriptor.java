package classes;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

/**
 * Implements the methods of {@link BCM4JavaEndPointDescriptorI}, providing a descriptor
 * for end points in a network, including utilities for managing inbound connections and
 * offered interfaces.
 * <p>
 * This class is part of the sensor network communication module, facilitating the handling
 * of inbound communication ports and their corresponding offered interfaces.
 * </p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI {
    
    private static final long serialVersionUID = 1L;
    private String inboundPortURI;
    private Class<? extends OfferedCI> offeredInterface;
    
    /**
     * Constructs a new descriptor with the specified inbound port URI and offered interface.
     *
     * @param port the URI of the inbound port
     * @param offeredInterface the class of the interface that is offered through the inbound port
     */
    public BCM4JavaEndPointDescriptor(String port, Class<? extends OfferedCI> offeredInterface) {
        this.inboundPortURI = port;
        this.offeredInterface = offeredInterface;
    }

    /**
     * Retrieves the URI of the inbound port associated with this endpoint.
     *
     * @return the inbound port URI
     */
    @Override
    public String getInboundPortURI() {
        return inboundPortURI;
    }

    /**
     * Checks if the provided interface is the same as, or a superclass of, the offered interface.
     *
     * @param inter the interface class to check against the offered interface
     * @return true if the specified interface is assignable from the offered interface, false otherwise
     */
    @Override
    public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
        if (inter == null) return false;
        return offeredInterface.isAssignableFrom(inter);
    }
}
