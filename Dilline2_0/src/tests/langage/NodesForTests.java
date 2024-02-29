package tests.langage;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import classes.BCM4JavaEndPointDescriptor;
import classes.ExecutionState;
import classes.NodeInfo;
import classes.Position;
import classes.ProcessingNode;
import classes.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import langage.ast.SRand;

/** Pour print les erreurs car Junit ne peut rien afficher **/

public class NodesForTests {
	
	private static SRand sRand;
	private static ProcessingNodeI prcNode;
	private static ExecutionStateI exState;
	
	private static String nodeId1 = "node1";
	private static PositionI positionNode1= new Position(10,10);
	private static double range = 35.0;
	private static NodeInfoI nodeInfo1;
	
	private static String sensorId1 = "temperature";
	private static double sensorValue1 = 51.0;
	private static SensorDataI sensorNode1;
	private static ArrayList<SensorDataI> sensorsNode1;
	
	public final static String	NODE1_P2P_INBOUND_PORT_URI =
			"node1P2PibpURI" ;
	public final static String	NODE1_REQUESTING_INBOUND_PORT_URI =
			"node1RrequestingibpURI" ;
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			sensorNode1 = new SensorData(nodeId1, sensorId1, sensorValue1);
			sensorsNode1= new ArrayList<SensorDataI>();
			sensorsNode1.add(sensorNode1);
			
			nodeInfo1 = new NodeInfo(
					nodeId1, 
					positionNode1,
					new BCM4JavaEndPointDescriptor(NODE1_REQUESTING_INBOUND_PORT_URI), 
					new BCM4JavaEndPointDescriptor(NODE1_P2P_INBOUND_PORT_URI),
					range);
			
			prcNode = new ProcessingNode(nodeInfo1, sensorsNode1);
			exState = new ExecutionState(prcNode);
			
	        sRand = new SRand(sensorId1);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        
        System.out.println(sRand.eval(exState));
	}
	

	
}
