package langage.ast;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBase;

/**
 * {@code ABase} class implements {@code IBase} to provide a basic representation of a base entity within a sensor network,
 * encapsulating a position attribute and offering a mechanism to evaluate and retrieve its position.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public class ABase implements IBase {
    
    private static final long serialVersionUID = 1L;
    private PositionI position;

    /**
     * Constructs a new {@code ABase} with the specified initial position.
     *
     * @param position the initial position of the base entity.
     */
    public ABase(PositionI position) {
        super();  // Though not required as Java does it implicitly for the Object class
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PositionI getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>In this implementation, the evaluation simply returns the current position without modifying it,
     * assuming no dependency on the execution state.</p>
     */
    @Override
    public PositionI eval(ExecutionStateI data) {
        return position;  // No actual evaluation logic, just returns current position
    }
}
