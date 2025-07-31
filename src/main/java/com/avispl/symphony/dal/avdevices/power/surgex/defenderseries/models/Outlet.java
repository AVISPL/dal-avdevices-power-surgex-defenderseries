package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the outlet of a {@link Device}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Outlet {
	private String id;
	private Integer state;
	private Integer status;
	private Integer initialState;
	private String name;
	@JsonProperty("Connected")
	private Boolean connected;
	private Integer rebootTime;

	public Outlet() {
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
	 * Retrieves {@link #state}
	 *
	 * @return value of {@link #state}
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * Sets {@link #state} value
	 *
	 * @param state new value of {@link #state}
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #initialState}
	 *
	 * @return value of {@link #initialState}
	 */
	public Integer getInitialState() {
		return initialState;
	}

	/**
	 * Sets {@link #initialState} value
	 *
	 * @param initialState new value of {@link #initialState}
	 */
	public void setInitialState(Integer initialState) {
		this.initialState = initialState;
	}

	/**
	 * Retrieves {@link #connected}
	 *
	 * @return value of {@link #connected}
	 */
	public Boolean getConnected() {
		return connected;
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

	/**
	 * Retrieves {@link #connected}
	 *
	 * @return value of {@link #connected}
	 */
	public Boolean isConnected() {
		return connected;
	}

	/**
	 * Sets {@link #connected} value
	 *
	 * @param connected new value of {@link #connected}
	 */
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	/**
	 * Retrieves {@link #rebootTime}
	 *
	 * @return value of {@link #rebootTime}
	 */
	public Integer getRebootTime() {
		return rebootTime;
	}

	/**
	 * Sets {@link #rebootTime} value
	 *
	 * @param rebootTime new value of {@link #rebootTime}
	 */
	public void setRebootTime(Integer rebootTime) {
		this.rebootTime = rebootTime;
	}
}
