package com.dsdar.thosearoundme.dto;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LocationDto {

	private long devicetime;
	private double lat;
	private double lng;
	private float speed;
	private float accuracy;
	private float bearing;

	public long getDevicetime() {
		return devicetime;
	}

	public void setDevicetime(long devicetime) {
		this.devicetime = devicetime;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("devicetime", devicetime);
			obj.put("lat", lat);
			obj.put("lng", lng);
			obj.put("speed", speed);
			obj.put("accuracy", accuracy);
			obj.put("bearing", bearing);
		} catch (JSONException e) {
			Log.d("LUPD","DefaultListItem.toString JSONException: " + e.getMessage());
		}
		return obj;
	}

}