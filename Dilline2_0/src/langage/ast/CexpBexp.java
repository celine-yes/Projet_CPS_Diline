package langage.ast;


import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;
import langage.interfaces.ICexp;

/**
 * Represents a boolean expression that wraps a complex expression ({@code ICexp}) within the
 * domain-specific language for sensor network queries. This class is part of the language's
 * abstract syntax tree and allows complex expressions to be evaluated in a boolean context.
 *
 * <p>The class is designed to facilitate the integration of non-boolean expressions into contexts
 * where boolean results are expected, thereby expanding the flexibility and expressiveness of the
 * query language.</p>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class CexpBexp implements IBexp{
	private static final long serialVersionUID = 1L;
	
	private ICexp cexp;

	public CexpBexp(ICexp cexp) {
		super();
		this.cexp = cexp;
	}
	
	/**
     * Retrieves the complex expression associated with this boolean expression.
     *
     * @return the complex expression.
     */
	public ICexp getCexp() {
		return cexp;
	}
	
	   /**
     * Evaluates the encapsulated complex expression within the context of the given execution state.
     *
     * @param data the execution state that provides context for this evaluation.
     * @return the boolean result of evaluating the complex expression.
     * @throws Exception if there are errors during the evaluation process.
     */
	@Override
	public boolean eval(ExecutionStateI data) throws Exception{
		return cexp.eval(data);
	}

}
