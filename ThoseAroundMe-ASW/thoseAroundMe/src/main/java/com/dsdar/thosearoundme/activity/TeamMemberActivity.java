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

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.ContactsListActivity;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamMemberAddActivity;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;

/**
 * Activity to view Team Members.
 * 
 * @author Bharath_Murali
 */

public class TeamMemberActivity extends Activity implements OnClickListener {

	private TextView itsHomeView, itsTeamView, itsFollowersView;
	private ActionBar itsActionBar;
	private static final String ACTION_BAR_COLOR = "#ab402e";
	private Animation right_to_left;
	private LinearLayout itsTeamMemeberLayout;
	private ListView itsTeamMemberListLv;
	private Drawable itsHomeImgBlack, itsTeamImgGreen, itsHomeImgGreen,
			itsTeamImgBlack, itsFollowerImgBlack, itsFollowerImgGreen;
	private static int imgBounds = 44;
	private String itsLoginUserId = null;
	private JSONArray itsFollowersArrayJson;
	private String[] itsFollowersArray;
	private JSONArray itsTeamMembersArrayJson;
	private String[] itsTeamMembersArray;

	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gestureDetector;
	SharedPreferences aSharedPreference;
	String aTeamName;
	public static ProgressDialog itsProgressDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_team_member);
		 aSharedPreference = getSharedPreferences(
				MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
		itsLoginUserId = aSharedPreference.getString(TeamBuilder.MEMBERID, "");
		Intent intent = getIntent();
		String aTeamId = intent.getStringExtra("team_id");
		aTeamName = intent.getStringExtra("team_name");
		getandLoadTeamMembers(aTeamId);
		initializeComponents();
		if (TeamViewActivity.itsProgressDialog != null) {
			TeamViewActivity.itsProgressDialog.dismiss();
		}

		getAndLoadFollowers();
		gestureDetector = new GestureDetector(this, new MyGestureDetector());

	}

	@SuppressLint("NewApi")
	private void initializeComponents() {
		itsActionBar = getActionBar();
		itsActionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		itsActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor(ACTION_BAR_COLOR)));
		itsActionBar.setTitle(aTeamName);

		itsTeamMemeberLayout = (LinearLayout) findViewById(R.id.layout_team_member);

		itsHomeView = (TextView) findViewById(R.id.tvHomeTeamMemebers);
		itsHomeView.setOnClickListener(this);
		itsFollowersView = (TextView) findViewById(R.id.tvFollowersTeamMemebers);
		itsFollowersView.setOnClickListener(this);
		itsTeamView = (TextView) findViewById(R.id.tvTeamTabTeamMemebers);
		itsTeamMemberListLv = (ListView) findViewById(R.id.lvteam_member);
		itsTeamMemberListLv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});
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
		right_to_left = AnimationUtils.loadAnimation(TeamMemberActivity.this,
				R.anim.rbm_out_to_left);
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
					return false;
				}
				int position = itsTeamMemberListLv.pointToPosition(
						(int) e1.getX(), (int) e1.getY());
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					itsTeamMemberListLv.startAnimation(right_to_left);
					Toast.makeText(TeamMemberActivity.this,
							"Swipe Effect Right to Left", Toast.LENGTH_LONG)
							.show();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(TeamMemberActivity.this,
							"Swipe Effect Left to Right", Toast.LENGTH_LONG)
							.show();
					return true;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			return false;
		}
	}

	/**
	 * Method to create menu in action bar
	 * 
	 * @param theMenu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu theMenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_teammember_menu, theMenu);
		return super.onCreateOptionsMenu(theMenu);
	}

	/**
	 * Method to handle click events of menu in action bar
	 * 
	 * 
	 * @param theItem
	 * */
	public boolean onOptionsItemSelected(MenuItem theItem) {
		switch (theItem.getItemId()) {
		// action with ID action_refresh was selected

		case R.id.menuteammember:
			// Niaz check for Contact list

			// Intent aWelcomeIntent = new Intent()
			// .setClass(
			// TeamMemberActivity.this,
			// TeamMemberAddActivity.class);
			// aWelcomeIntent.putExtra("parentActivity", "Add Team Member");
			// aWelcomeIntent.putExtra("teamName", aTeamName);
			// startActivity(aWelcomeIntent);

			Intent aWelcomeIntent = new Intent().setClass(
					TeamMemberActivity.this, ContactsListActivity.class);
			aWelcomeIntent.putExtra("parentActivity", "Add Team Member");
			aWelcomeIntent.putExtra("teamName", aTeamName);
			startActivity(aWelcomeIntent);
			break;

		}

		return true;
	}

	/**
	 * Method to handle click events of button
	 * 
	 * @param theView
	 */
	@Override
	public void onClick(View theView) {
		switch (theView.getId()) {
		case R.id.tvHomeTeamMemebers:
			itsHomeView.setCompoundDrawables(null, itsHomeImgGreen, null, null);
			itsTeamView.setCompoundDrawables(null, itsTeamImgBlack, null, null);
			itsFollowersView.setCompoundDrawables(null, itsFollowerImgBlack,
					null, null);
			Intent aTeamViewIntent = new Intent(this, TeamViewActivity.class);
			startActivity(aTeamViewIntent);
			this.finish();
			break;

		case R.id.tvFollowersTeamMemebers:
			itsHomeView.setCompoundDrawables(null, itsHomeImgBlack, null, null);
			itsTeamView.setCompoundDrawables(null, itsTeamImgBlack, null, null);
			itsFollowersView.setCompoundDrawables(null, itsFollowerImgGreen,
					null, null);
			itsProgressDialog = new ProgressDialog(this);
			itsProgressDialog.setMessage("Please Wait......");
			itsProgressDialog.show();

			Intent aCreateFollowerIntent = new Intent().setClass(this,
					FollowersListActivity.class);
			aCreateFollowerIntent.putExtra("aTeamList", itsFollowersArray);
			startActivity(aCreateFollowerIntent);

			break;

		case R.id.menuteammember:
			System.out.println("Clicked Add Team member");
			Log.d("TeamMemberActivity", "niaz........Clicked Add Team member");
			break;
		}

	}

	/**
	 * Method to get users follower information
	 */
	private void getAndLoadFollowers() {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(TeamMemberActivity.this,
					MyAppConstants.GET_MY_FOLLOWERS,
					MyAppConstants.API_POST_TYPE, MyAppConstants.TEAM_LOADING);
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
								String[] aFollowersNameArray = getFollowersNames(itsFollowersArrayJson);
								itsFollowersArray = aFollowersNameArray;
							} else {
								itsFollowersArray = new String[0];
							}
						} catch (JSONException theJsonException) {
							theJsonException.printStackTrace();
							Log.e(this.getClass().getName(),
									"JSON Exception while retrieving response from getMyTeam webservice");
						}
					} else {
						Toast.makeText(TeamMemberActivity.this,
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
				private String[] getFollowersNames(JSONArray theFollowersArray) {
					String[] aFollowersNameArray = new String[theFollowersArray
							.length()];
					try {
						for (int index = 0; index < theFollowersArray.length(); index++) {
							if (theFollowersArray.getJSONObject(index)
									.getString(TeamBuilder.ALIAS) != null
									&& !theFollowersArray.getJSONObject(index)
											.getString(TeamBuilder.ALIAS)
											.equals(""))
								aFollowersNameArray[index] = theFollowersArray
										.getJSONObject(index).getString(
												TeamBuilder.ALIAS);

							else
								aFollowersNameArray[index] = theFollowersArray
										.getJSONObject(index).getString(
												TeamBuilder.PHONE);
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

	/**
	 * Method to get Team Members information Niaz to get Contact
	 */
	private void getandLoadTeamMembers(String team_id) {
		try {
			
			 aSharedPreference = getSharedPreferences(
						MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
			String aMemberId = aSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");
			JSONObject aRequestJson = new JSONObject();
			
			aRequestJson.put(MyAppConstants.TEAM_ID, team_id);
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			AppAsyncTask aAsyncTask = new AppAsyncTask(TeamMemberActivity.this,
					MyAppConstants.GET_TEAM_MEMBERS,
					MyAppConstants.API_POST_TYPE, MyAppConstants.TEAM_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@SuppressWarnings("deprecation")
				@Override
				public void onPostExecuteConcluded(String theResult) {
					if (theResult != null) {
						try {
							JSONObject aTeamMemberJson = new JSONObject(
									theResult);
							String aResponseStatus = aTeamMemberJson
									.getString(MyAppConstants.STATUS);
							String aResponseMessage = aTeamMemberJson
									.getString(MyAppConstants.MESSAGE);
							if (aResponseStatus
									.equals(MyAppConstants.SUCCESS_STATUS)
									&& aResponseMessage.equals("")) {
								itsTeamMembersArrayJson = aTeamMemberJson
										.getJSONArray(MyAppConstants.RESULT);
								String[] aTeamMemebersNameArray = getTeamMemberNames(itsTeamMembersArrayJson);
								itsTeamMembersArray = aTeamMemebersNameArray;
							} else {
								itsTeamMembersArray = new String[0];
							}
						} catch (JSONException theJsonException) {
							theJsonException.printStackTrace();
							Log.e(this.getClass().getName(),
									"JSON Exception while retrieving response from getMyTeam webservice");
						}

						try {
							/*
							 * check whether teams are present, if there show
							 * them in textview
							 */

							if (itsTeamMembersArray.length == 0) {
								final AlertDialog alertDialog = new AlertDialog.Builder(
										TeamMemberActivity.this).create();
								LayoutInflater inflater = getLayoutInflater();
								View view = inflater.inflate(
										R.layout.alertbox_title, null);
								alertDialog.setCustomTitle(view);
								alertDialog
										.setMessage("No Members have been added in '"
												+ aTeamName + "'");
								alertDialog.setButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												alertDialog.cancel();
											}
										});
								alertDialog.show();
							} else {
								ArrayList<String> aArrayListFollowers = new ArrayList<String>(
										Arrays.asList(itsTeamMembersArray));

								final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										TeamMemberActivity.this,
										R.layout.team_members_textview,
										R.id.tvteam_members_list,
										aArrayListFollowers);
								itsTeamMemberListLv.setAdapter(adapter);
							}
						} catch (Exception theEx) {
							theEx.printStackTrace();
						}
					} else {
						Toast.makeText(TeamMemberActivity.this,
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
				private String[] getTeamMemberNames(JSONArray theTeamMemberArray) {
					String[] aTeamMemberNameArray = new String[theTeamMemberArray
							.length()];
					try {
						for (int index = 0; index < theTeamMemberArray.length(); index++) {
							aTeamMemberNameArray[index] = theTeamMemberArray
									.getJSONObject(index).getString(
											TeamBuilder.PHONE);
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aTeamMemberNameArray;
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
