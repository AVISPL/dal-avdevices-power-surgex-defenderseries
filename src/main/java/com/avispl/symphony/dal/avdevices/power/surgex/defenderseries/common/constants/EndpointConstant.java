package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants;

/**
 * Utility class that defines API endpoint paths and URI patterns for REST controllers.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class EndpointConstant {
	private EndpointConstant() {
		// Prevent instantiation
	}

	//	API parameters
	public static final String DEVICE_ID_AND_COMPONENT_ID = "{deviceIdAndComponentId}";

	//	URIs
	private static final String API_PREFIX = "api/v1";
	public static final String CURRENT_STATUS = API_PREFIX + "/currentStatus";
	public static final String NETWORK_SETTINGS = API_PREFIX + "/NetworkSettings";
	public static final String POWER_ON = API_PREFIX + String.format("/%s/PowerOn", DEVICE_ID_AND_COMPONENT_ID);
	public static final String POWER_OFF = API_PREFIX + String.format("/%s/PowerOff", DEVICE_ID_AND_COMPONENT_ID);
	public static final String REBOOT = API_PREFIX + String.format("/%s/Reboot", DEVICE_ID_AND_COMPONENT_ID);
}
