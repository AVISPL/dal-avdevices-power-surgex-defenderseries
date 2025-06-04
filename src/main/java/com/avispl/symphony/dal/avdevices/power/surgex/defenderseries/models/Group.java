package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the outlet group of a {@link Device}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
	private String id;
	private Integer state;
	private List<String> outlets;
	private String name;
	@JsonProperty("Connected")
	private Boolean connected;

	public Group() {
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
	 * Retrieves {@link #outlets}
	 *
	 * @return value of {@link #outlets}
	 */
	public List<String> getOutlets() {
		return outlets;
	}

	/**
	 * Sets {@link #outlets} value
	 *
	 * @param outlets new value of {@link #outlets}
	 */
	public void setOutlets(List<String> outlets) {
		this.outlets = outlets;
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
}
