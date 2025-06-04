package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types.properties;

import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.bases.BaseProperty;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;

/**
 * Enum representing properties related to an outlet.
 * Each property corresponds to a display name or configuration aspect of the outlet.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum OutletProperty implements BaseProperty {
	NAME("Name"),
	STATUS("Status"),
	INITIAL_STATE("InitialState"),
	TOGGLE(Constant.TOGGLE),
	REBOOT_DELAY("RebootDelay(s)"),
	REBOOT(Constant.REBOOT);

	private final String name;

	OutletProperty(String name) {
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
