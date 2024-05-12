package composants.connector;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

/**
 * The {@code ClientNodeConnector} class implements {@link RequestingCI} and serves as a connector
 * between clients and sensor nodes within a sensor network. This connector facilitates the execution
 * of synchronous and asynchronous requests from the client to the sensor nodes.
 *
 * <p>It extends {@link AbstractConnector}, leveraging its functionality to connect component interfaces
 * for remote method invocation based on BCM (BIP Component Model) principles. This implementation ensures
 * that requests can be forwarded to sensor nodes effectively, handling both immediate responses and
 * deferred execution through callbacks.
 *
 * <p>Methods implemented:
 * <ul>
 * <li>{@code execute} - Forwards a synchronous request to the sensor node and returns its result.</li>
 * <li>{@code executeAsync} - Forwards an asynchronous request to the sensor node for processing.</li>
 * </ul>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class ClientNodeConnector extends AbstractConnector implements RequestingCI {

    /**
     * Executes a synchronous request sent by the client and returns the response from the sensor node.
     * This method essentially invokes the {@code execute} method on the sensor node's side of the connection.
     *
     * @param request the request to be sent to the sensor node
     * @return {@link QueryResultI} the result of the query processed by the sensor node
     * @throws Exception if there is an error during the request processing
     */
    @Override
    public QueryResultI execute(RequestI request) throws Exception {
        return ((RequestingCI)this.offering).execute(request);
    }

    /**
     * Executes an asynchronous request sent by the client. This method invokes the {@code executeAsync}
     * on the sensor node's side of the connection, where the actual processing is deferred and handled separately.
     *
     * @param request the request to be sent to the sensor node
     * @throws Exception if there is an error initiating the asynchronous request processing
     */
    @Override
    public void executeAsync(RequestI request) throws Exception {
        ((RequestingCI)this.offering).executeAsync(request);
    }

}
