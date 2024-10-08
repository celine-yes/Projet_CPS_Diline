package classes.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import classes.NodeInfo;
import classes.Position;
import classes.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

/**
 * {@code NodeFactory} extends {@link JPanel} and serves as a factory for creating
 * and managing a graphical network of sensor nodes. It visualizes the nodes and
 * their sensor states on a graphical interface, with colors indicating different
 * types of sensors.
 *
 * <p>Key functionalities include:
 * <ul>
 * <li>Creating nodes with random sensors and displaying them in a grid layout.</li>
 * <li>Graphical representation of nodes with different colors based on sensor types.</li>
 * <li>Static methods for node and sensor creation which can be used independently of the UI.</li>
 * </ul>
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class NodeFactory extends JPanel {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * List of sensor types available to be assigned to nodes.
     */
	private static final List<String> SENSOR_TYPES = Arrays.asList(
             "temperature"//, "fumee"
        );
	
	/**
     * A map holding the node information along with their respective sensor data.
     */
    private Map<NodeInfoI, ArrayList<SensorDataI>> nodeMap;    
    
    
    /**
     * Constructs a new {@code NodeFactory} panel with the specified map of nodes and their sensors.
     *
     * @param nodeMap the map of nodes with their associated sensor data
     */
    public NodeFactory(Map<NodeInfoI, ArrayList<SensorDataI>> nodeMap) {
		super();
        this.nodeMap = nodeMap;
        setPreferredSize(new Dimension(800, 700));
	}
    
    /**
     * Generates a specified number of nodes with random positions and sensors. 
     * Positions are distributed in a square grid layout, with the communication range set per node.
     *
     * @param totalNodes the total number of nodes to be created
     * @param range the communication range of each node
     * @return a map of {@code NodeInfoI} to {@code ArrayList<SensorDataI>} representing the nodes and their sensors
     */
    public static Map<NodeInfoI, ArrayList<SensorDataI>> createNodes(int totalNodes, int range) {
    	
        Map<NodeInfoI, ArrayList<SensorDataI>> nodeMap = new LinkedHashMap<>();
        int horizontalSpacing = 5;
        int verticalSpacing = 5;
        int nodesPerRow = (int) Math.ceil(Math.sqrt(totalNodes)); // Approximate a square for the grid size
        int middleRowIndex = nodesPerRow / 2;
        int nodeCount = 0;

        for (int row = 0; row <= nodesPerRow; row++) { 
            int nodesThisRow;
            if (row == nodesPerRow) {
                nodesThisRow = totalNodes - nodeCount; // Add all remaining nodes in this last row
                if (nodesThisRow == 0) break; 
            } else {
                nodesThisRow = Math.min(nodesPerRow - Math.abs(row - middleRowIndex), totalNodes - nodeCount);
            }
            int rowStartX = -(nodesThisRow - 1) * horizontalSpacing / 2 + 20;

            for (int i = 0; i < nodesThisRow; i++) {
                int x = rowStartX + i * horizontalSpacing;
                int y = row * verticalSpacing + 5;
                NodeInfo nodeInfo = new NodeInfo("n" + (nodeCount + 1), new Position(x, y), range);
                nodeMap.put(nodeInfo, createSensorsForNode(nodeInfo.nodeIdentifier())); 
                nodeCount++;
                if (nodeCount >= totalNodes) break;
            }
        }

        return nodeMap;
    }
    
    /**
     * Creates sensors for a specified node identified by its nodeId.
     * Each node gets a random number of sensors between 1 and 2, and the sensor type is randomly selected.
     *
     * @param nodeId the identifier of the node for which sensors are to be created
     * @return an {@code ArrayList} of {@code SensorDataI} representing the sensors for the node
     */
    public static ArrayList<SensorDataI> createSensorsForNode(String nodeId) {
    	
    	ArrayList<SensorDataI> sensors = new ArrayList<>();
        Random random = new Random();

        // Each node gets a random number of sensors between 1 and 2
        int numSensors = random.nextInt(2) + 1;

        for (int i = 0; i < numSensors; i++) {
            String sensorType = SENSOR_TYPES.get(random.nextInt(SENSOR_TYPES.size()));
            Serializable value;
            switch (sensorType) {
                case "temperature":
                    value = 30.0;
                    //20.0 + random.nextDouble() * 15.0; // Température entre 20 et 35
                    break;
                case "fumee":
                    value = random.nextBoolean();
                    break;
                default:
                    value = random.nextDouble() * 100.0; // Valeur générique pour autres capteurs
            }
            sensors.add(new SensorData(nodeId, sensorType, value));
        }
        return sensors;
    }
  
    
    /**
     * Customizes the graphical representation of each node, painting nodes with specific colors
     * depending on the sensors they contain.
     *
     * @param g the {@code Graphics} object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int scale = 15;
        int nodeRadius = 5;  // Taille du cercle pour chaque nœud

        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        int height = getHeight(); // la hauteur du panel pour inverser l'axe Y

        for (Map.Entry<NodeInfoI, ArrayList<SensorDataI>> entry : nodeMap.entrySet()) {
            NodeInfoI node = entry.getKey();
            Position pos = (Position) node.nodePosition();
            int x = (int) (pos.getX() * scale);
            int y = height - (int) (pos.getY() * scale);

            // Le reste du code reste inchangé
            boolean hasTemperatureSensor = false;
            boolean hasSmokeSensor = false;
            for (SensorDataI sensor : entry.getValue()) {
                if (sensor.getSensorIdentifier().equalsIgnoreCase("temperature")) {
                    hasTemperatureSensor = true;
                }
                if (sensor.getSensorIdentifier().equalsIgnoreCase("fumee")) {
                    hasSmokeSensor = true;
                }
            }

            if (hasTemperatureSensor && hasSmokeSensor) {
                g.setColor(Color.BLUE);
            } else if (hasTemperatureSensor) {
                g.setColor(Color.ORANGE);
            } else if (hasSmokeSensor) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }

            g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
            g.setColor(Color.BLACK);
            String nodeInfo = node.nodeIdentifier() + "(" + pos.getX() + ", " + pos.getY() + ")";
            g.drawString(nodeInfo, x - nodeRadius, y - nodeRadius + 2 * nodeRadius + 5);
        }
    }

    
    /**
     * Displays the nodes in a JFrame. Each node is visualized with a specific color based on its sensors.
     *
     * @param nodes the map of nodes with their associated sensor data to be displayed
     */
    public static void displayNodes(Map<NodeInfoI, ArrayList<SensorDataI>> nodes) {
        JFrame frame = new JFrame("Node Network Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NodeFactory panel = new NodeFactory(nodes);
        frame.getContentPane().add(new JScrollPane(panel));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    
    /**
     * The main method to test node creation and display functionality.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
    	Map<NodeInfoI, ArrayList<SensorDataI>> nodes = createNodes(10, 30); 
        displayNodes(nodes);  
    }

}
