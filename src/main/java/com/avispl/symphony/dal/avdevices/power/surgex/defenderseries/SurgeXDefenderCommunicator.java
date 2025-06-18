package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries;

import java.nio.file.FileSystemNotFoundException;
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
	 * Holds the application configuration properties loaded from the {@code application.properties} file.
	 */
	private final Properties applicationProperties;

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
	 * Store of historical properties
	 */
	private Set<String> historicalProperties;

	public SurgeXDefenderCommunicator() {
		this.reentrantLock = new ReentrantLock();
		this.applicationProperties = new Properties();

		this.localExtendedStatistics = new ExtendedStatistics();
		this.currentStatus = new CurrentStatus();
		this.networkSetting = new NetworkSetting();

		this.historicalProperties = new HashSet<>();

		this.setTrustAllCertificates(true);
		this.loadProperties(this.applicationProperties);
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
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(Constant.COMMA)).forEach(propertyName -> this.historicalProperties.add(propertyName.trim()));
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		this.reentrantLock.lock();
		try {
			this.authenticate();
			this.setupData();
			Map<String, String> statistics = new HashMap<>(this.getGeneralProperties());
			statistics.putAll(this.getAdapterMetadataProperties());
			statistics.putAll(this.getNetworkSettingProperties());
			statistics.putAll(this.getOutletProperties());
			statistics.putAll(this.getOutletGroupProperties());

			List<AdvancedControllableProperty> controllableProperties = new ArrayList<>(this.getOutletControllers());
			controllableProperties.addAll(this.getOutletGroupControllers());

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
			switch (groupName) {
				case Constant.OUTLET_GROUP: {
					Outlet outlet = Util.getOutlets(this.currentStatus).get(componentIndex);
					this.performData(Util.getControlEndpoint(action, outlet.getState()), outlet.getId());
					return;
				}
				case Constant.GROUP_OUTLET_GROUP: {
					Group group = Util.getOutletGroups(this.currentStatus).get(componentIndex);
					this.performData(Util.getControlEndpoint(action, group.getState()), group.getId());
					return;
				}
				default:
					throw new InvalidArgumentException(Constant.CONTROL_PROPERTY_FAILED + controllableProperty.getProperty());
			}
		} finally {
			this.reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				this.controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
			}
		}
	}

	@Override
	protected void authenticate() throws Exception {
		if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		}
	}

	@Override
	protected void internalDestroy() {
		this.currentStatus = null;
		this.networkSetting = null;
		this.localExtendedStatistics = null;
		this.historicalProperties = null;
		super.internalDestroy();
	}

	/**
	 * Loads properties from the {@code application.properties} file into the provided {@link Properties} object.
	 *
	 * @param properties The {@link Properties} object to load the configuration into.
	 * @throws ResourceNotReachableException if the properties file cannot be loaded.
	 */
	private void loadProperties(Properties properties) {
		try {
			properties.load(getClass().getResourceAsStream("/application.properties"));
		} catch (Exception e) {
			throw new FileSystemNotFoundException(Constant.READ_PROPERTIES_FILE_FAILED);
		}
	}

	/**
	 * Initializes device data by fetching the currentStatus and networkSettings.
	 *
	 * @throws FailedLoginException If the login fails during data fetching.
	 */
	private void setupData() throws FailedLoginException {
		this.currentStatus = this.fetchData(EndpointConstant.CURRENT_STATUS, CurrentStatus.class);
		this.networkSetting = this.fetchData(EndpointConstant.NETWORK_SETTINGS, NetworkSetting.class);

		String timeZone = Optional.ofNullable(this.networkSetting)
				.map(NetworkSetting::getNtp).map(Ntp::getTimeZoneInfo).map(TimeZoneInfo::getName)
				.orElse(null);
		this.currentStatus.setTimeZone(timeZone);
	}

	/**
	 * Generates general properties from the currentStatus.
	 *
	 * @return A map of general property names and their corresponding values,
	 * or an empty map if the current status is not available.
	 */
	private Map<String, String> getGeneralProperties() {
		if (Objects.isNull(this.currentStatus)) {
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
		Map<String, String> properties = new HashMap<>();
		Arrays.stream(AdapterMetadataProperty.values()).forEach(property -> {
			String propertyName = String.format(Constant.PROPERTY_FORMAT, Constant.ADAPTER_METADATA_GROUP, property.getName());
			properties.put(propertyName, Util.mapToAdapterMetadataProperty(property, this.applicationProperties));
		});

		return properties;
	}

	/**
	 * Generates network setting properties from the networkSettings.
	 *
	 * @return A map of network property names and their corresponding values,
	 * or an empty map if the network settings are not available.
	 */
	private Map<String, String> getNetworkSettingProperties() {
		if (Objects.isNull(this.networkSetting)) {
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
		List<Outlet> deviceOutlets = Util.getOutlets(this.currentStatus);
		if (CollectionUtils.isEmpty(deviceOutlets)) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();
		for (int i = 0; i < deviceOutlets.size(); i++) {
			Outlet outlet = deviceOutlets.get(i);
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
		List<Group> deviceOutletGroups = Util.getOutletGroups(this.currentStatus);
		if (CollectionUtils.isEmpty(deviceOutletGroups)) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();
		for (int i = 0; i < deviceOutletGroups.size(); i++) {
			Group group = deviceOutletGroups.get(i);
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
		List<Outlet> deviceOutlets = Util.getOutlets(this.currentStatus);
		if (CollectionUtils.isEmpty(deviceOutlets)) {
			return Collections.emptyList();
		}
		List<AdvancedControllableProperty> properties = new ArrayList<>();
		for (int i = 0; i < deviceOutlets.size(); i++) {
			Outlet outlet = deviceOutlets.get(i);
			if (Util.isRebootingComponent(outlet.getState())) {
				continue;
			}
			String buttonName = String.format(Constant.GROUP_FORMAT, Constant.OUTLET_GROUP, i + 1);
			InitialState initialState = InitialState.getByValue(outlet.getInitialState());
			if (initialState.equals(InitialState.UNDEFINED)) {
				this.logger.warn(Constant.INITIAL_STATE_UNDEFINED_WARNING + outlet.getInitialState());
			}
			if (!InitialState.NOT_TOGGLE_STATES.contains(initialState)) {
				properties.add(this.generateControllableSwitch(
						String.format(Constant.PROPERTY_FORMAT, buttonName, OutletProperty.TOGGLE.getName()),
						Constant.ON, Constant.OFF, Util.mapToToggle(outlet.getState())
				));
			}
			if (!InitialState.NOT_REBOOT_STATES.contains(initialState)) {
				properties.add(this.generateControllableButton(
						String.format(Constant.PROPERTY_FORMAT, buttonName, OutletProperty.REBOOT.getName()),
						Constant.REBOOT, Constant.REBOOTING, 0L
				));
			}
		}
		return properties;
	}

	/**
	 * Generates controllable properties (e.g. toggle, reboot) for each outlet group.
	 *
	 * @return A list of controllable properties for outlet groups,
	 * or an empty list if no outlet groups are found.
	 */
	private List<AdvancedControllableProperty> getOutletGroupControllers() {
		List<Group> deviceOutletGroups = Util.getOutletGroups(this.currentStatus);
		if (CollectionUtils.isEmpty(deviceOutletGroups)) {
			return Collections.emptyList();
		}
		List<AdvancedControllableProperty> properties = new ArrayList<>();
		for (int i = 0; i < deviceOutletGroups.size(); i++) {
			Group group = deviceOutletGroups.get(i);
			if (Util.isRebootingComponent(group.getState())) {
				continue;
			}
			String buttonName = String.format(Constant.GROUP_FORMAT, Constant.GROUP_OUTLET_GROUP, i + 1);
			properties.add(this.generateControllableSwitch(
					String.format(Constant.PROPERTY_FORMAT, buttonName, OutletGroupProperty.TOGGLE.getName()),
					Constant.ON, Constant.OFF, Util.mapToToggle(group.getState())
			));
			properties.add(this.generateControllableButton(
					String.format(Constant.PROPERTY_FORMAT, buttonName, OutletGroupProperty.REBOOT.getName()),
					Constant.REBOOT, Constant.REBOOTING, 0L
			));
		}
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
		if (this.currentStatus == null
				|| CollectionUtils.isEmpty(this.historicalProperties)
				|| MapUtils.isEmpty(statistics)) {
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
	 * Generates a map of properties with optional grouping and custom mapping logic.
	 *
	 * @param <T> The enum type that extends {@link BaseProperty}.
	 * @param properties An array of enum constants representing the properties.
	 * @param groupName An optional group name to prefix property names. Can be null.
	 * @param mapper A function to map each property to its corresponding value.
	 * @return A map of property names and their values, with "None" for null values.
	 */
	private <T extends Enum<T> & BaseProperty> Map<String, String> generateProperties(T[] properties, String groupName, Function<T, String> mapper) {
		Map<String, String> result = new HashMap<>();
		Arrays.stream(properties).forEach(property -> result.put(
				Objects.isNull(groupName) ? property.getName() : String.format(Constant.PROPERTY_FORMAT, groupName, property.getName()),
				Optional.ofNullable(mapper.apply(property)).orElse(Constant.NONE))
		);

		return result;
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
			return this.doGet(endpoint, responseClass);
		} catch (FailedLoginException e) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		} catch (Exception e) {
			throw new ResourceNotReachableException(Constant.SET_UP_DATA_FAILED + responseClass.getSimpleName(), e);
		}
	}

	/**
	 * Sends a POST request to control a device component based on the given URI and component ID.
	 *
	 * @param uri The URI template for the action endpoint, which may contain placeholders.
	 * @param componentID The ID of the device component to be controlled.
	 * @throws NotImplementedException If the request fails or the server returns a negative response.
	 */
	private void performData(String uri, String componentID) {
		try {
			String apiURI = uri.replace(EndpointConstant.DEVICE_ID_AND_COMPONENT_ID, componentID).replace("//", Constant.SLASH);
			Boolean response = this.doPost(apiURI, null, Boolean.class);
			if (response.equals(Boolean.FALSE)) {
				throw new NotImplementedException(Constant.RESPONSE_FAILED + apiURI);
			}
		} catch (Exception e) {
			String[] parts = uri.split(Constant.SLASH);
			throw new NotImplementedException(Constant.ACTION_PERFORM_FAILED + parts[parts.length - 1], e);
		}
	}
}
