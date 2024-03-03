package composants.register;

import java.util.HashSet;
import java.util.Set;

import classes.ConnectionInfo;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
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
	
	protected Set<NodeInfoI> noeudEnregistres;
	protected RegisterLookupInboundPort	inboundPortLookup ;
	protected RegisterRegistrationInboundPort	inboundPortRegistration ;
	

	protected Register(String ibPortLookup, String ibPortRegistration) throws Exception {
		super(1, 0);
		noeudEnregistres = new HashSet<>();
		this.inboundPortLookup = new RegisterLookupInboundPort(ibPortLookup, this) ;
		this.inboundPortRegistration = new RegisterRegistrationInboundPort(ibPortRegistration, this) ;
		this.inboundPortLookup.publishPort();
		this.inboundPortRegistration.publishPort();
	}
	

	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		//On ajoute nouveau noeud et lui envoyer ses voisins
		this.logMessage("Register: registering "+nodeInfo.nodeIdentifier() + " ...");
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
		
		double minDist = Double.MAX_VALUE;
		double tmpDist;
		NodeInfoI minNode = null;
		
		for (NodeInfoI n : noeudEnregistres) {
			PositionI p2 = n.nodePosition();
			if(! nodeInfo.nodeIdentifier().equals(n.nodeIdentifier())) {
				if (d == p1.directionFrom(p2)) {
					tmpDist = p1.distance(p2);
					if(tmpDist < rangeNode && tmpDist < minDist) {
						minDist = tmpDist;
						minNode = n;
					}
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
	
	@Override
    public void start() throws ComponentStartException
    {
        this.logMessage("starting register component.");
        
        super.start();
    }
	
	@Override
	public void			execute() throws Exception
	{
		
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		this.logMessage("stopping register component.");
        this.printExecutionLogOnFile("register");
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.inboundPortLookup.unpublishPort();
			this.inboundPortRegistration.unpublishPort();
		}catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
	}
}
