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

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Class to declare constants
 *
 * @author senthil_kumaran
 */
public class MyAppConstants {
    // Application constants
    public static final String APP_PREFERENCE = "TamPreference";
    public static final String ERROR_MSG_REQUIRED_ALL_VALUE = "Please provide all values";
    public static final String ERROR_MSG_VALID_VALUE = "Please provide valid values";
    public static final String CONNECTION_ERROR = "Connection error...! Please try again";
    public static final String JSON_ERROR_RES = "Error retriving response from server...! Please try again";
    public static final String JSON_ERROR_REQ = "Error sending request to server...! Please try again";

    // API
//    public static final String API_URL = "http://192.168.15.107:9090/SecurityTeam";// local
    // 202

    // ip
    // public static final String API_URL =
    // "http://65.111.165.215:9090/SecurityTeam";// prod

    // ip
    public static final String API_URL =
            "http://api.thosearoundme.com:9090/SecurityTeam";// prod

    // ip
    public static final String API_CONTENT_TYPE = "Content-Type";
    public static final String API_JSON_CONTENT_TYPE = "application/json";
    public static final String API_POST_TYPE = "POST";
    public static final String API_DELETE_TYPE = "DELETE";

    // API URI

    public static final String LOGIN = "/login";
    public static final String SIGNUP = "/register";
    public static final String GET_MY_TEAM = "/getmyteams";
    public static final String GET_MY_FOLLOWERS = "/getmyfollowers";
    public static final String GET_TEAM_MEMBERS = "/getteammembers";
    public static final String UPDATE_LOCATION = "/updatememberlocation";
    public static final String CREATE_TEAM = "/createteam";
    public static final String ADD_TEAM_MEMBER = "/addteammember";

    public static final String VALIDATE_REGISTRATION = "/validateregistration";
    public static final String FORGOT_PASSWORD = "/forgotpassword";
    public static final String DELETE_USER = "/deleteuser/";
    public static final String GET_MY_INVITATION = "/dogetmyinvitation";
    public static final String GET_AVAIL_TEAM = "/getavailableteams";
    public static final String UPDATE_MY_INVITATION = "/updatemyinvitation";
    public static final String REMOVE_TEAM_MEMBER = "/removeteammember";
    public static final String REMOVE_TEAM = "/removeteam";

    public static final String ADD_TEAM_MEMBER_NEW = "/addteammembernew";
    public static final String GET_MY_INVITATION_NEW = "/dogetmyinvitationnew";
    public static final String GET_MY_FOLLOWERS_NEW = "/getmyfollowersnew";
    public static final String UPDATE_MY_INVITATION_NEW = "/updatemyinvitationnew";
    public static final String REMOVE_MY_INVITATIONS = "/removemyinvitations";
    public static final String REMOVE_MY_FOLLOWERS = "/removemyfollowers";
    public static final String MOVE_TEAM_MEMBER = "/moveteammember";
    public static final String LOAD_INVITATION = "/loadinvitation";
    public static final String GET_MEMBER_FOLLOWERS = "/getmemberfollowers";
    public static final String UPDATE_MEMBER_FOLLOWERS = "/updatememberfollowers";
    public static final String GET_FOLLOWER_COLOR_CODE = "/dogetmemberfollowerscolorcode";
    public static final String INSERT_STICKY_MARKER = "/insertstickyteammarker";
    public static final String GET_MARKERS = "/getmarkers";
    public static final String UPDATE_RECORD = "/updatestickyrecords";
    public static final String GET_RECORDINGS = "/getrecordinginfo";

    public static final String GET_SETTINGS = "/getsettings";

    // -- Constants by activity --
    // Splash activity
    public static final long SPLASH_DURATION = 1000; // duration in milliseconds
    public static final String SERVER_URL = "serverUrl";
    // Login activity
    public static final String IS_USER_ALREADY_AUTHENTICATED = "isAuthenticated";
    public static final String USER_LOGIN_NAME = "LoginID";
    // TeamViewActivity
    // public static boolean IS_FIRST_TIME = true;
    public static int map_view_type = 1;
    public static Marker myMarker = null;
    public static Marker myStickyMarker = null;
    public static Circle myCircle = null;
    public static int selTab = 0;
    public static boolean isRefreshMap = false;
    public static String URL = "E:/TAM/APK";
    public static long STICKY_MEMBERID = 0;
    public static boolean recording = false;

    public static final String USER_SEL_TAB = "SEL_TAB";
    public static final String USER_SEL_ZOOM = "SEL_ZOOM";

    // My PROFILE
    public static final String USER_SEL_TIME = "USER_SEL_TIME";
    public static final String USER_SEL_ACCURACY = "USER_SEL_ACCURACY";
    public static boolean isProfileChanges = false;

    // TeamMapFragment
    public static HashMap<String, String> hmapMarkerId = new HashMap<String, String>();
    public static boolean IS_MAP_AUTOZOOM = true;
    public static boolean IS_ON_WELCOME = false;
    public static int INVITATION_CNT = -1;
    public static int FOLLOWERS_CNT = 0;
    public static int TEAM_CNT = -1;
    public static int TEAM_CNT_FIRST = -1;
    public static int TEAM_CNT_UPDATE = -1;
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String MARKER_NAME = "markerName";
    public static final String MARKER_LAT = "markerLat";
    public static final String MARKER_LNG = "markerLng";
    public static double LAT_USER = 0;
    public static double LNG_USER = 0;
    public static double LAT_MARKER = 0;
    public static double LNG_MARKER = 0;
    public static String NAME_MARKER = null;
    public static double LAT_MY_MARKER_PREV_LAT = 0;
    public static double LAT_MY_MARKER_PREV_LNG = 0;

    // -- API Keys --
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String FAILURE_STATUS = "FAILURE";
    // Login and sign up API Key
    public static final String MARKER_ID = "markerId";
    public static final String RESULT = "result";
    public static final String MEMBER_ID = "memberId";
    public static final String TEAM_ID = "teamId";
    public static final String USERNAME = "username";
    public static final String ALIAS = "alias";
    public static final String PASSWORD = "virus";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String LOCATIONS = "locations";
    public static final String NAME = "name";
    public static final String MEMBERS = "members";
    public static final String TOKEN = "token";
    public static final String OS_VERSION = "osVersion";
    public static final String ANDROID = "Android";
    public static final String FOLLOWER_ID = "followerId";
    public static final String INFO = "info";
    public static final String REQUEST_ID = "requestId";
    public static final String FROM_TEAM_ID = "from_teamId";
    public static final String TO_TEAM_ID = "to_teamId";
    public static final String ACTION = "action";
    public static final String LASTUPDDT = "lastUpdDt";
    public static final String COLOR = "color";
    public static final String TEAMS = "teams";
    public static final String ISSTICKY = "isSticky";
    public static boolean ISOWNER = false;
    public static String IMEI = "imei";
    public static final String DEVICENAME = "devicename";
    public static final String MARKER_CODE = "markerCode";
    public static final String MARKER_CODEDESC = "markerCodeDesc";
    public static final String MARKER_DESC = "markerDesc";

    // Team member API Key
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String DEVICETIME = "devicetime";
    public static double LAT_DUPLICATE = 0;
    public static double LNG_DUPLICATE = 0;
    public static final String DIRECTION_DEGREE = "direction_deg";
    public static final String ACCURACY = "accuracy";
    public static final String SPEED = "speed";
    public static final String TEAM_NA = "No Teams Are Created Yet";
    public static final String FOLLOWERS_NA = "You do not have any Followers";
    public static final String INVITATION_NA = "You do not have any Invitation";

    // Logger information for tags
    public static final String SPLASH_LOAD_INFO = "Loading Splash Activity...";
    public static final String TEAM_ACTIVITY_LOAD_INFO = "Loading TeamViewActivity...";

    // -- Progress Dialog Message --
    public static final String LOADING = "Please wait...";
    public static final String TEAM_LOADING = "Retrieving your team information. Please wait...";
    public static final String FOLLOWERS_LOADING = "Retrieving your followers information. Please wait...";
    public static final String TEAM_MEMBERS_LOADING = "Retrieving team members information. Please wait...";
    public static final String ACCOUNT_VALIDATION = "Activating your account and performing autologin. Please wait...";
    public static final String PHONENUM_VALIDATION = "Verifying Phone number. Please wait...";
    public static final String INVITAION_LOADING = "Retrieving your Invitation information. Please wait...";

    // SignUpActivity
    public static final String ERR_ENT_UNAME = "Enter Your Name.";
    public static final String ERR_ENT_PWD = "Enter Password.";
    public static final String ERR_ENT_PHONE = "Enter phone number.";
    public static final String ERR_PWD_MATCH = "Passwords did not match.";
    public static final String ERR_PWD_SHORT = "Password is too short.";
    public static final String ERR_ENT_EMAIL = "Enter Your Email.";
    public static final String ERR_VALID_EMAIL = "Enter a valid email Id.";
    public static final String ERR_VALID_PHONE = "Enter a vaild phone number.";
    public static final String ERR_VALID_COUNTRY = "Please choose any country";

    // Alert messages
    public static final String SUCCESS_REG_MESSAGE = "was created succesfully. Please check your message and click the confirmation link to activate your account";
    public static final String SUCCESS_REG_TITLE = "Registration Successful";
    public static final String PASSWORD_RESET_MESSAGE = "Your account's password reset link was succesfully sent to your phone number. Please check your message and click the link to reset your password";
    public static final String PASSWORD_RESET_TITLE = "Password Reset SMS";

    // API messages
    public static final String ALREADY_USER = "Your account has been already verified";
    public static final String FRESH_USER = "Your account has been verification was successful. Enjoy TAM!!";
    public static final String ACTIVATION_FAILURE = "Invalid activation token";

    // Add Team
    public static final String ERR_ENT_TEAMNAME = "Enter Team Name.";

    public static final String[] colors = {"#F61414", "#A4035C", "#2B0218",
            "#BD0BAE", "#460250", "#1804C5", "#5B5B8B", "#04888C", "#035906",
            "#462B03", "#C45908", "#027F90", "#284D52", "#278888", "#024232",
            "#0E2401", "#412D01", "#DB6D05", "#501C02", "#C1AAA5"};
    public static int colorIncrement = 0;

    public static String getMemberRandomColor() {
        if (colorIncrement > (colors.length - 1)) {
            colorIncrement = 0;
        }
        String val = colors[colorIncrement];
        colorIncrement++;
        return val;
    }

    public static void resetMemberRandomColor() {
        colorIncrement = 0;
    }

    public static final String BATCH_INTERVAL = "BATCH_INTERVAL";
    public static final String TEAMUPDATE_INTERVAL = "TEAMUPDATE_INTERVAL";
    public static final String SUPPORTED = "SUPPORTEED";

    public static float getRotation(LatLng startPosition, LatLng finalPosition) {
        Location locationA = new Location("A");
        locationA.setLatitude(startPosition.latitude);
        locationA.setLongitude(startPosition.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(finalPosition.latitude);
        locationB.setLongitude(finalPosition.longitude);
        float dist = locationA.distanceTo(locationB);
        Log.d("MyAppConstant", "dist =" + dist);
//        if (dist < 10) {
//            return 0;
//        } else
        return locationA.bearingTo(locationB);
//        double lat1 = startPosition.latitude;
//        double lat2 = finalPosition.latitude;
//        double lng1 = startPosition.latitude;
//        double lng2 = finalPosition.longitude;
//
//        double dLon = (lng2 - lng1);
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
//        double brng = Math.toDegrees((Math.atan2(y, x)));
//        brng = (360 - ((brng + 360) % 360) - 45);
////        brng = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
//        Log.d("TMF", "lat1=" + startPosition.toString() + ",lat2=" + finalPosition.toString() + ",bearing value=" + brng);
//        double deg = brng * (Math.PI / 180);
//        return (float) deg;
    }
}