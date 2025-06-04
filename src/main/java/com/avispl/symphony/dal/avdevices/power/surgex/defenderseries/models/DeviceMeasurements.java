package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the measurements of a {@link Device}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceMeasurements {
	private Double line1Line2;
	private Double temperature;
	private Boolean surgeGood;
	private Double current;
	@JsonProperty("vlotageInLimit")
	private Boolean voltageInLimit;
	private Double power;

	public DeviceMeasurements() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #line1Line2}
	 *
	 * @return value of {@link #line1Line2}
	 */
	public Double getLine1Line2() {
		return line1Line2;
	}

	/**
	 * Sets {@link #line1Line2} value
	 *
	 * @param line1Line2 new value of {@link #line1Line2}
	 */
	public void setLine1Line2(Double line1Line2) {
		this.line1Line2 = line1Line2;
	}

	/**
	 * Retrieves {@link #temperature}
	 *
	 * @return value of {@link #temperature}
	 */
	public Double getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@link #temperature} value
	 *
	 * @param temperature new value of {@link #temperature}
	 */
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	/**
	 * Retrieves {@link #surgeGood}
	 *
	 * @return value of {@link #surgeGood}
	 */
	public Boolean isSurgeGood() {
		return surgeGood;
	}

	/**
	 * Sets {@link #surgeGood} value
	 *
	 * @param surgeGood new value of {@link #surgeGood}
	 */
	public void setSurgeGood(Boolean surgeGood) {
		this.surgeGood = surgeGood;
	}

	/**
	 * Retrieves {@link #current}
	 *
	 * @return value of {@link #current}
	 */
	public Double getCurrent() {
		return current;
	}

	/**
	 * Sets {@link #current} value
	 *
	 * @param current new value of {@link #current}
	 */
	public void setCurrent(Double current) {
		this.current = current;
	}

	/**
	 * Retrieves {@link #voltageInLimit}
	 *
	 * @return value of {@link #voltageInLimit}
	 */
	public Boolean isVoltageInLimit() {
		return voltageInLimit;
	}

	/**
	 * Sets {@link #voltageInLimit} value
	 *
	 * @param voltageInLimit new value of {@link #voltageInLimit}
	 */
	public void setVoltageInLimit(Boolean voltageInLimit) {
		this.voltageInLimit = voltageInLimit;
	}

	/**
	 * Retrieves {@link #power}
	 *
	 * @return value of {@link #power}
	 */
	public Double getPower() {
		return power;
	}

	/**
	 * Sets {@link #power} value
	 *
	 * @param power new value of {@link #power}
	 */
	public void setPower(Double power) {
		this.power = power;
	}
}
