package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * The {@code IBase} interface defines the core functionalities of base entities in a sensor network,
 * focusing on position evaluation and retrieval.
 *
 * <p>This interface ensures that any implementing class provides mechanisms to evaluate its position based on
 * execution state data and to retrieve its current position, supporting dynamic interactions within the network.</p>
 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IBase extends Serializable {
    /**
     * Evaluates the position of this entity based on the provided execution state.
     * 
     * @param data the execution state that might influence the evaluation of the position.
     * @return the evaluated position.
     */
    PositionI eval(ExecutionStateI data);

    /**
     * Retrieves the current position of this entity.
     * 
     * @return the current position.
     */
    PositionI getPosition();
}
