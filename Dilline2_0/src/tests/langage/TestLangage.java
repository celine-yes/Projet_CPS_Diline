package tests.langage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import langage.ast.AndBexp;
import langage.ast.CRand;
import langage.ast.EqCexp;
import langage.ast.GCexp;
import langage.ast.GeqCexp;
import langage.ast.LCexp;
import langage.ast.LeqCexp;
import langage.ast.NotBexp;
import langage.ast.OrBexp;
import langage.ast.SRand;

public class TestLangage {
	private static SRand sRand;
	private static CRand cRand;
	private static GCexp gcexp;
	private static GeqCexp geqcexp;
	private static LCexp lcexp;
	private static LeqCexp leqcexp;
	private static EqCexp eqcexp;
	
	private static AndBexp andbexp1;
	private static AndBexp andbexp2;
	
	private static OrBexp orbexp1;
	private static OrBexp orbexp2;
	
	private static NotBexp notbexp;
	
	private static ProcessingNodeI prcNode;
	private static ExecutionStateI exState;
	
	private static String nodeId1 = "node1";
	private static PositionI positionNode1= new Position(10,10);
	private static double range = 35.0;
	private static NodeInfoI nodeInfo1;
	
	private static String sensorId1 = "temperature";
	private static double sensorValue1 = 51.0;
	private static double cSensorValue1 = 50.0;
	private static SensorDataI sensorNode1;
	private static ArrayList<SensorDataI> sensorsNode1;
	
	public final static String	NODE1_P2P_INBOUND_PORT_URI =
			"node1P2PibpURI" ;
	public final static String	NODE1_REQUESTING_INBOUND_PORT_URI =
			"node1RrequestingibpURI" ;
	
	
	@Before
    public void setUp() {
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
        cRand = new CRand(cSensorValue1);
        
        gcexp = new GCexp(sRand, cRand);
        geqcexp = new GeqCexp(sRand, cRand);     
        lcexp = new LCexp(sRand, cRand); 
        leqcexp =  new LeqCexp(sRand, cRand); 
        eqcexp = new EqCexp(sRand, cRand); 
        
        andbexp1 = new AndBexp(gcexp, geqcexp);
        andbexp2 = new AndBexp(gcexp, leqcexp);
        orbexp1 = new OrBexp(leqcexp, geqcexp);
        orbexp2 = new OrBexp(leqcexp, eqcexp);
        notbexp = new NotBexp(gcexp);

    }
	
	@Test
    public void testSrand() {
        System.out.println();
    	assertEquals(sensorValue1, sRand.eval(exState));
    }
	
	@Test
    public void testCrand() {
    	assertEquals(cSensorValue1, cRand.eval(exState));
    }
	
	@Test
    public void testGCexp() {
    	assertEquals(true, gcexp.eval(exState));
    }
	
	@Test
    public void testLCexp() {
    	assertEquals(false, lcexp.eval(exState));
    }
	
	@Test
    public void testLeqCexp() {
    	assertEquals(false, leqcexp.eval(exState));
    }
	
	@Test
    public void testEqCexp() {
    	assertEquals(false, eqcexp.eval(exState));
    }

	
	@Test
    public void testAndBexp() {
    	assertEquals(true, andbexp1.eval(exState));
    	assertEquals(false, andbexp2.eval(exState));
    }
	
	@Test
    public void testOrBexp() {
    	assertEquals(true, orbexp1.eval(exState));
    	assertEquals(false, orbexp2.eval(exState));
    }
	
	@Test
    public void testNotBexp() {
    	assertEquals(false, notbexp.eval(exState));
    }
	
}
