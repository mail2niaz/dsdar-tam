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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;

/**
 * Activity to validate users login credential and navigate them to either login
 * activity or team activity
 * 
 * @author senthil_kumaran
 */
public class WelcomeActivity extends Activity implements OnClickListener {
	SharedPreferences itsSharedPreference;
	private Button itsLoginBtn, itsSignupBtn;
	private String itsUserNameVal = null, itsPasswordVal = null;
	private RelativeLayout itsRelativeLayout;
	private JSONObject account_details;
	private JSONArray itsInvitationArrayJson;
	public String[] itsInvitationArray;
	public String[] itsInvitationMemberIdArray;
	boolean isInvited = false;

	@Override
	protected void onCreate(Bundle theSavedInstanceState) {
		super.onCreate(theSavedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);

		itsSharedPreference = getSharedPreferences(
				MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
		boolean isUserLogined = itsSharedPreference.getBoolean(
				MyAppConstants.IS_USER_ALREADY_AUTHENTICATED, false);
		Intent loginIntent = getIntent();
		String registration_token = loginIntent.getStringExtra("reg_token");

		if (registration_token == null) {
			if (isUserLogined) {
				// If user already logined, load team activity
				getLoadInvitaion();

			} else {
				setContentView(R.layout.activity_welcome);
				itsLoginBtn = (Button) findViewById(R.id.btnLoginWelcome);
				itsSignupBtn = (Button) findViewById(R.id.btnSignUpWelcome);
				itsRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutWelcome);

				itsLoginBtn.setOnClickListener(this);
				itsSignupBtn.setOnClickListener(this);
				itsRelativeLayout.setOnClickListener(this);
			}
		} else {
			if (isUserLogined) {
				SharedPreferences aSharedPreference = getSharedPreferences(
						MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
				SharedPreferences.Editor aEditor = aSharedPreference.edit();
				aEditor.remove(MyAppConstants.IS_USER_ALREADY_AUTHENTICATED);
				aEditor.remove(MyAppConstants.MEMBER_ID);
				aEditor.remove(MyAppConstants.USER_LOGIN_NAME);
				aEditor.remove(MyAppConstants.PASSWORD);
				aEditor.commit();
			} else {
				activateAccountAndAutologin(registration_token);
			}
		}
	}

	/**
	 * Method to handle click events of button
	 * 
	 * @param theView
	 */

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
										.setClass(WelcomeActivity.this,
												InvitationListActivity.class);
								startActivity(aCreateInvitationIntent);
							} else {
								Log.i(this.getClass().getName(),
										MyAppConstants.TEAM_ACTIVITY_LOAD_INFO);
								Intent aTeamIntent = new Intent().setClass(
										WelcomeActivity.this,
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

	@Override
	public void onClick(View theView) {
		switch (theView.getId()) {
		case R.id.btnLoginWelcome:
			startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
			break;
		case R.id.btnSignUpWelcome:
			startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
			break;
		}
	}

	/**
	 * Method to activate newly signed up acount and auto login
	 */
	private void activateAccountAndAutologin(String reg_token) {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.TOKEN, reg_token);

			AppAsyncTask aAsyncTask = new AppAsyncTask(WelcomeActivity.this,
					MyAppConstants.VALIDATE_REGISTRATION,
					MyAppConstants.API_POST_TYPE,
					MyAppConstants.ACCOUNT_VALIDATION);

			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@SuppressWarnings("deprecation")
				@Override
				public void onPostExecuteConcluded(String theResult) {
					if (theResult != null) {
						try {
							JSONObject aResultJson = new JSONObject(theResult);
							String aResponseStatus = aResultJson
									.getString(MyAppConstants.STATUS);
							String aResponseMessage = aResultJson
									.getString(MyAppConstants.MESSAGE);
							if (aResponseStatus
									.equals(MyAppConstants.SUCCESS_STATUS)) {

								account_details = aResultJson
										.getJSONObject(MyAppConstants.RESULT);

								itsUserNameVal = account_details
										.getString(MyAppConstants.PHONE);
								itsPasswordVal = account_details
										.getString("password");
								String aUserId = account_details
										.getString(MyAppConstants.MEMBER_ID);
								String alias = aResultJson.getJSONObject(
										MyAppConstants.RESULT).getString(
										MyAppConstants.ALIAS);
								// Saving user credentials to preference
								SharedPreferences.Editor aEditor = itsSharedPreference
										.edit();
								aEditor.putString(MyAppConstants.ALIAS, alias);
								aEditor.putString(MyAppConstants.PASSWORD,
										itsPasswordVal);
								aEditor.putString(MyAppConstants.MEMBER_ID,
										aUserId);
								aEditor.putString(MyAppConstants.PHONE, itsUserNameVal);
								aEditor.putBoolean(
										MyAppConstants.IS_USER_ALREADY_AUTHENTICATED,
										true);
								aEditor.commit();
								getLoadInvitaion();
								// Displaying Team intent
								// Intent aTeamIntent = new Intent().setClass(
								// WelcomeActivity.this,
								// TeamViewActivity.class);
								// aTeamIntent.putExtra(MyAppConstants.MEMBER_ID,
								// aUserId);
								// startActivity(aTeamIntent);
								if (aResponseMessage
										.equals(MyAppConstants.ALREADY_USER)) {
									final AlertDialog alertDialog = new AlertDialog.Builder(
											WelcomeActivity.this).create();
									LayoutInflater inflater = getLayoutInflater();
									View view = inflater.inflate(
											R.layout.alertbox_title, null);
									alertDialog.setCustomTitle(view);
									alertDialog
											.setMessage(MyAppConstants.ALREADY_USER);
									alertDialog
											.setButton(
													"OK",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															alertDialog
																	.cancel();
														}
													});
									alertDialog.show();
								} else {
									// final AlertDialog alertDialog = new
									// AlertDialog.Builder(
									// WelcomeActivity.this).create();
									// LayoutInflater inflater =
									// getLayoutInflater();
									// View view = inflater.inflate(
									// R.layout.alertbox_title, null);
									// alertDialog.setCustomTitle(view);
									// alertDialog
									// .setMessage(MyAppConstants.FRESH_USER);
									// alertDialog
									// .setButton(
									// "OK",
									// new DialogInterface.OnClickListener() {
									// public void onClick(
									// DialogInterface dialog,
									// int which) {
									// alertDialog
									// .cancel();
									// }
									// });
									// alertDialog.show();
								}
							} else {
								Intent reload = getIntent();
								reload.removeExtra("reg_token");
								startActivity(reload);
								final AlertDialog alertDialog = new AlertDialog.Builder(
										WelcomeActivity.this).create();
								alertDialog.setTitle("Alert");
								alertDialog
										.setMessage(MyAppConstants.ACTIVATION_FAILURE);
								alertDialog.setButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												alertDialog.cancel();
											}
										});
								alertDialog.show();
							}
						} catch (JSONException theJsonException) {
							theJsonException.printStackTrace();
							Log.e(this.getClass().getName(),
									"JSON Exception while retrieving response from getMyTeam webservice");
						}
					} else {
						Toast.makeText(WelcomeActivity.this,
								MyAppConstants.CONNECTION_ERROR,
								Toast.LENGTH_LONG).show();
					}
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJsonException) {
			theJsonException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while retrieving response from getMyTeam webservice");
		}
	}

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
								// MyAppConstants.IS_FOLLOW_ME = false;
								// MyAppConstants.IS_MAP_SHOW_ALL = true;
								moveTaskToBack(true);
								android.os.Process
										.killProcess(android.os.Process.myPid());
								System.exit(1);
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
}