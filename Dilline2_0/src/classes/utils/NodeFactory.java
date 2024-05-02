package classes.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import classes.NodeInfo;
import classes.Position;
import classes.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class NodeFactory {
	
    private static final List<String> SENSOR_TYPES = Arrays.asList(
            "fumée", "température", "vitesse_vent", "humidité",  "lumière", "son"
        );
    
    
    public static List<NodeInfoI> createNodes(int totalNodes) {
        List<NodeInfoI> nodes = new ArrayList<>();
        int startX = 1;   // Coordonnée X de départ
        int startY = 1;   // Coordonnée Y de départ
        int stepX = 5;    // Espacement entre les nœuds en X
        int stepY = 5;    // Espacement entre les nœuds en Y
        double range = 30.0; 
        int cols = (int) Math.sqrt(totalNodes);
        int rows = (int) Math.ceil((double) totalNodes / cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (nodes.size() >= totalNodes) break;
                int x = startX + col * stepX;
                int y = startY + row * stepY;
                String nodeId = "node" + (nodes.size() + 1);
                NodeInfoI nodeInfo = new NodeInfo(nodeId, new Position(x, y), range);
                nodes.add(nodeInfo);
            }
        }
        return nodes;
    }

    public static List<SensorDataI> createSensorsForNode(String nodeId) {
        List<SensorDataI> sensors = new ArrayList<>();
        Random random = new Random();

        // Each node gets a random number of sensors between 1 and 3
        int numSensors = random.nextInt(3) + 1;

        for (int i = 0; i < numSensors; i++) {
            String sensorType = SENSOR_TYPES.get(random.nextInt(SENSOR_TYPES.size()));
            Serializable value;
            switch (sensorType) {
                case "température":
                    value = 20.0 + random.nextDouble() * 15.0; // Température entre 20 et 35
                    break;
                case "fumée":
                    value = random.nextBoolean();
                    break;
                default:
                    value = random.nextDouble() * 100.0; // Valeur générique pour autres capteurs
            }
            sensors.add(new SensorData(nodeId, sensorType, value));
        }
        return sensors;
    }

}
