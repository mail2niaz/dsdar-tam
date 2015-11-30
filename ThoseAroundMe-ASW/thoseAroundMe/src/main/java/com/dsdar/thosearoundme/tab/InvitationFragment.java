package com.dsdar.thosearoundme.tab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.invitation.AdapterInvitationFragment;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.thosearoundme.util.UIUtil;
import com.dsdar.util.TeamBuilder;

public class InvitationFragment extends Fragment {
	InvitationFragment fragment;
	ListView list;
	// String[] web = { "Roger", "Asiq", "Nirmal", "Niaz", "James", "Stanley" };
	Context context;
	Button btnBack;
	private JSONArray itsInvitationArrayJson;
	public String[] itsInvitationArray;
	public String[] itsInvitationMemberIdArray;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment = this;
		View rootView = inflater.inflate(R.layout.invitation_activity_main,
				container, false);
		list = (ListView) rootView.findViewById(R.id.listView1);

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
		btnBack = (Button) rootView.findViewById(R.id.btnDone);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		getAndLoadInvitaion();
		return rootView;
	}

//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		getAndLoadInvitaion();
//		super.onResume();
//	}
//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		if (hidden) {
//			// do when hidden
//		} else {
//			// do when show
//			getAndLoadFollowers();
//		}
//	}

	public void getAndLoadInvitaion() {

		try {
			JSONObject aRequestJson = new JSONObject();
			// aRequestJson.put(MyAppConstants.MEMBER_ID, itsLoginUserId);
			aRequestJson.put(
					MyAppConstants.PHONE,
					getActivity().getSharedPreferences(
							MyAppConstants.APP_PREFERENCE,
							getActivity().MODE_PRIVATE).getString(
							MyAppConstants.PHONE, ""));
			aRequestJson.put(
					MyAppConstants.MEMBER_ID,
					getActivity().getSharedPreferences(
							MyAppConstants.APP_PREFERENCE,
							getActivity().MODE_PRIVATE).getString(
							TeamBuilder.MEMBERID, ""));

			AppAsyncTask aAsyncTask = new AppAsyncTask(getActivity(),
					MyAppConstants.GET_MY_INVITATION,
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
										TeamBuilder.PHONE);
								itsInvitationArray = aInvitationNameArray;
								itsInvitationMemberIdArray = getFollowersNames(
										itsInvitationArrayJson,
										TeamBuilder.MEMBERID);
							} else {
								itsInvitationArray = new String[0];
							}
							// Setting Adapter
							if ((itsInvitationArray.length == 0)
									|| (itsInvitationArray == null)) {
								// final AlertDialog alertDialog = new
								// AlertDialog.Builder(
								// getActivity()).create();
								// LayoutInflater inflater = getActivity()
								// .getLayoutInflater();
								// View view = inflater.inflate(
								// R.layout.alertbox_title, null);
								// alertDialog.setCustomTitle(view);
								// alertDialog
								// .setMessage("You do not have any Invitations");
								// alertDialog.setButton("OK",
								// new DialogInterface.OnClickListener() {
								// public void onClick(
								// DialogInterface dialog,
								// int which) {
								// alertDialog.cancel();
								// }
								// });
								// alertDialog.show();

							} else {
								AdapterInvitationFragment adapter = new AdapterInvitationFragment(
										getActivity(), itsInvitationArray,
										fragment);
								list.setAdapter(adapter);
								adapter.notifyDataSetChanged();
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
