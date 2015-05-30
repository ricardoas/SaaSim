package saasim.ext.iaas;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import saasim.core.iaas.BillingInfo;
import saasim.core.iaas.Customer;

@Singleton
public class LoggerCustomer implements Customer {
	
	private Logger logger = Logger.getLogger(Customer.class);

	@Override
	public void reportIaaSUsage(BillingInfo bill) {
		logger.info(bill);
	}

}
