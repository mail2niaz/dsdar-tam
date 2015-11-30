package com.dsdar.thosearoundme.ep;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.ColorPickerDialog;
import com.dsdar.thosearoundme.ColorPickerDialog.OnColorChangedListener;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.MemberFollowersDto;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.UIUtil;
import com.dsdar.util.TeamBuilder;

public class EpActivity extends Activity implements OnClickListener,
		AdapterView.OnItemClickListener {

	ListView list;
	String[] web = { "Home", "Team 1", "Team 2", "Team 3", "Team 4", "Team 5" };
	Context context;
	ImageView imageView;
	int mStackLevel = 0;
	Button btnBack, btnApply, btnSelect;
	TextView editText1;
	public String followerId, memberPhone, teamName, loginMemberId;
	ArrayList<String> team = new ArrayList<String>();
	ArrayList<String> teamId = new ArrayList<String>();
	SharedPreferences itsSharedPreference;
	private JSONArray itsTeamMembersArrayJson;
	private LinearLayout ll;
	public ArrayList<MemberFollowersDto> followerDtos = new ArrayList<MemberFollowersDto>();
	MemberFollowersDto[] arrayFollowersDto;
	int color;
	private int ColorAh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		itsSharedPreference = getSharedPreferences(
				MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_activity_main);
		editText1 = (TextView) findViewById(R.id.editText1);
		// editTextPhone = (TextView) findViewById(R.id.tvAddMemberTeamName);
		Intent intent = getIntent();
		loginMemberId = itsSharedPreference.getString(MyAppConstants.MEMBER_ID,
				"");
		memberPhone = intent.getExtras().getString("aMemberPhone");
		followerId = intent.getExtras().getString("aFollowerId");
		teamName = intent.getExtras().getString("aTeamName");
		// myProfileIntent.putExtra("aTeamIdList", web);
		editText1.setText(teamName);
		// editTextPhone.setText(memberPhone);
		list = (ListView) findViewById(R.id.listView1);

		// imageView = (ImageView) findViewById(R.id.imageView1);
		// imageView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// mStackLevel++;
		//
		// // DialogFragment.show() will take care of adding the fragment
		// // in a transaction. We also want to remove any currently
		// // showing
		// // dialog, so make our own transaction and take care of that
		// // here.
		// FragmentTransaction ft = getFragmentManager()
		// .beginTransaction();
		// Fragment prev = getFragmentManager()
		// .findFragmentByTag("dialog");
		// if (prev != null) {
		// ft.remove(prev);
		// }
		// ft.addToBackStack(null);
		//
		// // Create and show the dialog.
		// // DialogFragment newFragment =
		// // MyDialogFragment.newInstance(mStackLevel);
		//
		// // ColorPickerDialog newFragment = new ColorPickerDialog();
		// // newFragment.setShowsDialog(true);
		// // newFragment.showPaletteView();
		//
		// // newFragment.show(ft, "dialog");
		// }
		// });

		context = this.getApplication().getApplicationContext();
		int ht = UIUtil.getHeight(context);
		Log.d("niaz..", "niaz..==" + ht);
		// list.setLayoutParams(new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, ht - 550));
		// list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// }
		// });

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnApply = (Button) findViewById(R.id.btnApply);
		btnApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				color = ColorAh;
				updateMemberFollowers();
			}
		});
		getMemberFollowers();

		btnSelect = (Button) findViewById(R.id.btnSelectColor);
		ll = (LinearLayout) findViewById(R.id.colorBox);

		btnSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPickerDialog cpd = new ColorPickerDialog(EpActivity.this,
						listener, ColorAh);
				cpd.show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		ll.setBackgroundColor(ColorAh);
	}

	OnColorChangedListener listener = new OnColorChangedListener() {
		@Override
		public void colorChanged(int color) {
			ColorAh = color;
			ll.setBackgroundColor(ColorAh);
		}
	};

	public void updateMemberFollowers() {
		ArrayList<MemberFollowersDto> selFollowerDtos = new ArrayList<MemberFollowersDto>();
		for (MemberFollowersDto followerDto : followerDtos) {
			// if (followerDto.isValue()) {

			selFollowerDtos.add(followerDto);
			// }
		}

		if (selFollowerDtos.size() == 0) {
			Toast.makeText(this, "No Teams Selected", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		try {
			JSONArray aTeamsArray = new JSONArray();
			for (MemberFollowersDto followerDto : selFollowerDtos) {
				JSONObject aContactJSONObject = new JSONObject();
				aContactJSONObject.put("name", followerDto.getTeamName());
				aContactJSONObject.put("teamId", followerDto.getTeamId());
				aContactJSONObject.put("value", followerDto.isValue());
				aTeamsArray.put(aContactJSONObject);
			}
			itsSharedPreference = this.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put(MyAppConstants.FOLLOWER_ID, followerId);
			aRequestJson.put(MyAppConstants.COLOR, color);
			aRequestJson.put(MyAppConstants.TEAMS, aTeamsArray);
			AppAsyncTask aAsyncTask = new AppAsyncTask(this,
					MyAppConstants.UPDATE_MEMBER_FOLLOWERS,
					MyAppConstants.API_POST_TYPE, MyAppConstants.LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				String aResponseStatus, aResponseMsg, aUserId;

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
							Toast.makeText(EpActivity.this,
									"Member profile updated successfully.",
									Toast.LENGTH_LONG).show();
							// MyAppConstants.IS_MAP_SHOW_ALL = true;
							// MyAppConstants.IS_FOLLOW_ME = false;
							finish();

						} else if (aResponseStatus
								.equals(MyAppConstants.FAILURE_STATUS))
							Toast.makeText(EpActivity.this, aResponseMsg,
									Toast.LENGTH_LONG).show();
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(EpActivity.this,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(EpActivity.this, aResponseMsg,
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJSONException) {
			theJSONException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while constructing request for login webservice");
			Toast.makeText(EpActivity.this, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}

	}

	private void getMemberFollowers() {
		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, followerId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(this,
					MyAppConstants.GET_MEMBER_FOLLOWERS,
					MyAppConstants.API_POST_TYPE, null);
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

								String[] aTeamNameArray = getTeamNames(itsTeamMembersArrayJson);
								getFollowerColorCode();
								getAvailableTeams(aTeamNameArray);

							} else {
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
				private String[] getTeamNames(JSONArray theTeamMemberArray) {
					String[] aTeamMemberNameArray = new String[theTeamMemberArray
							.length()];
					try {
						for (int index = 0; index < theTeamMemberArray.length(); index++) {
							aTeamMemberNameArray[index] = theTeamMemberArray
									.getJSONObject(index).getString(
											TeamBuilder.NAME);
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

	private void getFollowerColorCode() {
		try {
			// Constructing request JSON
			JSONObject aRequestJson = new JSONObject();
			itsSharedPreference = this.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put(MyAppConstants.FOLLOWER_ID, followerId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(this,
					MyAppConstants.GET_FOLLOWER_COLOR_CODE,
					MyAppConstants.API_POST_TYPE,
					MyAppConstants.TEAM_MEMBERS_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String outputjson) {
					if (outputjson != null) {
						try {

							JSONObject aTeamMemberJson = new JSONObject(
									outputjson);

							JSONArray itsMemberFollowerArrayJson = aTeamMemberJson
									.getJSONArray(MyAppConstants.RESULT);
							String color = itsMemberFollowerArrayJson
									.getJSONObject(0).getString("color");
							ColorAh = Integer.parseInt(color);
							ll.setBackgroundColor(ColorAh);
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

	private void getAvailableTeams(final String[] aTeamNameArray) {
		try {
			// Constructing request JSON
			JSONObject aRequestJson = new JSONObject();

			aRequestJson.put(MyAppConstants.MEMBER_ID, followerId);
			aRequestJson
					.put(MyAppConstants.FOLLOWER_ID, itsSharedPreference
							.getString(MyAppConstants.MEMBER_ID, ""));

			AppAsyncTask aAsyncTask = new AppAsyncTask(this,
					MyAppConstants.GET_AVAIL_TEAM,
					MyAppConstants.API_POST_TYPE,
					MyAppConstants.TEAM_MEMBERS_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String outputjson) {
					if (outputjson != null) {
						iterateAndDrawMarkers(outputjson, aTeamNameArray);

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

	private void iterateAndDrawMarkers(String theMemberInfo,
			String[] aTeamNameArray) {
		followerDtos.clear();
		try {
			JSONObject aTeamMembersJson = new JSONObject(theMemberInfo);

			// Retrieving status from response JSON
			String aResponseStatus = aTeamMembersJson
					.getString(MyAppConstants.STATUS);

			// If API response status is success
			if (aResponseStatus.equals(MyAppConstants.SUCCESS_STATUS)) {
				// Get team members array
				JSONArray aTeamMemberArray = aTeamMembersJson
						.getJSONArray(MyAppConstants.RESULT);

				for (int index = 0; index < aTeamMemberArray.length(); index++) {
					JSONObject aMemberJson;
					aMemberJson = aTeamMemberArray.getJSONObject(index);
					MemberFollowersDto followersDto = new MemberFollowersDto();
					followersDto.setMemberId(aMemberJson
							.getString(MyAppConstants.MEMBER_ID));
					followersDto.setTeamName(aMemberJson
							.getString(MyAppConstants.NAME));
					followersDto.setTeamId(aMemberJson
							.getString(MyAppConstants.TEAM_ID));

					followerDtos.add(followersDto);
					// team.add(aMemberJson.getString(MyAppConstants.NAME));
					// teamId.add(aMemberJson.getString(MyAppConstants.TEAM_ID));
				}
			}
			// If API response status is failure
			else if (aResponseStatus.equals(MyAppConstants.FAILURE_STATUS)) {
				String aResponseMsg = aTeamMembersJson
						.getString(MyAppConstants.MESSAGE);
			}
		} catch (JSONException theJSONException) {
			theJSONException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSONException when retrieving members information from getMyTeamMembers webservice");
		}

		// String[] stockArr = new String[team.size()];
		// stockArr = team.toArray(stockArr);
		// for (String s : stockArr)
		// System.out.println(s);
		list.setFastScrollEnabled(true);

		arrayFollowersDto = new MemberFollowersDto[followerDtos.size()];
		arrayFollowersDto = followerDtos.toArray(arrayFollowersDto);
		for (MemberFollowersDto followerDto : arrayFollowersDto)
			System.out.println(followerDto);
		AdapterEp adapter = new AdapterEp(EpActivity.this, arrayFollowersDto,
				aTeamNameArray);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		// AdapterMMP adapter = new AdapterMMP(this, stockArr);
		// list.setAdapter(adapter);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("tag", "inside click");
		CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox1);
		if (!cb.isChecked())
			cb.setChecked(true);
		else
			cb.setChecked(false);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnApply:
			updateMemberFollowers();
			break;

		default:
			break;
		}
	};

}