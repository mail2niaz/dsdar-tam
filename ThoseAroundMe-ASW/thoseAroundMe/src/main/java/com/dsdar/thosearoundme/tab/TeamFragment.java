package com.dsdar.thosearoundme.tab;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.TeamDto;
import com.dsdar.thosearoundme.mmp.MMPActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;

public class TeamFragment extends Fragment {
	ListView list;
	// String[] web = { "Home", "Team 1", "Team 2", "Team 3", "Team 4", "Team 5"
	// };
	String[] teams, teamsId, stickyTeam,recordingStatus;
	Context itsContext;
	Button btnBack, btnCreateTeam;
	private EditText itsTeamNameEt;
	private String itsTeamNameVal;
	private JSONArray itsTeamArrayJson;
	String itsLoginUserId;
	FragmentActivity fragmentActivity;
	TeamFragment teamFragment;
	public CheckBox cb;
	SharedPreferences itsSharedPreference;
	boolean isSticky = false;
	public ArrayList<TeamDto> teamDtos = new ArrayList<TeamDto>();
	TeamDto[] arrayTeamsDto;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		teamFragment = this;
		View rootView = inflater.inflate(R.layout.t_activity_main, container,
				false);
		cb = (CheckBox) rootView.findViewById(R.id.checkBox1);
		Intent intent = this.getActivity().getIntent();
		itsTeamNameEt = (EditText) rootView.findViewById(R.id.txtTeam);

		fragmentActivity = (FragmentActivity) this.getActivity();
		teams = intent.getExtras().getStringArray("aTeamList");
		teamsId = intent.getExtras().getStringArray("aTeamIdList");
		stickyTeam = intent.getExtras().getStringArray("aStickyTeamList");
		itsLoginUserId = intent.getExtras().getString("itsLoginUserId");

		// TeamListAdapter adapter = new TeamListAdapter(this.getActivity(),
		// teams);
		list = (ListView) rootView.findViewById(R.id.listView1);
		list.setBackgroundResource(R.drawable.rounded_corners);
		// list.setAdapter(adapter);
		itsContext = this.getActivity().getApplication()
				.getApplicationContext();

		InputMethodManager in = (InputMethodManager) itsContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(itsTeamNameEt.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		// int ht = UIUtil.getHeight(context);
		// Log.d("niaz..", "niaz..==" + ht);
		// list.setLayoutParams(new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, ht - 480));
		// list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Intent aCreateTeamIntent = new Intent().setClass(itsContext,
		// MMPActivity.class);
		// aCreateTeamIntent.putExtra("aTeamName", teams[+position]);
		// aCreateTeamIntent.putExtra("aTeamId", teamsId[+position]);
		// aCreateTeamIntent.putExtra("aTeamNames", teams);
		// aCreateTeamIntent.putExtra("aTeamIds", teamsId);
		// startActivity(aCreateTeamIntent);
		//
		// }
		// });

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}

			}
		});
		btnBack = (Button) rootView.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		btnCreateTeam = (Button) rootView.findViewById(R.id.btnCreateTeam);

		btnCreateTeam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb.isChecked()) {
					isSticky = true;
				}
				addTeam();
				// Intent aWelcomeIntent = new Intent().setClass(getActivity(),
				// ContactsListActivity.class);
				// aWelcomeIntent.putExtra("parentActivity", "Add Team");
				// startActivity(aWelcomeIntent);
			}

		});

		getAndLoadTeamInfo();

		return rootView;
	}

	public void addTeam() {
		// niaz
		itsTeamNameVal = itsTeamNameEt.getText().toString().trim();
		if (itsTeamNameVal == null || itsTeamNameVal.equals("")) {
			Toast.makeText(itsContext, "Please Enter a Team Name",
					Toast.LENGTH_LONG).show();
			return;
		}

		try {
			itsContext = this.getActivity();
			itsSharedPreference = itsContext.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.NAME, itsTeamNameVal);
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put("isSticky", isSticky);
			AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
					MyAppConstants.CREATE_TEAM, MyAppConstants.API_POST_TYPE,
					MyAppConstants.LOADING);
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
							Toast.makeText(
									itsContext,
									"Team "
											+ itsTeamNameEt.getText()
													.toString()
											+ " Created Successfully",
									Toast.LENGTH_LONG).show();
							// ((Activity) itsContext).finish();
							MyAppConstants.isRefreshMap = true;
							itsTeamNameEt.setText("");
							cb.setChecked(false);
							isSticky = false;
							getAndLoadTeamInfo();

						}
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(itsContext,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(itsContext, aResponseMsg,
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
			Toast.makeText(itsContext, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}

	}

	// protected Boolean validate() {
	// String aErrorMessage = null;
	// Boolean isValid = true;
	// itsTeamNameVal = itsTeamNameEt.getText().toString().trim();
	//
	// if (itsTeamNameVal.equals("")) {
	// aErrorMessage = MyAppConstants.ERR_ENT_TEAMNAME;
	// isValid = false;
	// itsTeamNameEt.requestFocus();
	// }
	// if (aErrorMessage != null) {
	// Toast.makeText(getApplicationContext(), aErrorMessage,
	// Toast.LENGTH_SHORT).show();
	// }
	// return isValid;
	// }
	@Override
	public void onResume() {
		Log.d("TeamFragment", "calling team fragment..");
		getAndLoadTeamInfo();
		super.onResume();
	}

	public void getAndLoadTeamInfo() {

		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);

			AppAsyncTask aAsyncTask = new AppAsyncTask(fragmentActivity,
					MyAppConstants.GET_MY_TEAM, MyAppConstants.API_POST_TYPE,
					MyAppConstants.TEAM_LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String theResult) {
					if (theResult != null) {
						loadTeamInfo(theResult);
					} else {
						Toast.makeText(fragmentActivity,
								MyAppConstants.CONNECTION_ERROR,
								Toast.LENGTH_LONG).show();
					}
				}

				/**
				 * Method to load action bar with team information
				 * 
				 * @param theTeamJson
				 */
				private void loadTeamInfo(String theTeamJson) {
					teamDtos.clear();
					try {
						// Users team information in JSON format
						JSONObject aTeamJson = new JSONObject(theTeamJson);

						// Retrieving status from response JSON
						String aResponseStatus = aTeamJson
								.getString(MyAppConstants.STATUS);

						// If API response status is success
						if (aResponseStatus
								.equals(MyAppConstants.SUCCESS_STATUS)) {

							JSONArray aTeamMemberArray = aTeamJson
									.getJSONArray(MyAppConstants.RESULT);

							for (int index = 0; index < aTeamMemberArray
									.length(); index++) {
								JSONObject aMemberJson;
								aMemberJson = aTeamMemberArray
										.getJSONObject(index);
								TeamDto teamDto = new TeamDto();
								teamDto.setTeamName(aMemberJson
										.getString(MyAppConstants.NAME));
								teamDto.setTeamId(aMemberJson
										.getString(MyAppConstants.TEAM_ID));
								teamDto.setIsSticky(aMemberJson
										.getString("isSticky"));
								teamDto.setIsOwner(aMemberJson
										.getString("isOwner"));
								if (itsLoginUserId.equals(aMemberJson
										.getString(MyAppConstants.MEMBER_ID))) {
									teamDto.setIsStickyOwner("true");
								} else {
									teamDto.setIsStickyOwner("false");
								}
								teamDto.setOwnerName(aMemberJson
										.getString("ownerName"));
								teamDtos.add(teamDto);
								itsTeamArrayJson = aTeamJson
										.getJSONArray(MyAppConstants.RESULT);
								teams = getTeamNames(itsTeamArrayJson);
								teamsId = getTeamId(itsTeamArrayJson);
								stickyTeam = getStickyTeamNames(itsTeamArrayJson);
								recordingStatus = getRecordingStatus(itsTeamArrayJson);
								// team.add(aMemberJson.getString(MyAppConstants.NAME));
								// teamId.add(aMemberJson.getString(MyAppConstants.TEAM_ID));
							}
						}

						//

						// TeamListAdapter adapter = new TeamListAdapter(
						// fragmentActivity, teams, teamFragment);
						// list.setAdapter(adapter);
						// adapter.notifyDataSetChanged();
						// create new tabs and display team name in tabs

						// }
						// If API response status is failure
						else if (aResponseStatus
								.equals(MyAppConstants.FAILURE_STATUS)) {
							String aResponseMsg = aTeamJson
									.getString(MyAppConstants.MESSAGE);
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from getMyTeam webservice");
					}

					list.setFastScrollEnabled(true);

					arrayTeamsDto = new TeamDto[teamDtos.size()];
					arrayTeamsDto = teamDtos.toArray(arrayTeamsDto);
					for (TeamDto teamDto : arrayTeamsDto)
						System.out.println(teamDto);
					TeamListAdapter adapter = new TeamListAdapter(
							fragmentActivity, arrayTeamsDto, teamFragment);
					list.setAdapter(adapter);
					// adapter.notifyDataSetChanged();

				}

				/**
				 * Method to get team names from a team array
				 * 
				 * @param theTeamArray
				 * @return aTeamNameArray
				 */
				private String[] getTeamNames(JSONArray theTeamArray) {
					String[] aTeamNameArray = new String[theTeamArray.length()];
					try {
						for (int index = 0; index < theTeamArray.length(); index++) {
							aTeamNameArray[index] = theTeamArray.getJSONObject(
									index).getString(TeamBuilder.NAME);
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aTeamNameArray;
				}

				/**
				 * Method to get sticky team from a team array
				 * 
				 * @param theTeamArray
				 * @return aStickyTeamNameArray
				 */
				private String[] getStickyTeamNames(JSONArray theTeamArray) {
					String[] aStickyTeamNameArray = new String[theTeamArray
							.length()];
					try {
						for (int index = 0; index < theTeamArray.length(); index++) {
							aStickyTeamNameArray[index] = theTeamArray
									.getJSONObject(index).getString("isSticky");
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aStickyTeamNameArray;
				}

				/**
				 * Method to get recording status from a team array
				 * 
				 * @param theTeamArray
				 * @return aRecordingArray
				 */
				private String[] getRecordingStatus(JSONArray theTeamArray) {
					String[] aRecordingArray = new String[theTeamArray.length()];
					try {
						for (int index = 0; index < theTeamArray.length(); index++) {
							aRecordingArray[index] = theTeamArray
									.getJSONObject(index).getString(
											"isRecording");
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aRecordingArray;
				}

				/**
				 * Method to get team id from a team array
				 * 
				 * @param theTeamArray
				 * @return aTeamIdArray
				 */
				private String[] getTeamId(JSONArray theTeamArray) {
					String[] aTeamIdArray = new String[theTeamArray.length()];
					try {
						for (int index = 0; index < theTeamArray.length(); index++) {
							aTeamIdArray[index] = theTeamArray.getJSONObject(
									index).getString(TeamBuilder.TEAMID);
						}
					} catch (JSONException theJsonException) {
						theJsonException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception when constructing a team name array");
					}
					return aTeamIdArray;
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJsonException) {
			theJsonException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while constructing request for getMyTeam webservice");
		}

	}

	public void removeTeam(int position) {

		try {
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
			aRequestJson.put(TeamBuilder.TEAMID, teamsId[position]);

			AppAsyncTask aAsyncTask = new AppAsyncTask(this.getActivity(),
					MyAppConstants.REMOVE_TEAM, MyAppConstants.API_POST_TYPE,
					MyAppConstants.LOADING);
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
							Toast.makeText(itsContext, "successfully Removed.",
									Toast.LENGTH_LONG).show();
							// ((Activity) context).finish();
							MyAppConstants.isRefreshMap = true;
							getAndLoadTeamInfo();

						}
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(itsContext,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
						}
					}
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJSONException) {
			theJSONException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while constructing request for login webservice");
			Toast.makeText(itsContext, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}

	}

	public void openTeam(int position) {
		Intent aCreateTeamIntent = new Intent().setClass(itsContext,
				MMPActivity.class);
		aCreateTeamIntent.putExtra("aTeamName", teams[+position]);
		aCreateTeamIntent.putExtra("aTeamId", teamsId[+position]);
		aCreateTeamIntent.putExtra("isSticky", stickyTeam[+position]);
		aCreateTeamIntent.putExtra("isRecording", recordingStatus[+position]);
		aCreateTeamIntent.putExtra("aTeamNames", teams);
		aCreateTeamIntent.putExtra("aTeamIds", teamsId);
		startActivity(aCreateTeamIntent);
	}

}
