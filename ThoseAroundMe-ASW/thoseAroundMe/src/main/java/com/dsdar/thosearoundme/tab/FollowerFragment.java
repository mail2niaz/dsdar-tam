package com.dsdar.thosearoundme.tab;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamViewActivity;
import com.dsdar.thosearoundme.activity.AdapterFollower;
import com.dsdar.thosearoundme.activity.FollowersListActivity;
import com.dsdar.thosearoundme.follower.MyFollowerListAdapter;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.UIUtil;
import com.dsdar.util.TeamBuilder;

public class FollowerFragment extends Fragment {
	FollowerFragment fragment;
	ListView list;
	// String[] web = { "Roger", "Asiq", "Nirmal", "Niaz", "James", "Stanley" };
	Context context;
	Button btnBack;
	private JSONArray itsFollowersArrayJson;
	public String[] itsFollowersArray;
	public String[] itsFollowersMemberIdArray;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment = this;
		View rootView = inflater.inflate(R.layout.follower_activity_main,
				container, false);
		// MyFollowerListAdapter adapter = new MyFollowerListAdapter(
		// this.getActivity(), web);
		list = (ListView) rootView.findViewById(R.id.listView1);
		// list.setAdapter(adapter);
		context = this.getActivity().getApplication().getApplicationContext();
		int ht = UIUtil.getHeight(context);
		Log.d("niaz..", "niaz..==" + ht);
		list.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, ht - 480));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		btnBack = (Button) rootView.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		getAndLoadFollowers();
		return rootView;
	}

	public void getAndLoadFollowers() {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(
					MyAppConstants.MEMBER_ID,
					getActivity().getSharedPreferences(
							MyAppConstants.APP_PREFERENCE,
							getActivity().MODE_PRIVATE).getString(
							TeamBuilder.MEMBERID, ""));

			AppAsyncTask aAsyncTask = new AppAsyncTask(getActivity(),
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
										TeamBuilder.PHONE);
								itsFollowersArray = aFollowersNameArray;
								itsFollowersMemberIdArray = getFollowersNames(
										itsFollowersArrayJson,
										TeamBuilder.MEMBERID);
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
//											getActivity()).create();
//									LayoutInflater inflater = getActivity()
//											.getLayoutInflater();
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
									 * (getResources() .getDrawable(R.drawable
									 * .navigation_arrow ), null, null, null);
									 * aTvTeamMember.setTextSize(25);
									 * aTvTeamMember .setGravity(Gravity.LEFT);
									 * aTvTeamMember.setPadding(0, 20, 0, 0); //
									 * add the textview to the linearlayout
									 * itsCreateTeamLayout
									 * .addView(aTvTeamMember);
									 * 
									 * // save a reference to the textview for
									 * later aTextViews[i] = aTvTeamMember; }
									 */
									//
									ArrayList<String> aArrayListFollowers = new ArrayList<String>(
											Arrays.asList(itsFollowersArray));

									// final ArrayAdapter<String> adapter =
									// new
									// ArrayAdapter<String>(
									// this, R.layout.my_followers_textview,
									// R.id.tvFollowerslist_content,
									// aArrayListFollowers);
									// itsTeamListLv.setAdapter(adapter);

									MyFollowerListAdapter adapter = new MyFollowerListAdapter(
											getActivity(), itsFollowersArray,
											fragment);

									list.setAdapter(adapter);
									// list.invalidateViews();
									adapter.notifyDataSetChanged();

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
						Toast.makeText(getActivity(),
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

}
