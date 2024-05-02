package cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.examples.basic_cs.components.URIConsumer;
import fr.sorbonne_u.components.examples.basic_cs.components.URIProvider;
import withplugin.composants.Client;

public class DistributedCVM extends	AbstractDistributedCVM{
	
	//les uris des JVM
	protected static final String JVM1_URI ="jvm1";
	protected static final String JVM2_URI ="jvm2";
	protected static final String JVM3_URI ="jvm3";
	protected static final String JVM4_URI ="jvm4";
	protected static final String JVM5_URI ="jvm5";
	
	
	
	public DistributedCVM(String[] args) throws Exception{
		super(args);
	}
	
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {

	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone,requetes});


		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {

	        /** création du composant client           **/
			AbstractComponent.createComponent(
					Client.class.getCanonicalName(), new Object [] {zone,requetes});

		} else {

			System.out.println("Unknown JVM URI... " + AbstractCVM.getThisJVMURI());

		}

		super.instantiateAndPublish();
	}
	
	
//	@Override
//	public void interconnect() throws Exception {
//		
//		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {
//
//	        /** création du composant client           **/
//			AbstractComponent.createComponent(
//					Client.class.getCanonicalName(), new Object [] {zone,requetes});
//
//
//		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {
//
//	        /** création du composant client           **/
//			AbstractComponent.createComponent(
//					Client.class.getCanonicalName(), new Object [] {zone,requetes});
//
//		} else {
//
//			System.out.println("Unknown JVM URI... " + AbstractCVM.getThisJVMURI());
//
//		}
//		super.interconnect();
//	}


	public static void main(String[] args) {
		
		DistributedCVM dcvm;
		try {
			dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(2500L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
