package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.EndpointConstant;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.CurrentStatus;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Device;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.DeviceMeasurements;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.EthInterface;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Group;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.NetworkSetting;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Outlet;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.InitialState;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.AdapterMetadataProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.GeneralProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.NetworkSettingProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.OutletGroupProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.OutletProperty;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Utility class for this adapter. This class includes helper methods to extract and convert properties.
 * <p>This class is non-instantiable and provides only static utility methods.</p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Util {
	private static final Log LOGGER = LogFactory.getLog(Util.class);

	private Util() {
		// Prevent instantiation
	}

	/**
	 * Maps a {@link GeneralProperty} from the given {@link CurrentStatus} object to a string representation.
	 *
	 * @param currentStatus the current status of the device
	 * @param property the general property to map
	 * @return the mapped value as a string, or {@code null} if inputs are invalid
	 */
	public static String mapToGeneralProperty(CurrentStatus currentStatus, GeneralProperty property) {
		if (currentStatus == null || property == null) {
			return null;
		}

		switch (property) {
			case FIRMWARE_VERSION:
				return mapToValue(currentStatus.getFirmware());
			case MODEL_NUMBER:
				return mapToValue(currentStatus.getModel());
			case MAC_ADDRESS:
				return mapToValue(currentStatus.getMac());
			case DEVICE_TIME:
				return mapTo12hDatetime(currentStatus.getTime(), currentStatus.getTimeZone());
			case INTERNAL_TEMPERATURE:
				return mapToValue(getMeasurements(currentStatus.getDevices()).getTemperature());
			case LINE_VOLTAGE:
				return mapToValue(getMeasurements(currentStatus.getDevices()).getLine1Line2());
			case CURRENT:
				return mapToValue(getMeasurements(currentStatus.getDevices()).getCurrent());
			case POWER:
				return mapToValue(getMeasurements(currentStatus.getDevices()).getPower());
			case SURGE_PROTECTION_STATUS:
				return mapToValue(getMeasurements(currentStatus.getDevices()).isSurgeGood(), Constant.OK, Constant.ALERT);
			case VOLTAGE_LEVEL_STATUS:
				return mapToValue(getMeasurements(currentStatus.getDevices()).isVoltageInLimit(), Constant.OK, Constant.ALERT);
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToGeneralProperty", property));
				return null;
		}
	}

	/**
	 * Maps the given {@link AdapterMetadataProperty} to its value using the provided properties.
	 *
	 * @param applicationProperties the source of property values
	 * @param property the property to map
	 * @return the formatted value, or {@code Constant.NONE} if not available
	 */
	public static String mapToAdapterMetadataProperty(Properties applicationProperties, AdapterMetadataProperty property) {
		String adapterBuildDate = applicationProperties.getProperty("adapter.build.date");
		String adapterUptime = applicationProperties.getProperty("adapter.uptime");
		String adapterVersion = applicationProperties.getProperty("adapter.version");

		switch (property) {
			case ADAPTER_BUILD_DATE:
				return mapToValue(adapterBuildDate);
			case ADAPTER_UPTIME:
				return mapToUptime(adapterUptime);
			case ADAPTER_VERSION:
				return mapToValue(adapterVersion);
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToAdapterMetadataProperty", property));
				return null;
		}
	}

	/**
	 * Maps a {@link NetworkSettingProperty} from the given {@link NetworkSetting} object to a string representation.
	 *
	 * @param networkSetting the network setting object
	 * @param property the network property to map
	 * @return the mapped value as a string, or {@code null} if inputs are invalid
	 */
	public static String mapToNetworkSettingProperty(NetworkSetting networkSetting, NetworkSettingProperty property) {
		if (networkSetting == null || property == null) {
			return null;
		}

		switch (property) {
			case IP_SETUP:
				return mapToValue(getFirstEthInterface(networkSetting.getEthInterfaces()).isDhcp(), "DHCP", "Static");
			case IP_ADDRESS:
				return mapToValue(getFirstEthInterface(networkSetting.getEthInterfaces()).getIPAddress());
			case SUBNET_MASK:
				return mapToValue(getFirstEthInterface(networkSetting.getEthInterfaces()).getMask());
			case GATEWAY:
				return mapToValue(getFirstEthInterface(networkSetting.getEthInterfaces()).getGateway());
			case HOST_NAME:
				return mapToValue(networkSetting.getHostname());
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToNetworkSettingProperty", property));
				return null;
		}
	}

	/**
	 * Maps an {@link OutletProperty} from the given {@link Outlet} object to a string representation.
	 *
	 * @param outlet the outlet object
	 * @param property the outlet property to map
	 * @return the mapped value as a string, or {@code null} if inputs are invalid
	 */
	public static String mapToOutletProperty(Outlet outlet, OutletProperty property) {
		if (outlet == null || property == null) {
			return null;
		}

		switch (property) {
			case NAME:
				return mapToValue(outlet.getName());
			case STATUS:
				return mapToStatus(outlet.getState());
			case INITIAL_STATE:
				return InitialState.getByValue(outlet.getInitialState()).getName();
			case REBOOT_DELAY:
				return mapToValue(outlet.getRebootTime());
			case POWER:
				if (isRebootingComponent(outlet.getState())) {
					return Constant.OFF;
				}
				InitialState initialState = InitialState.getByValue(outlet.getInitialState());
				String toggleValue = Optional.ofNullable(mapToToggle(outlet.getState())).orElse("0");
				if (InitialState.NOT_TOGGLE_STATES.contains(initialState)) {
					return toggleValue.equals("1") ? Constant.ON : Constant.OFF;
				}
				return toggleValue;
			case REBOOT:
				if (isRebootingComponent(outlet.getState())) {
					return Constant.IN_PROGRESS;
				}
				return InitialState.NOT_REBOOT_STATES.contains(InitialState.getByValue(outlet.getInitialState()))
						? Constant.REBOOT
						: Constant.NOT_AVAILABLE;
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToOutletProperty", property));
				return null;
		}
	}

	/**
	 * Maps an {@link OutletGroupProperty} from the given {@link Group} object to a string representation.
	 *
	 * @param group the outlet group
	 * @param property the group property to map
	 * @return the mapped value as a string, or {@code null} if inputs are invalid
	 */
	public static String mapToOutletGroupProperty(Group group, OutletGroupProperty property) {
		if (group == null || property == null) {
			return null;
		}

		switch (property) {
			case NAME:
				return mapToValue(group.getName());
			case STATUS:
				return mapToStatus(group.getState());
			case POWER:
				return isRebootingComponent(group.getState()) ? Constant.OFF : mapToToggle(group.getState());
			case REBOOT:
				return isRebootingComponent(group.getState()) ? Constant.IN_PROGRESS : Constant.NOT_AVAILABLE;
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToOutletGroupProperty", property));
				return null;
		}
	}

	/**
	 * Returns the measurements of the first {@link Device}, or a new {@link DeviceMeasurements} if unavailable.
	 *
	 * @param devices the list of devices
	 * @return the measurements of the first device, or default measurements
	 */
	private static DeviceMeasurements getMeasurements(List<Device> devices) {
		return Optional.ofNullable(devices).filter(device -> !device.isEmpty())
				.map(device -> device.get(0).getMeasurements())
				.orElse(new DeviceMeasurements());
	}

	/**
	 * Returns the first {@link EthInterface} from the list, or a new one if unavailable.
	 *
	 * @param ethInterfaces the list of Ethernet interfaces
	 * @return the first Ethernet interface, or a default one
	 */
	private static EthInterface getFirstEthInterface(List<EthInterface> ethInterfaces) {
		return Optional.ofNullable(ethInterfaces).filter(ethInterface -> !ethInterface.isEmpty())
				.map(ethInterface -> ethInterface.get(0))
				.orElse(new EthInterface());
	}

	/**
	 * Returns the control endpoint based on the given action and current state.
	 * <p>
	 * Supports {@link Constant#POWER} and {@link Constant#REBOOT} actions.
	 *
	 * @param action the control action (e.g., toggle or reboot)
	 * @param state the current state (1 for ON, others for OFF)
	 * @return the corresponding endpoint, or {@code null} if the action is unsupported
	 */
	public static String getControlEndpoint(String action, int state) {
		if (action.equals(Constant.POWER)) {
			return state == 1 ? EndpointConstant.POWER_OFF : EndpointConstant.POWER_ON;
		}
		if (action.equals(Constant.REBOOT)) {
			return EndpointConstant.REBOOT;
		}
		LOGGER.warn(Constant.UNSUPPORTED_ACTION_WARNING + action);
		return null;
	}

	/**
	 * Checks if the given state is rebooting {@link Outlet} or {@link Group}.
	 *
	 * @param state the state of the component
	 * @return {@code true} if rebooting; otherwise {@code false}
	 */
	public static boolean isRebootingComponent(Integer state) {
		return state != null && state == 2;
	}

	/**
	 * Maps a string value to itself if not null or empty; otherwise returns null.
	 */
	private static String mapToValue(String value) {
		return StringUtils.isNotNullOrEmpty(value) ? value : null;
	}

	/**
	 * Returns the string representation of the first element in the list, or {@code null} if the list is null or empty.
	 *
	 * @param values the list of values
	 * @param <T>    the type of list elements
	 * @return string representation of the first element, or {@code null} if empty
	 */
	private static <T> String mapToValue(List<T> values) {
		return CollectionUtils.isNotEmpty(values) ? String.valueOf(values.get(0)) : null;
	}

	/**
	 * Returns the rounded string representation of a {@link Double},
	 formatted with at most one decimal place, or {@code null} if input is null.
	 *
	 * @param value the double value
	 * @return formatted string or {@code null} if input is null
	 */
	private static String mapToValue(Double value) {
		return value != null ? new DecimalFormat("0.#").format(value) : null;
	}

	/**
	 * Returns the string representation of an {@link Integer}, or {@code null} if input is null.
	 *
	 * @param value the integer value
	 * @return string representation or {@code null} if input is null
	 */
	private static String mapToValue(Integer value) {
		return value != null ? String.valueOf(value) : null;
	}

	/**
	 * Maps a {@link Boolean} to its corresponding string value.
	 * Returns {@code trueValue} if {@code true}, {@code falseValue} if {@code false}, or {@code null} if input is null.
	 *
	 * @param value      the boolean value
	 * @param trueValue  string to return if value is {@code true}
	 * @param falseValue string to return if value is {@code false}
	 * @return mapped string or {@code null} if input is null
	 */
	private static String mapToValue(Boolean value, String trueValue, String falseValue) {
		if (value == null) {
			return null;
		}

		return value.equals(Boolean.TRUE) ? trueValue : falseValue;
	}

	/**
	 * Converts an integer value to a toggle string representation.
	 * Returns "1" if value is 1, "0" otherwise (including all other integers).
	 * Returns {@code null} if input is null.
	 *
	 * @param value the integer toggle value
	 * @return "1", "0", or {@code null} if input is null
	 */
	public static String mapToToggle(Integer value) {
		if (value == null) {
			return null;
		}

		return value == 1 ? "1" : "0";
	}

	/**
	 * Converts a timestamp string to a formatted to {@link Constant#DATETIME_12H_FORMAT}
	 * based on the given time zone. If the time zone is invalid or empty,
	 * the system default time zone is used.
	 *
	 * @param timestamp the ISO-8601 timestamp string (e.g., "2024-06-05T14:30:00Z")
	 * @param timeZone  the IANA time zone ID (e.g., "Asia/Ho_Chi_Minh"), or null/empty for system default
	 * @return formatted to {@link Constant#DATETIME_12H_FORMAT}, or {@code null} if timestamp is null/empty
	 */
	private static String mapTo12hDatetime(String timestamp, String timeZone) {
		try {
			if (StringUtils.isNullOrEmpty(timestamp)) {
				return null;
			}

			ZoneId zoneId = StringUtils.isNullOrEmpty(timeZone) || !ZoneId.getAvailableZoneIds().contains(timeZone) ? ZoneId.systemDefault() : ZoneId.of(timeZone);
			ZonedDateTime zdt = ZonedDateTime.parse(timestamp).toInstant().atZone(zoneId);
			LocalDateTime localDateTime = zdt.toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATETIME_12H_FORMAT);

			return localDateTime.format(formatter);
		} catch (Exception e) {
			LOGGER.error(String.format(Constant.MAP_12_DATE_TIME_FAILED, timestamp, timestamp), e);
			return null;
		}
	}

	/**
	 * Converts the integer state of an {@link Outlet} or {@link Group} to a readable status string.
	 * <ul>
	 *   <li>0 → {@link Constant#OFF}</li>
	 *   <li>1 → {@link Constant#ON}</li>
	 *   <li>2 → {@link Constant#REBOOTING}</li>
	 * </ul>
	 *
	 * @param state the integer representing the component state
	 * @return the corresponding status string, or {@code null} if unknown or null
	 */
	private static String mapToStatus(Integer state) {
		if (state == null) {
			return null;
		}
		switch (state) {
			case 0:
				return Constant.OFF;
			case 1:
				return Constant.ON;
			case 2:
				return Constant.REBOOTING;
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_MAP_PROPERTY_WARNING, "mapToStatus", state));
				return null;
		}
	}

	/**
	 * Returns the elapsed uptime between the current system time and the given timestamp in milliseconds.
	 * <p>
	 * The input timestamp represents the start time in milliseconds (typically from {@link System#currentTimeMillis()}).
	 * The returned string represents the absolute duration in the format:
	 * "X day(s) Y hour(s) Z minute(s) W second(s)", omitting any zero-value units except seconds.
	 *
	 * @param uptime the start time in milliseconds as a string (e.g., "1717581000000")
	 * @return a formatted duration string like "2 day(s) 3 hour(s) 15 minute(s) 42 second(s)",
	 *         or {@link Constant#NONE} if parsing fails
	 */
	private static String mapToUptime(String uptime) {
		try {
			if (StringUtils.isNullOrEmpty(uptime)) {
				return Constant.NONE;
			}

			long uptimeSecond = (System.currentTimeMillis() - Long.parseLong(uptime)) / 1000;
			long seconds = uptimeSecond % 60;
			long minutes = uptimeSecond % 3600 / 60;
			long hours = uptimeSecond % 86400 / 3600;
			long days = uptimeSecond / 86400;
			StringBuilder rs = new StringBuilder();
			if (days > 0) {
				rs.append(days).append(" day(s) ");
			}
			if (hours > 0) {
				rs.append(hours).append(" hour(s) ");
			}
			if (minutes > 0) {
				rs.append(minutes).append(" minute(s) ");
			}
			rs.append(seconds).append(" second(s)");

			return rs.toString().trim();
		} catch (Exception e) {
			LOGGER.error(Constant.MAP_ELAPSED_TIME_FAILED + uptime, e);
			return Constant.NONE;
		}
	}
}
