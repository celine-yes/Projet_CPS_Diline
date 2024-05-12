package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICexp;
import langage.interfaces.IRand;

/**
 * Represents an equality comparison expression within the domain-specific language for sensor networks.
 * This class compares two real numbers, each provided by an {@code IRand} instance, for equality.
 *
 * <p>{@code EqCexp} is used in the context of sensor network queries to perform conditional evaluations.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class EqCexp implements ICexp{
	
	private static final long serialVersionUID = 1L;
	private IRand rand1;
	private IRand rand2;
	
	public EqCexp(IRand rand1, IRand rand2) {
		super();
		this.rand1 = rand1;
		this.rand2 = rand2;
	}
	
	/**
     * Retrieves the first real number operand.
     *
     * @return the first operand as an {@code IRand}.
     */
	public IRand getRand1() {
		return rand1;
	}
	
	/**
     * Retrieves the second real number operand.
     *
     * @return the second operand as an {@code IRand}.
     */
	public IRand getRand2() {
		return rand2;
	}
	
	/**
     * Evaluates the equality of the two real number values encapsulated by the {@code IRand} instances.
     * The equality is determined based on the standard Java double comparison rules.
     *
     * @param data the execution state context (not directly used in this evaluation).
     * @return {@code true} if the two numbers are equal, otherwise {@code false}.
     * @throws Exception if there are errors during the evaluation process.
     */
	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		double value1 = ((Number) rand1.eval(data)).doubleValue();
        double value2 = ((Number) rand2.eval(data)).doubleValue();
        return value1 == value2;
	}
}
