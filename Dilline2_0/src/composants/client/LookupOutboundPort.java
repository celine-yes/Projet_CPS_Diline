package composants.client;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

/**
 * The class <code>LookupOutboundPort</code> implements an outbound
 * port which implements the required interface <code>LookupCI</code> so
 * that it can call its providers through this port.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 * 
 */

public class LookupOutboundPort extends AbstractOutboundPort implements LookupCI{
	
	private static final long serialVersionUID = 1L;
	
    /**
     * Constructs a {@code LookupOutboundPort} with the specified owner.
     * 
     * @param owner the component that owns this port; should not be {@code null}
     * @throws Exception if the port cannot be properly initialized
     */
	public LookupOutboundPort(ComponentI owner) throws Exception {
        super(LookupCI.class, owner);
    }
	
	/**
     * Requests the connection information for a sensor node identified by the given identifier.
     * This method calls the {@code findByIdentifier} method on the connected provider component
     * which implements {@code LookupCI}.
     *
     * @param sensorNodeId the unique identifier of the sensor node
     * @return the connection information of the specified sensor node
     * @throws Exception if the lookup process fails, such as when the identifier does not match any node
     */
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.getConnector()).findByIdentifier(sensorNodeId);
	}
	
	/**
     * Requests the connection information for all sensor nodes located within a specified geographical zone.
     * This method invokes the {@code findByZone} method on the connected provider component
     * which implements {@code LookupCI}.
     *
     * @param z the geographical zone within which the sensor nodes are to be found
     * @return a set of connection information for all sensor nodes located within the specified zone
     * @throws Exception if the lookup process fails or if the zone details are incorrect
     */
	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.getConnector()).findByZone(z);
	}
}
