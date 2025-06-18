package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.security.auth.login.FailedLoginException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.avispl.symphony.api.common.error.InvalidArgumentException;
import com.avispl.symphony.api.common.error.NotImplementedException;
import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Button;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.bases.BaseProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.Util;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.EndpointConstant;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.CurrentStatus;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Group;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.NetworkSetting;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Ntp;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.Outlet;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models.TimeZoneInfo;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.InitialState;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.AdapterMetadataProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.GeneralProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.NetworkSettingProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.OutletGroupProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties.OutletProperty;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Main adapter class for SurgeX Defender.
 * Responsible for generating monitoring, controllable, and graph properties.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class SurgeXDefenderCommunicator extends RestCommunicator implements Monitorable, Controller {
	/**
	 * Set of property names supported for historical data tracking.
	 */
	private static final Set<String> SUPPORTED_HISTORICAL_PROPS = new HashSet<>(Arrays.asList(
			GeneralProperty.INTERNAL_TEMPERATURE.getName(),
			GeneralProperty.LINE_VOLTAGE.getName(),
			GeneralProperty.CURRENT.getName(),
			GeneralProperty.POWER.getName()
	));

	/**
	 * Lock used to ensure thread-safe operations.
	 */
	private final ReentrantLock reentrantLock;
	/**
	 * Holds the application configuration properties loaded from the {@code version.properties} file.
	 */
	private final Properties versionProperties;
	/**
	 * Device adapter instantiation timestamp.
	 */
	private final Long adapterInitializationTimestamp;
	/**
	 * A dummy {@link AdvancedControllableProperty} that is added when the list of {@link ControllableProperty} is empty.
	 * This ensures the system has at least one controllable property to handle.
	 */
	private final AdvancedControllableProperty dummyControllableProperty;

	/**
	 * Store of extended statistics object.
	 */
	private ExtendedStatistics localExtendedStatistics;
	/**
	 * Represents the current status of the adapter.
	 */
	private CurrentStatus currentStatus;
	/**
	 * Represents the network settings of the adapter.
	 */
	private NetworkSetting networkSetting;
	/**
	 * The outlets from {@link CurrentStatus}
	 */
	private List<Outlet> outlets;
	/**
	 * The outlet groups from {@link CurrentStatus}
	 */
	private List<Group> groups;

	/**
	 * Store of historical properties
	 */
	private Set<String> historicalProperties;

	public SurgeXDefenderCommunicator() {
		this.reentrantLock = new ReentrantLock();
		this.versionProperties = new Properties();
		this.adapterInitializationTimestamp = System.currentTimeMillis();
		this.dummyControllableProperty = new AdvancedControllableProperty(null, null, new AdvancedControllableProperty.Button(), null);

		this.localExtendedStatistics = new ExtendedStatistics();
		this.currentStatus = new CurrentStatus();
		this.networkSetting = new NetworkSetting();
		this.outlets = new ArrayList<>();
		this.groups = new ArrayList<>();

		this.historicalProperties = new HashSet<>();

		this.setTrustAllCertificates(true);
		this.loadProperties(this.versionProperties);
		this.logger.info(Constant.INITIALIZED_SUCCESSFULLY_INFO);
	}

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(Constant.COMMA, this.historicalProperties);
	}

	/**
	 * Sets historical properties from a comma-separated string.
	 * Clears existing set before updating.
	 *
	 * @param historicalProperties comma-separated property names
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		if (StringUtils.isNullOrEmpty(historicalProperties)) {
			this.logger.warn(Constant.HISTORICAL_PROPS_EMPTY_WARNING);
			return;
		}
		Arrays.stream(historicalProperties.split(Constant.COMMA)).map(String::trim)
				.filter(historicalProperty -> !historicalProperty.isEmpty())
				.forEach(historicalProperty -> {
					if (!SUPPORTED_HISTORICAL_PROPS.contains(historicalProperty)) {
						this.logger.warn(Constant.UNDEFINED_HISTORICAL_PROP_WARNING + historicalProperty);
						return;
					}
					this.historicalProperties.add(historicalProperty);
				});
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		this.reentrantLock.lock();
		try {
			if (!this.isDataSetup()) {
				return Collections.emptyList();
			}
			Map<String, String> statistics = new HashMap<>(this.getGeneralProperties());
			statistics.putAll(this.getAdapterMetadataProperties());
			statistics.putAll(this.getNetworkSettingProperties());
			statistics.putAll(this.getOutletProperties());
			statistics.putAll(this.getOutletGroupProperties());

			List<AdvancedControllableProperty> controllableProperties = new ArrayList<>(this.getOutletControllers());
			controllableProperties.addAll(this.getOutletGroupControllers());
			Optional.of(controllableProperties).filter(List::isEmpty).ifPresent(l -> l.add(this.dummyControllableProperty));

			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			extendedStatistics.setControllableProperties(controllableProperties);
			extendedStatistics.setStatistics(statistics);
			extendedStatistics.setDynamicStatistics(this.getDynamicStatistics(statistics));
			this.localExtendedStatistics = extendedStatistics;
		} finally {
			this.reentrantLock.unlock();
		}
		return Collections.singletonList(this.localExtendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		this.reentrantLock.lock();
		try {
			String[] propertyInfo = controllableProperty.getProperty().split(Constant.HASH_SYMBOL, 2);
			String[] groupInfo = propertyInfo[0].split(Constant.UNDERSCORE);
			if (propertyInfo.length < 2 || groupInfo.length < 2) {
				throw new InvalidArgumentException(Constant.CONTROL_PROPERTY_FAILED + controllableProperty.getProperty());
			}

			String groupName = groupInfo[0];
			String action = propertyInfo[1];
			int componentIndex = Integer.parseInt(groupInfo[groupInfo.length - 1]) - 1;
			this.logger.info(String.format("Start control to %s", controllableProperty.getProperty()));
			switch (groupName) {
				case Constant.OUTLET_GROUP: {
					if (componentIndex < 0 || componentIndex >= this.outlets.size()) {
						throw new IndexOutOfBoundsException(Constant.DEFINE_OUTLET_INDEX_FAILED + componentIndex);
					}
					Outlet outlet = this.outlets.get(componentIndex);
					if (Util.isRebootingComponent(outlet.getState())) {
						return;
					}
					this.performControlOperation(Util.getControlEndpoint(action, outlet.getState()), outlet.getId());
					return;
				}
				case Constant.GROUP_OUTLET_GROUP: {
					if (componentIndex < 0 || componentIndex >= this.groups.size()) {
						throw new IndexOutOfBoundsException(Constant.DEFINE_OUTLET_GROUP_INDEX_FAILED + componentIndex);
					}
					Group group = this.groups.get(componentIndex);
					if (Util.isRebootingComponent(group.getState())) {
						return;
					}
					this.performControlOperation(Util.getControlEndpoint(action, group.getState()), group.getId());
					return;
				}
				default:
					this.logger.warn(Constant.CONTROL_PROPERTY_FAILED + controllableProperty.getProperty());
			}
		} finally {
			this.logger.info("End control " + controllableProperty.getProperty());
			this.reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			this.logger.warn(Constant.CONTROLLABLE_PROPS_EMPTY_WARNING);
			return;
		}
		controllableProperties.forEach(controllableProperty -> {
			try {
				this.controlProperty(controllableProperty);
			} catch (Exception e) {
				this.logger.error(Constant.CONTROL_PROPERTY_FAILED + controllableProperty.getProperty(), e);
			}
		});
	}

	@Override
	protected void authenticate() throws Exception {
		if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		}
	}

	@Override
	protected void internalDestroy() {
		this.logger.info(Constant.DESTROY_INTERNAL_INFO + this);
		this.currentStatus = null;
		this.networkSetting = null;
		this.outlets = null;
		this.groups = null;
		this.localExtendedStatistics = null;
		this.historicalProperties = null;
		super.internalDestroy();
	}

	/**
	 * Load properties from the {@code version.properties} file into the provided {@link Properties} object.
	 * <p>
	 * If the file is not found or an error occurs during reading, the method logs the error but does not throw an exception.
	 *
	 * @param properties The {@link Properties} object to populate with configuration values.
	 */
	private void loadProperties(Properties properties) {
		try {
			properties.load(getClass().getResourceAsStream("/version.properties"));
			properties.setProperty("adapter.uptime", String.valueOf(this.adapterInitializationTimestamp));
		} catch (IOException e) {
			this.logger.error(Constant.READ_PROPERTIES_FILE_FAILED + e.getMessage());
		}
	}

	/**
	 * Authenticates and attempts to fetch the device's current status and network settings.
	 * Also extracts and sets the time zone info into the current status.
	 *
	 * @return {@code true} if data is successfully fetched and initialized; {@code false} otherwise.
	 * @throws FailedLoginException if authentication fails.
	 */
	private boolean isDataSetup() throws FailedLoginException {
		try {
			this.authenticate();
			this.currentStatus = this.fetchData(EndpointConstant.CURRENT_STATUS, CurrentStatus.class);
			this.networkSetting = this.fetchData(EndpointConstant.NETWORK_SETTINGS, NetworkSetting.class);

			this.currentStatus.setTimeZone(Optional.ofNullable(this.networkSetting)
					.map(NetworkSetting::getNtp).map(Ntp::getTimeZoneInfo).map(TimeZoneInfo::getName)
					.orElse(null));
			this.outlets = Optional.ofNullable(currentStatus)
					.map(CurrentStatus::getDevices).filter(devices -> !devices.isEmpty())
					.map(devices -> devices.get(0).getOutlets()).filter(outletList -> !outletList.isEmpty())
					.orElse(new ArrayList<>());
			this.groups = Optional.ofNullable(currentStatus)
					.map(CurrentStatus::getDevices).filter(devices -> !devices.isEmpty())
					.map(devices -> devices.get(0).getGroups()).filter(groupList -> !groupList.isEmpty())
					.orElse(new ArrayList<>());

			return true;
		} catch (FailedLoginException e) {
			throw e;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Generates general properties from the currentStatus.
	 *
	 * @return A map of general property names and their corresponding values,
	 * or an empty map if the current status is not available.
	 */
	private Map<String, String> getGeneralProperties() {
		if (Objects.isNull(this.currentStatus)) {
			this.logger.warn(Constant.CURRENT_STATUS_NULL_WARNING);
			return Collections.emptyMap();
		}
		return this.generateProperties(
				GeneralProperty.values(),
				null,
				property -> Util.mapToGeneralProperty(this.currentStatus, property)
		);
	}

	/**
	 * Retrieves properties related to Adapter, mapping them using a utility method.
	 *
	 * @return A map of Adapter metadata properties with the property names as keys and their corresponding mapped values as values.
	 */
	private Map<String, String> getAdapterMetadataProperties() {
		return this.generateProperties(
				AdapterMetadataProperty.values(),
				Constant.ADAPTER_METADATA_GROUP,
				property -> Util.mapToAdapterMetadataProperty(this.versionProperties, property)
		);
	}

	/**
	 * Generates network setting properties from the networkSettings.
	 *
	 * @return A map of network property names and their corresponding values,
	 * or an empty map if the network settings are not available.
	 */
	private Map<String, String> getNetworkSettingProperties() {
		if (Objects.isNull(this.networkSetting)) {
			this.logger.warn(Constant.NETWORK_STATUS_NULL_WARNING);
			return Collections.emptyMap();
		}
		return this.generateProperties(
				NetworkSettingProperty.values(),
				Constant.NETWORK_GROUP,
				property -> Util.mapToNetworkSettingProperty(this.networkSetting, property)
		);
	}

	/**
	 * Generates outlet-related properties from the currentStatus.
	 *
	 * @return A map of outlet property names and values grouped by outlet index,
	 * or an empty map if no outlets are found.
	 */
	private Map<String, String> getOutletProperties() {
		if (CollectionUtils.isEmpty(this.outlets)) {
			this.logger.warn(Constant.OUTLETS_EMPTY_WARNING);
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();
		for (int i = 0; i < this.outlets.size(); i++) {
			Outlet outlet = this.outlets.get(i);
			properties.putAll(this.generateProperties(
					OutletProperty.values(),
					String.format(Constant.GROUP_FORMAT, Constant.OUTLET_GROUP, i + 1),
					property -> Util.mapToOutletProperty(outlet, property)
			));
		}

		return properties;
	}

	/**
	 * Generates properties for outlet groups from the currentStatus.
	 *
	 * @return A map of outlet group property names and values grouped by group index,
	 * or an empty map if no outlet groups are found.
	 */
	private Map<String, String> getOutletGroupProperties() {
		if (CollectionUtils.isEmpty(this.groups)) {
			this.logger.warn(Constant.OUTLET_GROUPS_EMPTY_WARNING);
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();
		for (int i = 0; i < this.groups.size(); i++) {
			Group group = this.groups.get(i);
			properties.putAll(this.generateProperties(
					OutletGroupProperty.values(),
					String.format(Constant.GROUP_FORMAT, Constant.GROUP_OUTLET_GROUP, i + 1),
					property -> Util.mapToOutletGroupProperty(group, property)
			));
		}

		return properties;
	}

	/**
	 * Generates controllable properties (e.g. toggle, reboot) for each outlet.
	 *
	 * @return A list of controllable properties for outlets,
	 * or an empty list if no outlets are found.
	 */
	private List<AdvancedControllableProperty> getOutletControllers() {
		if (CollectionUtils.isEmpty(this.outlets)) {
			this.logger.warn(Constant.OUTLETS_EMPTY_WARNING);
			return Collections.emptyList();
		}
		List<AdvancedControllableProperty> properties = new ArrayList<>();
		this.outlets.forEach(outlet -> {
			if (Util.isRebootingComponent(outlet.getState())) {
				this.logger.info(String.format(Constant.OUTLET_REBOOTING_INFO, outlet.getId()));
				return;
			}
			int index = this.outlets.indexOf(outlet);
			String controlName = String.format(Constant.GROUP_FORMAT, Constant.OUTLET_GROUP, index + 1);
			InitialState initialState = InitialState.getByValue(outlet.getInitialState());
			if (initialState.equals(InitialState.UNDEFINED)) {
				this.logger.warn(Constant.INITIAL_STATE_UNDEFINED_WARNING + outlet.getInitialState());
			}
			if (!InitialState.NOT_TOGGLE_STATES.contains(initialState)) {
				properties.add(this.generateControllableSwitch(
						String.format(Constant.PROPERTY_FORMAT, controlName, OutletProperty.POWER.getName()),
						Constant.ON, Constant.OFF, Util.mapToToggle(outlet.getState())
				));
			}
			if (!InitialState.NOT_REBOOT_STATES.contains(initialState)) {
				properties.add(this.generateControllableButton(
						String.format(Constant.PROPERTY_FORMAT, controlName, OutletProperty.REBOOT.getName()),
						Constant.REBOOT, Constant.REBOOTING, 0L
				));
			}
		});
		return properties;
	}

	/**
	 * Generates controllable properties (e.g. toggle, reboot) for each outlet group.
	 *
	 * @return A list of controllable properties for outlet groups,
	 * or an empty list if no outlet groups are found.
	 */
	private List<AdvancedControllableProperty> getOutletGroupControllers() {
		if (CollectionUtils.isEmpty(this.groups)) {
			this.logger.warn(Constant.OUTLET_GROUPS_EMPTY_WARNING);
			return Collections.emptyList();
		}
		List<AdvancedControllableProperty> properties = new ArrayList<>();
		this.groups.forEach(group -> {
			if (Util.isRebootingComponent(group.getState())) {
				this.logger.info(String.format(Constant.OUTLET_GROUP_REBOOTING_INFO, group.getId()));
				return;
			}
			int index = this.groups.indexOf(group);
			String controlName = String.format(Constant.GROUP_FORMAT, Constant.GROUP_OUTLET_GROUP, index + 1);
			properties.add(this.generateControllableSwitch(
					String.format(Constant.PROPERTY_FORMAT, controlName, OutletGroupProperty.POWER.getName()),
					Constant.ON, Constant.OFF, Util.mapToToggle(group.getState())
			));
			properties.add(this.generateControllableButton(
					String.format(Constant.PROPERTY_FORMAT, controlName, OutletGroupProperty.REBOOT.getName()),
					Constant.REBOOT, Constant.REBOOTING, 0L
			));
		});
		return properties;
	}

	/**
	 * Filters the input statistics to include only supported historical properties.
	 *
	 * @param statistics A map of all available statistics.
	 * @return A map containing only the supported historical properties and their values,
	 * or an empty map if the current status, historical properties, or input map is null/empty.
	 */
	private Map<String, String> getDynamicStatistics(Map<String, String> statistics) {
		if (this.currentStatus == null) {
			this.logger.warn(Constant.CURRENT_STATUS_NULL_WARNING);
			return Collections.emptyMap();
		}
		if (CollectionUtils.isEmpty(this.historicalProperties)) {
			this.logger.warn(Constant.HISTORICAL_PROPS_EMPTY_WARNING);
			return Collections.emptyMap();
		}
		if (MapUtils.isEmpty(statistics)) {
			this.logger.warn(Constant.STATISTICS_EMPTY_WARNING);
			return Collections.emptyMap();
		}

		Map<String, String> dynamicStatistics = new HashMap<>();
		this.historicalProperties.forEach(property -> {
			String value = statistics.get(property);
			if (SUPPORTED_HISTORICAL_PROPS.contains(property) && value != null) {
				dynamicStatistics.put(property, value);
			}
		});

		return dynamicStatistics;
	}

	/**
	 * Generates a map of property names and their corresponding values.
	 * <p>
	 * Each property name can be optionally prefixed with a group name using a predefined format.
	 * The values are derived using the provided mapping function, with {@link Constant#NONE} as a fallback for null results.
	 * </p>
	 *
	 * @param <T>        the enum type that extends {@link BaseProperty}
	 * @param properties the array of enum constants to be processed; if null, an empty map is returned
	 * @param groupName  optional group name used to prefix each property's name; can be null
	 * @param mapper     a function that maps each property to its corresponding string value;
	 *                   if null or if the result is null, {@link Constant#NONE} is used as the value
	 * @return a map where keys are (optionally grouped) property names and values are mapped strings or {@link Constant#NONE}
	 */
	private <T extends Enum<T> & BaseProperty> Map<String, String> generateProperties(T[] properties, String groupName, Function<T, String> mapper) {
		if (properties == null || mapper == null) {
			return Collections.emptyMap();
		}
		return Arrays.stream(properties).collect(Collectors.toMap(
				property -> Objects.isNull(groupName) ? property.getName() : String.format(Constant.PROPERTY_FORMAT, groupName, property.getName()),
				property -> Optional.ofNullable(mapper.apply(property)).orElse(Constant.NONE)
		));
	}

	/**
	 * Generates an {@link AdvancedControllableProperty} of type Button with the specified name, labels, and grace period.
	 *
	 * @param buttonName the name of the button control property
	 * @param label the label to display on the button
	 * @param labelPressed the label to display when the button is pressed
	 * @param gracePeriod the time in milliseconds before the button can be pressed again
	 * @return an {@link AdvancedControllableProperty} configured as a button control
	 */
	private AdvancedControllableProperty generateControllableButton(String buttonName, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);

		return new AdvancedControllableProperty(buttonName, new Date(), button, Constant.NOT_AVAILABLE);
	}

	/**
	 * Generates an {@link AdvancedControllableProperty} of type Switch with the specified name, labels, and value.
	 *
	 * @param switchName the name of the switch control property
	 * @param labelOn the label to display when the switch is in the "on" position
	 * @param labelOff the label to display when the switch is in the "off" position
	 * @param value the initial value of the switch (should be a Boolean or compatible object)
	 * @return an {@link AdvancedControllableProperty} configured as a switch control
	 */
	private AdvancedControllableProperty generateControllableSwitch(String switchName, String labelOn, String labelOff, Object value) {
		AdvancedControllableProperty.Switch toggleSwitch = new AdvancedControllableProperty.Switch();
		toggleSwitch.setLabelOn(labelOn);
		toggleSwitch.setLabelOff(labelOff);

		return new AdvancedControllableProperty(switchName, new Date(), toggleSwitch, value);
	}

	/**
	 * Fetches data from a given endpoint using a GET request and maps the response to the specified class type.
	 *
	 * @param <T> The type of the expected response.
	 * @param endpoint The API endpoint to send the GET request to.
	 * @param responseClass The class type to map the response to.
	 * @return The response mapped to the specified type {@code T}.
	 * @throws FailedLoginException If authorization fails (e.g., invalid username or password).
	 * @throws ResourceNotReachableException If any other error occurs while fetching the data.
	 */
	private <T> T fetchData(String endpoint, Class<T> responseClass) throws FailedLoginException {
		try {
			T response = this.doGet(endpoint, responseClass);
			if (Objects.isNull(response)) {
				this.logger.warn(String.format(Constant.FETCHED_DATA_NULL_WARNING, endpoint, responseClass.getSimpleName()));
			}

			return response;
		} catch (FailedLoginException e) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		} catch (Exception e) {
			this.logger.error(String.format(Constant.FETCH_DATA_FAILED, endpoint, responseClass.getSimpleName()), e);
			throw new ResourceNotReachableException(Constant.SET_UP_DATA_FAILED + responseClass.getSimpleName());
		}
	}

	/**
	 * Sends a POST request to control a component based on the given URI and component ID.
	 *
	 * @param uri The URI template for the action endpoint, which may contain placeholders.
	 * @param componentID The ID of the component to be controlled.
	 * @throws NotImplementedException If the request fails or the server returns a negative response.
	 */
	private void performControlOperation(String uri, String componentID) {
		try {
			String apiURI = uri.replace(EndpointConstant.DEVICE_ID_AND_COMPONENT_ID, componentID).replace("//", Constant.SLASH);
			Boolean response = this.doPost(apiURI, null, Boolean.class);
			if (response.equals(Boolean.FALSE)) {
				throw new NotImplementedException(Constant.RESPONSE_CONTROL_FAILED + apiURI);
			}
		} catch (Exception e) {
			String[] parts = uri.split(Constant.SLASH);
			this.logger.error(String.format("Exception occurred during control operation. Endpoint: %s, ComponentID: %s", uri, componentID), e);
			throw new NotImplementedException(Constant.ACTION_PERFORM_FAILED + parts[parts.length - 1]);
		}
	}
}
