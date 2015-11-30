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
package com.dsdar.thosearoundme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.dto.MarkerDto;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Class to get all member information of a team and load them in map
 *
 * @author senthil_kumaran
 */
public class TeamMapFragment {
    public static double markerlat, markerlng = 0;
    public static String itsSelectedTeamId = "";
    public static String isStickyTeam = "";
    public static String isRecordStatus = "";
    public static String itsSelectedTeamName = "";
    private Handler itsHandler = null, itsHandler1 = null;
    private static int itsTabPosition;
    private static Context itsContext;
    private Runnable mHandlerTask = null, mHandlerTask1 = null;
    private static GoogleMap itsGoogleMap;
    // private static Builder itsLocationBuilder;
    private static JSONArray itsUsersTeamArray;
    private static JSONObject itsSelectedTeamJson;
    private static TextView itsNormalMapView, itsHybridMapView,
            itsSatelliteMapView;
    public HashMap<String, LatLng> hmapLatLng = new HashMap<String, LatLng>();
    public HashMap<String, Marker> hmapMemberMarkerPrev = new HashMap<String, Marker>();
    public HashMap<String, LatLng> hmapMemberMarkerPrevLatLng = new HashMap<String, LatLng>();
    public HashMap<String, Double> hmapMemberMarkerPrevRotation = new HashMap<String, Double>();
    public HashMap<String, Circle> hmapMemberCirclePrev = new HashMap<String, Circle>();
    public HashMap<String, Marker> hmapMemberMarkerCurr = new HashMap<String, Marker>();
    public HashMap<String, LatLng> stickyhmapLatLng = new HashMap<String, LatLng>();
    public static ProgressDialog itsProgressDialog;
    // private final static int REFRESH_INTERVAL = 1000 * 60 * 5;
//    private final static int REFRESH_INTERVAL = 1000 * 5;
    private final static int REFRESH_MY_LOC_INTERVAL = 1000 * 5;
    // double latitude, longitude, accuracy;

    TeamViewActivity teamViewActivity;
    SharedPreferences aSharedPreference;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    String start_time = "", end_time = "";
    public String isMarker = "";

    // Time and Accuracy
    boolean booleanAccuracy, booleanTime;
    MarkerDto markerDto = null;

    LatLng startPosition = null, startPositionCircle = null;

    public TeamMapFragment(Context theContext, int position,
                           GoogleMap theGoogleMap, TextView theNormalMapView,
                           TextView theHybridMapView, TextView theSatelliteMapView,
                           JSONArray theTeamArray, String theLoginUserId) {
        itsContext = theContext;
        itsGoogleMap = theGoogleMap;
        itsUsersTeamArray = theTeamArray;
        itsNormalMapView = theNormalMapView;
        itsHybridMapView = theHybridMapView;
        itsSatelliteMapView = theSatelliteMapView;
        itsTabPosition = position;
        teamViewActivity = (TeamViewActivity) theContext;
        itsHandler = new Handler();
        itsHandler1 = new Handler();
        // Button aButton = (Button) ((FragmentActivity) theContext)
        // .findViewById(R.id.tvMainPageShowAllMembers);
        // aButton.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // displayAllMarkers();
        // }
        // });
        aSharedPreference = itsContext.getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, itsContext.MODE_PRIVATE);

        resetMapPreference();
        // try {
        // itsSelectedTeamJson = itsUsersTeamArray
        // .getJSONObject(itsTabPosition);
        // isStickyTeam = itsSelectedTeamJson.getString("isSticky");
        // itsSelectedTeamId = itsSelectedTeamJson
        // .getString(TeamBuilder.TEAMID);
        // } catch (Exception exception) {
        //
        // }
        // if (isStickyTeam.equals("true")) {
        // // if (TeamViewActivity.recordStatus.equals("true")) {
        // getRecordingInfo();
        //
        // // }
        //
        // } else {
        // if (mHandlerTask2 != null)
        // itsHandler2.removeCallbacks(mHandlerTask2);
        // }

        // getMarkers();
    }

    /**
     * Method to display all members of a team in Google Maps
     */

    /**
     * Method to set Google maps preference
     */
    private void resetMapPreference() {
        markerDto = null;
        itsGoogleMap.clear();
        itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        itsNormalMapView.setBackgroundResource(R.drawable.map_button_active);
        itsHybridMapView
                .setBackgroundResource(R.drawable.hybrid_button_inactive);
        itsSatelliteMapView
                .setBackgroundResource(R.drawable.satellite_button_inactive);

        // Adding ME location
        try {
            SharedPreferences aSharedPreference = itsContext
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            itsContext.MODE_PRIVATE);
            String lat = aSharedPreference.getString(MyAppConstants.LAT, "");
            String lng = aSharedPreference.getString(MyAppConstants.LNG, "");
            double dLat = Double.parseDouble(lat);
            double dLng = Double.parseDouble(lng);
            hmapLatLng.put("ME", new LatLng(dLat, dLng));

        } catch (Exception e) {
            Log.d("TMF", "Error=" + e);
        }
    }

    /**
     * Method to get users team member information and load their updated
     * location in Google maps
     */
    void getAndSetMyTeamMembersLoc() {

        Log.d("TeamMapFragment", "calling getAndSetMyTeamMembersLoc & Date= "
                + new Date());
        try {
            // Get selected teamID to retrieve their member information
            itsSelectedTeamJson = itsUsersTeamArray
                    .getJSONObject(itsTabPosition);
            itsSelectedTeamId = itsSelectedTeamJson
                    .getString(TeamBuilder.TEAMID);
            itsSelectedTeamName = itsSelectedTeamJson.getString("name");
            isStickyTeam = itsSelectedTeamJson.getString("isSticky");
            aSharedPreference = itsContext.getSharedPreferences(
                    MyAppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
            String aMemberId = aSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");
            // Constructing request JSON
            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.TEAM_ID, itsSelectedTeamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
                    MyAppConstants.GET_TEAM_MEMBERS,
                    MyAppConstants.API_POST_TYPE);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theMemberInfo) {
                    if (theMemberInfo != null) {
                        Log.d("TMF", "iterateAndDrawMarkers::theMemberInfo::" +
                                theMemberInfo);
                        iterateAndDrawMarkers(theMemberInfo);
                    } else {
                        // Toast.makeText(itsContext,
                        // MyAppConstants.CONNECTION_ERROR,
                        // Toast.LENGTH_LONG).show();
                    }
                }
            });

            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSONException when trying to construct request json for getMyTeamMembers webservices");
        }
    }

    public void getMarkers(final String fromPage) {
        MyAppConstants.resetMemberRandomColor();
//        itsGoogleMap.clear();

        Log.d("TeamMapFragment", "calling getMarkers & Date= "
                + new Date());
        try {
            // Get selected teamID to retrieve their member information
            itsSelectedTeamJson = itsUsersTeamArray
                    .getJSONObject(itsTabPosition);
            itsSelectedTeamId = itsSelectedTeamJson
                    .getString(TeamBuilder.TEAMID);
            itsSelectedTeamName = itsSelectedTeamJson.getString("name");
            isStickyTeam = itsSelectedTeamJson.getString("isSticky");
            isRecordStatus = itsSelectedTeamJson.getString("isRecording");
            aSharedPreference = itsContext.getSharedPreferences(
                    MyAppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
            String aMemberId = aSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");

            Log.d("TMF", "getMarkers ...aMemberId=" + aMemberId);
            // Constructing request JSON
            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.TEAM_ID, itsSelectedTeamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
                    MyAppConstants.GET_MARKERS, MyAppConstants.API_POST_TYPE);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theMarkerInfo) {
                    // if (theMarkerInfo != null) {
                    Log.d("TMF", theMarkerInfo);
                    try {
                        JSONObject aSTeamMarkerJson = new JSONObject(
                                theMarkerInfo);
                        // String[] result = aSTeamMarkerJson.getString(
                        // MyAppConstants.RESULT).split("~");
                        //
                        // String teamList = result[0];
                        // String sticky = result[1];

                        String isStickyOwner = aSTeamMarkerJson
                                .getString(MyAppConstants.MESSAGE);
                        if (isStickyOwner.equals("true")) {
                            MyAppConstants.ISOWNER = true;
                        }
                        if (aSTeamMarkerJson.has(MyAppConstants.RESULT)) {
                            JSONArray itsStickyMarkerArrayJson = aSTeamMarkerJson
                                    .getJSONArray(MyAppConstants.RESULT);
                            // JSONArray jsonArray = new JSONArray(teamList);
                            if (!itsStickyMarkerArrayJson.equals("[]")
                                    || itsStickyMarkerArrayJson != null) {

                                markerlat = Double.valueOf(itsStickyMarkerArrayJson
                                        .getJSONObject(0).getString("lat"));
                                markerlng = Double.valueOf(itsStickyMarkerArrayJson
                                        .getJSONObject(0).getString("lng"));

                                isMarker = "true";

                                if (MyAppConstants.hmapMarkerId.size() == 0
                                        || !MyAppConstants.hmapMarkerId
                                        .containsKey(itsStickyMarkerArrayJson
                                                .getJSONObject(0)
                                                .getString("teamId"))) {
                                    MyAppConstants.hmapMarkerId.put(
                                            itsStickyMarkerArrayJson.getJSONObject(
                                                    0).getString("teamId"),
                                            itsStickyMarkerArrayJson.getJSONObject(
                                                    0).getString("markerId"));
                                }

                                MyAppConstants.LAT_MARKER = markerlat;
                                MyAppConstants.LNG_MARKER = markerlng;
                                MyAppConstants.NAME_MARKER = null;
                                // Adding input to markerDto
                                LatLng latLng = new LatLng(markerlat, markerlng);
                                markerDto = new MarkerDto();
                                markerDto.setMarkerLatLng(latLng);

                                if (itsStickyMarkerArrayJson.getJSONObject(0).has(
                                        "markerCode"))
                                    markerDto
                                            .setMarkerCode(itsStickyMarkerArrayJson
                                                    .getJSONObject(0).getString(
                                                            "markerCode"));
                                if (itsStickyMarkerArrayJson.getJSONObject(0).has(
                                        "markerCodeDesc"))
                                    markerDto
                                            .setMarkerCodeDesc(itsStickyMarkerArrayJson
                                                    .getJSONObject(0).getString(
                                                            "markerCodeDesc"));
                                if (itsStickyMarkerArrayJson.getJSONObject(0).has(
                                        "markerDesc"))
                                    markerDto
                                            .setMarkerDesc(itsStickyMarkerArrayJson
                                                    .getJSONObject(0).getString(
                                                            "markerDesc"));
                                if (itsStickyMarkerArrayJson.getJSONObject(0).has(
                                        "markerName")) {
                                    markerDto
                                            .setMarkerName(itsStickyMarkerArrayJson
                                                    .getJSONObject(0).getString(
                                                            "markerName"));
                                    MyAppConstants.NAME_MARKER = itsStickyMarkerArrayJson
                                            .getJSONObject(0).getString(
                                                    "markerName");
                                }
                                MyAppConstants.STICKY_MEMBERID = Long.valueOf(
                                        itsStickyMarkerArrayJson.getJSONObject(0)
                                                .getString("memberId")).longValue();
                                if (markerlat != 0 && markerlng != 0) {


                                    View marker1 = ((LayoutInflater) itsContext
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                            .inflate(R.layout.sticky_marker,
                                                    null);
                                    ImageView sticky_marker_img = (ImageView) marker1
                                            .findViewById(R.id.stikcy_marker_img);
                                    TextView marker_text = (TextView) marker1.findViewById(R.id.marker_text);
                                    if (itsStickyMarkerArrayJson.getJSONObject(0).has(
                                            "markerName")) {
                                        marker_text.setText(itsStickyMarkerArrayJson.getJSONObject(0).getString("markerName"));
                                    } else {
                                        marker_text.setVisibility(View.GONE);
                                    }
//                                        TextView marker_date = (TextView) marker1.findViewById(R.id.marker_date);
//                                        Typeface font = Typeface.createFromAsset(teamViewActivity.getAssets(),
//                                                "fonts/digital.ttf");
//                                        marker_date.setTypeface(font);
//                                        marker_date.setText(date);
//                                        marker_date.setVisibility(booleanTime ? View.VISIBLE : View.GONE);

                                    // marker_img.getBackground().setColorFilter(-9175296,
                                    // Mode.SRC_IN);
                                    sticky_marker_img
                                            .setBackgroundResource(R.drawable.pin_red);


                                    if (isStickyTeam.equals("true")) {


//                                        if (MyAppConstants.myStickyMarker != null) {
//                                            MyAppConstants.myStickyMarker.remove();
//                                        }
                                        LatLng latLng1 = new LatLng(
                                                markerlat,
                                                markerlng);
//                                        if (!hmapMemberMarkerCurr.containsKey("Sticky")) {
                                        if (MyAppConstants.myStickyMarker == null) {
                                            MyAppConstants.myStickyMarker = itsGoogleMap
                                                    .addMarker(new MarkerOptions()
                                                            .position(latLng1
                                                            )
                                                            .icon(BitmapDescriptorFactory
                                                                    .fromBitmap(createDrawableFromView(
                                                                            itsContext,
                                                                            marker1)))
                                                            .draggable(true));
                                        } else {
                                            MyAppConstants.myStickyMarker.setIcon(BitmapDescriptorFactory
                                                    .fromBitmap(createDrawableFromView(
                                                            itsContext,
                                                            marker1)));
                                            animateMarker(MyAppConstants.myStickyMarker, latLng1);

                                        }
//                                        hmapMemberMarkerCurr.put("Sticky", MyAppConstants.myStickyMarker);

                                        if (stickyhmapLatLng != null) {
                                            stickyhmapLatLng.clear();
                                        }
                                        stickyhmapLatLng.put("Sticky", new LatLng(
                                                markerlat, markerlng));

                                    }
                                }

                            } else {
                                if (MyAppConstants.myStickyMarker != null) {
                                    MyAppConstants.myStickyMarker.remove();
                                }
                            }
                        }
                    } catch (JSONException theJsonException) {
                        theJsonException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from getMyTeam webservice");
                    }

                    // } else {
                    // Toast.makeText(itsContext,
                    // MyAppConstants.CONNECTION_ERROR,
                    // Toast.LENGTH_LONG).show();
                    // }

                    // if (fromPage.equals("map")) {
                    getAndSetMyTeamMembersLoc();
                    // }

                }
            });

            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSONException when trying to construct request json for getMyTeamMembers webservices");
        }
    }

    /**
     * Method to create timer which gets and sets team members location for
     * every n minutes
     */
    public void startRefreshTimer() {
        // if ((alatitude != null) && !(alatitude.trim().equals(""))) {
        // Log.d("startRefreshTimer", "latitude is empty since no lat lng");
        // latitude = Double.parseDouble(alatitude);
        // longitude = Double.parseDouble(alongitude);
        // accuracy = Double.parseDouble(aAccuracy);
        // }

        aSharedPreference = itsContext.getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
        final int teamupdateinterval = aSharedPreference.getInt(MyAppConstants.TEAMUPDATE_INTERVAL, 60);
        Log.d("TeamMapFragment", "teamupdateinterval=" + teamupdateinterval);

        // If handler already exists, remove it
        if (mHandlerTask != null) {
            itsHandler.removeCallbacks(mHandlerTask);
        }
        // Create a new handler to run for every n minutes
        mHandlerTask = new Runnable() {
            @Override
            public void run() {
                Log.d("tmf", "calling mHandlerTask.......");
                // Get and set team members location in map
                // getAndSetMyTeamMembersLoc();
//                MyAppConstants.myStickyMarker = null;
                getMarkers("map");
                itsHandler.postDelayed(mHandlerTask, teamupdateinterval * 1000);
            }
        };
        mHandlerTask.run();

        if (mHandlerTask1 != null)
            itsHandler1.removeCallbacks(mHandlerTask1);
        // Create a new handler to run for every n minutes
        mHandlerTask1 = new Runnable() {
            @Override
            public void run() {
                Log.d("tmf", "calling mHandlerTask1.......");
                try {
                    // UPDATE MY LOCATION IN MAP
                    String latStr = aSharedPreference.getString(
                            TeamBuilder.LAT, "");
                    String lngStr = aSharedPreference.getString(
                            TeamBuilder.LNG, "");
                    if (latStr == null || latStr.equals("")) {
                        teamViewActivity.itsGPSAlert
                                .setVisibility(View.VISIBLE);
                        // return;
                    } else {
                        double latDbl = Double.parseDouble(latStr);
                        double lngDbl = Double.parseDouble(lngStr);
                        hmapLatLng.put("ME", new LatLng(latDbl, lngDbl));

                        Log.d("TMF", "calling my location handler...");
                        showMyMarkerAndAccuracyCircle("locationupdate");

                        teamViewActivity.itsGPSAlert.setVisibility(GONE);
                        itsHandler1.removeCallbacks(mHandlerTask1);

                    }
                    itsHandler1.postDelayed(mHandlerTask1,
                            REFRESH_MY_LOC_INTERVAL);
                } catch (Exception e) {
                    Toast.makeText(itsContext, "EXCEption " + e,
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        mHandlerTask1.run();

    }

    /**
     * Method to stop timer
     */
    public void stopRefreshTimer() {
        Log.d("tmf", "calling stopRefreshTimer.......");
        if (mHandlerTask != null)
            itsHandler.removeCallbacks(mHandlerTask);
        if (mHandlerTask1 != null)
            itsHandler1.removeCallbacks(mHandlerTask1);
    }

    public void showMyStickyMarker(String fromloc) {
        // Show My Current Location
        // Check if LatLng is empty or not
        LatLng latlngpoint = null;
        if (stickyhmapLatLng != null) {
            latlngpoint = stickyhmapLatLng.get("Sticky");
        }

        // if (itsLocationBuilder == null)

        if (MyAppConstants.myStickyMarker != null) {
            MyAppConstants.myStickyMarker.remove();
        }

        View marker1 = ((LayoutInflater) itsContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.sticky_marker, null);
        ImageView sticky_marker_img = (ImageView) marker1
                .findViewById(R.id.stikcy_marker_img);
        // marker_img.getBackground().setColorFilter(-9175296,
        // Mode.SRC_IN);
        sticky_marker_img.setBackgroundResource(R.drawable.pin_red);

        if (latlngpoint != null) {
            MyAppConstants.myStickyMarker = itsGoogleMap
                    .addMarker(new MarkerOptions()
                            .position(latlngpoint)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(
                                            itsContext, marker1)))
                            .draggable(true));
        }

        animateMapByViewType(fromloc);

    }

    // public void updateRecording(String recordStatus) {
    // try {
    //
    // // Constructing request JSON
    // JSONObject aRequestJson = new JSONObject();
    // if (MyAppConstants.hmapMarkerId.size() != 0
    // && MyAppConstants.hmapMarkerId != null) {
    // aRequestJson.put(MyAppConstants.MARKER_ID,
    // MyAppConstants.hmapMarkerId
    // .get(TeamMapFragment.itsSelectedTeamId));
    // } else {
    // aRequestJson.put(MyAppConstants.MARKER_ID, 0);
    // }
    //
    // aSharedPreference = itsContext.getSharedPreferences(
    // MyAppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
    // String aMemberId = aSharedPreference.getString(
    // MyAppConstants.MEMBER_ID, "");
    //
    // aRequestJson.put(MyAppConstants.TEAM_ID,
    // TeamMapFragment.itsSelectedTeamId);
    // aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
    // aRequestJson.put(MyAppConstants.START_TIME, new Date());
    // aRequestJson.put(MyAppConstants.MARKER_NAME,
    // TeamMapFragment.itsSelectedTeamName);
    // aRequestJson.put(MyAppConstants.LAT, MyAppConstants.LAT_USER);
    // aRequestJson.put(MyAppConstants.LNG, MyAppConstants.LNG_USER);
    // aRequestJson.put(MyAppConstants.MARKER_LAT,
    // MyAppConstants.LAT_MARKER);
    // aRequestJson.put(MyAppConstants.MARKER_LNG,
    // MyAppConstants.LNG_MARKER);
    // aRequestJson.put("status", recordStatus);
    // AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
    // MyAppConstants.UPDATE_RECORD, MyAppConstants.API_POST_TYPE,
    // MyAppConstants.LOADING);
    // aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
    // @Override
    // public void onPreExecuteConcluded() {
    // }
    //
    // @Override
    // public void onPostExecuteConcluded(String theMemberInfo) {
    // // if (mHandlerTask2 != null)
    // // itsHandler2.removeCallbacks(mHandlerTask2);
    // // if (theMemberInfo != null) {
    // //
    // // } else {
    // // Toast.makeText(itsContext,
    // // MyAppConstants.CONNECTION_ERROR,
    // // Toast.LENGTH_LONG).show();
    // // }
    // }
    // });
    // aAsyncTask.execute(aRequestJson.toString());
    // } catch (JSONException theJsonException) {
    // theJsonException.printStackTrace();
    // Log.e(this.getClass().getName(),
    // "JSON Exception while constructing request for getMyTeam webservice");
    // }
    // }

    public void showMyMarkerAndAccuracyCircle(String fromloc) {
        aSharedPreference = itsContext.getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, itsContext.MODE_PRIVATE);
        booleanAccuracy = aSharedPreference.getBoolean(
                MyAppConstants.USER_SEL_ACCURACY, false);
        booleanTime = aSharedPreference.getBoolean(
                MyAppConstants.USER_SEL_TIME, false);

        // Show My Current Location
        String name = "";
        // Check if LatLng is empty or not
        String latStr = aSharedPreference.getString(TeamBuilder.LAT, "");
        if (!latStr.trim().equals("")) {
            teamViewActivity.itsGPSAlert.setVisibility(View.INVISIBLE);
            double latitude = Double.parseDouble(aSharedPreference.getString(
                    TeamBuilder.LAT, ""));
            double longitude = Double.parseDouble(aSharedPreference.getString(
                    TeamBuilder.LNG, ""));
            double accuracy = 0, speed = 0;
            if (!aSharedPreference.getString(MyAppConstants.ACCURACY, "")
                    .equals("")
                    && (!aSharedPreference.getString(MyAppConstants.SPEED, "")
                    .equals(""))
                    && (!aSharedPreference.getString(MyAppConstants.SPEED, "")
                    .equals("0.0"))) {
                accuracy = Double.parseDouble(aSharedPreference
                        .getString(MyAppConstants.ACCURACY, "0"));

                speed = Integer.parseInt(aSharedPreference.getString(
                        MyAppConstants.SPEED, "0"));
                name = aSharedPreference.getString(TeamBuilder.ALIAS, "");

                LatLng latlngpoint = new LatLng(latitude, longitude);
                // if (itsLocationBuilder == null)
                // itsLocationBuilder = new LatLngBounds.Builder();

                // itsLocationBuilder.include(latlngpoint);

            }

            addMyMarkersToMap(name, latitude, longitude, accuracy, speed);

            animateMapByViewType(fromloc);
        } else {
            teamViewActivity.itsGPSAlert.setVisibility(View.VISIBLE);
        }
    }

    public void animateMapByViewType(String fromloc) {
        // Animate Google Maps based on condition
        Log.d("TMF", "***********inside animateMapByViewType***********");
        Log.d("TMF", "fromloc=" + fromloc);
        Log.d("MAP TYPE ", "TYPE   " + MyAppConstants.map_view_type);
        Log.d("TMF", "hmapLatLng.=" + hmapLatLng.size());
        // float zoom_f =
        // aSharedPreference.getFloat(MyAppConstants.USER_SEL_ZOOM,
        // 16.0f);
        // Log.d("TMF", "hmapLatLng.=" + zoom_f);
        if (fromloc.equals("tmf")) {
            if (hmapLatLng.get("ME") != null) {
                if (MyAppConstants.map_view_type == 1 && hmapLatLng.size() == 1
                        && MyAppConstants.myStickyMarker == null) {

                    itsGoogleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(hmapLatLng.get("ME"), 11.0f));
                    if (hmapLatLng.get("ME").latitude != 0) {
                        MyAppConstants.map_view_type = 4;
                    } else {
                        Toast.makeText(itsContext, "inside lat = 0",
                                Toast.LENGTH_LONG).show();
                        MyAppConstants.map_view_type = 1;
                    }

                    // itsGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    return;
                }
            }

        }

        Builder itsLocationBuilder = new LatLngBounds.Builder();
        for (Map.Entry<String, LatLng> entry : hmapLatLng.entrySet()) {
            itsLocationBuilder.include(entry.getValue());
        }
        if (MyAppConstants.myStickyMarker != null) {
            Log.d("TMF", "pos="
                    + MyAppConstants.myStickyMarker.getPosition().latitude
                    + ","
                    + MyAppConstants.myStickyMarker.getPosition().longitude);
            itsLocationBuilder.include(MyAppConstants.myStickyMarker
                    .getPosition());
        } else {
            // Toast.makeText(itsContext, "Sticky is not available",
            // Toast.LENGTH_SHORT).show();
        }

        Log.d("TEAMMAPFRAGMENT", "before IS_FOLLOW_ME & IS_MAP_SHOW_ALL "
                + MyAppConstants.map_view_type + ", \n fromloc=" + fromloc);
        switch (MyAppConstants.map_view_type) {

            case 1:
                Log.d("TEAMMAPFRAGMENT", "Showing all markers");
                try {
                    itsGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                            itsLocationBuilder.build(), 100));

                } catch (Exception exception) {
                    Log.d("TEAMMAPFRAGMENT",
                            "Showing all markers catch exception...");
                    Builder itsLocationBuilder1 = new LatLngBounds.Builder();

                    HashMap<String, LatLng> hmapLatLng1 = new HashMap<String, LatLng>();
                    hmapLatLng1.put("KEY", new LatLng(MyAppConstants.LAT_DUPLICATE,
                            MyAppConstants.LNG_DUPLICATE));
                    itsLocationBuilder1.include(hmapLatLng1.get("KEY"));
                    itsGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                            itsLocationBuilder1.build(), 50));
                    MyAppConstants.map_view_type = 1;
                    break;
                }
                if (fromloc.equals("tmf")) {
                    MyAppConstants.map_view_type = 4;
                }
                break;
            case 2:
                Log.d("TEAMMAPFRAGMENT", "Center me icon");
                LatLng latLng1 = hmapLatLng.get("ME");
                if (latLng1 != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory
                            .newLatLng(latLng1);
                    itsGoogleMap.animateCamera(cameraUpdate);
                }

                // if (fromloc.equals("tmf")) {
                MyAppConstants.map_view_type = 4;
                // }
                break;
            case 3:
                Log.d("TEAMMAPFRAGMENT", "follow Me icon");
                LatLng latLng = hmapLatLng.get("ME");
                if (latLng != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory
                            .newLatLng(latLng);
                    itsGoogleMap.animateCamera(cameraUpdate);
                }
                // if (fromloc.equals("tmf")) {
                // MyAppConstants.map_view_type = 4;
                // }
                break;
            case 4:

                break;

            case 5:
                Log.d("TEAMMAPFRAGMENT", "Center me icon");
                LatLng stickyLatLng = stickyhmapLatLng.get("Sticky");

                if (stickyLatLng != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory
                            .newLatLng(stickyLatLng);
                    itsGoogleMap.animateCamera(cameraUpdate);
                }
                // MyAppConstants.map_view_type = 4;
                break;

            default:
                break;
        }

    }

    /**
     * Method to iterate response and draw team members location in map
     *
     * @param theMemberInfo
     */
    private void iterateAndDrawMarkers(String theMemberInfo) {
        // itsLocationBuilder = new LatLngBounds.Builder();
        // MyAppConstants.resetMemberRandomColor();
        // itsGoogleMap.clear();
        // remove all teammembers from hmap
        HashMap<String, LatLng> hmapLatLngtemp = new HashMap<String, LatLng>(
                hmapLatLng);
        for (Map.Entry<String, LatLng> entry : hmapLatLng.entrySet()) {
            Log.d("TMF", "entry.getKey()=" + entry.getKey());
            if (entry.getKey().startsWith("tm")) {
                hmapLatLngtemp.remove(entry.getKey());
            }
        }
        hmapLatLng.clear();
        hmapLatLng = new HashMap<String, LatLng>(hmapLatLngtemp);
        // for (Map.Entry<String, LatLng> entry : hmapLatLng.entrySet()) {
        // Log.d("TMF", "final entry.getKey()=" + entry.getKey());
        // }
        try {

            if (theMemberInfo != null) {
                JSONObject aTeamMembersJson = new JSONObject(theMemberInfo);

                // Retrieving status from response JSON
                String aResponseStatus = aTeamMembersJson
                        .getString(MyAppConstants.STATUS);

                // If API response status is success
                if (aResponseStatus.equals(MyAppConstants.SUCCESS_STATUS)) {
                    // Get team members array

                    JSONArray aTeamMemberArray = aTeamMembersJson
                            .getJSONArray(MyAppConstants.RESULT);
                    // Clear the markers on the google map.
                    int teamCount = Integer.parseInt(aTeamMembersJson
                            .getString(MyAppConstants.MESSAGE));
                    //
                    MyAppConstants.TEAM_CNT_UPDATE = teamCount;
                    // Updating team members location in map
                    double aLat, aLng, aAccuracy = 0;
                    int aSpeed = -1, dateLong = -1, color = 0, memberId = -1;
                    String name = "", date = "", phone = "";
                    // itsLocationBuilder = new LatLngBounds.Builder();
                    // Iterating teamMembersArray and drawing markers in
                    // map
                    JSONObject aMemberJson;
                    ArrayList<LatLng> aMemberLocList = new ArrayList<LatLng>();
                    int map_icon_cnt = 1;

                    for (int index = 0; index < aTeamMemberArray.length(); index++) {
                        aMemberJson = aTeamMemberArray.getJSONObject(index);
                        // aEmail = aMemberJson
                        // .getString(MyAppConstants.EMAIL);
                        aLat = aMemberJson.getDouble(MyAppConstants.LAT);
                        aLng = aMemberJson.getDouble(MyAppConstants.LNG);
                        aAccuracy = aMemberJson
                                .getDouble(MyAppConstants.ACCURACY);
                        aSpeed = aMemberJson.getInt(MyAppConstants.SPEED);
                        if (aLat == 0 || aLng == 0) {
                            return;
                        }
                        LatLng aMemberLocation = new LatLng(aLat, aLng);
                        name = aMemberJson.getString(MyAppConstants.ALIAS);
                        phone = aMemberJson.getString(MyAppConstants.PHONE);
                        memberId = aMemberJson.getInt(TeamBuilder.MEMBERID);
                        dateLong = aMemberJson.getInt(MyAppConstants.LASTUPDDT);
                        color = aMemberJson.getInt(MyAppConstants.COLOR);
                        Date expiry = new Date(Long.valueOf(dateLong) * 1000);
                        date = sdf.format(expiry);
                        // niaz
                        // itsLocationBuilder.include(aMemberLocation);
                        aMemberLocList.add(aMemberLocation);
                        int marker_icon = -1;
                        marker_icon = R.drawable.triangle;
                        addMarkersToMap(memberId, name, aLat, aLng, marker_icon,
                                aAccuracy, aSpeed, date, color, phone);

                        // adding team members to hashmap
                        hmapLatLng.put("tm" + index, aMemberLocation);
                    }
                    showMyMarkerAndAccuracyCircle("tmf");

                }
                // If API response status is failure
                else if (aResponseStatus.equals(MyAppConstants.FAILURE_STATUS)) {
                    String aResponseMsg = aTeamMembersJson
                            .getString(MyAppConstants.MESSAGE);

                }
                if (itsProgressDialog != null && itsProgressDialog.isShowing()) {
                    itsProgressDialog.dismiss();
                }

            }
        } catch (JSONException theJSONException) {
            theJSONException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSONException when retrieving members information from getMyTeamMembers webservice");
            showMyMarkerAndAccuracyCircle("tmf");
            if (itsProgressDialog != null && itsProgressDialog.isShowing()) {
                itsProgressDialog.dismiss();
            }

        }
    }

    private void addMyMarkersToMap(String theMemberEmail, double theLattitude,
                                   double theLongitude, double accuracy,
                                   double speed) {
        LatLng aMemberLocation = new LatLng(theLattitude, theLongitude);
        LatLng latLngPrev = null;
        if (MyAppConstants.myMarker != null) {
//            latLngPrev = MyAppConstants.myMarker.getPosition();
            latLngPrev = new LatLng(MyAppConstants.LAT_MY_MARKER_PREV_LAT, MyAppConstants.LAT_MY_MARKER_PREV_LNG);
            Log.d("test", "myMarker != null");
            if ((MyAppConstants.LAT_MY_MARKER_PREV_LAT == theLattitude) && (MyAppConstants.LAT_MY_MARKER_PREV_LNG == theLongitude)) {
                return;
            }
            MyAppConstants.myMarker.remove();
        } else {
            latLngPrev = aMemberLocation;
            MyAppConstants.LAT_MY_MARKER_PREV_LAT = theLattitude;
            MyAppConstants.LAT_MY_MARKER_PREV_LNG = theLongitude;
        }
        View marker = ((LayoutInflater) itsContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.custom_marker_layout, null);
        TextView marker_text = (TextView) marker.findViewById(R.id.marker_text);
        marker_text.setText(theMemberEmail);

        TextView marker_date = (TextView) marker.findViewById(R.id.marker_date);
        marker_date.setVisibility(GONE);

        Button marker_img = (Button) marker.findViewById(R.id.marker_img);
        // marker_img.getBackground().setColorFilter(-9175296, Mode.SRC_IN);
        marker_img.setBackgroundResource(R.drawable.map_arrow_5);
        float brng = MyAppConstants.getRotation(latLngPrev, aMemberLocation);
        marker_img.setRotation(brng-90);
        MyAppConstants.myMarker = null;
        MyAppConstants.myMarker = itsGoogleMap.addMarker(new MarkerOptions()
                .position(latLngPrev).icon(
                        BitmapDescriptorFactory
                                .fromBitmap(createDrawableFromView(itsContext,
                                        marker))));
        MyAppConstants.myMarker.setAnchor(0.5f, 0.8f);


        if (MyAppConstants.myCircle != null) {
            MyAppConstants.myCircle.remove();
        }
        if (booleanAccuracy) {
            Log.d("test", "myMarker accuracy = .." + accuracy);
            CircleOptions co = new CircleOptions();
            co.center(aMemberLocation);
            co.radius(accuracy);
//                if (speed > 2) {
            co.strokeColor(Color.BLUE);
//                } else {
//                    co.strokeColor(Color.RED);
//                }
            co.fillColor(0x400000ff);
            co.strokeWidth(2.0f);
            MyAppConstants.myCircle = itsGoogleMap.addCircle(co);
        }
        animateMarker(MyAppConstants.myMarker, aMemberLocation);
        MyAppConstants.LAT_MY_MARKER_PREV_LAT = theLattitude;
        MyAppConstants.LAT_MY_MARKER_PREV_LNG = theLongitude;
    }

    /**
     * Method to draw team members marker in map
     *
     * @param theMemberEmail
     * @param theLattitude
     * @param theLongitude
     * @param aSpeed
     * @param aAccuracy
     * @param date
     */
    private void addMarkersToMap(int memberId, String theMemberEmail, double theLattitude,
                                 double theLongitude, int id, double aAccuracy, int aSpeed,
                                 String date, int color, String phone) {
        booleanAccuracy = aSharedPreference.getBoolean(
                MyAppConstants.USER_SEL_ACCURACY, false);
        booleanTime = aSharedPreference.getBoolean(
                MyAppConstants.USER_SEL_TIME, false);

        LatLng aMemberLocation = new LatLng(theLattitude, theLongitude);

        View marker = ((LayoutInflater) itsContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.custom_marker_layout, null);
        TextView marker_text = (TextView) marker.findViewById(R.id.marker_text);
        marker_text.setText(theMemberEmail);
        TextView marker_date = (TextView) marker.findViewById(R.id.marker_date);
        Typeface font = Typeface.createFromAsset(teamViewActivity.getAssets(),
                "fonts/digital.ttf");
        marker_date.setTypeface(font);
        marker_date.setText(date);
        Log.d("test", "booleanTime=" + booleanTime);
        marker_date.setVisibility(booleanTime ? View.VISIBLE : View.INVISIBLE);
        Button marker_img = (Button) marker.findViewById(R.id.marker_img);
        // marker_img.setBackgroundResource(id);
        marker_img.getBackground().setColorFilter(
                Color.parseColor(MyAppConstants.getMemberRandomColor()),
                Mode.SRC_IN);

        Log.d("test", "latLngPrevPrint memberId=" + memberId + ", date=" + new Date());
//        if(booleanTime){
//            hmapMemberMarkerPrev.remove(memberId);
//            hmapMemberCirclePrev
//        }
//        if (hmapMemberMarkerPrev.containsKey(memberId + "")) {
//            LatLng latLngPrevPrint = hmapMemberMarkerPrev.get(memberId + "").getPosition();
//            Log.d("test", "latLngPrevPrint Prev=" + latLngPrevPrint.toString());
//        }


//        if (hmapMemberMarkerPrev.containsKey(1000 + "")) {
//            hmapMemberMarkerPrev.get(1000 + "").remove();
//        }
//        Marker markerTest = itsGoogleMap
//                .addMarker(new MarkerOptions()
//                                .position(aMemberLocation)
//                                .title(marker_text.getText() + "~" + phone)
//                                .snippet("Description")
//                );
//        hmapMemberMarkerPrev.put(1000 + "", markerTest);

        LatLng latLngPrev;
        if (hmapMemberMarkerPrevLatLng.containsKey(memberId + "")) {
            latLngPrev = hmapMemberMarkerPrevLatLng.get(memberId + "");

        } else {
            hmapMemberMarkerPrevLatLng.put(memberId + "", aMemberLocation);
            latLngPrev = aMemberLocation;
        }
        Log.d("", "date=" + new Date().toString());
        Log.d("test", "latLngPrevPrint Prev=" + latLngPrev.toString());
        Log.d("test", "latLngPrevPrint Current=" + aMemberLocation.toString());

        float brng = MyAppConstants.getRotation(latLngPrev, aMemberLocation);

        hmapMemberMarkerPrevLatLng.put(memberId + "", aMemberLocation);

        if (hmapMemberMarkerPrevRotation.containsKey(memberId + "")) {
            marker_img.setRotation(hmapMemberMarkerPrevRotation.get(memberId + "").floatValue());
        }


        if (brng != 0.0) {
            hmapMemberMarkerPrevRotation.put(memberId + "", (double) brng);
            Log.d("test", "latLngPrevPrint bearing value=" + brng);
            marker_img.setRotation(brng);
        }
        if (hmapMemberMarkerPrev.containsKey(memberId + "")) {
            hmapMemberMarkerPrev.get(memberId + "").remove();
            Marker marker1 = itsGoogleMap
                    .addMarker(new MarkerOptions()
                            .position(latLngPrev)
                            .title(marker_text.getText() + "~" + phone)
                            .snippet("Description")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(itsContext,
                                            marker))));
            marker1.setAnchor(0.5f, 0.8f);
//            hmapMemberMarkerPrev.get(memberId + "").remove();
//            hmapMemberCirclePrev.get(memberId + "").remove();
            hmapMemberCirclePrev.get(memberId + "").setVisible(false);
            hmapMemberCirclePrev.get(memberId + "").setCenter(aMemberLocation);


            hmapMemberMarkerPrev.put(memberId + "", marker1);
//            Marker markerLatest = hmapMemberMarkerPrev.get(memberId + "");
//            markerLatest.setPosition(aMemberLocation);
//            Circle circleLatest = hmapMemberCirclePrev.get(memberId + "");
//            circleLatest.setCenter(aMemberLocation);
//            hmapMemberMarkerPrev.put(memberId + "", markerLatest);
//            hmapMemberCirclePrev.put(memberId + "", circleLatest);
//            Marker markerEx = hmapMemberMarkerPrev.get(memberId + "");
//            markerEx.setIcon(BitmapDescriptorFactory
//                    .fromBitmap(createDrawableFromView(itsContext,
//                            marker)));
//            if (booleanTime) {
//                Log.d("test", "booleanTime=" +booleanTime);
//                hmapMemberMarkerPrev.put(memberId + "", marker1);
//            }

            animateMarker(hmapMemberMarkerPrev.get(memberId + ""), aMemberLocation);
            hmapMemberCirclePrev.get(memberId + "").setVisible(booleanAccuracy);

        } else {
            Marker marker1 = itsGoogleMap
                    .addMarker(new MarkerOptions()
                            .position(latLngPrev)
                            .title(marker_text.getText() + "~" + phone)
                            .snippet("Description")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(itsContext,
                                            marker))));
            Log.d("test", "hmapMemberMarkerPrev containsKey no ...");

            marker1.setAnchor(0.5f, 0.8f);
            hmapMemberMarkerPrev.put(memberId + "", marker1);


            CircleOptions co = new CircleOptions();
            co.center(aMemberLocation);
            co.radius(aAccuracy);
            if (aSpeed > 2) {
                co.strokeColor(Color.GREEN);
            } else {
                co.strokeColor(Color.RED);
            }
            co.fillColor(0x40ff0000);
            co.strokeWidth(2.0f);
            Circle circle = itsGoogleMap.addCircle(co);
            circle.setVisible(booleanAccuracy);
            hmapMemberCirclePrev.put(memberId + "", circle);
            animateMarker(marker1, aMemberLocation);
//            animateMarkerAndCircle(marker1, aMemberLocation, circle);

        }


    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public synchronized void animateMarker(final Marker marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 1000;
        final boolean hideMarker = false;

        Location locationA = new Location("point A");

        locationA.setLatitude(startPosition.latitude);
        locationA.setLongitude(startPosition.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(finalPosition.latitude);
        locationB.setLongitude(finalPosition.longitude);

        float distance = locationA.distanceTo(locationB);
        Log.d("TMF", "distance=" + distance);
        if (distance < 3) {
            marker.setPosition(finalPosition);
            return;
        }


        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                marker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public synchronized void animateMarkerAndCircle(final Marker marker,
                                                    final LatLng finalPosition, final Circle circle) {
        if (marker != null) {
            startPosition = marker.getPosition();
        }
        if (circle != null) {
            startPositionCircle = circle.getCenter();
        }
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        Location locationA = new Location("point A");

        locationA.setLatitude(startPosition.latitude);
        locationA.setLongitude(startPosition.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(finalPosition.latitude);
        locationB.setLongitude(finalPosition.longitude);

        float distance = locationA.distanceTo(locationB);
        Log.d("TMF", "distance=" + distance);
        if (distance < 20) {
            return;
        }


        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                if (startPosition != null) {
                    LatLng currentPosition = new LatLng(
                            startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                            startPosition.longitude * (1 - t) + finalPosition.longitude * t);
                    marker.setPosition(currentPosition);
                }
                if (startPositionCircle != null) {
                    LatLng currentPositionCircle = new LatLng(
                            startPositionCircle.latitude * (1 - t) + finalPosition.latitude * t,
                            startPositionCircle.longitude * (1 - t) + finalPosition.longitude * t);
                    circle.setCenter(currentPositionCircle);
                }


                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        if (marker != null)
                            marker.setVisible(false);
                        if (circle != null)
                            circle.setVisible(false);
                    } else {
                        if (marker != null)
                            marker.setVisible(true);
                        if (circle != null)
                            circle.setVisible(true);
                    }
                }
            }
        });
    }
}