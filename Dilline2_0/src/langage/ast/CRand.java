package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IRand;

/**
 * Represents a constant real number within the domain-specific language for sensor networks.
 * This class is part of the language's abstract syntax tree.
 *
 * <p>The {@code CRand} object encapsulates a real number and is typically used in queries and expressions
 * where fixed numerical values are necessary. It provides a straightforward way to integrate constant real
 * values into the dynamic evaluation processes of the sensor network queries.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class CRand implements IRand{
	
	private static final long serialVersionUID = 1L;
	private double reelle;

	public CRand(double reelle) {
		super();
		this.reelle = reelle;
	}

	public double getReelle() {
		return reelle;
	}
	
	/**
     * Evaluates the expression represented by this object, returning the encapsulated real number.
     * This method enables the {@code CRand} to be used in expressions where a real number is expected.
     *
     * @return the real number value encapsulated by this {@code CRand}.
     */
	@Override
	public Object eval(ExecutionStateI data) {
		return reelle;
	}

}
