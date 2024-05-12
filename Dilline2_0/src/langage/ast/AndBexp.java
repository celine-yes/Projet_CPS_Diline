package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBexp;

/**
 * {@code AndBexp} class implements {@code IBexp} to represent a logical "AND" operation between two boolean expressions
 * within the domain-specific language of a sensor network.
 *
 * <p>This class encapsulates two boolean expressions and evaluates them using logical conjunction, making it a fundamental
 * component for building complex conditional logic in sensor network configurations and queries.</p>
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class AndBexp implements IBexp {
    private static final long serialVersionUID = 1L;
    
    private IBexp bexp1;
    private IBexp bexp2;
    
    /**
     * Constructs a new {@code AndBexp} with the specified boolean expressions.
     *
     * @param bexp1 the first boolean expression part of this logical AND operation.
     * @param bexp2 the second boolean expression part of this logical AND operation.
     */
    public AndBexp(IBexp bexp1, IBexp bexp2) {
        super();
        this.bexp1 = bexp1;
        this.bexp2 = bexp2;
    }

    /**
     * Returns the first boolean expression in this AND operation.
     *
     * @return the first boolean expression.
     */
    public IBexp getBexp1() {
        return bexp1;
    }

    /**
     * Returns the second boolean expression in this AND operation.
     *
     * @return the second boolean expression.
     */
    public IBexp getBexp2() {
        return bexp2;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Evaluates the logical AND result of the two encapsulated boolean expressions based on the current execution state.</p>
     * 
     * @return the boolean result of this AND expression.
     * @throws Exception if there is an error during the evaluation of either expression.
     */
    @Override
    public boolean eval(ExecutionStateI data) throws Exception {
        boolean bexp1Result = bexp1.eval(data);
        boolean bexp2Result = bexp2.eval(data);
        return bexp1Result && bexp2Result;
    }
}
