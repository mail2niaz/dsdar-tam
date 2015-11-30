package com.dsdar.thosearoundme.dto;

import com.google.android.gms.maps.model.LatLng;

public class MarkerDto {

	private LatLng markerLatLng;
	private String markerCode;
	private String markerCodeDesc;
	private String markerDesc;
	private String markerName;



	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}



	public LatLng getMarkerLatLng() {
		return markerLatLng;
	}

	public void setMarkerLatLng(LatLng markerLatLng) {
		this.markerLatLng = markerLatLng;
	}

	public String getMarkerCode() {
		return markerCode;
	}

	public void setMarkerCode(String markerCode) {
		this.markerCode = markerCode;
	}

	public String getMarkerCodeDesc() {
		return markerCodeDesc;
	}

	public void setMarkerCodeDesc(String markerCodeDesc) {
		this.markerCodeDesc = markerCodeDesc;
	}

	public String getMarkerDesc() {
		return markerDesc;
	}

	public void setMarkerDesc(String markerDesc) {
		this.markerDesc = markerDesc;
	}

}