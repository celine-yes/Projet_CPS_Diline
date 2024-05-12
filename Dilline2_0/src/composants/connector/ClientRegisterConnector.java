package composants.connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

/**
 * The {@code ClientRegisterConnector} class extends {@link AbstractConnector} and implements
 * {@link LookupCI}, providing methods to interact with the registry service of the sensor network.
 * This connector facilitates the lookup operations to find sensor nodes by their identifiers or by their
 * geographical zone.
 *
 * <p>This connector is crucial for enabling client components to query the registry to locate sensor
 * nodes based on specific criteria, such as node identifiers or geographical locations, thus supporting
 * dynamic interaction within the sensor network.</p>
 *
 * <p>Methods provided include:
 * <ul>
 * <li>{@code findByIdentifier} - Retrieves the connection information of a sensor node based on its identifier.</li>
 * <li>{@code findByZone} - Finds all sensor nodes within a specified geographical zone.</li>
 * </ul>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class ClientRegisterConnector extends AbstractConnector implements LookupCI{
	
    /**
     * Retrieves the connection information for a sensor node identified by its unique identifier.
     * This method delegates the call to the underlying offering's {@code findByIdentifier} method,
     * ensuring that the lookup logic encapsulated in the registry component is utilized.
     *
     * @param sensorNodeId the unique identifier of the sensor node
     * @return {@link ConnectionInfoI} the connection information of the identified sensor node
     * @throws Exception if there is an error in retrieving the connection information
     */
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.offering).findByIdentifier(sensorNodeId);
	}
	
    /**
     * Finds all sensor nodes located within a specified geographical zone. This method delegates
     * the call to the underlying offering's {@code findByZone} method, utilizing the registry's
     * capability to filter nodes based on geographical criteria.
     *
     * @param z the geographical zone within which sensor nodes are to be found
     * @return a {@code Set} of {@link ConnectionInfoI}, representing the connection information for each node
     *         found within the specified zone
     * @throws Exception if there is an error in the lookup process
     */
	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.offering).findByZone(z);
	}

}
