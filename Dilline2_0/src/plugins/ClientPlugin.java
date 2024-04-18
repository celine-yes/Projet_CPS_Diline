package plugins;

import java.util.Set;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class ClientPlugin extends AbstractPlugin implements RequestingCI, LookupCI {

	private static final long serialVersionUID = 1L;

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
