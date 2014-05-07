package saasim.core.io;


public interface TraceReaderFactory<T>{
	
	TraceReader<T> create(String fileName, int tenantID);

}
