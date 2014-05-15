package saasim.core.iaas;

import saasim.core.infrastructure.InstanceDescriptor;

public interface BillingInfo {

	void reset();

	void account(InstanceDescriptor descriptor, long now);
}
