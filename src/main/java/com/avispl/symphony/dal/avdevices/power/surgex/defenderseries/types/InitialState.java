package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;

/**
 * Enum representing different states of Initial state.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum InitialState {
	UNDEFINED(Constant.NONE, -1),
	ALWAYS_ON("Always On", 0),
	ALWAYS_OFF("Always Off", 1),
	ON(Constant.ON, 3),
	OFF(Constant.OFF, 4),
	LAST_STATE("Last", 5),
	REBOOT_ONLY("Reboot Only", 6);

	/**
	 * Initial states that do not support toggle switch generation for the outlet.
	 */
	public static final Set<InitialState> NOT_TOGGLE_STATES = Collections.unmodifiableSet(EnumSet.of(ALWAYS_ON, ALWAYS_OFF, REBOOT_ONLY));
	/**
	 * Initial states that do not support reboot button generation for the outlet.
	 */
	public static final Set<InitialState> NOT_REBOOT_STATES = Collections.unmodifiableSet(EnumSet.of(ALWAYS_ON, ALWAYS_OFF));

	private final String name;
	private final int value;

	InitialState(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public int getValue() {
		return value;
	}

	public static InitialState getByValue(Integer value) {
		if (value == null) {
			return UNDEFINED;
		}
		return Arrays.stream(values()).filter(state -> state.getValue() == value).findFirst().orElse(UNDEFINED);
	}
}
