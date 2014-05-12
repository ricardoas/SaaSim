package saasim.core.cloud;

import saasim.core.infrastructure.InstanceDescriptor;

public interface BillingInfo {

	void reset();

	void account(InstanceDescriptor descriptor);
}
