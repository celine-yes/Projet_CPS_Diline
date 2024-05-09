package withplugin.composants;

import java.util.ArrayList;

import cvm.CVM;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import plugins.NodePlugin;


@OfferedInterfaces(offered = {SensorNodeP2PCI.class, RequestingCI.class})
@RequiredInterfaces(required = {RequestResultCI.class, RegistrationCI.class, ClocksServerCI.class})

public class Node extends AbstractComponent {
	
	protected static final String NODE_PLUGIN_URI = 
			"nodePluginURI";
	protected NodePlugin plugin;
	
	protected Node(NodeInfoI nodeinfo, ArrayList<SensorDataI> sensors ) throws Exception {
		super(1,1);
		plugin = new NodePlugin(nodeinfo, sensors);
		plugin.setPluginURI(NODE_PLUGIN_URI);
		this.initialise();	
	}


	
	protected void		initialise() throws Exception
	{	
		this.getTracer().setTitle("Node " + plugin.getNodeInfo().nodeIdentifier()) ;
		this.getTracer().setRelativePosition(CVM.number/5 + 1, CVM.number % 5) ;
		CVM.number++;
		this.toggleTracing() ;
		this.toggleLogging();

	}
	
	@Override
	public void	execute() throws Exception
	{
		super.execute() ;
		this.logMessage("dans execute de node");
		
		//Install the plug-in 
		this.installPlugin(plugin);
		
		assert this.isInstalled(NODE_PLUGIN_URI);
		assert this.getPlugin(NODE_PLUGIN_URI) == this.plugin;
		
		this.plugin.executePlugin();
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		
		
		this.logMessage("stopping node component.");
        this.printExecutionLogOnFile("node");
		super.finalise();
	}
	

}
