package saasim.core.config;

import saasim.core.cloud.IaaSProvider;
import saasim.ext.cloud.AmazonEC2;

import com.google.inject.AbstractModule;

public class BillingModule extends AbstractModule {
	
	@Override 
	protected void configure() {
		bind(IaaSProvider.class).to(AmazonEC2.class);
	}
}