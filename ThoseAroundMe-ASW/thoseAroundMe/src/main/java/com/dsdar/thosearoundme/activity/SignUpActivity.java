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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to register new users by validating their credentials
 *
 * @author Yash_Azgar
 */

public class SignUpActivity extends Activity implements OnClickListener,
        LocationListener {
    SharedPreferences itsSharedPreference;
    private EditText itsUserNameEt, itsUserEmailEt, itsPasswordEt,
            itsConfirmPasswordEt, itsUserPhoneEt, itsCountryCodeEt;
    private Button itsSignUpBtn, itsLoginBtn;
    private String itsUserNameVal, itsUserEMailVal, itsPasswordVal,
            itsConfirmPasswordVal, itsUserPhoneVal, itsCountryCode;
    private ScrollView itsScrollView;
    private RelativeLayout itsRelativeLayout, itsRelativeLayoutInner;
    private Spinner itsCountrySpinner;


    private String blockCharacterSet = "~#^|$%&*!";

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        itsUserNameEt = (EditText) findViewById(R.id.etUsernameSignUp);
        // itsUserEmailEt = (EditText) findViewById(R.id.etEmailSignUp);
        itsPasswordEt = (EditText) findViewById(R.id.etPwdSignUp);
        // itsConfirmPasswordEt = (EditText)
        // findViewById(R.id.etConfrimPwdSignUp);
        itsUserPhoneEt = (EditText) findViewById(R.id.etPhoneSignUp);
        itsUserNameEt.setFilters(new InputFilter[]{filter});
        itsUserPhoneEt.setFilters(new InputFilter[]{filter});
        itsCountryCodeEt = (EditText) findViewById(R.id.etCountryCodeSignUp);
        itsSignUpBtn = (Button) findViewById(R.id.btnSignUp);
        itsLoginBtn = (Button) findViewById(R.id.btnLoginSignUp);
        itsCountrySpinner = (Spinner) findViewById(R.id.spCountrySignUp);

        // itsScrollView = (ScrollView) findViewById(R.id.scrollViewSignUp);
        itsRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutSignUp);
        itsRelativeLayoutInner = (RelativeLayout) findViewById(R.id.relativeLayoutInnerSignUp);
        itsSharedPreference = getSharedPreferences(
                MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
        itsSignUpBtn.setOnClickListener(this);
        itsLoginBtn.setOnClickListener(this);
        // itsScrollView.setOnClickListener(this);
        itsRelativeLayout.setOnClickListener(this);
        itsRelativeLayoutInner.setOnClickListener(this);

        addItemstoCountrySpinner();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sign_up, menu);
        return true;
    }

    @Override
    protected void onResume() {
        Util.showKeypadOnActivityLoad(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Util.hideSoftKeyboard(this);
        super.onPause();
    }

    @Override
    public void onClick(View theView) {
        switch (theView.getId()) {
            case R.id.btnSignUp:
                if (isValidSignup())
                    signup();
                break;
            case R.id.btnLoginSignUp:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            case R.id.relativeLayoutSignUp:
                // case R.id.scrollViewSignUp:
            case R.id.relativeLayoutInnerSignUp:
                Util.hideSoftKeyboard(this);
                break;
        }
    }

	/*
     * Method to add items to COuntry Spinner
	 */

    public void addItemstoCountrySpinner() {
        List<String> list = new ArrayList<String>();
        list.add("United States");
        list.add("India");

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                R.layout.country_spinner_textview, list);
        countryAdapter
                .setDropDownViewResource(R.layout.country_spinner_checkedtextview);
        itsCountrySpinner.setAdapter(countryAdapter);
        itsCountrySpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapte,
                                               View view, int position, long id) {

                        switch (position) {
                            case 0:
                                itsCountryCodeEt.setText("+1");
                                break;

                            case 1:
                                itsCountryCodeEt.setText("+91");
                                break;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    /*
     * method to validate user values, if everything is fine returns true
     */
    protected Boolean isValidSignup() {
        String aErrorMessage = null;
        Boolean isValid = true;
        itsUserNameVal = itsUserNameEt.getText().toString().trim();
        // itsUserEMailVal = itsUserEmailEt.getText().toString().trim();
        itsPasswordVal = itsPasswordEt.getText().toString();
        // itsConfirmPasswordVal = itsConfirmPasswordEt.getText().toString();
        itsCountryCode = itsCountryCodeEt.getText().toString().trim();
        itsUserPhoneVal = itsUserPhoneEt.getText().toString().trim();
        if (itsUserPhoneVal.length() != 10) {
            aErrorMessage = MyAppConstants.ERR_VALID_PHONE;
            isValid = false;
            itsUserPhoneEt.requestFocus();
        }
        itsUserPhoneVal = itsCountryCode.concat(itsUserPhoneVal);

        if (itsUserNameVal.equals("")) {
            aErrorMessage = MyAppConstants.ERR_ENT_UNAME;
            isValid = false;
            itsUserNameEt.requestFocus();
        }
        // else if (itsUserEMailVal.equals("")) {
        // aErrorMessage = MyAppConstants.ERR_ENT_EMAIL;
        // isValid = false;
        // itsUserEmailEt.requestFocus();
        // } else if (!ValidationUtility.isValidEmailAddress(itsUserEMailVal)) {
        // aErrorMessage = MyAppConstants.ERR_VALID_EMAIL;
        // isValid = false;
        // itsUserEmailEt.requestFocus();
        // } else

        if (itsUserPhoneVal.equals("")) {
            aErrorMessage = MyAppConstants.ERR_ENT_PHONE;
            isValid = false;
            itsUserPhoneEt.requestFocus();
        }

        if (itsPasswordVal.equals("")) {
            aErrorMessage = MyAppConstants.ERR_ENT_PWD;
            isValid = false;
            itsPasswordEt.requestFocus();

        } else if (itsPasswordVal.length() <= 3) {
            aErrorMessage = MyAppConstants.ERR_PWD_SHORT;
            isValid = false;
            itsPasswordEt.requestFocus();
        }
        // else if (!itsPasswordVal.equals(itsConfirmPasswordVal)) {
        // aErrorMessage = MyAppConstants.ERR_PWD_MATCH;
        // isValid = false;
        // itsPasswordEt.requestFocus();
        // }
        // if (itsUserPhoneVal != null && !itsUserPhoneVal.equals("")) {
        // if (!Util.ValidateMobileNumber(itsUserPhoneVal)) {
        // aErrorMessage = MyAppConstants.ERR_VALID_PHONE;
        // isValid = false;
        // itsUserPhoneEt.requestFocus();
        // }
        // }
        // else{
        // itsUserPhoneVal = null;
        // }

        if (aErrorMessage != null) {
            Toast.makeText(getApplicationContext(), aErrorMessage,
                    Toast.LENGTH_SHORT).show();
        }
        if (itsUserPhoneVal.contains("+")) {
            itsUserPhoneVal = itsUserPhoneVal.replace("+", "");
        }
        return isValid;
    }

    /*
     * Method to construct request JSON and call register API
     */
    public void signup() {

        try {
            JSONObject aRequestJson = new JSONObject();

            // aRequestJson.put(MyAppConstants.EMAIL, itsUserEMailVal);
            // LocationManager locManager = (LocationManager)
            // getSystemService(Context.LOCATION_SERVICE);
            // // errors in getSystemService method
            // LocationListener locListener = new LocationListener();
            // locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            // 0,
            // 0, locListener);
            double lat, lng = 0;
            LocationManager locManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            // locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            // 0,
            // 0, this);
            Location location = locManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            }

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                MyAppConstants.LAT_DUPLICATE = lat;
                MyAppConstants.LNG_DUPLICATE = lng;

            } else {
                lat = 0;
                lng = 0;
            }

            aRequestJson.put(MyAppConstants.PHONE, itsUserPhoneVal);
            aRequestJson.put(MyAppConstants.PASSWORD, itsPasswordVal);
            aRequestJson.put(MyAppConstants.OS_VERSION, MyAppConstants.ANDROID);
            aRequestJson.put(MyAppConstants.ALIAS, itsUserNameVal);
            aRequestJson.put(MyAppConstants.LAT, lat);
            aRequestJson.put(MyAppConstants.LNG, lng);
            AppAsyncTask aAsyncTask = new AppAsyncTask(SignUpActivity.this,
                    MyAppConstants.SIGNUP, MyAppConstants.API_POST_TYPE,
                    MyAppConstants.LOADING);
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

                            // // Get IMEI
                            // TelephonyManager telephonyManager =
                            // (TelephonyManager)
                            // getSystemService(Context.TELEPHONY_SERVICE);
                            // MyAppConstants.IMEI = telephonyManager
                            // .getDeviceId();
                            // if (MyAppConstants.IMEI == null) {
                            // String UDID = Secure
                            // .getString(getContentResolver(),
                            // Secure.ANDROID_ID);
                            // Log.d(">>>>", "Android ID: " + UDID);
                            // MyAppConstants.IMEI = UDID;
                            // }
                            // MyAppConstants.DEVICENAME = getDeviceName();
                            // Log.d(TAG, "IMEI ::=" + MyAppConstants.IMEI
                            // + ",DEVICENAME ::="
                            // + MyAppConstants.DEVICENAME);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    SignUpActivity.this);

                            // set title
                            alertDialogBuilder
                                    .setTitle(MyAppConstants.SUCCESS_REG_TITLE);

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(
                                            "Your account "
                                                    + itsUserPhoneVal
                                                    + " "
                                                    + MyAppConstants.SUCCESS_REG_MESSAGE)
                                    .setCancelable(false)

                                    .setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    // if this button is
                                                    // clicked, it will
                                                    // redirects to mobile's
                                                    // default messaging
                                                    // application;
                                                    // MainActivity.this.finish();
//                                                    String DEFAULT_SMS_TYPE = "vnd.android-dir/mms-sms";
//                                                    Intent messagingIntent = new Intent(
//                                                            Intent.ACTION_MAIN);
//                                                    messagingIntent
//                                                            .setType(DEFAULT_SMS_TYPE);
//                                                    int flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP;
//                                                    messagingIntent
//                                                            .setFlags(flags);
//                                                    startActivity(messagingIntent);
                                                    openSMS();
                                                    clearAll();

													/*
                                                     * Intent aWelcomeIntent =
													 * new Intent() .setClass(
													 * SignUpActivity.this,
													 * WelcomeActivity.class);
													 * startActivity
													 * (aWelcomeIntent);
													 */
                                                }
                                            });

                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder
                                    .create();

                            // show it
                            alertDialog.show();
                        } else if (aResponseStatus
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

    private void clearAll() {
        itsUserPhoneEt.setText("");
        itsUserNameEt.setText("");
        itsPasswordEt.setText("");
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    private void openSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "text");

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);

        } else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "phoneNumber");
            smsIntent.putExtra("sms_body", "message");
            startActivity(smsIntent);
        }
    }
}
