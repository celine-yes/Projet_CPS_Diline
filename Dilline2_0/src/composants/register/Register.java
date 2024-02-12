package composants.register;

import java.util.HashSet;
import java.util.Set;

import classes.ConnectionInfo;
import composants.client.ClientInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


@OfferedInterfaces(offered = {LookupCI.class, RegistrationCI.class})
public class Register extends AbstractComponent {
	
	private Set<NodeInfoI> noeudEnregistres;
	protected RegisterInboundPort	inboundPortC ;
	protected RegisterInboundPort	inboundPortN ;
	

	protected Register(String uriC, String uriN) throws Exception {
		super(1, 0);
		noeudEnregistres = new HashSet<>();
		this.inboundPortC = new RegisterInboundPort(uriC, this) ;
		this.inboundPortN = new RegisterInboundPort(uriN, this) ;
		this.inboundPortC.publishPort();
		this.inboundPortN.publishPort();
	}
	

	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		//On ajoute nouveau noeud et lui envoyer ses voisins
		noeudEnregistres.add(nodeInfo);
		Set<NodeInfoI> voisins = new HashSet<>();
		NodeInfoI noeudNE = findNewNeighbour(nodeInfo, Direction.NE);
		NodeInfoI noeudNW = findNewNeighbour(nodeInfo, Direction.NW);
		NodeInfoI noeudSE = findNewNeighbour(nodeInfo, Direction.SE);
		NodeInfoI noeudSW = findNewNeighbour(nodeInfo, Direction.SW);
		voisins.add(noeudNE);
		voisins.add(noeudNW);
		voisins.add(noeudSE);
		voisins.add(noeudSW);
		return voisins;
		
		
	}


	public boolean registered(String nodeIdentifier) throws Exception {
		
		for (NodeInfoI n : noeudEnregistres) {
			if (n.nodeIdentifier() == nodeIdentifier){
				return true;
			}
		}
		return false;
	}


	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		PositionI p1 = nodeInfo.nodePosition();
		double rangeNode = nodeInfo.nodeRange();
		
		double minDist = 99999;
		NodeInfoI minNode = null;
		
		for (NodeInfoI n : noeudEnregistres) {
			PositionI p2 = nodeInfo.nodePosition();
			if (d == p1.directionFrom(p2)) {
				double tmpDist = p1.distance(p2);
				if(tmpDist < rangeNode && tmpDist < minDist) {
					minDist = tmpDist;
					minNode = n;
				}
			}
		}
		return minNode;	
	}


	public void unregister(String nodeIdentifier) throws Exception {
		for (NodeInfoI n : noeudEnregistres) {
			if (n.nodeIdentifier() == nodeIdentifier){
				noeudEnregistres.remove(n);
			}
		}		
	}


	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		for (NodeInfoI n : noeudEnregistres) {
			if (n.nodeIdentifier() == sensorNodeId){
				return new ConnectionInfo(n.nodeIdentifier(), (BCM4JavaEndPointDescriptorI)n.endPointInfo());
			}
		}
		return null;
	}
	
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception{
		Set<ConnectionInfoI> inZone = new HashSet<>();
		for (NodeInfoI n : noeudEnregistres) {
			PositionI p = n.nodePosition();
			if (z.in(p)){
				ConnectionInfoI c = new ConnectionInfo(n.nodeIdentifier(), (BCM4JavaEndPointDescriptorI)n.endPointInfo());
				inZone.add(c);
			}
		}
		return inZone;
	}

}
