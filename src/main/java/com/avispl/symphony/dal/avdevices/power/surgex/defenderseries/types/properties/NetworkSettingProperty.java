package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties;

import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.bases.BaseProperty;

/**
 * Enum representing properties related to a network settings.
 * Each property corresponds to a display name or configuration aspect of the network settings.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum NetworkSettingProperty implements BaseProperty {
	IP_SETUP("IPSetup"),
	IP_ADDRESS("IPAddress"),
	SUBNET_MASK("SubnetMask"),
	GATEWAY("Gateway"),
	HOST_NAME("HostName");

	private final String name;

	NetworkSettingProperty(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
