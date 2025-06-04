package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the network settings of the SurgeX Defender/Vertical Series.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSetting {
	private List<EthInterface> ethInterfaces;
	private String hostname;
	private Ntp ntp;

	public NetworkSetting() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #ethInterfaces}
	 *
	 * @return value of {@link #ethInterfaces}
	 */
	public List<EthInterface> getEthInterfaces() {
		return ethInterfaces;
	}

	/**
	 * Sets {@link #ethInterfaces} value
	 *
	 * @param ethInterfaces new value of {@link #ethInterfaces}
	 */
	public void setEthInterfaces(List<EthInterface> ethInterfaces) {
		this.ethInterfaces = ethInterfaces;
	}

	/**
	 * Retrieves {@link #hostname}
	 *
	 * @return value of {@link #hostname}
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets {@link #hostname} value
	 *
	 * @param hostname new value of {@link #hostname}
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Retrieves {@link #ntp}
	 *
	 * @return value of {@link #ntp}
	 */
	public Ntp getNtp() {
		return ntp;
	}
}
