package saasim.core.cloud;

import saasim.core.infrastructure.InstanceDescriptor;

public interface IaaSBillingInfo {

	void reset();

	void account(InstanceDescriptor descriptor, long now);
}
