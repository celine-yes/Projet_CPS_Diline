package classes.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import classes.NodeInfo;
import classes.Position;
import classes.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class NodeFactory extends JPanel {
	
    private static final long serialVersionUID = 1L;

	private static final List<String> SENSOR_TYPES = Arrays.asList(
            "fumée", "température", "vitesse_vent", "humidité",  "lumière", "son"
        );
    
    private List<NodeInfoI> nodes;
    
    
    public NodeFactory(List<NodeInfoI> nodes) {
		super();
		this.nodes = nodes;
		setPreferredSize(new Dimension(800, 700));
	}

    public static List<NodeInfoI> createNodes(int totalNodes) {
    	
        List<NodeInfoI> nodes = new ArrayList<>();
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
                int y = row * verticalSpacing;
                nodes.add(new NodeInfo("n" + (nodeCount + 1), new Position(x, y), 30.0));
                nodeCount++;
                if (nodeCount >= totalNodes) break;
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
  
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int scale = 15;
        for (NodeInfoI node : nodes) {
            int x = (int) (((Position) node.nodePosition()).getX() * scale);
            int y = (int) (((Position) node.nodePosition()).getY() * scale);

            int textX = x; 
            int textY = y + 20; // Adjust text position as needed

            g.drawString(node.nodeIdentifier(), textX, textY);
        }
    }
    
    public static void displayNodes(List<NodeInfoI> nodes) {
        JFrame frame = new JFrame("Node Network Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NodeFactory panel = new NodeFactory(nodes);
        frame.getContentPane().add(new JScrollPane(panel)); // Use JScrollPane
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        List<NodeInfoI> nodes = createNodes(50); 
        displayNodes(nodes);  
    }

}
