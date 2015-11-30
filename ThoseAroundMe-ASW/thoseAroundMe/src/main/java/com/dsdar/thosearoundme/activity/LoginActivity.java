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
package com.dsdar.thosearoundme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity to validate users login credential and navigate them to either login
 * activity or team activity
 *
 * @author senthil_kumaran
 */
public class LoginActivity extends Activity implements OnClickListener {
    SharedPreferences itsSharedPreference;
    private Button itsLoginBtn, itsSignupBtn, itsDeleteUser;
    private EditText itsUserNameEt, itsPasswordEt;
    private TextView itsForgotPassword;
    private String itsUserNameVal = null, itsPasswordVal = null;
    private RelativeLayout itsRelativeLayout;
    boolean isInvited = false;

    @Override
    protected void onCreate(Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // requestWindowFeature(Window.FEATURE_ACTION_BAR);

        itsSharedPreference = getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        itsUserNameEt = (EditText) findViewById(R.id.etEMailIdLogIn);
        itsPasswordEt = (EditText) findViewById(R.id.etPwdLogIn);
        itsLoginBtn = (Button) findViewById(R.id.btnLogin);
        itsSignupBtn = (Button) findViewById(R.id.btnSignUpLogin);
        itsRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutLogin);
        itsForgotPassword = (TextView) findViewById(R.id.forgot_password);
        itsDeleteUser = (Button) findViewById(R.id.delete_user);

        itsLoginBtn.setOnClickListener(this);
        itsSignupBtn.setOnClickListener(this);
        itsForgotPassword.setOnClickListener(this);
        itsRelativeLayout.setOnClickListener(this);
        itsDeleteUser.setOnClickListener(this);

        // HArdcoded
//		itsUserNameEt.setText("9486090108");
//		itsPasswordEt.setText("test");
    }

    /**
     * Method to handle click events of button
     *
     * @param theView
     */
    @Override
    public void onClick(View theView) {
        if (false)
            throw new RuntimeException("This is a crash");
        switch (theView.getId()) {
            case R.id.btnLogin:
                validateAndLogin();
                break;
            case R.id.btnSignUpLogin:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.relativeLayoutLogin:
                Util.hideSoftKeyboard(LoginActivity.this);
                break;
            case R.id.forgot_password:
                startActivity(new Intent(LoginActivity.this,
                        ForgotPasswordActivity.class));
                break;
            case R.id.delete_user:
                removeUser();
                break;

        }
    }

    private void removeUser() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Member");
        alert.setMessage("Enter your phone number");
        final EditText input = new EditText(this);
        input.setRawInputType(InputType.TYPE_CLASS_PHONE);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String phoneNo = input.getEditableText().toString();
                deleteUser(phoneNo);
            }
        });
        alert.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * Method to validate user name and password. If these credentials are
     * valid, login API will be called to retrieve their information
     */
    private void validateAndLogin() {
        boolean isValidData = false;
        itsUserNameVal = itsUserNameEt.getText().toString().trim();
        itsPasswordVal = itsPasswordEt.getText().toString().trim();

        // itsUserNameVal = "senthil@gmail.com";
        // itsPasswordVal = "password";

        if (itsUserNameVal == null || itsPasswordVal == null
                || itsUserNameVal.equals("") || itsPasswordVal.equals(""))
            Toast.makeText(getApplicationContext(),
                    MyAppConstants.ERROR_MSG_VALID_VALUE, Toast.LENGTH_SHORT)
                    .show();
        else {
            // if (ValidationUtility.isValidNumber(itsUserNameVal))
            isValidData = true;
            // if (ValidationUtility.isValidEmailAddress(itsUserNameVal))
            isValidData = true;
            // else
            // Toast.makeText(getApplicationContext(),
            // MyAppConstants.ERROR_MSG_VALID_VALUE,
            // Toast.LENGTH_SHORT).show();
            if (isValidData)
                getSettings();
            login();
        }
    }

    /**
     * Method to call delete user API
     *
     * @param phoneNo
     */
    private void deleteUser(String phoneNo) {
        try {
            AppAsyncTask aAsyncTask = new AppAsyncTask(LoginActivity.this,
                    MyAppConstants.DELETE_USER + phoneNo + "/",
                    MyAppConstants.API_DELETE_TYPE, MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                String aResponseStatus,
                        aResponseMsg,
                        aUserId;

                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    try {
                        Log.d("Response", theResult);
                        JSONObject aResponseJson = new JSONObject(theResult);
                        aResponseStatus = aResponseJson
                                .getString(MyAppConstants.STATUS);
                        aResponseMsg = aResponseJson
                                .getString(MyAppConstants.MESSAGE);
                        Log.d("Response", theResult);
                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                        } else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS))
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    MyAppConstants.CONNECTION_ERROR,
                                    Toast.LENGTH_LONG).show();
                        } else {
                        }
                    }
                }
            });
            aAsyncTask.execute();
        } catch (Exception theException) {
            theException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception while constructing request for login webservice");
            Toast.makeText(getApplicationContext(),
                    MyAppConstants.JSON_ERROR_REQ, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to construct request JSON and call login API
     */
    private void login() {
        try {
            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.PHONE, itsUserNameVal);
            aRequestJson.put(MyAppConstants.PASSWORD, itsPasswordVal);

            AppAsyncTask aAsyncTask = new AppAsyncTask(LoginActivity.this,
                    MyAppConstants.LOGIN, MyAppConstants.API_POST_TYPE,
                    MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                String aResponseStatus,
                        aResponseMsg,
                        aUserId,
                        aPhone,
                        alias;
                int supported;

                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    try {
                        Log.d("Response", theResult);
                        JSONObject aResponseJson = new JSONObject(theResult);
                        aResponseStatus = aResponseJson
                                .getString(MyAppConstants.STATUS);
                        aResponseMsg = aResponseJson
                                .getString(MyAppConstants.MESSAGE);
                        aUserId = aResponseJson.getJSONObject(
                                MyAppConstants.RESULT).getString(
                                MyAppConstants.MEMBER_ID);
                        aPhone = aResponseJson.getJSONObject(
                                MyAppConstants.RESULT).getString(
                                MyAppConstants.PHONE);
                        alias = aResponseJson.getJSONObject(
                                MyAppConstants.RESULT).getString(
                                MyAppConstants.ALIAS);
                        supported = aResponseJson.getJSONObject(
                                MyAppConstants.RESULT).getInt(
                                "supported");

                        //int supported = 0;
                        Log.d("supported", "supported--->"+supported);
                        Log.d("Response", theResult);
                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {
                            // Saving user credentials to preference
                            SharedPreferences.Editor aEditor = itsSharedPreference
                                    .edit();
                            aEditor.putString(MyAppConstants.USER_LOGIN_NAME,
                                    itsUserNameVal);
                            aEditor.putString(MyAppConstants.PASSWORD,
                                    itsPasswordVal);
                            aEditor.putString(MyAppConstants.MEMBER_ID, aUserId);
                            aEditor.putBoolean(
                                    MyAppConstants.IS_USER_ALREADY_AUTHENTICATED,
                                    true);
                            aEditor.putString(MyAppConstants.PHONE, aPhone);
                            aEditor.putString(MyAppConstants.ALIAS, alias);
                            aEditor.putInt(MyAppConstants.SUPPORTED, supported);
                            aEditor.commit();

                            // Map default setting
                            // MyAppConstants.IS_MAP_SHOW_ALL = true;
                            // MyAppConstants.IS_FOLLOW_ME = false;
                            // Displaying team intent
                            // niaz
                            // Log.i(this.getClass().getName(),
                            // MyAppConstants.TEAM_ACTIVITY_LOAD_INFO);
                            // Intent aTeamIntent = new Intent().setClass(
                            // LoginActivity.this, TeamViewActivity.class);
                            // aTeamIntent.putExtra(MyAppConstants.MEMBER_ID,
                            // aUserId);
                            // startActivity(aTeamIntent);
                            getLoadInvitaion();
                        } else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS))
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    MyAppConstants.CONNECTION_ERROR,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJSONException) {
            theJSONException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception while constructing request for login webservice");
            Toast.makeText(getApplicationContext(),
                    MyAppConstants.JSON_ERROR_REQ, Toast.LENGTH_LONG).show();
        }
    }

    private void getSettings() {
        try {
            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.PHONE, itsUserNameVal);

            AppAsyncTask aAsyncTask = new AppAsyncTask(LoginActivity.this,
                    MyAppConstants.GET_SETTINGS, MyAppConstants.API_POST_TYPE,
                    MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                String aResponseStatus,
                        aResponseMsg,
                        aUserId,
                        aPhone,
                        alias;

                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    try {
                        Log.d("Response", theResult);
                        JSONObject aResponseJson = new JSONObject(theResult);
                        aResponseStatus = aResponseJson
                                .getString(MyAppConstants.STATUS);
                        aResponseMsg = aResponseJson
                                .getString(MyAppConstants.MESSAGE);

                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {
                            // Saving user credentials to preference
                            int batchinterval = aResponseJson.getJSONObject(
                                    MyAppConstants.RESULT).getInt(
                                    "batchinterval");
                            int batchinterval_new = (batchinterval >= 2) ? batchinterval : 2;
                            int teamupdateinterval = aResponseJson.getJSONObject(
                                    MyAppConstants.RESULT).getInt(
                                    "teamupdateinterval");
                            int teamupdateinterval_new = (teamupdateinterval >= 2) ? teamupdateinterval : 2;

                            SharedPreferences.Editor aEditor = itsSharedPreference
                                    .edit();
                            aEditor.putInt(MyAppConstants.TEAMUPDATE_INTERVAL,
                                    batchinterval_new);
                            aEditor.putInt(MyAppConstants.BATCH_INTERVAL,
                                    batchinterval_new);
//                            aEditor.putInt(MyAppConstants.SUPPORTED,
//                                    supported);
                            aEditor.commit();
//                            Toast.makeText(getApplicationContext(),
//                                    "Got Settings " + batchinterval,
//                                    Toast.LENGTH_LONG).show();
                        } else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS))
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from getsettings webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    MyAppConstants.CONNECTION_ERROR,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    aResponseMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            aAsyncTask.execute(aRequestJson.toString());
        } catch (JSONException theJSONException) {
            theJSONException.printStackTrace();
            Log.e(this.getClass().getName(),
                    "JSON Exception while constructing request for getsettings webservice");
            Toast.makeText(getApplicationContext(),
                    MyAppConstants.JSON_ERROR_REQ, Toast.LENGTH_LONG).show();
        }
    }

    private void getLoadInvitaion() {

        try {

            JSONObject aRequestJson = new JSONObject();
            Log.d("InvitationListActivity", "MyAppConstants.PHONE"
                    + MyAppConstants.PHONE);
            aRequestJson.put(
                    MyAppConstants.PHONE,
                    getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            MODE_PRIVATE).getString(MyAppConstants.PHONE, ""));
            aRequestJson
                    .put(MyAppConstants.MEMBER_ID, itsSharedPreference
                            .getString(MyAppConstants.MEMBER_ID, ""));

            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.LOAD_INVITATION,
                    MyAppConstants.API_POST_TYPE,
                    MyAppConstants.INVITAION_LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {

                    if (theResult != null) {
                        try {
                            JSONObject aFollowersJson = new JSONObject(
                                    theResult);

                            String aResponseStatus = aFollowersJson
                                    .getString(MyAppConstants.RESULT);

                            if (aResponseStatus.equals("true")) {
                                isInvited = true;
                                // Toast.makeText(getApplicationContext(),
                                // "isInvited '"+isInvited+"'",
                                // Toast.LENGTH_LONG).show();
                            }

                            if (isInvited) {
                                MyAppConstants.IS_ON_WELCOME = true;
                                Intent aCreateInvitationIntent = new Intent()
                                        .setClass(LoginActivity.this,
                                                InvitationListActivity.class);
                                startActivity(aCreateInvitationIntent);
                            } else {
                                Log.i(this.getClass().getName(),
                                        MyAppConstants.TEAM_ACTIVITY_LOAD_INFO);
                                Intent aTeamIntent = new Intent().setClass(
                                        LoginActivity.this,
                                        TeamViewActivity.class);
                                aTeamIntent.putExtra(MyAppConstants.MEMBER_ID,
                                        itsSharedPreference.getString(
                                                MyAppConstants.MEMBER_ID, ""));
                                startActivity(aTeamIntent);
                            }

                        } catch (JSONException theJsonException) {
                            theJsonException.printStackTrace();
                            Log.e(this.getClass().getName(),
                                    "JSON Exception while retrieving response from getMyTeam webservice");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                MyAppConstants.CONNECTION_ERROR,
                                Toast.LENGTH_LONG).show();
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
}