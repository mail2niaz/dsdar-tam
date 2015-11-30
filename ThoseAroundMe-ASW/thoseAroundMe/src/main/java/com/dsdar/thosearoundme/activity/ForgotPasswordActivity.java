/**
 * Copyright (c) 2014 Dsdar Inc.
 * 
 * All rights reserved. For use only with Dsdar Inc.
 * This software is the confidential and proprietary information of
 * Dsdar Inc, ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dsdar Inc.
 */
package com.dsdar.thosearoundme.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.Util;


/**
 * Activity to validate users login credential and navigate them to either login
 * activity or team activity
 * 
 * @author senthil_kumaran
 */
public class ForgotPasswordActivity extends Activity implements OnClickListener {
	SharedPreferences itsSharedPreference;
	private Button itsSubmitBtn;
	private EditText itsPhoneNumberet;
	private String itsPhoneNumberVal = null;
	private RelativeLayout itsRelativeLayout;
	@Override
	protected void onCreate(Bundle theSavedInstanceState) {
		super.onCreate(theSavedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
			setContentView(R.layout.activity_forgot_password);
			itsPhoneNumberet = (EditText) findViewById(R.id.etPhoneNum);
			itsSubmitBtn = (Button) findViewById(R.id.btnSubmitForgotPwd);
			itsRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutForgotPwd);
			
			itsSubmitBtn.setOnClickListener(this);
			itsRelativeLayout.setOnClickListener(this);
			
	}
	
	/**
	 * Method to handle click events of button
	 * 
	 * @param theView
	 */
	@Override
	public void onClick(View theView) {
		switch (theView.getId()) {
		case R.id.btnSubmitForgotPwd:
			validatePhoneNum();
			break;
		case R.id.relativeLayoutForgotPwd:
			Util.hideSoftKeyboard(ForgotPasswordActivity.this);
			break;
		}
	}
	
	/**
	 * Method to validate user phone number. If these credentials are
	 * valid, forgot password API will be called to retrieve their information
	 */
	private void validatePhoneNum() {
		boolean isValidData = false;
		itsPhoneNumberVal = itsPhoneNumberet.getText().toString().trim();

		if (itsPhoneNumberVal == null || itsPhoneNumberVal.equals(""))
			Toast.makeText(getApplicationContext(),
					MyAppConstants.ERROR_MSG_VALID_VALUE, Toast.LENGTH_SHORT)
					.show();
		else 
			isValidData = true;
		
		if (isValidData)
			resetPassword();
	}
	
	/**
	 * Method to activate newly signed up acount and auto login
	 */
	private void resetPassword()
	{
		try{
				JSONObject aRequestJson = new JSONObject();
				aRequestJson.put(MyAppConstants.PHONE, itsPhoneNumberVal);
				
				AppAsyncTask aAsyncTask = new AppAsyncTask(ForgotPasswordActivity.this,
						MyAppConstants.FORGOT_PASSWORD, MyAppConstants.API_POST_TYPE,
						MyAppConstants.PHONENUM_VALIDATION);
				
				aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
					@Override
					public void onPreExecuteConcluded() {
					}
					
					@SuppressWarnings("deprecation")
					@Override
					public void onPostExecuteConcluded(String theResult) {
						if (theResult != null) {
							try{
								JSONObject aResultJson = new JSONObject(theResult);
								String aResponseStatus = aResultJson.getString(MyAppConstants.STATUS);
								String aResponseMessage = aResultJson.getString(MyAppConstants.MESSAGE);
								if(aResponseStatus.equals(MyAppConstants.SUCCESS_STATUS))
								{
									
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											ForgotPasswordActivity.this);
									alertDialogBuilder
											.setTitle(MyAppConstants.PASSWORD_RESET_TITLE);
									alertDialogBuilder
											.setMessage(
													MyAppConstants.PASSWORD_RESET_MESSAGE)
											.setCancelable(false)

											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															Intent aWelcomeIntent = new Intent()
																	.setClass(
																			ForgotPasswordActivity.this,
																			WelcomeActivity.class);
															startActivity(aWelcomeIntent);
														}
													});
									AlertDialog alertDialog = alertDialogBuilder
											.create();
									alertDialog.show();
								}
								else
								{	
									final AlertDialog alertDialog = new AlertDialog.Builder(
					                        ForgotPasswordActivity.this).create();
									alertDialog.setTitle("Alert");
									alertDialog.setMessage(aResponseMessage);
									alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											alertDialog.cancel();
										}
									});
									alertDialog.show();
								}
								}
								catch (JSONException theJsonException) {
									theJsonException.printStackTrace();
									Log.e(this.getClass().getName(),
											"JSON Exception while retrieving response from getMyTeam webservice");
								}
						} else {
							Toast.makeText(ForgotPasswordActivity.this,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						}
					}
		});
		aAsyncTask.execute(aRequestJson.toString());
		}
		catch(JSONException theJsonException)
		{
			theJsonException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while retrieving response from getMyTeam webservice");
		}
	}
}