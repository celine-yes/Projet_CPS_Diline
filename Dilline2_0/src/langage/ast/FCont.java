package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IBase;
import langage.interfaces.IFCont;

public class FCont implements IFCont{
	private IBase base;
	private double maxDist;
	
	public FCont(IBase base, double maxDist) {
		super();
		this.base = base;
		this.maxDist = maxDist;
	}
	
	public IBase getBase() {
		return base;
	}

	public double getMaxDist() {
		return maxDist;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		//comment on fait la recursion pour trouver les voisins des voisins, 
		// si on passe ProcessingNode en fonction lambda?
		//comment utiliser Register
		
//		List<String> capteurs = new ArrayList<String>();
//		
//		BiFunction < List<String>, IBase, List<String> > g = (listCapteurs, base) -> {		
//			PositionI positionBase = base.eval(data);
//			
//			ProcessingNodeI node = data.getProcessingNode();
//			Set<NodeInfoI> voisins = node.getNeighbours();
//			for (NodeInfoI voisin : voisins) {
//				PositionI positionVoisin = voisin.nodePosition();
//				if (data.withinMaximalDistance(positionVoisin)) {
//					listCapteurs.add(0, voisin.nodeIdentifier());
//				}
//			}
//			
//			//updateProcessingNode(ProcessingNodeI pn);
//			return listCapteurs;
//	    };
//		return capteurs;
		return null;
		
	}

	


	
}
