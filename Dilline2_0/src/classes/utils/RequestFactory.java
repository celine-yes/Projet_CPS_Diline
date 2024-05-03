package classes.utils;

import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import langage.interfaces.*;
import langage.ast.*;

import java.util.List;

import classes.Request;

public class RequestFactory {
	
	//compteur pour les URIs des requetes
	public static int compteur = 0;
	
	private static IDirs createDirsFromList(List<String> directionStrings) {
        IDirs dirs = null;
        for (String dir : directionStrings) {
            Direction direction = Direction.valueOf(dir); 
            if (dirs == null) {
                dirs = new FDirs(direction);
            } else {
                dirs = new RDirs(direction, dirs);
            }
        }
        return dirs;
    }

	/** -------------------------------------------------------------------------------------------------------------------------------------- **/
    
	// Requête Booléenne avec Continuation Vide
    public static RequestI createBooleanRequestWithECont(String sensorId, double value) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(value));
        ICont econt = new ECont();
        QueryI query = new BQuery(econt, bexp);
        return new Request("request" + (compteur + 1), query);
    }
    
    // Requête de Collecte avec Continuation Vide
    public static RequestI createGatherRequestWithECont(RGather rgather) {
        ICont econt = new ECont();
        QueryI query = new GQuery(econt, rgather);
        return new Request("request" + (compteur + 1), query);
    }
    
	/** -------------------------------------------------------------------------------------------------------------------------------------- **/

    // Requête Booléenne avec Continuation d'inondation
    private static RequestI createFloodingBooleanRequest(String sensorId, double value, IBase base, double dist) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(value));
        IFCont fcont = new FCont(base, dist);
        QueryI query = new BQuery(fcont, bexp);
        return new Request("request" + (compteur + 1), query);
    }
    
    public static RequestI createBooleanFloodingRequestWithABase(String sensorId, double value, PositionI position, double dist) {
        IBase base = new ABase(position);
        return createFloodingBooleanRequest(sensorId, value, base, dist);
    }
    
    public static RequestI createBooleanFloodingRequestWithRBase(String sensorId, double value, double dist) {
        IBase base = new RBase();
        return createFloodingBooleanRequest(sensorId, value, base, dist);
    }
    
    // Requête de Collecte avec Continuation d'inondation
    private static RequestI createFloodingGatherRequest(RGather rgather, IBase base, double dist) {
        IFCont fcont = new FCont(base, dist);
        QueryI query = new GQuery(fcont, rgather);
        return new Request("request" + (compteur + 1), query);
    }
    
    public static RequestI createGatherFloodingRequestWithABase(RGather rgather, PositionI position, double dist) {
        IBase base = new ABase(position);
        return createFloodingGatherRequest(rgather, base, dist);
    }
    
    public static RequestI createGatherFloodingRequestWithRBase(RGather rgather, double dist) {
        IBase base = new RBase();
        return createFloodingGatherRequest(rgather, base, dist);
    }
    
    
	/** -------------------------------------------------------------------------------------------------------------------------------------- **/


    // Requête Booléenne avec Continuation Directionnelle
    public static RequestI createBooleanRequestWithDCont(String sensorId, double threshold, List<String> directions, int maxHops) {
        IBexp bexp = new GeqCexp(new SRand(sensorId), new CRand(threshold));
        IDirs dirs = createDirsFromList(directions);
        IDCont dcont = new DCont(dirs, maxHops);
        QueryI query = new BQuery(dcont, bexp);
        return new Request("request" + (compteur + 1), query);
    }

    // Requête de Collecte avec Continuation Directionnelle
    public static RequestI createGatherRequestWithDCont(RGather rgather, List<String> directions, int maxHops) {
        IDirs dirs = createDirsFromList(directions);
        IDCont dcont = new DCont(dirs, maxHops);
        QueryI query = new GQuery(dcont, rgather);
        return new Request("request" + (compteur + 1), query);
    }
}