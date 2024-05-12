package classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;


/**
 * The class <code>ProcessingNode</code> implements the methods
 * of the interface <code>ProcessingNodeI</code>.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

public class ProcessingNode implements ProcessingNodeI{

	
	private static final long serialVersionUID = 1L;
	private NodeInfoI node ;
	private ArrayList<SensorDataI> sensorsinfo;
	
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

	@Override
	public Set<NodeInfoI> getNeighbours() {
		return null;
	}
	
	public void updateSensorinfo(ArrayList<SensorDataI> sensorsinfo) {
		this.sensorsinfo = sensorsinfo;
	}

	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		for(SensorDataI capteur : sensorsinfo) {
			System.out.println("capteurrrrr "+ capteur.getSensorIdentifier());
			if(sensorIdentifier.equals(capteur.getSensorIdentifier())) {
				System.out.println("truuuuuuue");
				return capteur;
			}
		}
		return null;
	}
	
	public int getsize() {
		return sensorsinfo.size();
	}

}
