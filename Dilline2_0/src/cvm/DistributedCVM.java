package cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.examples.basic_cs.components.URIConsumer;
import fr.sorbonne_u.components.examples.basic_cs.components.URIProvider;

public class DistributedCVM extends	AbstractDistributedCVM{
	
	public DistributedCVM(String[] args) throws Exception{
		super(args);
	}
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {

			// create the provider component
			this.uriProviderURI =
					AbstractComponent.createComponent(
							URIProvider.class.getCanonicalName(),
							new Object[]{PROVIDER_COMPONENT_URI,
										 URIProviderInboundPortURI});
			assert	this.isDeployedComponent(this.uriProviderURI);
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.uriProviderURI);
			this.toggleLogging(this.uriProviderURI);
			assert	this.uriConsumerURI == null && this.uriProviderURI != null;

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {

			// create the consumer component
			this.uriConsumerURI =
					AbstractComponent.createComponent(
							URIConsumer.class.getCanonicalName(),
							new Object[]{CONSUMER_COMPONENT_URI,
										 URIGetterOutboundPortURI});
			assert	this.isDeployedComponent(this.uriConsumerURI);
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.uriConsumerURI);
			this.toggleLogging(this.uriConsumerURI);
			assert	this.uriConsumerURI != null && this.uriProviderURI == null;

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.instantiateAndPublish();
	}
	
}
