package classes;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ProcessingNode implements ProcessingNodeI{
	
	private NodeInfoI node ;
	private SensorDataI sensorinfo;
	
	public ProcessingNode(NodeInfoI noeud, SensorDataI sensorinfo) {
		this.node=noeud;
		this.sensorinfo = sensorinfo;
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
		if(sensorIdentifier == sensorinfo.getSensorIdentifier()) {
			return sensorinfo;
		}
		return null;
	}

}
