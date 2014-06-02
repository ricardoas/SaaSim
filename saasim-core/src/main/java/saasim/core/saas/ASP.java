package saasim.core.saas;

public interface ASP {

	void setUp();

	void finished(Request request);

	void failed(Request request);
	

}
