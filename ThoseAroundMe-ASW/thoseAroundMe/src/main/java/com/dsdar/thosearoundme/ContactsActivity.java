/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dsdar.thosearoundme;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.activity.ContactInstantAdapter;
import com.dsdar.thosearoundme.activity.ContactsAdapter;
import com.dsdar.thosearoundme.dto.ContactDto;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.ContactsListFragment;
import com.dsdar.thosearoundme.util.MyAppConstants;

//import com.google.android.gms.drive.internal.ac;
/**
 * FragmentActivity to hold the main {@link ContactsListFragment}. On larger
 * screen devices which can fit two panes also load
 * {@link ContactDetailFragment}.
 */
public class ContactsActivity extends Activity implements OnClickListener,
		AdapterView.OnItemClickListener {

	public ArrayList<ContactDto> contactDtos = new ArrayList<ContactDto>();
	private ListView itsTeamListLv, listViewInstant;
	ContactsActivity activity;
	ContactDto[] arrayContactDto;
	Button btnRequest, btnCancel, btnInstantAdd;
	SharedPreferences itsSharedPreference;
	String selectedTeam = "";
	ContactsAdapter adapter;
	private ProgressBar spinner;
	Handler mHandler;

	// instant add
	ArrayList<String> arrayContactInstant = new ArrayList<String>();
	ContactInstantAdapter contactInstantAdapter;
	EditText etInstantPhone;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity);
		mHandler = new Handler();
		activity = this;
		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		selectedTeam = getIntent().getSerializableExtra("teamName").toString();

		// Stop spinner after 5 seconds

		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		//
		//
		// }
		// }, 5000);

		prepareContacts();

		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setText("Add Team Member [ " + selectedTeam + " ]");

		Button btnRequest = (Button) findViewById(R.id.btnRequest);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);

		btnRequest.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		// for (ContactDto contactDto : contactDtos) {
		// Log.e("TAG", "Name=" + contactDto.getName() + ", phone="
		// + contactDto.getPhone() + ", Type=" + contactDto.getType());
		// }

		itsTeamListLv = (ListView) findViewById(R.id.listView1);
		itsTeamListLv.setFastScrollEnabled(true);
		itsTeamListLv.setEmptyView(spinner);
		arrayContactDto = new ContactDto[contactDtos.size()];
		arrayContactDto = contactDtos.toArray(arrayContactDto);
		adapter = new ContactsAdapter(activity, arrayContactDto);
		itsTeamListLv.setAdapter(adapter);
		itsTeamListLv.setOnItemClickListener(this);

		// instant
		Button btnInstantAdd = (Button) findViewById(R.id.btnInstantAdd);
		btnInstantAdd.setOnClickListener(this);
		etInstantPhone = (EditText) findViewById(R.id.etInstantPhone);

		listViewInstant = (ListView) findViewById(R.id.listViewInstant);
		listViewInstant.setFastScrollEnabled(true);
		contactInstantAdapter = new ContactInstantAdapter(activity,
				arrayContactInstant);
		listViewInstant.setAdapter(contactInstantAdapter);
		listViewInstant.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("tag", "inside click");
		CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox1);
		if (!cb.isChecked())
			cb.setChecked(true);
		else
			cb.setChecked(false);

	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnRequest:
			addTeamMember();
			break;

		case R.id.btnCancel:

			for (ContactDto contactDto : contactDtos) {
				Log.e("TAG",
						"Name=" + contactDto.getName() + ", phone="
								+ contactDto.getPhone() + ", Type="
								+ contactDto.getType());
				contactDto.setValue(false);
				itsTeamListLv.invalidateViews();
			}
			break;

		case R.id.btnInstantAdd:
			// arrayContactInstant.add("9486090108");
			// arrayContactInstant.add("9486090104");
			// arrayContactInstant.add("9486090102");
			// arrayContactInstant.add("9486090103");
			addInstantPhone();

			break;

		default:
			break;
		}
	}

	private void addInstantPhone() {
		String etPhone = etInstantPhone.getText().toString().trim();
		if ((etPhone.length() == 10) || (etPhone.length() == 11)) {
			arrayContactInstant.add(etPhone);
			listViewInstant.invalidateViews();
			etInstantPhone.setText("");
		} else {
			Toast.makeText(
					activity,
					"Please enter valid phone number ! for e.g. 10 digit 1234567890 ",
					Toast.LENGTH_LONG).show();
		}
	}

	public void addTeamMember() {
		ArrayList<ContactDto> selContactDtos = new ArrayList<ContactDto>();
		for (ContactDto contactDto : contactDtos) {
			if (contactDto.isValue()) {
				Log.e("TAG",
						"Name=" + contactDto.getName() + ", phone="
								+ contactDto.getPhone() + ", Type="
								+ contactDto.getType());
				selContactDtos.add(contactDto);
			}
		}

		if ((selContactDtos.size() == 0) && (arrayContactInstant.size() == 0)) {
			Toast.makeText(activity, "No Members Selected", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		try {
			JSONArray aMembersArray = new JSONArray();
			for (ContactDto contactDto : selContactDtos) {
				JSONObject aContactJSONObject = new JSONObject();
				aContactJSONObject.put("phone", contactDto.getPhone());
				aContactJSONObject.put("name", contactDto.getName());
				aMembersArray.put(aContactJSONObject);
			}

			// Adding instant Phone number
			for (String str : arrayContactInstant) {
				JSONObject aContactJSONObject = new JSONObject();
				aContactJSONObject.put("phone", str);
				aContactJSONObject.put("name", "Instant");
				aMembersArray.put(aContactJSONObject);
			}

			itsSharedPreference = activity.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.NAME, selectedTeam);
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put(MyAppConstants.PHONE,
					itsSharedPreference.getString(MyAppConstants.PHONE, ""));
			aRequestJson.put(MyAppConstants.MEMBERS, aMembersArray);

			AppAsyncTask aAsyncTask = new AppAsyncTask(activity,
					MyAppConstants.ADD_TEAM_MEMBER_NEW,
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
							Toast.makeText(activity,
									"Request has been sent successfully.",
									Toast.LENGTH_LONG).show();
							// MyAppConstants.IS_MAP_SHOW_ALL = true;
							// MyAppConstants.IS_FOLLOW_ME = false;
							((Activity) activity).finish();
							// MyAppConstants.isRefreshMap = true;

						} else if (aResponseStatus
								.equals(MyAppConstants.FAILURE_STATUS))
							Toast.makeText(activity, aResponseMsg,
									Toast.LENGTH_LONG).show();
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(activity,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(activity, aResponseMsg,
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
			Toast.makeText(activity, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}

	}

	private void prepareContacts() {
		int ID = 0;
		int LOOKUP_KEY = 1;
		int DISPLAY_NAME = 2;
		int PHOTO_THUMBNAIL_DATA = 3;
		int SORT_KEY = 4;

		contactDtos.clear();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, Phone.DISPLAY_NAME + " ASC");
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						ContactDto contactDto = new ContactDto();
						int phoneType = pCur
								.getInt(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						String phoneNumber = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						// Uri contactUri = ContactsContract.Contacts
						// .getLookupUri(pCur.getLong(ID),
						// pCur.getString(LOOKUP_KEY));
						// String PHOTO_THUMBNAIL_URI = pCur
						// .getString(pCur
						// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

						String type = "";
						switch (phoneType) {
						case Phone.TYPE_MOBILE:
							type = "MOBILE";
							// Log.e(name + "(mobile number)", phoneNumber);
							break;
						case Phone.TYPE_HOME:
							type = "HOME";
							// Log.e(name + "(home number)", phoneNumber);
							break;
						case Phone.TYPE_WORK:
							type = "WORK";
							// Log.e(name + "(work number)", phoneNumber);
							break;
						case Phone.TYPE_OTHER:
							type = "OTHER";
							// Log.e(name + "(other number)", phoneNumber);
							break;
						default:
							break;
						}
						contactDto.setName(name);
						contactDto.setPhone(phoneNumber);
						contactDto.setType(type);
						// if (PHOTO_THUMBNAIL_URI != null) {
						// Log.d("TAG", "PHOTO_THUMBNAIL_URI="
						// + PHOTO_THUMBNAIL_URI);
						// contactDto.setPhoto(PHOTO_THUMBNAIL_URI);
						// }
						// contactDto.setCursor(cur);
						contactDtos.add(contactDto);

					}
					pCur.close();

				}
			}
		}
	}
}
