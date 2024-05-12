package langage.ast;

import classes.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBexp;
import langage.interfaces.ICont;
import langage.interfaces.QueryI;


/**
 * Implements {@code QueryI} to provide a Boolean query functionality.
 * This class encapsulates a content and a boolean expression to determine the query result.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class BQuery implements QueryI {
    
    private static final long serialVersionUID = 1L;
    private ICont cont;
    private IBexp bexp;
    
    /**
     * Constructs a new {@code BQuery} with the specified content and boolean expression.
     *
     * @param cont the content associated with this query.
     * @param bexp the boolean expression used to evaluate this query.
     */
    public BQuery(ICont cont, IBexp bexp) {
        super();
        this.cont = cont;
        this.bexp = bexp;
    }

    /**
     * Retrieves the content associated with this query.
     *
     * @return the content of this query.
     */
    public ICont getCont() {
        return cont;
    }

    /**
     * Retrieves the boolean expression associated with this query.
     *
     * @return the boolean expression of this query.
     */
    public IBexp getBexp() {
        return bexp;
    }

    /**
     * Evaluates the query based on the current execution state.
     * The evaluation checks the truth of the boolean expression to conditionally populate the result.
     *
     * @param data the execution state providing context and necessary information for this evaluation.
     * @return the result of the query evaluation.
     * @throws Exception if there are errors during the evaluation process.
     */
    @Override
    public QueryResultI eval(ExecutionStateI data) throws Exception {
        QueryResultI result = new QueryResult();
        ProcessingNodeI processingNode = data.getProcessingNode();

        ((QueryResult) result).setIsBoolean();   
        
        // Evaluate the boolean expression for the current node
        if (bexp.eval(data)) {   
            ((QueryResult) result).setpositiveSensorNodes(processingNode.getNodeIdentifier()); 
        }
        cont.eval(data);

        return result;
    }
}
