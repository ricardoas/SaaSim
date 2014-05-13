package saasim.core.provisioning;

public interface ApplicationConfiguration {
	
	public static final String TIER_ID = "default.tier.id";
	public static final String INSTANCE_DESCRIPTOR = "default.tier.descriptor";
	public static final String FORCE = "default.tier.force";
	public static final String ACTION =  "default.action";
	
	public static final String ACTION_INCREASE =  "default.action.increase";
	public static final String ACTION_DECREASE =  "default.action.decrease";
	public static final String ACTION_RECONFIGURE =  "default.action.reconfigure";
	public static final String ACTION_ADMISSION_CONTROL = "default.action.admissioncontrol";
}