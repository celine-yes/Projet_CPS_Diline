package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICexp;
import langage.interfaces.IRand;

/**
 * Represents a greater-than comparison expression within the domain-specific language for sensor networks.
 * This class compares two numerical values, each provided by an {@code IRand} instance, to determine if
 * the first value is greater than the second.
 *
 * <p>{@code GCexp} is used in the context of sensor network queries for performing conditional evaluations,
 * typically where data processing decisions depend on the comparison results of numerical values.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class GCexp implements ICexp{
	
	private static final long serialVersionUID = 1L;
	private IRand rand1;
	private IRand rand2;
	
	public GCexp(IRand rand1, IRand rand2) {
		super();
		this.rand1 = rand1;
		this.rand2 = rand2;
	}
		
	/**
     * Retrieves the first numeric operand.
     *
     * @return the first operand as an {@code IRand}.
     */
	public IRand getRand1() {
		return rand1;
	}
	
	/**
     * Retrieves the second numeric operand.
     *
     * @return the second operand as an {@code IRand}.
     */
	public IRand getRand2() {
		return rand2;
	}
	
	 /**
     * Evaluates the greater-than comparison between the two numeric values encapsulated
     * by the {@code IRand} instances. The result is determined by comparing the double
     * values of the operands.
     *
     * @param data the execution state context (not directly used in this evaluation).
     * @return {@code true} if the value of the first operand is greater than that of the second operand,
     * otherwise {@code false}.
     * @throws Exception if there are errors during the evaluation process.
     */
	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		double value1 = ((Number) rand1.eval(data)).doubleValue();
        double value2 = ((Number) rand2.eval(data)).doubleValue();
        return value1 > value2;
	}


}
