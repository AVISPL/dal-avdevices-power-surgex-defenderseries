package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the NTP of the {@link NetworkSetting}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Ntp {
	@JsonProperty("enabled")
	private Boolean isEnabled;
	private Integer frequency;
	private String server;
	private String status;
	@JsonProperty("tz")
	private TimeZoneInfo timeZoneInfo;

	public Ntp() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #isEnabled}
	 *
	 * @return value of {@link #isEnabled}
	 */
	public Boolean getEnabled() {
		return isEnabled;
	}

	/**
	 * Sets {@link #isEnabled} value
	 *
	 * @param enabled new value of {@link #isEnabled}
	 */
	public void setEnabled(Boolean enabled) {
		isEnabled = enabled;
	}

	/**
	 * Retrieves {@link #frequency}
	 *
	 * @return value of {@link #frequency}
	 */
	public Integer getFrequency() {
		return frequency;
	}

	/**
	 * Sets {@link #frequency} value
	 *
	 * @param frequency new value of {@link #frequency}
	 */
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	/**
	 * Retrieves {@link #server}
	 *
	 * @return value of {@link #server}
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Sets {@link #server} value
	 *
	 * @param server new value of {@link #server}
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #timeZoneInfo}
	 *
	 * @return value of {@link #timeZoneInfo}
	 */
	public TimeZoneInfo getTimeZoneInfo() {
		return timeZoneInfo;
	}

	/**
	 * Sets {@link #timeZoneInfo} value
	 *
	 * @param timeZoneInfo new value of {@link #timeZoneInfo}
	 */
	public void setTimeZoneInfo(TimeZoneInfo timeZoneInfo) {
		this.timeZoneInfo = timeZoneInfo;
	}
}
