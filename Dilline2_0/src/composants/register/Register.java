package composants.register;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.*;

import classes.ConnectionInfo;
import cvm.CVM;
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
	
	protected ReadWriteLock rwLock;
	
	protected Set<NodeInfoI> noeudEnregistres;
	protected LookupInboundPort	inboundPortLookup ;
	protected RegistrationInboundPort	inboundPortRegistration ;
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				REGISTER_POOL_URI =
													"register pool URI";
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				FBG_POOL_URI =
													"fbg pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				REGISTER_POOL_SIZE = 5;
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				FBG_POOL_SIZE = 2;
	

	protected Register(String ibPortLookup, String ibPortRegistration) throws Exception {
		super(1, 0);
		noeudEnregistres = new HashSet<>();
		this.inboundPortLookup = new LookupInboundPort(ibPortLookup, this, FBG_POOL_URI) ;
		this.inboundPortRegistration = new RegistrationInboundPort(ibPortRegistration, this, REGISTER_POOL_URI ) ;
		this.inboundPortLookup.publishPort();
		this.inboundPortRegistration.publishPort();
		
		this.rwLock = new ReentrantReadWriteLock();
		
		this.initialise();
	}
	

	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		//On ajoute nouveau noeud et lui envoyer ses voisins
		this.logMessage("Register: registering "+nodeInfo.nodeIdentifier() + " ...");
		
		//section critique
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			noeudEnregistres.add(nodeInfo);
		} finally {
			writeLock.unlock();
		}
		
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
		Lock readLock = rwLock.readLock();
		readLock.lock();
		 
		try {
			for (NodeInfoI n : noeudEnregistres) {
				if (n.nodeIdentifier() == nodeIdentifier){
					return true;
				}
			}
		} finally {
		    readLock.unlock();
		}
		
		return false;
	}


	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		PositionI p1 = nodeInfo.nodePosition();
		double rangeNode = nodeInfo.nodeRange();
		
		double minDist = Double.MAX_VALUE;
		double tmpDist;
		NodeInfoI minNode = null;
		
		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
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
			
		} finally {
		    readLock.unlock();
		}
		return minNode;	
	}


	public void unregister(String nodeIdentifier) throws Exception {

		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			for (NodeInfoI n : noeudEnregistres) {
				if (n.nodeIdentifier() == nodeIdentifier){
					noeudEnregistres.remove(n);
				}
			}
			
		} finally {
		    readLock.unlock();
		}
	}


	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		
		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			for (NodeInfoI n : noeudEnregistres) {
				if (n.nodeIdentifier() == sensorNodeId){
					return new ConnectionInfo(n.nodeIdentifier(), (BCM4JavaEndPointDescriptorI)n.endPointInfo());
				}
			}
			
		} finally {
		    readLock.unlock();
		}
		return null;
	}
	
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception{
		Set<ConnectionInfoI> inZone = new HashSet<>();
		
		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			for (NodeInfoI n : noeudEnregistres) {
				PositionI p = n.nodePosition();
				if (z.in(p)){
					ConnectionInfoI c = new ConnectionInfo(n.nodeIdentifier(), (BCM4JavaEndPointDescriptorI)n.endPointInfo());
					inZone.add(c);
				}
			}
			
		} finally {
		    readLock.unlock();
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
	
	protected void		initialise() throws Exception
	{	
		this.getTracer().setTitle("Register ") ;
		this.getTracer().setRelativePosition(CVM.number/5 + 1, CVM.number % 5) ;
		CVM.number++;
		this.toggleTracing() ;
		this.toggleLogging();
		
		this.createNewExecutorService(REGISTER_POOL_URI,
									  REGISTER_POOL_SIZE,
									  false);
		this.createNewExecutorService(FBG_POOL_URI,
				  				FBG_POOL_SIZE,
				  				false);

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
