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
package com.dsdar.thosearoundme.location;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.activity.SplashActivity;
import com.dsdar.thosearoundme.dto.LocationDto;
import com.dsdar.thosearoundme.util.LocationUtil;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to get users current location and update to server
 *
 * @author senthil_kumaran
 */
public class LocationUpdates extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;// 5
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 5;// 5
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;

    private static final int SMALLEST_DISPLACEMENT_IN_METER = 1;

    private LocationRequest itsLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    //    private LocationClient itsLocationClient;
    double latitude, longitude, accuracy;
    private Runnable mHandlerTask = null;
    private Handler itsHandler = null;
    Location oldLocation = null, newLocation = null;
    int prevCnt = -1, newCnt = -1;
    boolean isFirstTime = true;
    // define sound URI, the sound to be played when there's a notification
    Uri soundUri = RingtoneManager
            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    // Context context;
    List<LocationDto> locationDtos = new ArrayList<LocationDto>();
    int batch = -1;

    // Send an Intent with an action named "my-event".
    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    public LocationUpdates() {
        // super("Downloader");
        // If handler already exists, remove it

    }

    protected void updateServer() {
        // TODO Auto-generated method stub
//        if ((oldLocation != null) && (newLocation != null)) {
//            if ((oldLocation.getLatitude() != newLocation.getLatitude())
//                    || (oldLocation.getLongitude() != newLocation
//                    .getLongitude())) {
        if (locationDtos != null && locationDtos.size() > 0) {

            oldLocation = newLocation;
            MyAppConstants.LAT_USER = newLocation.getLatitude();
            MyAppConstants.LNG_USER = newLocation.getLongitude();
            Log.d("LocationUpdates", "Location batch size.. ="
                    + locationDtos.size());
            newCnt = LocationUtil.updateLocationToServer(
                    getApplicationContext(), locationDtos);
            Log.d("LocationUpdates", "newCnt.. ="
                    + newCnt);
            if (newCnt != -2) {
                locationDtos.clear();
            }

            if (newCnt > 0) {
                if (isFirstTime) {
                    prevCnt = newCnt;
                    isFirstTime = false;
                    initNotification(newCnt);
                } else {
                    if (prevCnt != newCnt) {
                        initNotification(newCnt);
                    }
                }
                prevCnt = newCnt;
            }
        }

//            } else {
//                Log.d("LocationUpdates", "Location not changed ...");
//            }
//        } else {
//            Log.d("LocationUpdates", "Location are null ...");
//        }
    }

    void initNotification(int cnt) {
        Log.d("TAG", "cnt...................=" + cnt);
        // prepare intent which is triggered if the
        // notification is selected

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancelAll();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setContentTitle("Those Around Me").setSound(soundUri)
                .setAutoCancel(true)
                .setContentText("You received " + cnt + " Invitation");
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.d("LocationUpdates", "LocationUpdates started...");
        setLocationListener();
//        itsLocationClient = new LocationClient(getApplicationContext(), this,
//                this);
//        itsLocationClient.connect();
        buildGoogleApiClient();

        itsHandler = new Handler();
        if (mHandlerTask != null)
            itsHandler.removeCallbacks(mHandlerTask);
        // context = getApplicationContext();
        // Getting refresh batch interval to update server
        SharedPreferences aPreference = getApplicationContext()
                .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                        Context.MODE_PRIVATE);
        batch = aPreference.getInt(MyAppConstants.BATCH_INTERVAL, 120);
        Log.d("LocationUpdates", "batch value =  " + batch);
        // Toast.makeText(context, "batch value =  " + batch,
        // Toast.LENGTH_SHORT)
        // .show();
        // Create a new handler to run for every n minutes
        mHandlerTask = new Runnable() {
            @Override
            public void run() {
                // Get and set team members location in map
                Thread aThread = new Thread() {
                    public void run() {
                        updateServer();
                    }

                    ;
                };
                aThread.start();
                itsHandler.postDelayed(mHandlerTask, batch * 1000);
            }
        };
        mHandlerTask.run();

        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("LocationUpdates", "LocationUpdates onDestroy...");
//        itsLocationClient.disconnect();
        mGoogleApiClient.disconnect();
        if (mHandlerTask != null) {
            itsHandler.removeCallbacks(mHandlerTask);
        }
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to set parameters for location listener
     */

    public void setLocationListener() {
        Log.d("LocationUpdates", "setLocationListener started...");
        itsLocationRequest = LocationRequest.create();
        // Using high accuracy
        itsLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Update interval for every n milliseconds
        itsLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        itsLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        itsLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_IN_METER);
    }

    /**
     * Method to stop or remove location updates
     */
    public void disconnectLocationClient() {
//        if (itsLocationClient.isConnected()) {
//            itsLocationClient.removeLocationUpdates(this);
//        }
//        itsLocationClient.disconnect();
        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.(this);
        }
        mGoogleApiClient.disconnect();
    }

    /**
     * Method will be called during location updates
     */
    @Override
    public void onConnected(Bundle theConnectionHint) {
        Log.i(this.getClass().getName(), "Started to get location updates");
//        itsLocationClient.requestLocationUpdates(itsLocationRequest, this);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, itsLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //    @Override
    public void onDisconnected() {
        Log.i(this.getClass().getName(), "Stopped to receive location updates");
    }

    @Override
    public void onConnectionFailed(ConnectionResult theResult) {
        Log.e(this.getClass().getName(),
                "Connection error when retrieving location updates");
    }

    /**
     * Method will be called when there is a new location update. This new
     * location will be updated to server
     *
     * @param theLocation
     */
    @Override
    public void onLocationChanged(final Location theLocation) {
//        Toast.makeText(
//                getApplicationContext(),
//                "Location changed to " + theLocation.getLatitude() + ","
//                        + theLocation.getLongitude(), Toast.LENGTH_SHORT)
//                .show();
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

        // Update MY location
        // teamViewActivity.itsTeamMapFragment.hmapLatLng.put("ME", new LatLng(
        // theLocation.getLatitude(), theLocation.getLongitude()));
        // teamViewActivity.itsTeamMapFragment.showMyMarkerAndAccuracyCircle("locationupdate");
        // Update the new location to server
        // Thread aThread = new Thread() {
        // public void run() {
        // LocationUtil.updateLocationToServer(getApplicationContext(),
        // theLocation);
        // };
        // };
        // aThread.start();
    }
}