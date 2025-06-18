package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the device of a {@link CurrentStatus}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
	private String id;
	@JsonProperty("deviceMeasurements")
	private DeviceMeasurements measurements;
	private List<Group> groups;
	private List<Outlet> outlets;

	public Device() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #measurements}
	 *
	 * @return value of {@link #measurements}
	 */
	public DeviceMeasurements getMeasurements() {
		return measurements;
	}

	/**
	 * Sets {@link #measurements} value
	 *
	 * @param measurements new value of {@link #measurements}
	 */
	public void setDeviceMeasurements(DeviceMeasurements measurements) {
		this.measurements = measurements;
	}

	/**
	 * Retrieves {@link #groups}
	 *
	 * @return value of {@link #groups}
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Sets {@link #groups} value
	 *
	 * @param groups new value of {@link #groups}
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	/**
	 * Retrieves {@link #outlets}
	 *
	 * @return value of {@link #outlets}
	 */
	public List<Outlet> getOutlets() {
		return outlets;
	}

	/**
	 * Sets {@link #outlets} value
	 *
	 * @param outlets new value of {@link #outlets}
	 */
	public void setOutlets(List<Outlet> outlets) {
		this.outlets = outlets;
	}
}
