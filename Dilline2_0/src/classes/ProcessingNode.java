package classes;

import java.util.ArrayList;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ProcessingNode implements ProcessingNodeI{
	
	private NodeInfoI node ;
	private ArrayList<SensorDataI> sensorsinfo;
	
	public ProcessingNode(NodeInfoI noeud, ArrayList<SensorDataI> sensorsinfo) {
		this.node=noeud;
		this.sensorsinfo = sensorsinfo;
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

	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		for(SensorDataI capteur : sensorsinfo) {
			if(sensorIdentifier == capteur.getSensorIdentifier()) {
				return capteur;
			}
		}
		return null;
	}

}
