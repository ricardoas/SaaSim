package saasim.ext.iaas;

import org.apache.log4j.Logger;

import saasim.core.iaas.BillingInfo;
import saasim.core.iaas.Customer;

public class LoggerCustomer implements Customer {

	@Override
	public void reportIaaSUsage(BillingInfo bill) {
		Logger.getLogger(Customer.class).info(bill);
	}

}
