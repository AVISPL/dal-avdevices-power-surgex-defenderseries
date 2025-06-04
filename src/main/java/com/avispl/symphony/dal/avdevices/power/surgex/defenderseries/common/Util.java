package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;

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
			case HARDWARE_VERSION:
				return Constant.HARDWARE_VERSION;
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
				return mapToValue(getMeasurements(currentStatus.getDevices()).isSurgeGood(), Constant.OK, Constant.FAULT);
			case VOLTAGE_LEVEL_STATUS:
				return mapToValue(getMeasurements(currentStatus.getDevices()).isVoltageInLimit(), Constant.OK, Constant.OUT_OF_RANGE);
			default:
				return null;
		}
	}

	/**
	 * Maps the given {@link AdapterMetadataProperty} to its value using the provided properties.
	 *
	 * @param property the property to map
	 * @param applicationProperties the source of property values
	 * @return the formatted value, or {@code Constant.NONE} if not available
	 */
	public static String mapToAdapterMetadataProperty(AdapterMetadataProperty property, Properties applicationProperties) {
		String adapterBuildDate = applicationProperties.getProperty("adapter.build.date");
		String adapterVersion = applicationProperties.getProperty("adapter.version");

		switch (property) {
			case ADAPTER_BUILD_DATE:
				return adapterBuildDate == null ? Constant.NONE : adapterBuildDate;
			case ADAPTER_UPTIME:
				return adapterBuildDate == null ? Constant.NONE : formatElapsedTime(adapterBuildDate);
			case ADAPTER_VERSION:
				return adapterVersion == null ? Constant.NONE : adapterVersion;
			default:
				return Constant.NONE;
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
			case TOGGLE:
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
				boolean isRebootReadonly = isRebootingComponent(outlet.getState())
						|| InitialState.NOT_REBOOT_STATES.contains(InitialState.getByValue(outlet.getInitialState()));
				return isRebootReadonly ? Constant.REBOOT : Constant.NOT_AVAILABLE;
			default:
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
			case TOGGLE:
				return isRebootingComponent(group.getState()) ? Constant.OFF : mapToToggle(group.getState());
			case REBOOT:
				return isRebootingComponent(group.getState()) ? Constant.REBOOT : Constant.NOT_AVAILABLE;
			default:
				return null;
		}
	}

	/**
	 * Retrieves the list of outlets from the {@link CurrentStatus}.
	 *
	 * @param currentStatus the current status
	 * @return the list of outlets, or an empty list if unavailable
	 */
	public static List<Outlet> getOutlets(CurrentStatus currentStatus) {
		return Optional.ofNullable(currentStatus)
				.map(CurrentStatus::getDevices).filter(devices -> !devices.isEmpty())
				.map(devices -> devices.get(0).getOutlets()).filter(outlets -> !outlets.isEmpty())
				.orElse(new ArrayList<>());
	}

	/**
	 * Retrieves the list of outlet groups from the {@link CurrentStatus}.
	 *
	 * @param currentStatus the current status
	 * @return the list of outlet groups, or an empty list if unavailable
	 */
	public static List<Group> getOutletGroups(CurrentStatus currentStatus) {
		return Optional.ofNullable(currentStatus)
				.map(CurrentStatus::getDevices).filter(devices -> !devices.isEmpty())
				.map(devices -> devices.get(0).getGroups()).filter(group -> !group.isEmpty())
				.orElse(new ArrayList<>());
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
	 * Supports {@link Constant#TOGGLE} and {@link Constant#REBOOT} actions.
	 *
	 * @param action the control action (e.g., toggle or reboot)
	 * @param state the current state (1 for ON, others for OFF)
	 * @return the corresponding endpoint, or {@code null} if the action is unsupported
	 */
	public static String getControlEndpoint(String action, int state) {
		if (action.equals(Constant.TOGGLE)) {
			return state == 1 ? EndpointConstant.POWER_OFF : EndpointConstant.POWER_ON;
		}
		if (action.equals(Constant.REBOOT)) {
			return EndpointConstant.REBOOT;
		}
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
	 * Maps the first element of a list to its string representation, or returns null if empty.
	 */
	private static <T> String mapToValue(List<T> values) {
		return CollectionUtils.isNotEmpty(values) ? String.valueOf(values.get(0)) : null;
	}

	/**
	 * Maps a Double to its rounded string value, or null if null.
	 */
	private static String mapToValue(Double value) {
		return value != null ? new DecimalFormat("0.#").format(value) : null;
	}

	/**
	 * Maps an Integer to its string representation, or null if null.
	 */
	private static String mapToValue(Integer value) {
		return value != null ? String.valueOf(value) : null;
	}

	/**
	 * Maps a Boolean to a string based on true/false values provided.
	 */
	private static String mapToValue(Boolean value, String trueValue, String falseValue) {
		if (value == null) {
			return null;
		}

		return value.equals(Boolean.TRUE) ? trueValue : falseValue;
	}

	/**
	 * Converts an integer state to toggle string ("1" or "0"), or null if invalid.
	 */
	public static String mapToToggle(Integer value) {
		if (value == null) {
			return null;
		}

		return value == 1 ? "1" : "0";
	}

	/**
	 * Converts a timestamp string to a formatted 12-hour local datetime string.
	 */
	private static String mapTo12hDatetime(String timestamp, String timeZone) {
		if (StringUtils.isNullOrEmpty(timestamp)) {
			return null;
		}

		ZoneId zoneId = StringUtils.isNullOrEmpty(timeZone) || !ZoneId.getAvailableZoneIds().contains(timeZone) ? ZoneId.systemDefault() : ZoneId.of(timeZone);
		ZonedDateTime zdt = ZonedDateTime.parse(timestamp).toInstant().atZone(zoneId);
		LocalDateTime localDateTime = zdt.toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATETIME_12H_FORMAT);

		return localDateTime.format(formatter);
	}

	/**
	 * Maps an {@link Outlet}/{@link Group} state integer to a human-readable status string.
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
				return null;
		}
	}

	/**
	 * Returns the elapsed time between now and the given date-time string.
	 *
	 * @param adapterBuildDate the date-time in format "yyyy-MM-dd HH:mm"
	 * @return formatted string like "X day(s) Y hour(s) Z minute(s) W second(s)"
	 */
	private static String formatElapsedTime(String adapterBuildDate) {
		LocalDateTime target = LocalDateTime.parse(adapterBuildDate, DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN));
		Duration duration = Duration.between(LocalDateTime.now(), target).abs();
		long totalSeconds = duration.getSeconds();
		long days = totalSeconds / (24 * 3600);
		long hours = totalSeconds % (24 * 3600) / 3600;
		long minutes = totalSeconds % 3600 / 60;
		long seconds = totalSeconds % 60;
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
	}
}
