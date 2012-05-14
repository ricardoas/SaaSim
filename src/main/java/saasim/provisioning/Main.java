package saasim.provisioning;

import java.io.IOException;

import saasim.config.Configuration;


/**
 * Provisioning simulator execution entry point.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * Entry point.
	 * 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if(args.length != 0){
			System.out.println("Usage: java <-Dsaasim.properties=path_to_file> -cp saasim.jar saasim.provisioning.Main");
			System.exit(1);
		}
		
		Configuration.getInstance().getSimulator().start();
	}
}
