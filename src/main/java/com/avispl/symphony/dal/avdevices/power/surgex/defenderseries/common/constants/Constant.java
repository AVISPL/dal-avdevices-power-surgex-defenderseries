package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants;


/**
 * Utility class that defines constant values used across the application.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Constant {
	private Constant() {
		// Prevent instantiation
	}

	public static final String DATETIME_12H_FORMAT = "MM/dd/yyyy, hh:mm:ss a";
	public static final String GROUP_FORMAT = "%s_%02d";
	public static final String PROPERTY_FORMAT = "%s#%s";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

	public static final String COMMA = ",";
	public static final String SLASH = "/";
	public static final String HASH_SYMBOL = "#";
	public static final String UNDERSCORE = "_";

	public static final String ADAPTER_METADATA_GROUP = "AdapterMetadata";
	public static final String NETWORK_GROUP = "Network";
	public static final String OUTLET_GROUP = "Outlet";
	public static final String GROUP_OUTLET_GROUP = "OutletGroup";

	public static final String HARDWARE_VERSION = "1.0.0";
	public static final String NONE = "None";
	public static final String NOT_AVAILABLE = "N/A";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String REBOOT = "Reboot";
	public static final String REBOOTING = "Rebooting";
	public static final String TOGGLE = "Toggle";
	public static final String OK = "OK";
	public static final String FAULT = "Fault";
	public static final String OUT_OF_RANGE = "Out of range";

	public static final String INITIAL_STATE_UNDEFINED_WARNING = "Undefined initial state for outlet with value: ";

	public static final String READ_PROPERTIES_FILE_FAILED = "Failed to load properties file";
	public static final String CONTROL_PROPERTY_FAILED = "Failed to control property: ";
	public static final String LOGIN_FAILED = "Failed to login, please check the username and password";
	public static final String SET_UP_DATA_FAILED = "Failed to set up data for ";
	public static final String RESPONSE_FAILED = "Failed to get response during request to URI: ";
	public static final String ACTION_PERFORM_FAILED = "Failed to perform data for action: ";
}
