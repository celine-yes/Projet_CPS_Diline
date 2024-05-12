package composants.connector;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

/**
 * The {@code NodeClientConnector} class extends {@link AbstractConnector} and implements
 * {@link RequestResultCI}, facilitating the return of asynchronous request results from sensor
 * nodes to the client components.
 *
 * <p>This connector is essential for implementing the asynchronous communication pattern within
 * the sensor network, allowing sensor nodes to report back the results of processed requests
 * to the client that initiated them.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class NodeClientConnector extends AbstractConnector implements RequestResultCI {

    /**
     * Sends the result of a request back to the client component that issued the request.
     * This method is part of the asynchronous communication pattern in the sensor network,
     * enabling results to be reported back once they are available, without blocking the
     * sensor nodes or the client.
     *
     * @param requestURI the URI of the request for which results are being returned
     * @param result the result of the request, encapsulated in a {@link QueryResultI} instance
     * @throws Exception if an error occurs during the result transmission process
     */
    @Override
    public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
        ((RequestResultCI)this.offering).acceptRequestResult(requestURI, result);
    }
}
