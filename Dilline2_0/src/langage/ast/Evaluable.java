package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Provides an abstract base for creating evaluable entities within the domain-specific language for sensor networks.
 * This class defines a framework for evaluation processes that can be performed with or without an execution state context.
 *
 * <p>Classes that extend {@code Evaluable} are expected to provide concrete implementations for evaluation mechanisms
 * that may depend on the state of the system or can be state-independent. This flexibility allows for a wide range
 * of expressions and computations within the DSL, facilitating complex decision-making and data processing logic.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public abstract class Evaluable {
	public abstract Object eval();
	public abstract Object eval(ExecutionStateI data);
	
}
