package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the time zone information of the {@link Ntp}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class TimeZoneInfo {
	@JsonProperty("dst")
	private Boolean isDst;
	private Integer offset;
	private String name;

	public TimeZoneInfo() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #isDst}
	 *
	 * @return value of {@link #isDst}
	 */
	public Boolean getDst() {
		return isDst;
	}

	/**
	 * Sets {@link #isDst} value
	 *
	 * @param dst new value of {@link #isDst}
	 */
	public void setDst(Boolean dst) {
		isDst = dst;
	}

	/**
	 * Retrieves {@link #offset}
	 *
	 * @return value of {@link #offset}
	 */
	public Integer getOffset() {
		return offset;
	}

	/**
	 * Sets {@link #offset} value
	 *
	 * @param offset new value of {@link #offset}
	 */
	public void setOffset(Integer offset) {
		this.offset = offset;
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
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}
}
