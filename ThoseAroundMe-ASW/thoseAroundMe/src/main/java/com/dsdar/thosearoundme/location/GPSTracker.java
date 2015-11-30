package com.dsdar.thosearoundme.location;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dsdar.thosearoundme.dto.LocationDto;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    Location oldLocation = null, newLocation = null;
    List<LocationDto> locationDtos = new ArrayList<LocationDto>();


    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled && false) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public void disconnectLocationClient() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location theLocation) {
        Log.d("LocationUpdates", "onLocationChanged...");
        if (oldLocation == null) {
            oldLocation = theLocation;
        }
        if ((theLocation.getTime() - oldLocation.getTime()) <= 10) {
            Log.d("LocationUpdates", "< 10 returning....");
            return;
        }
        newLocation = theLocation;
        if (!theLocation.hasAccuracy()) {
            Log.d("LocationUpdates", "No Accuracy...");
            return;
        } else if (theLocation.getAccuracy() > 100) {
            Log.d("LocationUpdates", "Poor Accuracy...");
            return;
        }
        Log.d("LocationUpdates", "Provider==" + theLocation.getProvider() +
                ",Location changed to " + theLocation.getLatitude() + ","
                + theLocation.getLongitude() + ", theLocation.getAccuracy()=" + theLocation.getAccuracy());

        // Updating SharedPreferences
        SharedPreferences aPreference = getApplicationContext()
                .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                        Context.MODE_PRIVATE);
        // Updating users location to preference for later use
        SharedPreferences.Editor aPrefEditor = aPreference.edit();
        aPrefEditor.putString(MyAppConstants.LAT,
                "" + theLocation.getLatitude());
        aPrefEditor.putString(MyAppConstants.LNG,
                "" + theLocation.getLongitude());
        aPrefEditor.putString(MyAppConstants.DIRECTION_DEGREE, "" + 0);
        aPrefEditor.putString(MyAppConstants.ACCURACY,
                "" + theLocation.getAccuracy());
        Log.d("LU",
                "Speed before = " + theLocation.getSpeed()
                        + ",speed after conversion="
                        + Math.round(theLocation.getSpeed()));
        aPrefEditor.putString(MyAppConstants.SPEED,
                "" + Math.round(theLocation.getSpeed()));
        aPrefEditor.commit();

        LatLng latLng = new LatLng(theLocation.getLatitude(),
                theLocation.getLongitude());

        // Adding to List which will be sent to Server based on batch frequency
        // interva;

        LocationDto dto = new LocationDto();
        dto.setAccuracy(theLocation.getAccuracy());
        dto.setBearing(theLocation.getBearing());
        dto.setDevicetime(theLocation.getTime());
        dto.setLat(theLocation.getLatitude());
        dto.setLng(theLocation.getLongitude());
        locationDtos.add(dto);

        // Service to update MY location
        sendMessage("loc changed broadcast rexr..."
                + new Date(theLocation.getTime()));

    }

    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}