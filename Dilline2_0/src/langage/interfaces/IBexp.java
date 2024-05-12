package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * The {@code IBexp} interface defines the contract for boolean expressions in a sensor network's domain-specific language.
 *
 * <p>This interface requires implementing classes to provide a mechanism to evaluate the expression based on the current execution state,
 * returning a boolean result.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IBexp extends Serializable {
    /**
     * Evaluates this boolean expression based on the provided execution state.
     *
     * @param data the current execution state.
     * @return the result of the evaluation as a boolean.
     * @throws Exception if there are errors during the evaluation process.
     */
    boolean eval(ExecutionStateI data) throws Exception;
}
