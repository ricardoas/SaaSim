package saasim.ext.cloud;

import org.apache.log4j.Logger;

import saasim.core.iaas.BillingInfo;
import saasim.core.iaas.Customer;

public class LoggerIaaSCustomer implements Customer {

	@Override
	public void reportIaaSUsage(BillingInfo bill) {
		Logger.getLogger(Customer.class).info(bill);
	}

}
