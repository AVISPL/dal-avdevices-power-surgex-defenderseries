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

	//	Formats
	public static final String DATETIME_12H_FORMAT = "MM/dd/yyyy, hh:mm:ss a";
	public static final String GROUP_FORMAT = "%s_%02d";
	public static final String PROPERTY_FORMAT = "%s#%s";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";

	//	Special characters
	public static final String COMMA = ",";
	public static final String SLASH = "/";
	public static final String HASH_SYMBOL = "#";
	public static final String UNDERSCORE = "_";

	//	Groups
	public static final String GENERAL_GROUP = "General";
	public static final String ADAPTER_METADATA_GROUP = "AdapterMetadata";
	public static final String NETWORK_GROUP = "Network";
	public static final String OUTLET_GROUP = "Outlet";
	public static final String GROUP_OUTLET_GROUP = "OutletGroup";

	//	Values
	public static final String NONE = "None";
	public static final String NOT_AVAILABLE = "N/A";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String REBOOT = "Reboot";
	public static final String REBOOTING = "Rebooting";
	public static final String POWER = "Power";
	public static final String OK = "OK";
	public static final String ALERT = "ALERT";

	//	Info messages
	public static final String INITIALIZED_SUCCESSFULLY_INFO = "SurgeXDefenderCommunicator initialized successfully";
	public static final String OUTLET_REBOOTING_INFO = "The Outlet with ID %s is rebooting, skipping controller creation";
	public static final String OUTLET_GROUP_REBOOTING_INFO = "The Outlet group with ID %s is rebooting, skipping controller creation";
	public static final String DESTROY_INTERNAL_INFO = "Destroying internal state of instance: ";

	//	Warning messages
	public static final String UNDEFINED_HISTORICAL_PROP_WARNING = "Undefined property ignored: ";
	public static final String CONTROLLABLE_PROPS_EMPTY_WARNING = "ControllableProperties list is null or empty, skipping control operation";
	public static final String FETCHED_DATA_NULL_WARNING = "Fetched data is null. Endpoint: %s, ResponseClass: %s";
	public static final String INITIAL_STATE_UNDEFINED_WARNING = "Undefined initial state for outlet with value: ";
	public static final String OUTLETS_EMPTY_WARNING = "The outlets is empty, returning empty collection.";
	public static final String OUTLET_GROUPS_EMPTY_WARNING = "The outlet groups is empty, returning empty collection.";
	public static final String CURRENT_STATUS_NULL_WARNING = "The current status object is null, returning empty map.";
	public static final String NETWORK_STATUS_NULL_WARNING = "The network status object is null, returning empty map.";
	public static final String HISTORICAL_PROPS_EMPTY_WARNING = "The historical properties is empty, returning empty map.";
	public static final String STATISTICS_EMPTY_WARNING = "The statistics is empty, returning empty map.";

	//	Fail messages
	public static final String READ_PROPERTIES_FILE_FAILED = "Failed to load properties file: ";
	public static final String FETCH_DATA_FAILED = "Exception while fetching data. Endpoint: %s, ResponseClass: %s";
	public static final String DEFINE_OUTLET_INDEX_FAILED = "Invalid component index for Outlets: ";
	public static final String DEFINE_OUTLET_GROUP_INDEX_FAILED = "Invalid component index for Outlet groups: ";
	public static final String CONTROL_PROPERTY_FAILED = "Failed to control property: ";
	public static final String LOGIN_FAILED = "Failed to login, please check the username and password";
	public static final String SET_UP_DATA_FAILED = "Failed to set up data for ";
	public static final String RESPONSE_CONTROL_FAILED = "Response to control operation failed (API returned false). URI: %s";
	public static final String ACTION_PERFORM_FAILED = "Failed to perform data for action: ";
	public static final String MAP_12_DATE_TIME_FAILED = "Failed to mapTo12hDatetime with timestamp: %s timezone: %s";
	public static final String MAP_ELAPSED_TIME_FAILED = "Failed to mapToElapsedTime with uptime: ";
}
