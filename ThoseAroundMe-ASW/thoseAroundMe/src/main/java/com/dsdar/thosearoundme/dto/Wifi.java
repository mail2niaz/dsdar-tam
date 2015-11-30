package com.dsdar.thosearoundme.dto;

public class Wifi {
	private int locId;
	private String Bssid;
	private String Name;
	private String lat;
	private String lng;
	private String Accuracy;

	public int getLocId() {
		return locId;
	}

	public void setLocId(int locId) {
		this.locId = locId;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getBssid() {
		return Bssid;
	}

	public void setBssid(String bssid) {
		Bssid = bssid;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getAccuracy() {
		return Accuracy;
	}

	public void setAccuracy(String accuracy) {
		Accuracy = accuracy;
	}
}