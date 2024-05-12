package langage.ast;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IECont;

/**
 * Implements {@code IECont} to provide a minimalistic empty continuation evaluation.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class ECont implements IECont{
	
	private static final long serialVersionUID = 1L;
	
	/**
     * Evaluates this continuation within the given execution state, producing an empty list.
     *
     * @param data the execution state that provides context for this evaluation.
     * @return an empty {@code ArrayList<String>}, representing empty continuation.
     */
	@Override
	public Object eval(ExecutionStateI data) {
		return new ArrayList<String>();
	}
	
}
