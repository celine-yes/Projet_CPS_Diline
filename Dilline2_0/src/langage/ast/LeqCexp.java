package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;
import langage.interfaces.IRand;

/**
* Represents a less-than-or-equal-to comparison expression within the domain-specific
* language for sensor networks. This class compares two numerical values, each provided
* by an {@code IRand} instance, to determine if the first value is less than or equal to the second.
*
* <p>{@code LeqCexp} is used in the context of sensor network queries for performing conditional
* evaluations, typically where decisions or data flows depend on the outcomes of these comparisons.</p>
*
* @author Dilyara Babanazarova
* @author Céline Fan
*/
public class LeqCexp implements IBexp{
	
	private static final long serialVersionUID = 1L;
	private IRand rand1;
	private IRand rand2;
	
	/**
     * Constructs a new {@code LeqCexp} with two operands for the comparison.
     *
     * @param rand1 the first numeric operand encapsulated by an {@code IRand}
     * @param rand2 the second numeric operand encapsulated by an {@code IRand}
     */
	public LeqCexp(IRand rand1, IRand rand2) {
		super();
		this.rand1 = rand1;
		this.rand2 = rand2;
	}
	
	public IRand getRand1() {
		return rand1;
	}

	public IRand getRand2() {
		return rand2;
	}
	
	/**
     * Evaluates the less-than-or-equal-to comparison between the two numeric values encapsulated
     * by the {@code IRand} instances. The result is determined by comparing the double
     * values of the operands.
     *
     * @param data the execution state context (not directly used in this evaluation).
     * @return {@code true} if the value of the first operand is less than or equal to that of the second operand,
     * otherwise {@code false}.
     * @throws Exception if there are errors during the evaluation process.
     */
	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		double value1 = ((Number) rand1.eval(data)).doubleValue();
        double value2 = ((Number) rand2.eval(data)).doubleValue();
        return value1 <= value2;
	}


}
