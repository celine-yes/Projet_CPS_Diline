package tests.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import classes.Request;

import classes.utils.RequestFactory;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

public class RequestTest {
	// Attributs statiques finals pour les paramètres des requêtes
    public static final String SENSOR_ID = "temperature";
    public static final double THRESHOLD_VALUE = 25.0;
    public static final double DISTANCE = 100;
    public static final List<String> DIRECTIONS = Arrays.asList("NE", "SE", "SW", "NW");
    public static final List<String> SENSOR_IDS = Arrays.asList("temperature", "fumee");
    public static final int MAX_HOPS = 50;

    // Crée une liste de requêtes booléennes directionnelles
    public static List<RequestI> createBooleanDirectionalRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
        	RequestI request = RequestFactory.createBooleanRequestWithDCont(SENSOR_ID, THRESHOLD_VALUE, DIRECTIONS, MAX_HOPS, jvmURI);
        	if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }
    // Crée une liste de requêtes booléennes de flooding
    public static List<RequestI> createBooleanFloodingRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            RequestI request = RequestFactory.createBooleanFloodingRequestWithRBase(SENSOR_ID, THRESHOLD_VALUE, DISTANCE, jvmURI);
            if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }

    // Crée une liste de requêtes booléennes avec continuation vide
    public static List<RequestI> createBooleanEmptyRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            RequestI request = RequestFactory.createBooleanRequestWithECont(SENSOR_ID, THRESHOLD_VALUE, jvmURI);
            if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }

    // Crée une liste de requêtes gather directionnelles
    public static List<RequestI> createGatherDirectionalRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            RequestI request = RequestFactory.createGatherRequestWithDCont(SENSOR_IDS, DIRECTIONS, MAX_HOPS, jvmURI);
            if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }

    // Crée une liste de requêtes gather de flooding
    public static List<RequestI> createGatherFloodingRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            RequestI request = RequestFactory.createGatherFloodingRequestWithRBase(SENSOR_IDS, DISTANCE, jvmURI);
            if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }

    // Crée une liste de requêtes gather avec continuation vide
    public static List<RequestI> createGatherEmptyRequests(int length, boolean async, String jvmURI) {
        List<RequestI> requests = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            RequestI request = RequestFactory.createGatherRequestWithECont(SENSOR_IDS, jvmURI);
            if (async) ((Request) request).setAsynchronous();
            requests.add(request);
        }
        return requests;
    }

}