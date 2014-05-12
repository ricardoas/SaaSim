package saasim.ext.cloud;

import org.apache.log4j.Logger;

import saasim.core.cloud.BillingInfo;
import saasim.core.cloud.IaaSCustomer;

public class LoggerIaaSCustomer implements IaaSCustomer {

	@Override
	public void reportIaaSUsage(BillingInfo bill) {
		Logger.getLogger(IaaSCustomer.class).info(bill);
	}

}
