package config;

import commons.cloud.Request;
import commons.config.WorkloadParser;

public class GEISTSimpleWorkloadParser implements WorkloadParser<Request> {

    @Override
    public Request next() {
	return null;
    }

}
