/**
 * Copyright (c) 2014 Dsdar Inc.
 * <p/>
 * All rights reserved. For use only with Dsdar Inc.
 * This software is the confidential and proprietary information of
 * Dsdar Inc, ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dsdar Inc.
 */
package com.dsdar.thosearoundme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.util.Log;

import com.dsdar.thosearoundme.dto.LocationDto;
import com.dsdar.thosearoundme.dto.Wifi;
import com.dsdar.thosearoundme.location.LocationFinder;
import com.dsdar.thosearoundme.location.LocationFinder.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Utility class for location
 *
 * @author david_reynolds
 */
public class LocationUtil {
    public static boolean readCurrentLocation(Context theContext,
                                              LocationResult theLocationResult) {
        return LocationFinder.getInstance().readCurrentLocation(theContext,
                theLocationResult);
    }

    /**
     * Method to calculate distance between two location
     *
     * @param theLocation1
     * @param theLocation2
     * @return distance
     */
    public static double getDistance(LatLng theLocation1, LatLng theLocation2) {
        double lat1 = theLocation1.latitude;
        double lon1 = theLocation1.longitude;
        double lat2 = theLocation2.latitude;
        double lon2 = theLocation2.longitude;
        double dLon = Math.toRadians(lon2 - lon1);
        double dLat = Math.toRadians(lat2 - lat1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return c * 6366000;
    }

    /**
     * Method to check whether network provider is enabled in phone or not
     *
     * @param theContext
     * @return status
     */
    public static boolean isNetworkEnabled(Context theContext) {
        boolean isNetworkEnabled = false;

        LocationManager aLocationManager = (LocationManager) theContext
                .getSystemService(Context.LOCATION_SERVICE);
        isNetworkEnabled = aLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isNetworkEnabled;
    }

    /**
     * Method to check whether GPS provider is enabled in phone or not
     *
     * @param theContext
     * @return status
     */
    public static boolean isGpsEnabled(Context theContext) {
        boolean isGpsEnabled = false;

        LocationManager aLocationManager = (LocationManager) theContext
                .getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = aLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isGpsEnabled;
    }

    /**
     * Method to get associated wifi location (if exists) and then compare it
     * with a new location to find a best fix
     *
     * @param theWifiUtil
     * @param theNewFix
     * @return aValidLocation
     */
    public static Location associateAndGetBestFix(WifiUtil theWifiUtil,
                                                  Location theNewFix) {
        Location aValidLocation = new Location(theNewFix);
        // get associated wifi location if exists
        Wifi aAssociatedWifi = theWifiUtil.getAssociatedWifi();

        // If already a valid location is associated to wifi
        if (aAssociatedWifi != null) {
            Float aWifiAccuracy = Float.parseFloat(aAssociatedWifi
                    .getAccuracy());
            // If the new fix has accuracy, compare the accuracy of the new fix
            // with the existing wifi location
            if (theNewFix.hasAccuracy()) {
                Float aNewFixAccuracy = theNewFix.getAccuracy();

                if (aWifiAccuracy <= aNewFixAccuracy) {
                    // the location already associated to the wifi is accurate
                    // when compared to the new fix
                    aValidLocation.setLatitude(Double
                            .parseDouble(aAssociatedWifi.getLat()));
                    aValidLocation.setLongitude(Double
                            .parseDouble(aAssociatedWifi.getLng()));
                    aValidLocation.setAccuracy(aWifiAccuracy);
                } else {
                    // the new fix is more accurate, so update it to the
                    // connected wifi
                    aValidLocation.setLatitude(theNewFix.getLatitude());
                    aValidLocation.setLongitude(theNewFix.getLongitude());
                    aValidLocation.setAccuracy(aNewFixAccuracy);

                    theWifiUtil.updateValidCoordinates(aValidLocation,
                            aAssociatedWifi.getBssid());
                }
            }
            // The new fix has no accuracy parameter, so better to use the
            // associate wifi value itself
            else {
                aValidLocation.setLatitude(Double.parseDouble(aAssociatedWifi
                        .getLat()));
                aValidLocation.setLongitude(Double.parseDouble(aAssociatedWifi
                        .getLng()));
                aValidLocation.setAccuracy(aWifiAccuracy);
            }
        }
        // No valid location is currently associated to the connected wifi
        else {
            // check the new location and if it is more accurate associate it to
            // the wifi
            if ((theNewFix.hasAccuracy()) && (theNewFix.getAccuracy() < 500)) {
                WifiInfo aWifiInfo = theWifiUtil.getWifiInfo();

                if (aWifiInfo.getBSSID() != null) {
                    String aBssid = aWifiInfo.getBSSID();
                    String aWifiName = aWifiInfo.getSSID();
                    aValidLocation.setLatitude(theNewFix.getLatitude());
                    aValidLocation.setLongitude(theNewFix.getLongitude());
                    aValidLocation.setAccuracy(theNewFix.getAccuracy());
                    theWifiUtil.associateValidCoordinates(aValidLocation,
                            aBssid, aWifiName);
                }
            }
        }
        return aValidLocation;
    }

    public static int updateLocationToServer(Context theContext,
                                             List<LocationDto> locationDtos) {
        int cnt = 0;
        if (locationDtos != null && locationDtos.size() > 0) {
            JSONObject aRequestJson = new JSONObject();
            try {
                // Get users memberId and API URL
                SharedPreferences aPreference = theContext
                        .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                                Context.MODE_PRIVATE);
                URL aUrl = new URL(aPreference.getString(
                        MyAppConstants.SERVER_URL, "")
                        + MyAppConstants.UPDATE_LOCATION);
                // Constructing request JSON
                aRequestJson.put(MyAppConstants.MEMBER_ID,
                        aPreference.getString(MyAppConstants.MEMBER_ID, ""));
                aRequestJson.put(MyAppConstants.PHONE,
                        aPreference.getString(MyAppConstants.PHONE, ""));

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < locationDtos.size(); i++) {
                    jsonArray.put(locationDtos.get(i).getJSONObject());
                    Log.d("", "Updating server::@" + new Date() + "  with value::" + locationDtos.get(i).getJSONObject().toString());

                }

                aRequestJson.put(MyAppConstants.LOCATIONS, jsonArray);

                // if (true) {
                // Log.d("LUPD", "LUPD=" + aRequestJson.toString());
                // return 0;
                // }
                // aRequestJson.put(MyAppConstants.LAT,
                // locationDtos.getLatitude());
                // aRequestJson
                // .put(MyAppConstants.LNG, locationDtos.getLongitude());
                // aRequestJson.put(MyAppConstants.DIRECTION_DEGREE,
                // locationDtos.getBearing());
                // aRequestJson.put(MyAppConstants.ACCURACY,
                // locationDtos.getAccuracy());
                // double speedDbl = locationDtos.getSpeed();
                // Log.d("LocationUtil", "actual speed=" + speedDbl);
                // if you need to convert it to km/h use this:
                // int speed = (int) ((theLocation.getSpeed() * 3600) / 1000);
                // if you need to convert it to mph use this:
                // int speed = (int) (locationDtos.getSpeed() * 2.2369);
                // aRequestJson.put(MyAppConstants.SPEED, speed);

                Log.i(theContext.getClass().getName(),
                        "Request for users location update: " + aRequestJson);
                HttpURLConnection aHttpConnection = (HttpURLConnection) aUrl
                        .openConnection();
                aHttpConnection.setRequestMethod(MyAppConstants.API_POST_TYPE);
                aHttpConnection.setRequestProperty(
                        MyAppConstants.API_CONTENT_TYPE,
                        MyAppConstants.API_JSON_CONTENT_TYPE);
                aHttpConnection.setDoOutput(true);

                PrintStream aPrintStream = new PrintStream(
                        aHttpConnection.getOutputStream());
                aPrintStream.print(aRequestJson);
                aPrintStream.close();

                BufferedReader aReader = new BufferedReader(
                        new InputStreamReader(aHttpConnection.getInputStream()));
                String aResponseJson = aReader.readLine();
                aReader.close();
                Log.i(theContext.getClass().getName(),
                        "Response for users location update: " + aResponseJson);
                JSONObject aJson = new JSONObject(aResponseJson);
                String result = aJson.getString("result");
                String[] parts = result.split("~");
                cnt = Integer.parseInt(parts[0]); // 004
                int teamCount = Integer.parseInt(parts[1]); // 034556
                int followersCount = Integer.parseInt(parts[2]);
                // int cnt = aJson.getInt("result");
                Log.d("TAG", "cnt...................=" + cnt);
                MyAppConstants.TEAM_CNT = teamCount;
                MyAppConstants.INVITATION_CNT = cnt;
                MyAppConstants.FOLLOWERS_CNT = followersCount;

                return cnt;

            } catch (JSONException theJSONException) {
                theJSONException.printStackTrace();
                Log.e(theContext.getClass().getName(),
                        "JSONException when updating users location to server");
                cnt = -2;
            } catch (MalformedURLException theMalformedURLException) {
                theMalformedURLException.printStackTrace();
                Log.e(theContext.getClass().getName(),
                        "MalformedURLException when updating users location to server");
                cnt = -2;
            } catch (IOException theIOException) {
                theIOException.printStackTrace();
                Log.e(theContext.getClass().getName(),
                        "IOException when updating users location to server");
                cnt = -2;
            }

        } else {
            Log.d(theContext.getClass().getName(),
                    "Location is null or size 0.......................");
        }

        return cnt;
    }
}