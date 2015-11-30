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

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.ep.EpActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class InvitationListActivity extends Activity implements OnClickListener {
	private Runnable mHandlerTask = null;
	private Handler itsHandler = null;
	private TextView itsHomeView, itsTeamView, itsFollowersView;
	private ActionBar itsActionBar;
	private static final String ACTION_BAR_COLOR = "#ab402e";
	private ListView itsTeamListLv;
	private Drawable itsHomeImgBlack, itsTeamImgGreen, itsHomeImgGreen,
			itsTeamImgBlack, itsFollowerImgBlack, itsFollowerImgGreen;
	private static int imgBounds = 44;
	HashMap<String, Boolean> hmap = new HashMap<String, Boolean>();
	public static ProgressDialog itsProgressDialog;
	private String itsLoginUserId = null;
	private String phone = null;
	private String itsResult = null;
	private JSONArray itsTeamArrayJson;
	private String[] itsTeamArray;
	private String[] itsTeamIdArray;

	private JSONArray itsInvitationArrayJson;
	public String[] itsInvitationArray;
	public String[] itsInvitationMemberIdArray;
	public String[] itsInvitationTeamIdArray;
	public String[] itsInvitationTeamNameArray;
	public Button buttonDone;
	SharedPreferences aSharedPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itsHandler = new Handler();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_followers_list);
		aSharedPreference = getSharedPreferences(MyAppConstants.APP_PREFERENCE,
				MODE_PRIVATE);
		itsLoginUserId = aSharedPreference.getString(TeamBuilder.MEMBERID, "");
		phone = aSharedPreference.getString(TeamBuilder.PHONE, "");
		initializeComponents();
		if (TeamViewActivity.itsProgressDialog != null) {
			TeamViewActivity.itsProgressDialog.dismiss();
		}

		// showTeamInfo();

	}

	// @SuppressWarnings("deprecation")
	// @SuppressLint("NewApi")
	private void initializeComponents() {
		// itsActionBar = getActionBar();
		// itsActionBar.setIcon(new ColorDrawable(getResources().getColor(
		// android.R.color.transparent)));
		// // itsActionBar.setTitle("My Invitation");
		// itsActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
		// .parseColor(ACTION_BAR_COLOR)));

		// itsHomeView = (TextView) findViewById(R.id.tvHomeFollowers);
		// itsHomeView.setOnClickListener(this);
		// itsFollowersView = (TextView)
		// findViewById(R.id.tvFollowersTabFollowers);
		// itsTeamView = (TextView) findViewById(R.id.tvTeamFollowers);
		// itsTeamView.setOnClickListener(this);
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setText("My Invitations");

		itsTeamListLv = (ListView) findViewById(R.id.lvTeamListFollowers);
		itsTeamListLv
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent myProfileIntent = new Intent().setClass(
								getApplicationContext(), EpActivity.class);
						myProfileIntent.putExtra("aMemberPhone",
								itsInvitationArray[position]);
						myProfileIntent.putExtra("aFollowerId",
								itsInvitationMemberIdArray[position]);
						startActivity(myProfileIntent);
					}
				});
		itsTeamListLv.setBackgroundResource(R.drawable.rounded_corners);
		//
		// itsHomeImgBlack = getResources()
		// .getDrawable(R.drawable.home_icon_black);
		// itsHomeImgBlack.setBounds(0, 0, imgBounds, imgBounds);
		// itsHomeImgGreen = getResources()
		// .getDrawable(R.drawable.home_icon_green);
		// itsHomeImgGreen.setBounds(0, 0, imgBounds, imgBounds);
		// itsTeamImgGreen = getResources()
		// .getDrawable(R.drawable.team_icon_green);
		// itsTeamImgGreen.setBounds(0, 0, imgBounds, imgBounds);
		// itsTeamImgBlack = getResources()
		// .getDrawable(R.drawable.team_icon_black);
		// itsTeamImgBlack.setBounds(0, 0, imgBounds, imgBounds);
		// itsFollowerImgGreen = getResources().getDrawable(
		// R.drawable.followers_icon_green);
		// itsFollowerImgGreen.setBounds(0, 0, imgBounds, imgBounds);
		// itsFollowerImgBlack = getResources().getDrawable(
		// R.drawable.followers_icon_black);
		// itsFollowerImgBlack.setBounds(0, 0, imgBounds, imgBounds);
		Intent intent = getIntent();
		String[] aFollowersArray = intent.getStringArrayExtra("aTeamList");

		getAndLoadInvitaion();

		// startRefreshTimer();

		buttonDone = (Button) findViewById(R.id.buttonDone);

		buttonDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mHandlerTask != null)
					itsHandler.removeCallbacks(mHandlerTask);
				finish();
				Intent welcomeIntent = new Intent().setClass(
						InvitationListActivity.this, TeamViewActivity.class);
				startActivity(welcomeIntent);
				// TODO Auto-generated method stub
				// String str = "";
				// for (Map.Entry<String, Boolean> entry : hmap.entrySet()) {
				// String key = entry.getKey();
				// Boolean value = entry.getValue();
				// Log.d("HMAP", "key=" + key + "=" + value);
				// if (value)
				// str += key + ",";
				// }
				//
				// int strlen = str.length();
				// str = str.substring(0, strlen - 1);
				// Log.d("InvitationListActivity", "str=" + str);
				//
				// updateInvitation(str);
			}
		});

	}

	public void startRefreshTimer() {
		// if ((alatitude != null) && !(alatitude.trim().equals(""))) {
		// Log.d("startRefreshTimer", "latitude is empty since no lat lng");
		// latitude = Double.parseDouble(alatitude);
		// longitude = Double.parseDouble(alongitude);
		// accuracy = Double.parseDouble(aAccuracy);
		// }

		// If handler already exists, remove it
		if (mHandlerTask != null)
			itsHandler.removeCallbacks(mHandlerTask);
		// Create a new handler to run for every n minutes
		mHandlerTask = new Runnable() {
			@Override
			public void run() {
				// Get and set team members location in map
				getAndLoadInvitaion();
				itsHandler.postDelayed(mHandlerTask, 20000);
			}
		};
		mHandlerTask.run();
	}

	/**
	 * Method to stop timer
	 */
	public void stopRefreshTimer() {
		if (mHandlerTask != null)
			itsHandler.removeCallbacks(mHandlerTask);
	}

	/**
	 * Method to update Invitation Request
	 * 
	 * @param theView
	 */

	public void updateInvitation(String regMemberId, String teamId,
			boolean isAccept) {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
			aRequestJson.put(MyAppConstants.INFO, teamId);
			aRequestJson.put(MyAppConstants.PHONE, phone);
			aRequestJson.put(MyAppConstants.REQUEST_ID, regMemberId);

			String mode = null;
			if (isAccept) {
				mode = MyAppConstants.UPDATE_MY_INVITATION_NEW;
			} else {
				mode = MyAppConstants.REMOVE_MY_INVITATIONS;
			}

			AppAsyncTask aAsyncTask = new AppAsyncTask(this, mode,
					MyAppConstants.API_POST_TYPE,
					MyAppConstants.INVITAION_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String theResult) {
					finish();
					startActivity(getIntent());
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJsonException) {
			theJsonException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while retrieving response from getMyTeam webservice");
		}
	}

	/**
	 * Method to handle click events of button
	 * 
	 * @param theView
	 */
	@Override
	public void onClick(View theView) {
		if (mHandlerTask != null)
			itsHandler.removeCallbacks(mHandlerTask);
		switch (theView.getId()) {
		case R.id.tvHomeFollowers:
			itsHomeView.setCompoundDrawables(null, itsHomeImgGreen, null, null);
			itsTeamView.setCompoundDrawables(null, itsTeamImgBlack, null, null);
			itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
					null, null);
			Intent aTeamViewIntent = new Intent(this, TeamViewActivity.class);
			startActivity(aTeamViewIntent);
			this.finish();

			break;

		// case R.id.tvTeamFollowers:
		// itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
		// itsTeamView.setCompoundDrawables(null, itsTeamImgGreen, null, null);
		// itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
		// null, null);
		// itsProgressDialog = new ProgressDialog(this);
		// itsProgressDialog.setMessage("Please Wait......");
		// itsProgressDialog.show();
		//
		// Intent aCreateTeamIntent = new Intent().setClass(this,
		// TeamListActivity.class);
		// aCreateTeamIntent.putExtra("aTeamList", itsTeamArray);
		// aCreateTeamIntent.putExtra("aTeamIdList", itsTeamIdArray);
		// startActivity(aCreateTeamIntent);
		//
		// break;
		}

	}

	// @Override
	// public void onBackPressed() {
	// // Intent intent = new Intent(Intent.ACTION_MAIN);
	// // intent.addCategory(Intent.CATEGORY_HOME);
	// // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// // startActivity(intent);
	// //
	// // super.onBackPressed();
	// finish();
	// }

	private void getAndLoadInvitaion() {

		try {
			JSONObject aRequestJson = new JSONObject();
			Log.d("InvitationListActivity", "MyAppConstants.PHONE"
					+ MyAppConstants.PHONE);
			aRequestJson.put(
					MyAppConstants.PHONE,
					getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							MODE_PRIVATE).getString(MyAppConstants.PHONE, ""));
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(this,
					MyAppConstants.GET_MY_INVITATION_NEW,
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
									.getString(MyAppConstants.STATUS);
							String aResponseMessage = aFollowersJson
									.getString(MyAppConstants.MESSAGE);
							if (aResponseStatus
									.equals(MyAppConstants.SUCCESS_STATUS)
									&& aResponseMessage.equals("")) {
								itsInvitationArrayJson = aFollowersJson
										.getJSONArray(MyAppConstants.RESULT);
								String[] aInvitationNameArray = getFollowersNames(
										itsInvitationArrayJson,
										TeamBuilder.ALIAS);
								itsInvitationArray = aInvitationNameArray;
								itsInvitationMemberIdArray = getFollowersNames(
										itsInvitationArrayJson,
										TeamBuilder.MEMBERID);
								itsInvitationTeamIdArray = getFollowersNames(
										itsInvitationArrayJson,
										TeamBuilder.TEAMID);
								itsInvitationTeamNameArray = getFollowersNames(
										itsInvitationArrayJson,
										TeamBuilder.NAME);
								// prepare hmap

								for (int i = 0; i < itsInvitationMemberIdArray.length; i++) {
									hmap.put(itsInvitationTeamIdArray[i] + "",
											true);
								}

								// Setting Adapter
								if ((itsInvitationArray.length == 0)
										|| (itsInvitationArray == null)) {
									// final AlertDialog alertDialog = new
									// AlertDialog.Builder(
									// InvitationListActivity.this)
									// .create();
									// LayoutInflater inflater =
									// getLayoutInflater();
									// View view = inflater.inflate(
									// R.layout.alertbox_title, null);
									// alertDialog.setCustomTitle(view);
									// alertDialog
									// .setMessage("You do not have any Invitations");
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

								} else {
									AdapterInvitation adapter = new AdapterInvitation(
											InvitationListActivity.this,
											itsInvitationArray,
											itsInvitationMemberIdArray,
											aSharedPreference);

									itsTeamListLv.setAdapter(adapter);
								}

							} else {
								itsInvitationArray = new String[0];
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

				/**
				 * Method to get followers names from a followers array
				 * 
				 * @param theFollowersArray
				 * @return aFollowersNameArray
				 */
				private String[] getFollowersNames(JSONArray theFollowersArray,
						String field) {
					String[] aFollowersNameArray = new String[theFollowersArray
							.length()];
					try {
						for (int index = 0; index < theFollowersArray.length(); index++) {

							aFollowersNameArray[index] = theFollowersArray
									.getJSONObject(index).getString(field);
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aFollowersNameArray;
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
	public void onBackPressed() {
		// stopRefreshTimer();
		if (MyAppConstants.IS_ON_WELCOME) {
			Intent teamViewIntent = new Intent().setClass(
					InvitationListActivity.this, TeamViewActivity.class);
			startActivity(teamViewIntent);
		} else {
			super.onBackPressed();
		}

	}
	// private void showTeamInfo() {
	// try {
	// JSONObject aRequestJson = new JSONObject();
	// aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
	//
	// AppAsyncTask aAsyncTask = new AppAsyncTask(this,
	// MyAppConstants.GET_MY_TEAM, MyAppConstants.API_POST_TYPE,
	// MyAppConstants.FOLLOWERS_LOADING);
	// aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
	// @Override
	// public void onPreExecuteConcluded() {
	// }
	//
	// @Override
	// public void onPostExecuteConcluded(String theResult) {
	// itsResult = theResult;
	// if (theResult != null) {
	// try {
	// JSONObject aTeamJson = new JSONObject(itsResult);
	// String aResponseStatus = aTeamJson
	// .getString(MyAppConstants.STATUS);
	// if (aResponseStatus
	// .equals(MyAppConstants.SUCCESS_STATUS)) {
	// itsTeamArrayJson = aTeamJson
	// .getJSONArray(MyAppConstants.RESULT);
	// String[] aTeamNameArray = getTeamNames(itsTeamArrayJson);
	// itsTeamArray = aTeamNameArray;
	// String[] aTeamIdArray = getTeamId(itsTeamArrayJson);
	// itsTeamIdArray = aTeamIdArray;
	// }
	// } catch (JSONException theJsonException) {
	// theJsonException.printStackTrace();
	// Log.e(this.getClass().getName(),
	// "JSON Exception while retrieving response from getMyTeam webservice");
	// }
	// } else {
	// Toast.makeText(InvitationListActivity.this,
	// MyAppConstants.CONNECTION_ERROR,
	// Toast.LENGTH_LONG).show();
	// }
	// }
	//
	// /**
	// * Method to get team names from a team array
	// *
	// * @param theTeamArray
	// * @return aTeamNameArray
	// */
	// private String[] getTeamNames(JSONArray theTeamArray) {
	// String[] aTeamNameArray = new String[theTeamArray.length()];
	// try {
	// for (int index = 0; index < theTeamArray.length(); index++) {
	// aTeamNameArray[index] = theTeamArray.getJSONObject(
	// index).getString(TeamBuilder.NAME);
	// }
	// } catch (JSONException theJsonException) {
	// theJsonException.printStackTrace();
	// Log.e(this.getClass().getName(),
	// "JSON Exception when constructing a team name array");
	// }
	// return aTeamNameArray;
	// }
	//
	// /**
	// * Method to get team id from a team array
	// *
	// * @param theTeamArray
	// * @return aTeamIdArray
	// */
	// private String[] getTeamId(JSONArray theTeamArray) {
	// String[] aTeamIdArray = new String[theTeamArray.length()];
	// try {
	// for (int index = 0; index < theTeamArray.length(); index++) {
	// aTeamIdArray[index] = theTeamArray.getJSONObject(
	// index).getString(TeamBuilder.TEAMID);
	// }
	// } catch (JSONException theJsonException) {
	// theJsonException.printStackTrace();
	// Log.e(this.getClass().getName(),
	// "JSON Exception when constructing a team name array");
	// }
	// return aTeamIdArray;
	// }
	// });
	// aAsyncTask.execute(aRequestJson.toString());
	// } catch (JSONException theJsonException) {
	// theJsonException.printStackTrace();
	// Log.e(this.getClass().getName(),
	// "JSON Exception while constructing request for getMyTeam webservice");
	// }
	// }

}
