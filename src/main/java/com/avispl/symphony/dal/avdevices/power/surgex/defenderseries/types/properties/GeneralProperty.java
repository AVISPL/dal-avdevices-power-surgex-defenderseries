package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties;

import java.util.EnumSet;
import java.util.Set;

import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.bases.BaseProperty;

/**
 * Enum representing properties related to a general.
 * Each property corresponds to a display name or configuration aspect of the general.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum GeneralProperty implements BaseProperty {
	FIRMWARE_VERSION("FirmwareVersion"),
	MODEL_NUMBER("ModelNumber"),
	MAC_ADDRESS("MACAddress"),
	DEVICE_TIME("DeviceTime"),
	INTERNAL_TEMPERATURE("InternalTemperature(C)"),
	LINE_VOLTAGE("LineVoltage(V)"),
	CURRENT("Current(A)"),
	POWER("Power(W)"),
	SURGE_PROTECTION_STATUS("SurgeProtectionStatus"),
	VOLTAGE_LEVEL_STATUS("VoltageLevelStatus");

	private final String name;

	GeneralProperty(String name) {
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

	public static Set<GeneralProperty> getGraphProperties() {
		return EnumSet.of(INTERNAL_TEMPERATURE, LINE_VOLTAGE, CURRENT, POWER);
	}
}
