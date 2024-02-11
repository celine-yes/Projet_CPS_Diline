package composants.register;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


@OfferedInterfaces(offered = {LookupCI.class, RegistrationCI.class})
public class Register extends AbstractComponent {

	protected Register(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

}
