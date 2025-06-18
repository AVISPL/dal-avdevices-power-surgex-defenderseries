package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the ethernet interface of a {@link NetworkSetting}.
 * <p>
 * This class is typically deserialized from a JSON response.
 * Unknown properties in the JSON will be ignored.
 * </p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EthInterface {
	private String mac;
	@JsonProperty("ifname")
	private String ifName;
	private List<String> dns;
	@JsonProperty("addr")
	private String ipAddress;
	private boolean dhcp;
	private String mask;
	@JsonProperty("gw")
	private String gateway;

	public EthInterface() {
		/*
		 * This constructor is used to instantiate the object when mapping JSON data.
		 */
	}

	/**
	 * Retrieves {@link #mac}
	 *
	 * @return value of {@link #mac}
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Sets {@link #mac} value
	 *
	 * @param mac new value of {@link #mac}
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Retrieves {@link #ifName}
	 *
	 * @return value of {@link #ifName}
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * Sets {@link #ifName} value
	 *
	 * @param ifName new value of {@link #ifName}
	 */
	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	/**
	 * Retrieves {@link #dns}
	 *
	 * @return value of {@link #dns}
	 */
	public List<String> getDns() {
		return dns;
	}

	/**
	 * Sets {@link #dns} value
	 *
	 * @param dns new value of {@link #dns}
	 */
	public void setDns(List<String> dns) {
		this.dns = dns;
	}

	/**
	 * Retrieves {@link #ipAddress}
	 *
	 * @return value of {@link #ipAddress}
	 */
	public String getIPAddress() {
		return ipAddress;
	}

	/**
	 * Sets {@link #ipAddress} value
	 *
	 * @param ipAddress new value of {@link #ipAddress}
	 */
	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Retrieves {@link #dhcp}
	 *
	 * @return value of {@link #dhcp}
	 */
	public boolean isDhcp() {
		return dhcp;
	}

	/**
	 * Sets {@link #dhcp} value
	 *
	 * @param dhcp new value of {@link #dhcp}
	 */
	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}

	/**
	 * Retrieves {@link #mask}
	 *
	 * @return value of {@link #mask}
	 */
	public String getMask() {
		return mask;
	}

	/**
	 * Sets {@link #mask} value
	 *
	 * @param mask new value of {@link #mask}
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/**
	 * Retrieves {@link #gateway}
	 *
	 * @return value of {@link #gateway}
	 */
	public String getGateway() {
		return gateway;
	}

	/**
	 * Sets {@link #gateway} value
	 *
	 * @param gateway new value of {@link #gateway}
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
}
