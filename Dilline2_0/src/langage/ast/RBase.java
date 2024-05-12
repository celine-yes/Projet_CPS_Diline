package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.interfaces.IBase;

/**
 * Represents a dynamic base reference in a sensor network query language.
 * This class evaluates to the current position of the processing node, effectively
 * updating the reference base position dynamically during the execution of queries.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class RBase implements IBase{
	
	private static final long serialVersionUID = 1L;
	private PositionI position;

	public RBase() {
		super();
	}
	
	@Override
	public PositionI getPosition() {
		return position;
	}
	
	 /**
     * Evaluates the position by setting it to the current position of the processing node.
     * This method updates the base position dynamically based on the current processing node's position.
     * 
     * @param data the execution state which contains references to the current processing node.
     * @return the updated position of the base.
     */
	@Override
	public PositionI eval(ExecutionStateI data) {
		ProcessingNodeI processingNode = data.getProcessingNode();
		position = processingNode.getPosition();
		return position;
	}

}
