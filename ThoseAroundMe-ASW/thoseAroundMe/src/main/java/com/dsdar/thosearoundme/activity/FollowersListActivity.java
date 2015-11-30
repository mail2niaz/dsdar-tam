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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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

public class FollowersListActivity extends Activity implements OnClickListener {
	private Runnable mHandlerTask = null;
	private Handler itsHandler = null;
	private TextView itsHomeView, itsTeamView, itsFollowersView;
	private ActionBar itsActionBar;
	private static final String ACTION_BAR_COLOR = "#ab402e";
	private ListView itsTeamListLv;
	private Drawable itsHomeImgBlack, itsTeamImgGreen, itsHomeImgGreen,
			itsTeamImgBlack, itsFollowerImgBlack, itsFollowerImgGreen;
	private static int imgBounds = 44;
	private HashMap<String, Boolean> hmap = new HashMap<String, Boolean>();
	public static ProgressDialog itsProgressDialog;
	private String itsLoginUserId = null;
	private String itsResult = null;
	private JSONArray itsTeamArrayJson;
	private String[] itsTeamArray;
	private String[] itsTeamIdArray;

	private JSONArray itsFollowersArrayJson;
	public String[] itsFollowersArray;
	public String[] itsFollowersMemberIdArray;
	public String[] itsPhoneNumberArray;
	public String[] itsFollowersTeamIdArray;
	public String[] itsFollowersTeamNameArray;
	private SharedPreferences aSharedPreference;
	public Button buttonDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itsHandler = new Handler();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_followers_list);
		aSharedPreference = getSharedPreferences(MyAppConstants.APP_PREFERENCE,
				MODE_PRIVATE);
		itsLoginUserId = aSharedPreference.getString(TeamBuilder.MEMBERID, "");

		initializeComponents();
		if (TeamViewActivity.itsProgressDialog != null) {
			TeamViewActivity.itsProgressDialog.dismiss();
		}

		// showTeamInfo();

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initializeComponents() {
		// itsActionBar = getActionBar();
		// itsActionBar.setIcon(new ColorDrawable(getResources().getColor(
		// android.R.color.transparent)));
		// itsActionBar.setTitle("My Followers");
		// itsActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
		// .parseColor(ACTION_BAR_COLOR)));

		// itsHomeView = (TextView) findViewById(R.id.tvHomeFollowers);
		// itsHomeView.setOnClickListener(this);
		// itsFollowersView = (TextView)
		// findViewById(R.id.tvFollowersTabFollowers);
		// itsTeamView = (TextView) findViewById(R.id.tvTeamFollowers);
		// itsTeamView.setOnClickListener(this);
		itsTeamListLv = (ListView) findViewById(R.id.lvTeamListFollowers);
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setText("My Followers");
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
		// Intent intent = getIntent();
		// String[] aFollowersArray = intent.getStringArrayExtra("aTeamList");
		getAndLoadFollowers();

		startRefreshTimer();

		buttonDone = (Button) findViewById(R.id.buttonDone);

		buttonDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				
			}
		});

		itsTeamListLv
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent myProfileIntent = new Intent().setClass(
								getApplicationContext(), EpActivity.class);
						myProfileIntent.putExtra("aMemberPhone",
								itsFollowersArray[position]);
						myProfileIntent.putExtra("aFollowerId",
								itsFollowersArray[position]);
						startActivity(myProfileIntent);
					}
				});

		itsTeamListLv.setBackgroundResource(R.drawable.rounded_corners);

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
				getAndLoadFollowers();

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
	 * Method to handle click events of button
	 * 
	 * @param theView
	 */
	@Override
	public void onClick(View theView) {
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

	/**
	 * Method to get users follower information
	 */
	private void getAndLoadFollowers() {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(
					FollowersListActivity.this,
					MyAppConstants.GET_MY_FOLLOWERS,
					MyAppConstants.API_POST_TYPE,
					MyAppConstants.FOLLOWERS_LOADING);
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
								itsFollowersArrayJson = aFollowersJson
										.getJSONArray(MyAppConstants.RESULT);
								String[] aFollowersNameArray = getFollowersNames(
										itsFollowersArrayJson,
										TeamBuilder.ALIAS);
								itsFollowersArray = aFollowersNameArray;
								itsFollowersMemberIdArray = getFollowersNames(
										itsFollowersArrayJson,
										TeamBuilder.MEMBERID);
								itsPhoneNumberArray=getFollowersNames(
										itsFollowersArrayJson,
										TeamBuilder.PHONE);
								itsFollowersTeamIdArray = getFollowersNames(
										itsFollowersArrayJson,
										TeamBuilder.TEAMID);
								itsFollowersTeamNameArray = getFollowersNames(
										itsFollowersArrayJson, TeamBuilder.NAME);
								// prepare hmap

								for (int i = 0; i < itsFollowersMemberIdArray.length; i++) {
									hmap.put(itsFollowersTeamIdArray[i] + "",
											true);
								}

							} else { 
								itsFollowersArray = new String[0];
							}

							try {
								/*
								 * check whether teams are present, if there
								 * show them in textview
								 */

								if ((itsFollowersArray.length == 0)
										|| (itsFollowersArray == null)) {
//									final AlertDialog alertDialog = new AlertDialog.Builder(
//											FollowersListActivity.this)
//											.create();
//									LayoutInflater inflater = getLayoutInflater();
//									View view = inflater.inflate(
//											R.layout.alertbox_title, null);
//									alertDialog.setCustomTitle(view);
//									alertDialog
//											.setMessage("You do not have any Followers");
//									alertDialog
//											.setButton(
//													"OK",
//													new DialogInterface.OnClickListener() {
//														public void onClick(
//																DialogInterface dialog,
//																int which) {
//															alertDialog
//																	.cancel();
//														}
//													});
//									alertDialog.show();

								}

								else {

									/*
									 * final TextView[] aTextViews = new
									 * TextView[TEAM_LENGHT]; // create // an //
									 * empty // array;
									 * 
									 * for (int i = 0; i < TEAM_LENGHT; i++) {
									 * // create a new textview final TextView
									 * aTvTeamMember = new TextView(this);
									 * aTvTeamMember.setTextColor(Color
									 * .parseColor(ACTION_BAR_COLOR));
									 * 
									 * // set some properties of rowTextView or
									 * something
									 * aTvTeamMember.setText(aTeamArray[i] +
									 * String.valueOf(i));
									 * aTvTeamMember.setCompoundDrawables
									 * (getResources()
									 * .getDrawable(R.drawable.navigation_arrow
									 * ), null, null, null);
									 * aTvTeamMember.setTextSize(25);
									 * aTvTeamMember.setGravity(Gravity.LEFT);
									 * aTvTeamMember.setPadding(0, 20, 0, 0); //
									 * add the textview to the linearlayout
									 * itsCreateTeamLayout
									 * .addView(aTvTeamMember);
									 * 
									 * // save a reference to the textview for
									 * later aTextViews[i] = aTvTeamMember; }
									 */
									//
									// ArrayList<String> aArrayListFollowers =
									// new ArrayList<String>(
									// Arrays.asList(itsFollowersArray));

									// final ArrayAdapter<String> adapter = new
									// ArrayAdapter<String>(
									// this, R.layout.my_followers_textview,
									// R.id.tvFollowerslist_content,
									// aArrayListFollowers);
									// itsTeamListLv.setAdapter(adapter);

									AdapterFollower adapter = new AdapterFollower(
											FollowersListActivity.this,
											itsFollowersArray,
											itsFollowersMemberIdArray,itsPhoneNumberArray,
											aSharedPreference);

									itsTeamListLv.setAdapter(adapter);

								}
							} catch (Exception theEx) {
								theEx.printStackTrace();
							}

						} catch (JSONException theJsonException) {
							theJsonException.printStackTrace();
							Log.e(this.getClass().getName(),
									"JSON Exception while retrieving response from getMyTeam webservice");
						}
					} else {
						Toast.makeText(FollowersListActivity.this,
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
							// if (theFollowersArray.getJSONObject(index)
							// .getString(TeamBuilder.ALIAS) != null
							// && !theFollowersArray.getJSONObject(index)
							// .getString(TeamBuilder.ALIAS)
							// .equals(""))
							// aFollowersNameArray[index] = theFollowersArray
							// .getJSONObject(index).getString(
							// TeamBuilder.ALIAS);
							//
							// else
							// aFollowersNameArray[index] = theFollowersArray
							// .getJSONObject(index).getString(
							// TeamBuilder.PHONE);

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
	// Toast.makeText(FollowersListActivity.this,
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

	// @Override
	// public void onBackPressed() {
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_HOME);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(intent);
	//
	// super.onBackPressed();
	// }

	public void updateFollwers(String regMemberId) {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
			aRequestJson.put(MyAppConstants.INFO, "12");
			aRequestJson.put(MyAppConstants.REQUEST_ID, regMemberId);

			String mode = null;

			mode = MyAppConstants.REMOVE_MY_FOLLOWERS;

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

	@Override
	public void onBackPressed() {
		stopRefreshTimer();
		super.onBackPressed();
	}
}
