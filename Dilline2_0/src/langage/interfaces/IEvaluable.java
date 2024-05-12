package langage.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents an evaluatable entity within the sensor network query system.
 * This interface is designed to be implemented by all classes that need to
 * be evaluated dynamically, based on the execution state of a sensor network query.
 * It extends {@link Serializable} to ensure that implementations can be serialized
 * for distributed processing or storage purposes.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface IEvaluable extends Serializable {
	
	 /**
     * Evaluates this entity based on the provided execution state context.
     * This method is intended to compute or derive a value dynamically,
     * depending on the state of the execution and potentially involving
     * sensor data or calculated values.
     *
     * @param data the current execution state of the sensor network, providing context
     *             and potentially sensor data required for the evaluation.
     * @return the result of the evaluation, which could be of any type, depending on
     *         the specific implementation.
     * @throws Exception if there is any error during the evaluation process, allowing
     *         for robust error handling during complex evaluations.
     */
	public Object eval(ExecutionStateI data) throws Exception;

}