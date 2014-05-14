package saasim.core.saas;

public interface Tenant {
	
	public static String SAAS_TENANT_NUMBER = "saas.tenant.number";

	public static String SAAS_TENANT_MODE = "saas.tenant.mode";

	public static String SAAS_TENANT_MODE_ARCHITECTURE = "architecture";
	public static String SAAS_TENANT_MODE_VIRTUALIZED = "virtualized";
	
	
	void start();

}
