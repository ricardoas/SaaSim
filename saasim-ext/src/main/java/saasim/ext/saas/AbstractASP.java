package saasim.ext.saas;

import org.apache.log4j.Logger;

import saasim.core.saas.ASP;
import saasim.core.saas.Request;

public abstract class AbstractASP implements ASP{

	protected static Logger LOGGER = Logger.getLogger(ASP.class);

	@Override
	public void finished(Request request) {
		LOGGER.info(request.getArrivalTimeInMillis() + " " + request.getResponseTimeInMillis());
	}

	@Override
	public void failed(Request request) {
		LOGGER.info(request.getArrivalTimeInMillis() + " " + -1);
	}

}