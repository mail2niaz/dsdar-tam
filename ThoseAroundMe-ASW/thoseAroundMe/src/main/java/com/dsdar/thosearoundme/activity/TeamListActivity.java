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

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.ContactsListActivity;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;

/**
 * Activity to create new teams.
 * 
 * @author Yash_Azgar
 */

public class TeamListActivity extends Activity implements OnClickListener {

	private TextView itsHomeView, itsTeamView, itsFollowersView;
	private ActionBar itsActionBar;
	private static final String ACTION_BAR_COLOR = "#ab402e";
	private LinearLayout itsCreateTeamLayout;
	private ListView itsTeamListLv;
	private Drawable itsHomeImgBlack, itsTeamImgGreen, itsHomeImgGreen, itsTeamImgBlack, itsFollowerImgBlack, itsFollowerImgGreen;
	private static int imgBounds = 44;
	private String itsLoginUserId = null;
	private JSONArray itsFollowersArrayJson;
	private String[] itsFollowersArray;
	private String team_id_extra;
	private String team_name_extra;
	public static ProgressDialog itsProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_team_list);
		SharedPreferences aSharedPreference = getSharedPreferences(
				MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
		itsLoginUserId = aSharedPreference.getString(TeamBuilder.MEMBERID, "");
		
		initializeComponents();
		if(TeamViewActivity.itsProgressDialog!=null){
			TeamViewActivity.itsProgressDialog.dismiss();	
		}
		
		getAndLoadFollowers();
		//Add Team Member flow
		String parentActivity = getIntent().getExtras().getString("teamActivity");
        if(parentActivity != null && parentActivity.equals("addTeamMember")) {
        	int position = getIntent().getExtras().getInt("tabPosition");
        	itsTeamListLv.performItemClick(itsTeamListLv, position, itsTeamListLv.getItemIdAtPosition(position));
		}
	}

	@SuppressLint("NewApi")
	private void initializeComponents() {
		itsActionBar = getActionBar();
		itsActionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		itsActionBar.setTitle("My Teams");
		itsActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor(ACTION_BAR_COLOR)));

		itsCreateTeamLayout = (LinearLayout) findViewById(R.id.layoutCreateTeam);
		
		itsHomeView = (TextView) findViewById(R.id.tvHomeCreateTeam);
		itsHomeView.setOnClickListener(this);
		itsFollowersView = (TextView) findViewById(R.id.tvFollowersCreateTeam);
		itsFollowersView.setOnClickListener(this);
		itsTeamView = (TextView) findViewById(R.id.tvTeamCreateTeam);
		itsTeamListLv = (ListView) findViewById(R.id.lvTeamListCreateTeam);
		itsHomeImgBlack = getResources().getDrawable(R.drawable.home_icon_black);
		itsHomeImgBlack.setBounds( 0, 0, imgBounds, imgBounds );
		itsHomeImgGreen = getResources().getDrawable(R.drawable.home_icon_green);
		itsHomeImgGreen.setBounds( 0, 0, imgBounds, imgBounds );
		itsTeamImgGreen = getResources().getDrawable(R.drawable.team_icon_green);
		itsTeamImgGreen.setBounds( 0, 0, imgBounds, imgBounds );
		itsTeamImgBlack = getResources().getDrawable(R.drawable.team_icon_black);
		itsTeamImgBlack.setBounds( 0, 0, imgBounds, imgBounds );
		itsFollowerImgGreen = getResources().getDrawable(R.drawable.followers_icon_green);
		itsFollowerImgGreen.setBounds( 0, 0, imgBounds, imgBounds );
		itsFollowerImgBlack = getResources().getDrawable(R.drawable.followers_icon_black);
		itsFollowerImgBlack.setBounds( 0, 0, imgBounds, imgBounds );
		Intent intent = getIntent();
		final String[] aTeamArray = intent.getStringArrayExtra("aTeamList");
		final String[] aTeamIdArray = intent.getStringArrayExtra("aTeamIdList");
		try {
                /*
                 *  check whether teams are present, if there show them in textview
                 *   
                 */
			
			if (aTeamArray.length == 0) {
				TextView aTeamNaTv = new TextView(this);
				aTeamNaTv.setText(MyAppConstants.TEAM_NA);
				itsCreateTeamLayout.addView(aTeamNaTv);

			}

			else {
				
				/*final TextView[] aTextViews = new TextView[TEAM_LENGHT]; // create
																			// an
																			// empty
																			// array;

				for (int i = 0; i < TEAM_LENGHT; i++) {
					// create a new textview
					final TextView aTvTeamMember = new TextView(this);
					aTvTeamMember.setTextColor(Color
							.parseColor(ACTION_BAR_COLOR));

					// set some properties of rowTextView or something
					aTvTeamMember.setText(aTeamArray[i] + String.valueOf(i));
					aTvTeamMember.setCompoundDrawables(getResources()
							.getDrawable(R.drawable.navigation_arrow), null,
							null, null);
					aTvTeamMember.setTextSize(25);
					aTvTeamMember.setGravity(Gravity.LEFT);
					aTvTeamMember.setPadding(0, 20, 0, 0);
					// add the textview to the linearlayout
					itsCreateTeamLayout.addView(aTvTeamMember);

					// save a reference to the textview for later
					aTextViews[i] = aTvTeamMember;
				}*/
//				
				ArrayList<String> aArrayListTeam = 	new ArrayList<String>(Arrays.asList(aTeamArray));
	
				    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				    		R.layout.team_list_textview,
				        R.id.tvTeamlist_content, aArrayListTeam);
				    itsTeamListLv.setAdapter(adapter);
				    
				    itsTeamListLv.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> adapter, View view, int position, long item) {
							//niaz team_id_extra = 1156 & team_name_extra	"Home" 
							//int pos = adapter.getPositionForView(view);
							team_id_extra = aTeamIdArray[position];
							team_name_extra = aTeamArray[position];
							//niaz
							Intent team_member = new Intent().setClass(TeamListActivity.this, TeamMemberActivity.class);
							team_member.putExtra("team_id", team_id_extra);
							team_member.putExtra("team_name", team_name_extra);
							startActivity(team_member);
						}
				    	
				    });
			}
		} catch (Exception theEx) {
			theEx.printStackTrace();
		}

	}

	/**
	 * Method to menu in action bar
	 * 
	 * @param theMenu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu theMenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_createteam_menu, theMenu);
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
	
			case R.id.menuCreate:
				Intent aWelcomeIntent = new Intent()
				.setClass(
						TeamListActivity.this,
						ContactsListActivity.class);
				aWelcomeIntent.putExtra("parentActivity", "Add Team");
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
		case R.id.tvHomeCreateTeam:
			itsHomeView.setCompoundDrawables(null,itsHomeImgGreen, null, null);
			itsTeamView.setCompoundDrawables(null,itsTeamImgBlack,null,null);
			itsFollowersView.setCompoundDrawables(null,itsFollowerImgBlack,null,null);
			Intent aTeamViewIntent = new Intent(this, TeamViewActivity.class);
			startActivity(aTeamViewIntent);
			this.finish();
		break;
		
		case R.id.tvFollowersCreateTeam: 
			itsHomeView.setCompoundDrawables(null,itsHomeImgBlack, null, null);
			itsTeamView.setCompoundDrawables(null,itsTeamImgBlack,null,null);
			itsFollowersView.setCompoundDrawables(null,itsFollowerImgGreen,null,null);
			itsProgressDialog = new ProgressDialog(this);
			itsProgressDialog.setMessage("Please Wait......");
			itsProgressDialog.show();
				
				Intent aCreateFollowerIntent = new Intent().setClass(this,
						FollowersListActivity.class);
				aCreateFollowerIntent
						.putExtra("aTeamList",itsFollowersArray);
				startActivity(aCreateFollowerIntent);
		
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

			AppAsyncTask aAsyncTask = new AppAsyncTask(TeamListActivity.this,
					MyAppConstants.GET_MY_FOLLOWERS, MyAppConstants.API_POST_TYPE,
					MyAppConstants.TEAM_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String theResult) {
					if (theResult != null) {
						try{
							JSONObject aFollowersJson = new JSONObject(theResult);
							String aResponseStatus = aFollowersJson.getString(MyAppConstants.STATUS);
							String aResponseMessage = aFollowersJson.getString(MyAppConstants.MESSAGE);
							if (aResponseStatus
									.equals(MyAppConstants.SUCCESS_STATUS) && aResponseMessage.equals("")) {
								itsFollowersArrayJson = aFollowersJson
										.getJSONArray(MyAppConstants.RESULT);
								String[] aFollowersNameArray = getFollowersNames(itsFollowersArrayJson);
								itsFollowersArray = aFollowersNameArray;
							}
							else
							{
								itsFollowersArray = new String[0];
							}
							}
							catch (JSONException theJsonException) {
								theJsonException.printStackTrace();
								Log.e(this.getClass().getName(),
										"JSON Exception while retrieving response from getMyTeam webservice");
							}
					} else {
						Toast.makeText(TeamListActivity.this,
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
					String[] aFollowersNameArray = new String[theFollowersArray.length()];
					try {
						for (int index = 0; index < theFollowersArray.length(); index++) {
							if(theFollowersArray.getJSONObject(index).getString(TeamBuilder.ALIAS) != null && !theFollowersArray.getJSONObject(index).getString(TeamBuilder.ALIAS).equals(""))
								aFollowersNameArray[index] = theFollowersArray.getJSONObject(index).getString(TeamBuilder.ALIAS);
						
							else
								aFollowersNameArray[index] = theFollowersArray.getJSONObject(index).getString(TeamBuilder.PHONE);
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
		Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);

	super.onBackPressed();
	}
}
