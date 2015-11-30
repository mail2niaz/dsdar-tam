package com.dsdar.thosearoundme.util;

import com.dsdar.thosearoundme.dao.WifiDatabaseHandler;
import com.dsdar.thosearoundme.dto.Wifi;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Utility class for ThoseAroundMe
 * 
 * @author Senthil Kumaran
 */
public class WifiUtil {
	Context itsContext = null;
	WifiDatabaseHandler itsWifiDatabaseHandler;

	public WifiUtil(Context theContext,
			WifiDatabaseHandler theWifiDatabaseHandler) {
		itsContext = theContext;
		itsWifiDatabaseHandler = theWifiDatabaseHandler;
	}

	/**
	 * Method to return a valid location which is associated to Wi-Fi, if exists
	 * 
	 * @return aWifi
	 */
	public Wifi getAssociatedWifi() {
		Wifi aAssociatedWifi = null;

		boolean hasConnectedWifi = hasConnectedWifi();
		if (hasConnectedWifi) {
			WifiInfo aWifiInfo = getWifiInfo();

			String aBssid = "";
			if (aWifiInfo.getBSSID() != null) {
				aBssid = aWifiInfo.getBSSID();
				Wifi aWifiLocation = itsWifiDatabaseHandler
						.getAssociatedWifi(aBssid);
				if (aWifiLocation != null) {
					aAssociatedWifi = aWifiLocation;
				}
			}
		}

		return aAssociatedWifi;
	}

	/**
	 * Method to check whether there is any connected Wi-Fi available at a
	 * particular point
	 * 
	 * @return
	 */
	public boolean hasConnectedWifi() {
		boolean hasConnectedWifi = false;

		ConnectivityManager aConnectivityManager = (ConnectivityManager) itsContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		hasConnectedWifi = aConnectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnected();

		return hasConnectedWifi;
	}

	/**
	 * Method to get the connected Wi-Fi details
	 * 
	 * @return aWifiInfo
	 */
	public WifiInfo getWifiInfo() {
		WifiManager aWifiManager = (WifiManager) itsContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo aWifiInfo = aWifiManager.getConnectionInfo();

		return aWifiInfo;
	}

	/**
	 * Method to update a better valid fix to a Wi-Fi
	 * 
	 * @param theValidLocation
	 * @param theWifiBssid
	 */
	public void updateValidCoordinates(Location theValidLocation,
			String theWifiBssid) {
		Wifi aWifiLocation = new Wifi();

		aWifiLocation.setBssid(theWifiBssid);
		aWifiLocation.setLat(String.valueOf(theValidLocation.getLatitude()));
		aWifiLocation.setLng(String.valueOf(theValidLocation.getLongitude()));
		aWifiLocation
				.setAccuracy(String.valueOf(theValidLocation.getAccuracy()));

		itsWifiDatabaseHandler.updateLocation(aWifiLocation);
	}

	/**
	 * Method to associate valid coordinates to a Wi-Fi
	 * 
	 * @param theValidLocation
	 * @param theBssid
	 * @param theWifiName
	 */
	public void associateValidCoordinates(Location theValidLocation,
			String theBssid, String theWifiName) {
		Wifi aWifiLocation = new Wifi();

		aWifiLocation.setBssid(theBssid);
		aWifiLocation.setName(theWifiName);
		aWifiLocation.setLat(String.valueOf(theValidLocation.getLatitude()));
		aWifiLocation.setLng(String.valueOf(theValidLocation.getLongitude()));
		aWifiLocation
				.setAccuracy(String.valueOf(theValidLocation.getAccuracy()));

		itsWifiDatabaseHandler.insertLocation(aWifiLocation);
	}
}