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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.activity.FollowersListActivity;
import com.dsdar.thosearoundme.activity.InvitationListActivity;
import com.dsdar.thosearoundme.activity.MyProfileActivity;
import com.dsdar.thosearoundme.activity.WelcomeActivity;
import com.dsdar.thosearoundme.dto.TeamCode;
import com.dsdar.thosearoundme.location.LocationUpdates;
import com.dsdar.thosearoundme.tab.TMTabMainActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to load Google maps and display team
 *
 * @author senthil_kumaran
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TeamViewActivity extends FragmentActivity implements
        OnClickListener, OnMarkerClickListener, OnMarkerDragListener,
        OnMapLongClickListener, TabListener {
    private Runnable mHandlerTask = null;
    private Runnable mHandlerTask1 = null;

    Handler itsHandlerServiceStop = new Handler();
    Runnable mHandlerTaskServiceStop = null;

    private Handler itsHandler = null;
    private Handler itsHandler1 = null;
    private JSONArray itsTeamArrayJson;
    private String[] itsTeamArray;
    private String[] itsTeamIdArray;
    private String[] itsTeamStickyArray;
    private String[] itsTeamRecordStatusArray;
    public GoogleMap itsGoogleMap;
    private ActionBar itsActionBar;
    private Button itsShowAllButton, itsAddTeam, itsAddTeamMember, itsTvMan,
            itsMyLoc, itsStickyMarker;
    private String itsLoginUserId = null;
    private String itsLoginPhone = null;
    private int intSupported;
    public TeamMapFragment itsTeamMapFragment = null;
    private LocationUpdates itsLocationUpdates = null;
    private static final String ACTION_BAR_COLOR = "#dcdcdc";
    public TextView itsNormalMapView, itsSatelliteMapView, itsHybridMapView,
            itsHomeView, itsTeamView, itsFollowersView, itsGPSAlert,
            itsInvitationcount;
    private Drawable itsHomeImgBlack, itsTeamImgGreen, itsHomeImgGreen,
            itsTeamImgBlack, itsFollowerImgBlack, itsFollowerImgGreen;
    private static int imgBounds = 44;
    private String itsResult = null;
    public static ProgressDialog itsProgressDialog;
    public SharedPreferences aSharedPreference;
    boolean isMEToggleOn = true, isStickyToggleOn = true;
    public HashMap<String, String> hMap = new HashMap<String, String>();
    public HashMap<String, String> hMapRecord = new HashMap<String, String>();
    public HashMap<Double, Double> hMapSticky = new HashMap<Double, Double>();
    public HashMap<String, String> hMapOwner = new HashMap<String, String>();
    public static CharSequence recordStatus = "";
    private boolean isTeamMemberCallFirstTime = true;
    SharedPreferences itsSharedPreference;
    SharedPreferences.Editor aEditor;
    List<TeamCode> teamCodes = null;

    // private String invite_count;

    @Override
    protected void onCreate(Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        itsHandler = new Handler();
        itsHandler1 = new Handler();

        // MyAppConstants.INVITATION_CNT=1;
        // invite_count = String.valueOf(MyAppConstants.INVITATION_CNT);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_team);
        initializeComponents();

        itsSharedPreference = getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);

        // Retrieving userId from preference
        aSharedPreference = getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                MODE_PRIVATE);
        itsLoginUserId = aSharedPreference.getString(TeamBuilder.MEMBERID, "");
        itsLoginPhone = aSharedPreference.getString(TeamBuilder.PHONE, "");
        intSupported= aSharedPreference.getInt(MyAppConstants.SUPPORTED,0);

        itsNormalMapView.setOnClickListener(this);
        itsSatelliteMapView.setOnClickListener(this);
        itsHybridMapView.setOnClickListener(this);
        itsShowAllButton.setOnClickListener(this);
        itsTeamView.setOnClickListener(this);
        itsHomeView.setOnClickListener(this);
        itsFollowersView.setOnClickListener(this);
        itsAddTeam.setOnClickListener(this);
        itsAddTeamMember.setOnClickListener(this);
        itsTvMan.setOnClickListener(this);
        itsMyLoc.setOnClickListener(this);
        itsStickyMarker.setOnClickListener(this);
        itsStickyMarker.setVisibility(View.GONE);
        // calling location service
        startService(new Intent(getBaseContext(), LocationUpdates.class));

        aEditor = itsSharedPreference.edit();

        getAndLoadTeamInfo();
        // MyAppConstants.selTab = 1;

    }

    /**
     * Method to initialize the components used in team activity
     */
    @SuppressLint("NewApi")
    private void initializeComponents() {

        itsActionBar = getActionBar();

        itsActionBar.setIcon(new ColorDrawable(getResources().getColor(
                android.R.color.transparent)));
        // itsActionBar.setDisplayShowTitleEnabled(false);
        itsActionBar.setDisplayShowTitleEnabled(true);
        itsActionBar.setTitle("Those Around Me");
        // itsActionBar.setCustomView(R.layout.invitation_count);
        // itsInvitationcount = (TextView) findViewById(R.id.text2);

        itsActionBar.setDisplayShowCustomEnabled(true);

        itsActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
                .parseColor(ACTION_BAR_COLOR)));
        // LayoutInflater inflator = (LayoutInflater) this
        // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
        // View v = inflator.inflate(R.layout.invitation_count, null);
        itsActionBar.setCustomView(R.layout.invitation_count);
        itsInvitationcount = (TextView) findViewById(R.id.text2);
        itsActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        startRefreshTimer();
        // if (invite_count != null || invite_count != "") {
        // itsInvitationcount.setText(invite_count);
        // } else {
        // itsInvitationcount.setText("");
        // }

        itsNormalMapView = (TextView) findViewById(R.id.tvMainPageMapMap);
        itsSatelliteMapView = (TextView) findViewById(R.id.tvMainPageMapSatellite);
        itsHybridMapView = (TextView) findViewById(R.id.tvMainPageMapHybrid);
        itsTeamView = (TextView) findViewById(R.id.tvTeamActTeam);
        itsTeamView.setText("Team");
        itsHomeView = (TextView) findViewById(R.id.tvTeamActHome);
        itsFollowersView = (TextView) findViewById(R.id.tvTeamActFollowers);
        itsHomeImgBlack = getResources()
                .getDrawable(R.drawable.home_icon_black);
        itsHomeImgBlack.setBounds(0, 0, imgBounds, imgBounds);
        itsHomeImgGreen = getResources()
                .getDrawable(R.drawable.home_icon_green);
        itsHomeImgGreen.setBounds(0, 0, imgBounds, imgBounds);
        itsTeamImgGreen = getResources()
                .getDrawable(R.drawable.team_icon_green);
        itsTeamImgGreen.setBounds(0, 0, imgBounds, imgBounds);
        itsTeamImgBlack = getResources()
                .getDrawable(R.drawable.team_icon_black);
        itsTeamImgBlack.setBounds(0, 0, imgBounds, imgBounds);
        itsFollowerImgGreen = getResources().getDrawable(
                R.drawable.followers_icon_green);
        itsFollowerImgGreen.setBounds(0, 0, imgBounds, imgBounds);
        itsFollowerImgBlack = getResources().getDrawable(
                R.drawable.followers_icon_black);
        itsFollowerImgBlack.setBounds(0, 0, imgBounds, imgBounds);

        itsShowAllButton = (Button) findViewById(R.id.tvMainPageShowAllMembers);
        itsAddTeam = (Button) findViewById(R.id.tvAddTeam);
        itsAddTeamMember = (Button) findViewById(R.id.tvAddMember);
        itsTvMan = (Button) findViewById(R.id.tvMan);
        itsMyLoc = (Button) findViewById(R.id.btnMyLoc);
        itsStickyMarker = (Button) findViewById(R.id.btnStickyMarker);
        itsTvMan.setBackgroundResource(R.drawable.map_follow_me_no);
        itsGPSAlert = (TextView) findViewById(R.id.tvGpsAlert);
        SupportMapFragment aSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.TeamViewMap);
        itsGoogleMap = aSupportMapFragment.getMap();
        enableMapsPreference();
        // itsGoogleMap.setMyLocationEnabled(true);
        itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        itsGoogleMap.setOnMarkerClickListener(this);
        itsGoogleMap.setOnMarkerDragListener(this);
        // itsGoogleMap.setOnMapClickListener(this);
        itsGoogleMap.setOnMapLongClickListener(this);

        itsGoogleMap.clear();
    }

    /**
     * Method to handle click events of menu in action bar
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (itsTeamMapFragment != null) {
            Log.d("tmf",
                    "calling stopRefreshTimer from onOptionsItemSelected.......");
            itsTeamMapFragment.stopRefreshTimer();
        }

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menuSignup:

                SharedPreferences aSharedPreference = getSharedPreferences(
                        MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPreference.edit();
                aEditor.remove(MyAppConstants.IS_USER_ALREADY_AUTHENTICATED);
                aEditor.remove(MyAppConstants.MEMBER_ID);
                aEditor.remove(MyAppConstants.PHONE);
                aEditor.remove(MyAppConstants.USER_LOGIN_NAME);
                aEditor.remove(MyAppConstants.PASSWORD);
                aEditor.remove(MyAppConstants.USER_SEL_TAB);
                aEditor.remove(MyAppConstants.USER_SEL_ZOOM);
                aEditor.commit();

                // Remove Service
                stopService(new Intent(getBaseContext(), LocationUpdates.class));

                // MyAppConstants.IS_MAP_SHOW_ALL = true;
                // MyAppConstants.IS_FOLLOW_ME = false;
                Log.d("TVA", "Menu Sign up");
                finish();
                stopRefreshTimer();
                Intent aWelcomeIntent = new Intent().setClass(
                        TeamViewActivity.this, WelcomeActivity.class);
                startActivity(aWelcomeIntent);
                break;

            case R.id.myProfile:
                Intent amyProfileIntent = new Intent().setClass(
                        TeamViewActivity.this, MyProfileActivity.class);
                startActivity(amyProfileIntent);
                break;
            case R.id.myTeams:

                Intent aCreateTeamIntent = new Intent().setClass(
                        TeamViewActivity.this, TMTabMainActivity.class);
                // Intent aCreateTeamIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamManagementActivity.class);
                aCreateTeamIntent.putExtra("aTeamList", itsTeamArray);
                aCreateTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
                aCreateTeamIntent.putExtra("itsLoginUserId", itsLoginUserId);
                startActivity(aCreateTeamIntent);

                break;

            case R.id.invitations:
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();

                Intent aCreateInvitationIntent = new Intent().setClass(
                        TeamViewActivity.this, InvitationListActivity.class);
                startActivity(aCreateInvitationIntent);
                break;

            case R.id.myFollowers:
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();
                Intent aCreateFollowerIntent = new Intent().setClass(
                        TeamViewActivity.this, FollowersListActivity.class);
                // aCreateFollowerIntent.putExtra("aTeamList", itsFollowersArray);
                startActivity(aCreateFollowerIntent);
                break;

        }

        return true;
    }

    public void startRefreshTimer() {

        // If handler already exists, remove it
        if (mHandlerTask != null)
            itsHandler.removeCallbacks(mHandlerTask);
        // Create a new handler to run for every n minutes
        mHandlerTask = new Runnable() {
            @Override
            public void run() {
                // Get and set team members location in map
                if (!(String.valueOf(MyAppConstants.INVITATION_CNT))
                        .equals("-1")) {
                    itsInvitationcount.setText(String
                            .valueOf(MyAppConstants.INVITATION_CNT));
                } else {
                    itsInvitationcount.setText("");
                }

                supportInvalidateOptionsMenu();

                itsHandler.postDelayed(mHandlerTask, 2000);
            }
        };
        mHandlerTask.run();

        // If handler already exists, remove it
        if (mHandlerTask1 != null)
            itsHandler1.removeCallbacks(mHandlerTask1);
        // Create a new handler to run for every n minutes
        mHandlerTask1 = new Runnable() {
            @Override
            public void run() {
                // Get and set team members location in map
                getAndLoadTeamInfo();

                itsHandler1.postDelayed(mHandlerTask1, 20000);
            }
        };
        mHandlerTask1.run();
    }

    /**
     * Method to stop timer
     */
    public void stopRefreshTimer() {
        if (mHandlerTask != null)
            itsHandler.removeCallbacks(mHandlerTask);

        if (mHandlerTask1 != null)
            itsHandler1.removeCallbacks(mHandlerTask1);
    }

    /**
     * Method to enable Google Maps preference
     */
    private void enableMapsPreference() {
        itsGoogleMap.setMyLocationEnabled(false);
        UiSettings uiSettings = itsGoogleMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setIndoorLevelPickerEnabled(true);
//        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
//        itsGoogleMap.getUiSettings().setAllGesturesEnabled(true);
//        itsGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//        itsGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    /**
     * Method to handle click events of button
     *
     * @param theView
     */
    public void onClick(View theView) {

        switch (theView.getId()) {
            case R.id.tvMainPageMapMap:
                itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                enableMapsPreference();
                itsNormalMapView
                        .setBackgroundResource(R.drawable.map_button_active);
                itsSatelliteMapView
                        .setBackgroundResource(R.drawable.satellite_button_inactive);
                itsHybridMapView
                        .setBackgroundResource(R.drawable.hybrid_button_inactive);
                break;
            case R.id.tvMainPageMapSatellite:
                itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                enableMapsPreference();
                itsNormalMapView
                        .setBackgroundResource(R.drawable.map_button_inactive);
                itsSatelliteMapView
                        .setBackgroundResource(R.drawable.satellite_button_active);
                itsHybridMapView
                        .setBackgroundResource(R.drawable.hybrid_button_inactive);
                break;
            case R.id.tvMainPageMapHybrid:
                itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                enableMapsPreference();
                itsNormalMapView
                        .setBackgroundResource(R.drawable.map_button_inactive);
                itsSatelliteMapView
                        .setBackgroundResource(R.drawable.satellite_button_inactive);
                itsHybridMapView
                        .setBackgroundResource(R.drawable.hybrid_button_active);
                break;

            case R.id.tvTeamActHome:

                break;

            case R.id.tvTeamActTeam:
                itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
                itsTeamView.setCompoundDrawables(null, itsTeamImgGreen, null, null);
                itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
                        null, null);
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();

                // Intent aAddTeamIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamListActivity.class);
                // aAddTeamIntent.putExtra("aTeamList", itsTeamArray);
                // aAddTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
                // aAddTeamIntent.putExtra("teamActivity", "addTeam");
                // startActivity(aAddTeamIntent);

                Intent aAddTeamIntent1 = new Intent().setClass(
                        TeamViewActivity.this, TMTabMainActivity.class);
                aAddTeamIntent1.putExtra("aTeamList", itsTeamArray);
                aAddTeamIntent1.putExtra("aTeamIdList", itsTeamIdArray);
                aAddTeamIntent1.putExtra("itsLoginUserId", itsLoginUserId);
                startActivity(aAddTeamIntent1);

                // Intent aCreateTeamIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamListActivity.class);
                // aCreateTeamIntent.putExtra("aTeamList", itsTeamArray);
                // aCreateTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
                // startActivity(aCreateTeamIntent);

                break;

            case R.id.tvTeamActFollowers:
                itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
                itsTeamView.setCompoundDrawables(null, itsTeamImgBlack, null, null);
                itsFollowersView.setCompoundDrawables(null, itsFollowerImgGreen,
                        null, null);
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();

                Intent aCreateFollowerIntent = new Intent().setClass(
                        TeamViewActivity.this, FollowersListActivity.class);
                // aCreateFollowerIntent.putExtra("aTeamList", itsFollowersArray);
                startActivity(aCreateFollowerIntent);

                break;

            case R.id.tvAddTeam:
                itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
                itsTeamView.setCompoundDrawables(null, itsTeamImgGreen, null, null);
                itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
                        null, null);
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();

                // Intent aAddTeamIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamListActivity.class);
                // aAddTeamIntent.putExtra("aTeamList", itsTeamArray);
                // aAddTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
                // aAddTeamIntent.putExtra("teamActivity", "addTeam");
                // startActivity(aAddTeamIntent);

                Intent aAddTeamIntent = new Intent().setClass(
                        TeamViewActivity.this, TMTabMainActivity.class);
                aAddTeamIntent.putExtra("aTeamList", itsTeamArray);
                aAddTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
                aAddTeamIntent.putExtra("itsLoginUserId", itsLoginUserId);
                startActivity(aAddTeamIntent);

                break;
            case R.id.tvAddMember:
                // Niaz Check for Add Team Member Page
                itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
                itsTeamView.setCompoundDrawables(null, itsTeamImgGreen, null, null);
                itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
                        null, null);
                // itsProgressDialog = new ProgressDialog(this);
                // itsProgressDialog.setMessage("Please Wait......");
                // itsProgressDialog.show();

                int position = itsActionBar.getSelectedTab().getPosition();
                System.out.println(position);
                // Intent aAddTeamMemberIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamListActivity.class);
                // aAddTeamMemberIntent.putExtra("tabPosition", position);
                // aAddTeamMemberIntent.putExtra("aTeamList", itsTeamArray);
                // aAddTeamMemberIntent.putExtra("aTeamIdList", itsTeamIdArray);
                // aAddTeamMemberIntent.putExtra("teamActivity", "addTeamMember");
                // startActivity(aAddTeamMemberIntent);

                // Intent aAddTeamMemberIntent = new Intent().setClass(
                // TeamViewActivity.this, TeamMemberAddActivity.class);

                Intent aAddTeamMemberIntent = new Intent().setClass(
                        TeamViewActivity.this, ContactsActivity.class);
                // aAddTeamMemberIntent.putExtra("tabPosition", position);
                // aAddTeamMemberIntent.putExtra("aTeamList", itsTeamArray);
                // aAddTeamMemberIntent.putExtra("aTeamIdList", itsTeamIdArray);
                // aAddTeamMemberIntent.putExtra("teamActivity", "addTeamMember");
                // aAddTeamMemberIntent.putExtra("parentActivity",
                // "Add Team Member");
                aAddTeamMemberIntent.putExtra("teamName", itsTeamArray[position]);
                startActivity(aAddTeamMemberIntent);

                break;
            case R.id.tvMan:
                // MyAppConstants.IS_FOLLOW_ME = true;
                // MyAppConstants.IS_MAP_SHOW_ALL = false;
                // Toast.makeText(getApplicationContext(),
                // "Changing View to Is Follow ME", Toast.LENGTH_LONG).show();
                // itsTvMan.setBackgroundResource(R.drawable.team_toggle);

                if (isMEToggleOn) {
                    MyAppConstants.map_view_type = 3;
                    isMEToggleOn = false;
                } else {
                    MyAppConstants.map_view_type = 4;
                    isMEToggleOn = true;
                }
                getMapViewType();
                if (MyAppConstants.map_view_type == 3) {
                    Toast.makeText(this, "Follow Me", Toast.LENGTH_LONG).show();
                } else if (MyAppConstants.map_view_type == 4) {
                    Toast.makeText(this, "Don't follow Me", Toast.LENGTH_LONG)
                            .show();
                }
                itsTeamMapFragment.showMyMarkerAndAccuracyCircle("toggle");
                break;

            case R.id.btnMyLoc:
                isMEToggleOn = true;

                // MyAppConstants.IS_FOLLOW_ME = true;
                // MyAppConstants.IS_MAP_SHOW_ALL = false;
                // Toast.makeText(getApplicationContext(),
                // "Changing View to Is Follow ME", Toast.LENGTH_LONG).show();
                // itsTvMan.setBackgroundResource(R.drawable.team_toggle);
                // getMapViewType();
                MyAppConstants.map_view_type = 2;
                getMapViewType();
                itsTeamMapFragment.showMyMarkerAndAccuracyCircle("toggle");
                Toast.makeText(getApplicationContext(), "Showing My location ",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnStickyMarker:
                isMEToggleOn = true;

                if (isStickyToggleOn) {
                    MyAppConstants.map_view_type = 5;
                    isStickyToggleOn = false;
                    if (itsTeamMapFragment.isMarker.equals("true")) {
                        MyAppConstants.map_view_type = 5;
//                        itsTeamMapFragment.showMyStickyMarker("toggle");
                        itsTeamMapFragment.animateMapByViewType("toggle");
                        Toast.makeText(getApplicationContext(),
                                "Showing Sticky Marker ", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No marker ",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyAppConstants.map_view_type = 4;
                    isStickyToggleOn = true;
                }
                // MyAppConstants.IS_FOLLOW_ME = true;
                // MyAppConstants.IS_MAP_SHOW_ALL = false;
                // Toast.makeText(getApplicationContext(),
                // "Changing View to Is Follow ME", Toast.LENGTH_LONG).show();
                // itsTvMan.setBackgroundResource(R.drawable.team_toggle);
                // getMapViewType();

                getMapViewType();
                break;
        }

        // Stop Timer when screen navigates to other Pages
        // if ((theView.getId() != R.id.tvMan) && (itsTeamMapFragment != null))
        // itsTeamMapFragment.stopRefreshTimer();
    }

    @Override
    protected void onRestart() {
        if (itsTeamMapFragment != null) {
            itsTeamMapFragment.stopRefreshTimer();
            itsTeamMapFragment.startRefreshTimer();
        }
        Log.d("TVA", "I am doing restart");
        // MyAppConstants.map_view_type = 1;
        if (MyAppConstants.isRefreshMap) {
            getAndLoadTeamInfo();
            MyAppConstants.isRefreshMap = false;
            MyAppConstants.selTab = 0;
        } else if (MyAppConstants.isProfileChanges
                && itsTeamMapFragment != null) {
//			itsTeamMapFragment.getAndSetMyTeamMembersLoc();
            itsTeamMapFragment.getMarkers("map");
        }
        MyAppConstants.isProfileChanges = false;
        super.onRestart();

    }

    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("my-event"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            if (itsTeamMapFragment != null) {
                itsTeamMapFragment
                        .showMyMarkerAndAccuracyCircle("locationupdate");
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    // @Override
    // protected void onResume() {
    // // if (itsTeamMapFragment != null) {
    // // itsTeamMapFragment.startRefreshTimer();
    // // }
    // // getAndLoadTeamInfo();
    // itsActionBar.selectTab(itsActionBar.getTabAt(MyAppConstants.selTab));
    // super.onRestart();
    //
    // }

    /**
     * Method to get users team information and design action bar accordingly
     */
    private void getAndLoadTeamInfo() {
        try {
            JSONObject aRequestJson = new JSONObject();
            if (itsLoginUserId != null) {
                aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
            } else {
                aRequestJson.put(MyAppConstants.MEMBER_ID, 0);
                return;
            }

            // AppAsyncTask aAsyncTask = new AppAsyncTask(TeamViewActivity.this,
            // MyAppConstants.GET_MY_TEAM, MyAppConstants.API_POST_TYPE,
            // MyAppConstants.TEAM_LOADING);
            AppAsyncTask aAsyncTask = new AppAsyncTask(TeamViewActivity.this,
                    MyAppConstants.GET_MY_TEAM, MyAppConstants.API_POST_TYPE);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    itsResult = theResult;
                    Log.d("TVA", "getAndLoadTeamInfo=" + theResult);
                    if (theResult != null) {
                        loadTeamInfo(theResult);
                    } else {
                        // Toast.makeText(TeamViewActivity.this,
                        // MyAppConstants.CONNECTION_ERROR,
                        // Toast.LENGTH_LONG).show();
                    }
                }

                /**
                 * Method to load action bar with team information
                 *
                 * @param theTeamJson
                 */
                @SuppressLint("NewApi")
                private void loadTeamInfo(String theTeamJson) {

                    try {

                        // Users team information in JSON format
                        JSONObject aTeamJson = new JSONObject(theTeamJson);

                        // Retrieving status from response JSON
                        String aResponseStatus = aTeamJson
                                .getString(MyAppConstants.STATUS);

                        // If API response status is success
                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {
                            // String result_val = aTeamJson
                            // .getString(MyAppConstants.RESULT);
                            // String[] result = result_val.split("~");
                            int teamCount = Integer.parseInt(aTeamJson
                                    .getString(MyAppConstants.MESSAGE));
                            //
                            MyAppConstants.TEAM_CNT_FIRST = teamCount;
                            // String team_result = result[0];

                            itsTeamArrayJson = aTeamJson
                                    .getJSONArray(MyAppConstants.RESULT);

                            String[] aTeamNameArray = getTeamNames(itsTeamArrayJson);
                            itsTeamArray = aTeamNameArray;
                            String[] aTeamIdArray = getTeamId(itsTeamArrayJson);
                            itsTeamIdArray = aTeamIdArray;

                            String[] aTeamStickyArray = getTeamSticky(itsTeamArrayJson);
                            itsTeamStickyArray = aTeamStickyArray;

                            String[] aTeamRecordStatusArray = getTeamRecordStatus(itsTeamArrayJson);
                            itsTeamRecordStatusArray = aTeamRecordStatusArray;
                            // create new tabs and display team name in tabs
                            itsActionBar.removeAllTabs();
                            // int tabSelect = MyAppConstants.selTab;
                            getTeams(itsTeamArrayJson);
                            Log.d("TVA", "2=" + MyAppConstants.selTab);
                            // for (Map.Entry<String, String> entry : hMap
                            // .entrySet()) {
                            // Tab tab = itsActionBar
                            // .newTab()
                            // .setText(
                            // "      " + entry.getKey()
                            // + "     ")
                            // .setTabListener(TeamViewActivity.this);
                            // if (entry.getValue().equals("true")) {
                            // tab.setIcon(R.drawable.sticky_icon);
                            // }
                            // itsActionBar.addTab(tab);
                            // }
                            for (String aTabName : aTeamNameArray) {

                                Tab tab = itsActionBar
                                        .newTab()
                                        .setText("      " + aTabName + "     ")
                                                // .setTabListener(TeamViewActivity.this);
                                        .setTabListener(
                                                new MyDummyTabListener());

                                String isSticky = hMap.get(aTabName);
                                String recordingStatus = hMapRecord
                                        .get(aTabName);
                                String memberid = hMapOwner.get(aTabName);
                                if (isSticky.equals("true")) {
                                    tab.setIcon(R.drawable.pin);

                                }

                                tab.setContentDescription(recordingStatus);
                                if (recordingStatus != null) {
                                    if (recordingStatus.equals("true")) {
                                        if (itsLoginUserId.equals(memberid)) {
                                            tab.setIcon(R.drawable.record);
                                        }

                                    }
                                }
                                itsActionBar.addTab(tab);
                            }
                            Log.d("TVA", "3=" + MyAppConstants.selTab);
                            //
                            // for (String aStickyTeam : aTeamStickyArray) {
                            // if (aStickyTeam.equals("true")) {
                            // itsActionBar.getSelectedTab().setIcon(
                            // R.drawable.sticky_icon);
                            // }

                            // }
                            itsActionBar
                                    .setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                            Log.d("TVA", "I am doing restart selecting tab"
                                    + MyAppConstants.selTab);
                            // Log.d("TVA", "isTeamMemberCallFirstTime="
                            // + isTeamMemberCallFirstTime);
                            Log.d("TVA", "I am doing after selecting tab"
                                    + MyAppConstants.selTab);

                            // itsActionBar.getTabAt(MyAppConstants.selTab).setTabListener(
                            // new MyDummyTabListener());

                            // Overriding this with shared pref
                            int selTab = itsSharedPreference.getInt(
                                    MyAppConstants.USER_SEL_TAB, -1);
                            Log.d("TVA", "itsSharedPreference selTab ="
                                    + selTab);
                            if ((itsActionBar.getTabCount() > selTab)
                                    && (selTab != -1)) {
                                itsActionBar.selectTab(itsActionBar
                                        .getTabAt(selTab));
                            } else if ((itsActionBar.getTabCount() > MyAppConstants.selTab)
                                    && (MyAppConstants.selTab != -1)) {

                                itsActionBar.selectTab(itsActionBar
                                        .getTabAt(MyAppConstants.selTab));
                            } else {
                                MyAppConstants.selTab = 0;
                                itsActionBar.selectTab(itsActionBar
                                        .getTabAt(MyAppConstants.selTab));
                            }

                            // itsActionBar.selectTab(itsActionBar
                            // .getTabAt(MyAppConstants.selTab));
                            recordStatus = itsActionBar.getSelectedTab()
                                    .getContentDescription();
                            // itsActionBar.getTabAt(MyAppConstants.selTab).setTabListener(
                            // TeamViewActivity.this);
                            int cnt = itsActionBar.getTabCount();
                            for (int i = 0; i < cnt; i++) {
                                itsActionBar.getTabAt(i).setTabListener(
                                        TeamViewActivity.this);
                            }
                            if (isTeamMemberCallFirstTime) {
                                // itsActionBar.selectTab(itsActionBar.getTabAt(0));
                                if ((itsActionBar.getTabCount() > selTab)
                                        && (selTab != -1)) {
                                    itsActionBar.selectTab(itsActionBar
                                            .getTabAt(selTab));
                                } else {

                                    itsActionBar.selectTab(itsActionBar
                                            .getTabAt(MyAppConstants.selTab));
                                }
                                isTeamMemberCallFirstTime = false;
                            }
                            // Send logined user's current location to server
                            // for every n minutes
                            // if (itsLocationUpdates == null) {
                            // itsLocationUpdates = new LocationUpdates(
                            // TeamViewActivity.this, itsGoogleMap);

                            // }
                        }
                        // If API response status is failure
                        else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS)) {
                            String aResponseMsg = aTeamJson
                                    .getString(MyAppConstants.MESSAGE);
                        }
                    } catch (JSONException theJsonException) {
                        theJsonException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from getMyTeam webservice");
                    }
                }

            });
            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception while constructing request for getMyTeam webservice");
        }
    }

    /**
     * Method to get users team information and design action bar accordingly
     */

    @Override
    public boolean onCreateOptionsMenu(Menu theMenu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_teamview_menu, theMenu);

        MenuItem myItem = theMenu.findItem(R.id.invitations);
        MenuItem team = theMenu.findItem(R.id.myTeams);
        int invite_cnt = 0;
        if (MyAppConstants.INVITATION_CNT == -1) {
            invite_cnt = 0;
        } else {
            invite_cnt = MyAppConstants.INVITATION_CNT;
        }

        myItem.setTitle(myItem.getTitle() + "(" + invite_cnt + ")");
        if(intSupported==0){
            itsAddTeam.setVisibility(View.INVISIBLE);
            team.setVisible(false);
        }else{
            itsAddTeam.setVisibility(View.VISIBLE);
            team.setVisible(true);
        }
        // MenuItem myItem1 = theMenu.findItem(R.id.myFollowers);
        // myItem1.setTitle(myItem1.getTitle() + "("
        // + MyAppConstants.FOLLOWERS_CNT + ")");
        // TextView txtInviteCnt = (TextView) findViewById(R.id.invitations);
        // txtInviteCnt.setText("(" + MyAppConstants.INVITATION_CNT + ")");
        //
        // TextView txtFolowersCnt = (TextView) findViewById(R.id.myFollowers);
        // txtFolowersCnt.setText("(" + MyAppConstants.FOLLOWERS_CNT + ")");

        return super.onCreateOptionsMenu(theMenu);
    }

    @Override
    protected void onStop() {
        if (itsLocationUpdates != null)
            itsLocationUpdates.disconnectLocationClient();
        super.onStop();
    }

    /**
     * Method to get team names from a team array
     *
     * @param theTeamArray
     * @return aTeamNameArray
     */
    private String[] getTeamNames(JSONArray theTeamArray) {
        String[] aTeamNameArray = new String[theTeamArray.length()];
        try {
            for (int index = 0; index < theTeamArray.length(); index++) {
                aTeamNameArray[index] = theTeamArray.getJSONObject(index)
                        .getString(TeamBuilder.NAME);
            }
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception when constructing a team name array");
        }
        return aTeamNameArray;
    }

    protected void getTeams(JSONArray theTeamArray) {

        try {
            for (int index = 0; index < theTeamArray.length(); index++) {

                String teamName = theTeamArray.getJSONObject(index).getString(
                        TeamBuilder.NAME);
                String isSticky = theTeamArray.getJSONObject(index).getString(
                        "isSticky");
                String isRecording = theTeamArray.getJSONObject(index)
                        .getString("isRecording");
                String memberId = theTeamArray.getJSONObject(index).getString(
                        "memberId");
                hMap.put(teamName, isSticky);
                hMapRecord.put(teamName, isRecording);
                hMapOwner.put(teamName, memberId);

            }
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception when constructing a team name array");
        }
    }

    /**
     * Method to get team id from a team array
     *
     * @param theTeamArray
     * @return aTeamIdArray
     */
    private String[] getTeamId(JSONArray theTeamArray) {
        String[] aTeamIdArray = new String[theTeamArray.length()];
        try {
            for (int index = 0; index < theTeamArray.length(); index++) {
                aTeamIdArray[index] = theTeamArray.getJSONObject(index)
                        .getString(TeamBuilder.TEAMID);
            }
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception when constructing a team name array");
        }
        return aTeamIdArray;
    }

    /**
     * Method to get team sticky from a team array
     *
     * @param theTeamArray
     * @return aTeamStickyArray
     */
    private String[] getTeamSticky(JSONArray theTeamArray) {
        String[] aTeamStickyArray = new String[theTeamArray.length()];
        try {
            for (int index = 0; index < theTeamArray.length(); index++) {
                aTeamStickyArray[index] = theTeamArray.getJSONObject(index)
                        .getString("isSticky");
            }
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception when constructing a team name array");
        }
        return aTeamStickyArray;
    }

    /**
     * Method to get team record Status from a team array
     *
     * @param theTeamArray
     * @return aTeamRecordStatusArray
     */
    private String[] getTeamRecordStatus(JSONArray theTeamArray) {
        String[] aTeamRecordStatusArray = new String[theTeamArray.length()];
        try {
            for (int index = 0; index < theTeamArray.length(); index++) {
                aTeamRecordStatusArray[index] = theTeamArray.getJSONObject(
                        index).getString("isRecording");
            }
        } catch (JSONException theJsonException) {
            theJsonException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception when constructing a team name array");
        }
        return aTeamRecordStatusArray;
    }

    @Override
    public boolean onMarkerClick(final Marker theMarker) {
        if (theMarker.equals(MyAppConstants.myMarker)) {
            return false;
        }

        // Normal Marker
        if (!theMarker.equals(MyAppConstants.myMarker)
                && (theMarker.getTitle() != null)) {

            final Dialog aMemberDialog = new Dialog(TeamViewActivity.this,
                    R.style.DialogSlideAnim);
            aMemberDialog.setContentView(R.layout.member_dialog);

            TextView aMemberTextName = (TextView) aMemberDialog
                    .findViewById(R.id.tvMemberName);
            Button aTextButton = (Button) aMemberDialog
                    .findViewById(R.id.bMemberText);
            Button aPushButton = (Button) aMemberDialog
                    .findViewById(R.id.bMemberPush);
            Button aCallButton = (Button) aMemberDialog
                    .findViewById(R.id.bMemberCall);
            Button aRemoveButton = (Button) aMemberDialog
                    .findViewById(R.id.bMemberRemove);

            String[] res = theMarker.getTitle().split("~");
            String title = res[0];
            final String phone = res[1];

            aMemberTextName.setText("Say hello to " + title + "!");
            aMemberDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(0));
            aMemberDialog.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            aMemberDialog.getWindow().setGravity(Gravity.BOTTOM);
            aMemberDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            aMemberDialog.show();
            ImageView aCancelButton = (ImageView) aMemberDialog
                    .findViewById(R.id.bCancelMemberDialog);
            aCancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    aMemberDialog.dismiss();
                }
            });

            aTextButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Uri sms_uri = Uri.parse("smsto:" + phone);
                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO,
                            sms_uri);
                    startActivity(sms_intent);
                }
            });

            // aPushButton.setOnClickListener(new OnClickListener() {
            // public void onClick(View v) {
            // // startActivity(new Intent(Intent.ACTION_VIEW,
            // // Uri.parse("sms:"
            // // + theMarker.getSnippet())));
            // }
            // });

            aCallButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    startActivity(callIntent);
                }
            });

            // aRemoveButton.setOnClickListener(new OnClickListener() {
            // public void onClick(View v) {
            // }
            // });
        } else {

            StickyMarkerBehaviorDialog dialog = new StickyMarkerBehaviorDialog(
                    this);
            dialog.show();
        }

        return true;
    }

    /**
     * Tab selection Methods
     */

    @Override
    public void onTabReselected(Tab theTab,
                                FragmentTransaction theFragmentTransaction) {
        populateTeam(theTab);
    }

    @Override
    public void onTabSelected(Tab theTab,
                              FragmentTransaction theFragmentTransaction) {
        // aEditor.putFloat(MyAppConstants.USER_SEL_ZOOM, 16.0f);
        aEditor.putInt(MyAppConstants.USER_SEL_TAB, theTab.getPosition());
        aEditor.commit();
        populateTeam(theTab);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    // @Override
    // public void onBackPressed() {
    // Intent intent = new Intent(Intent.ACTION_MAIN);
    // intent.addCategory(Intent.CATEGORY_HOME);
    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // startActivity(intent);
    //
    // super.onBackPressed();
    // }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Remove Service
                                stopService(new Intent(getBaseContext(),
                                        LocationUpdates.class));
                                // stopService(new Intent(getBaseContext(),
                                // LocationService.class));
                                if (itsLocationUpdates != null)
                                    itsLocationUpdates
                                            .disconnectLocationClient();
                                mHandlerTaskServiceStop = new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("tmf",
                                                "calling mHandlerTaskServiceStop.......");
                                        // Get and set team members location in
                                        // map

                                        // finish();
//                                        moveTaskToBack(true);
//
//                                        Intent amyProfileIntent = new Intent().setClass(
//                                                TeamViewActivity.this, WelcomeActivity.class);
//                                        startActivity(amyProfileIntent);
//
//
//                                        android.os.Process
//                                                .killProcess(android.os.Process
//                                                        .myPid());
//                                        System.exit(1);
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                };
                                itsHandlerServiceStop.postDelayed(
                                        mHandlerTaskServiceStop, 1500);

                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getMapViewType() {
        // MyAppConstants.map_view_type++;
        // if (MyAppConstants.map_view_type > 3) {
        // MyAppConstants.map_view_type = 1;
        // }

        switch (MyAppConstants.map_view_type) {
            // case 1:
            // itsTvMan.setBackgroundResource(R.drawable.team_toggle);
            // break;
            case 2:
                itsTvMan.setBackgroundResource(R.drawable.map_follow_me_no);
                itsStickyMarker.setBackgroundResource(R.drawable.marker);
                break;
            case 3:
                itsTvMan.setBackgroundResource(R.drawable.map_follow_me);
                itsStickyMarker.setBackgroundResource(R.drawable.marker);
                break;
            case 4:
                itsTvMan.setBackgroundResource(R.drawable.map_follow_me_no);
                itsStickyMarker.setBackgroundResource(R.drawable.marker);
                break;
            case 5:
                itsTvMan.setBackgroundResource(R.drawable.map_follow_me_no);
                itsStickyMarker.setBackgroundResource(R.drawable.marker_on);
                break;
            default:
                break;
        }

    }

    private void populateTeam(Tab theTab) {
        MyAppConstants.myMarker = null;
        MyAppConstants.myStickyMarker = null;
        MyAppConstants.LAT_MARKER = 0;
        MyAppConstants.LNG_MARKER = 0;
        MyAppConstants.LAT_MY_MARKER_PREV_LAT = 0;
        MyAppConstants.LAT_MY_MARKER_PREV_LNG = 0;
        MyAppConstants.NAME_MARKER = null;
        if (itsTeamMapFragment != null && itsTeamMapFragment.hmapMemberMarkerPrev != null) {
            for (Map.Entry<String, Marker> entry : itsTeamMapFragment.hmapMemberMarkerPrev.entrySet()) {
                entry.getValue().remove();
            }
            itsTeamMapFragment.hmapMemberMarkerPrev.clear();
            itsTeamMapFragment.hmapMemberMarkerPrevLatLng.clear();
            itsTeamMapFragment.hmapMemberMarkerPrevRotation.clear();

        }
        if (itsTeamMapFragment != null && itsTeamMapFragment.hmapMemberCirclePrev != null) {
            for (Map.Entry<String, Circle> entry : itsTeamMapFragment.hmapMemberCirclePrev.entrySet()) {
                entry.getValue().remove();
            }

            itsTeamMapFragment.hmapMemberCirclePrev.clear();
        }

//        if (itsTeamMapFragment != null && itsTeamMapFragment.hmapMemberMarkerPrev != null)
//            itsTeamMapFragment.hmapMemberMarkerPrev.clear();
        Log.d("TVA",
                "onTabSelected theTab.getPosition()=  " + theTab.getPosition());
        JSONObject itsSelectedTeamJson = null;
        String isStickyTeam = "";
        try {
            itsSelectedTeamJson = itsTeamArrayJson.getJSONObject(theTab
                    .getPosition());
            isStickyTeam = itsSelectedTeamJson.getString("isSticky");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isStickyTeam.equals("true")) {
            itsStickyMarker.setVisibility(View.VISIBLE);
        } else {
            itsStickyMarker.setVisibility(View.GONE);
        }

        MyAppConstants.selTab = theTab.getPosition();
        Log.d("TVA", "MyAppConstants.selTab=  " + MyAppConstants.selTab);
        // MyAppConstants.IS_MAP_SHOW_ALL = true;
        // MyAppConstants.IS_FOLLOW_ME = false;
        Log.i(this.getClass().getName(),
                "Selected tab is: " + theTab.getPosition());
        if (itsTeamMapFragment != null)
            itsTeamMapFragment.stopRefreshTimer();
        // Upon Tab press
        MyAppConstants.map_view_type = 1;
        itsTvMan.setBackgroundResource(R.drawable.map_follow_me_no);
        itsStickyMarker.setBackgroundResource(R.drawable.marker);
        // getMapViewType();
        itsTeamMapFragment = new TeamMapFragment(TeamViewActivity.this,
                theTab.getPosition(), itsGoogleMap, itsNormalMapView,
                itsHybridMapView, itsSatelliteMapView, itsTeamArrayJson,
                itsLoginUserId);

		/* Team code Mapping */
        teamCodes = new ArrayList<TeamCode>();
        try {
            JSONArray jsonArray = itsSelectedTeamJson.getJSONArray("teamCodes");
            for (int index = 0; index < jsonArray.length(); index++) {
                TeamCode teamCode = new TeamCode();
                teamCode.setTeamId(jsonArray.getJSONObject(index).getLong(
                        "teamId"));
                teamCode.setCode(jsonArray.getJSONObject(index).getLong("code"));
                teamCode.setDescription(jsonArray.getJSONObject(index)
                        .getString("description"));
                teamCodes.add(teamCode);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        itsTeamMapFragment.startRefreshTimer();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "Download complete!",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onMarkerDrag(Marker theMarker) {

        // MyAppConstants.myStickyMarker.remove();
    }

    @Override
    public void onMarkerDragEnd(Marker theMarker) {
        // MyAppConstants.myStickyMarker.remove();
        //
        // double lat = theMarker.getPosition().latitude;
        // double lng = theMarker.getPosition().longitude;
        // insertStickyTeamMarkerInfo(lat, lng);

    }

    @Override
    public void onMarkerDragStart(Marker theMarker) {
        // MyAppConstants.myStickyMarker.remove();

    }

    @Override
    public void onMapLongClick(LatLng point) {

        if (TeamMapFragment.isStickyTeam.equals("true")) {
            // if (MyAppConstants.ISOWNER) {
            View marker1 = ((LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.sticky_marker, null);
            ImageView sticky_marker_img = (ImageView) marker1
                    .findViewById(R.id.stikcy_marker_img);
            // marker_img.getBackground().setColorFilter(-9175296,
            // Mode.SRC_IN);
            sticky_marker_img.setBackgroundResource(R.drawable.pin_red);
            if (MyAppConstants.myStickyMarker != null) {
                MyAppConstants.myStickyMarker.remove();
            }
            MyAppConstants.myStickyMarker = itsGoogleMap
                    .addMarker(new MarkerOptions()
                            .position(
                                    new LatLng(point.latitude, point.longitude))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(TeamMapFragment
                                            .createDrawableFromView(this,
                                                    marker1))).draggable(true));

            insertStickyTeamMarkerInfo(point.latitude, point.longitude, null,
                    null, null, null);

        }
        // }
    }

    public void insertStickyTeamMarkerInfo(double markerlat, double markerlng,
                                           String markerCode, String markerCodeDesc, String markerDesc, String markerName) {
        try {
            JSONObject aRequestJson = new JSONObject();

            // if (MyAppConstants.ISOWNER) {
            aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);

            // } else {
            if (MyAppConstants.STICKY_MEMBERID != 0) {
                aRequestJson.put(MyAppConstants.FOLLOWER_ID,
                        MyAppConstants.STICKY_MEMBERID);
            } else {
                aRequestJson.put(MyAppConstants.FOLLOWER_ID, 0);
            }

            // }
            aRequestJson.put(MyAppConstants.TEAM_ID,
                    TeamMapFragment.itsSelectedTeamId);
            aRequestJson.put(MyAppConstants.NAME,
                    TeamMapFragment.itsSelectedTeamName);

            aRequestJson.put(MyAppConstants.MARKER_LAT, markerlat);
            aRequestJson.put(MyAppConstants.MARKER_LNG, markerlng);
            if (markerCode != null) {
                aRequestJson.put(MyAppConstants.MARKER_CODE, markerCode);
            }
            if (markerCodeDesc != null) {
                aRequestJson
                        .put(MyAppConstants.MARKER_CODEDESC, markerCodeDesc);
            }
            if (markerName != null) {
                aRequestJson.put(MyAppConstants.MARKER_NAME, markerName);
            }
            if (markerDesc != null) {
                aRequestJson.put(MyAppConstants.MARKER_DESC, markerDesc);
            }
            SharedPreferences aSharedPreference = getSharedPreferences(
                    MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
            String lat = aSharedPreference.getString(MyAppConstants.LAT, "");
            String lng = aSharedPreference.getString(MyAppConstants.LNG, "");
            double dLat = Double.parseDouble(lat);
            double dLng = Double.parseDouble(lng);
            aRequestJson.put(MyAppConstants.LAT, dLat);
            aRequestJson.put(MyAppConstants.LNG, dLng);
            aRequestJson.put(MyAppConstants.DEVICETIME, new Date().getTime());
            Log.d("TVA", "aRequestJson.toString()=" + aRequestJson.toString());

            // AppAsyncTask aAsyncTask = new AppAsyncTask(TeamViewActivity.this,
            // MyAppConstants.GET_MY_TEAM, MyAppConstants.API_POST_TYPE,
            // MyAppConstants.TEAM_LOADING);
            AppAsyncTask aAsyncTask = new AppAsyncTask(TeamViewActivity.this,
                    MyAppConstants.INSERT_STICKY_MARKER,
                    MyAppConstants.API_POST_TYPE);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    try {
                        Log.d("Response", theResult);
                        String aResponseStatus, aResponseMsg;
                        JSONObject aResponseJson = new JSONObject(theResult);
                        aResponseStatus = aResponseJson
                                .getString(MyAppConstants.STATUS);
                        aResponseMsg = aResponseJson
                                .getString(MyAppConstants.MESSAGE);

                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {

                            itsTeamMapFragment.getMarkers("teamView");

                        }
                        //
                        else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS))
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        Toast.makeText(getApplicationContext(),
                                MyAppConstants.CONNECTION_ERROR,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJSONException) {
            theJSONException.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    MyAppConstants.JSON_ERROR_RES, Toast.LENGTH_LONG).show();
        } catch (Exception theException) {
            theException.printStackTrace();
        }
    }

    class MyDummyTabListener implements TabListener {
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }
    }

}
