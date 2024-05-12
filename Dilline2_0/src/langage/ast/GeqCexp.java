package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.ICexp;
import langage.interfaces.IRand;

/**
 * Represents a greater or equal comparison expression within the domain-specific language for sensor networks.
 * This class compares two numerical values, each provided by an {@code IRand} instance, to determine if
 * the first value is greater or equal to the second.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class GeqCexp implements ICexp{
	
	private static final long serialVersionUID = 1L;
	private IRand rand1;
	private IRand rand2;
	
	public GeqCexp(IRand rand1, IRand rand2) {
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
     * Evaluates the greater or equal comparison between the two numeric values encapsulated
     * by the {@code IRand} instances. The result is determined by comparing the double
     * values of the operands.
     *
     * @param data the execution state context (not directly used in this evaluation).
     * @throws Exception if there are errors during the evaluation process.
     */
	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		double value1 = ((Number) rand1.eval(data)).doubleValue();
        double value2 = ((Number) rand2.eval(data)).doubleValue();
        return value1 >= value2;
	}

}
