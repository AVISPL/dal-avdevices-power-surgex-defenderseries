package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the current status of the SurgeX Defender/Vertical Series..
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentStatus {
	private String model;
	@JsonProperty("MAC")
	private List<String> mac;
	private String temperatureUnits;
	private String time;
	private String firmware;
	private List<Device> devices;
	private String timeZone;

	public CurrentStatus() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #model}
	 *
	 * @return value of {@link #model}
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets {@link #model} value
	 *
	 * @param model new value of {@link #model}
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Retrieves {@link #mac}
	 *
	 * @return value of {@link #mac}
	 */
	public List<String> getMac() {
		return mac;
	}

	/**
	 * Sets {@link #mac} value
	 *
	 * @param mac new value of {@link #mac}
	 */
	public void setMac(List<String> mac) {
		this.mac = mac;
	}

	/**
	 * Retrieves {@link #temperatureUnits}
	 *
	 * @return value of {@link #temperatureUnits}
	 */
	public String getTemperatureUnits() {
		return temperatureUnits;
	}

	/**
	 * Sets {@link #temperatureUnits} value
	 *
	 * @param temperatureUnits new value of {@link #temperatureUnits}
	 */
	public void setTemperatureUnits(String temperatureUnits) {
		this.temperatureUnits = temperatureUnits;
	}

	/**
	 * Retrieves {@link #time}
	 *
	 * @return value of {@link #time}
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Sets {@link #time} value
	 *
	 * @param time new value of {@link #time}
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * Retrieves {@link #firmware}
	 *
	 * @return value of {@link #firmware}
	 */
	public String getFirmware() {
		return firmware;
	}

	/**
	 * Sets {@link #firmware} value
	 *
	 * @param firmware new value of {@link #firmware}
	 */
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	/**
	 * Retrieves {@link #devices}
	 *
	 * @return value of {@link #devices}
	 */
	public List<Device> getDevices() {
		return devices;
	}

	/**
	 * Sets {@link #devices} value
	 *
	 * @param devices new value of {@link #devices}
	 */
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	/**
	 * Retrieves {@link #timeZone}
	 *
	 * @return value of {@link #timeZone}
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets {@link #timeZone} value
	 *
	 * @param timeZone new value of {@link #timeZone}
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
}
