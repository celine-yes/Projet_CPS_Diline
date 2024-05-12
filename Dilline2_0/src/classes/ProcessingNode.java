package classes;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;


/**
 * Implements the {@link ProcessingNodeI} interface, encapsulating functionalities
 * related to a processing node within a sensor network. This includes storing and
 * managing sensor data, maintaining node information, and keeping track of neighbouring nodes.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class ProcessingNode implements ProcessingNodeI{
	
	private NodeInfoI node ;
	private ArrayList<SensorDataI> sensorsinfo;
	private Set<NodeInfoI> neighbours;
	
	public ProcessingNode(NodeInfoI noeud, ArrayList<SensorDataI> sensorsinfo) {
		this.node=noeud;
		this.sensorsinfo = sensorsinfo.stream()
                .map(sensor -> ((SensorData)sensor).copy())
                .collect(Collectors.toCollection(ArrayList::new));
	}
	
	@Override
	public String getNodeIdentifier() {
		return node.nodeIdentifier();
	}

	@Override
	public PositionI getPosition() {
		return node.nodePosition();
	}
	
	public void updateSensorinfo(ArrayList<SensorDataI> sensorsinfo) {
		this.sensorsinfo = sensorsinfo;
	}

	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		for(SensorDataI capteur : sensorsinfo) {
			if(sensorIdentifier.equals(capteur.getSensorIdentifier())) {
				return capteur;
			}
		}
		return null;
	}

	@Override
	public Set<NodeInfoI> getNeighbours() {
		return this.neighbours;
	}

	public void setNeighbours(Set<NodeInfoI> neighbours) {
		this.neighbours = neighbours;
		
	}

}
