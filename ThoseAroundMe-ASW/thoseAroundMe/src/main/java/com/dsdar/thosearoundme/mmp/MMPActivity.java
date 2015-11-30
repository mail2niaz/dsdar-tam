package com.dsdar.thosearoundme.mmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.ContactsActivity;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.MemberFollowersDto;
import com.dsdar.thosearoundme.dto.RecordingDto;
import com.dsdar.thosearoundme.ep.EpActivity;
import com.dsdar.thosearoundme.util.AppAsyncTask;
import com.dsdar.thosearoundme.util.MyAppConstants;
import com.dsdar.util.TeamBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MMPActivity extends Activity implements OnClickListener,
        AdapterView.OnItemClickListener {
    ListView list;
    // String[] web = { "Roger", "Asiq", "Nirmal", "Niaz", "James", "Stanley" };
    Context context;
    SharedPreferences itsSharedPreference;
    Button btnBack, btnMove, btnAddMember, btnCopy, btnRecord, btnStop;
    private Spinner spinner2;
    private Spinner spinnerTime;
    TextView textView;
    TextView textDuration;
    String[] aTeamNames, aTeamIds;
    String aTeamName, aTeamId, sticky, recordingStatus;
    ArrayList<String> teammembers = new ArrayList<String>();
    ArrayList<String> teammembersId = new ArrayList<String>();
    HashMap<String, Integer> hmapTeamId;
    public ArrayList<MemberFollowersDto> memberDtos = new ArrayList<MemberFollowersDto>();
    MemberFollowersDto[] arrayFollowersDto;
    int selectedteamId = 0;
    String selectedVal;
    String selectedDuration;
    boolean recordStatus = false;
    private Handler itsHandler2 = null;
    private Runnable mHandlerTask2 = null;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    String start_time = "", end_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mmp_activity_main);
        context = this.getApplication().getApplicationContext();
        hmapTeamId = new HashMap<String, Integer>();
        // Team Name
        textView = (TextView) findViewById(R.id.tvAddMemberTeamName);
        Intent intent = getIntent();
        aTeamName = intent.getExtras().getString("aTeamName");
        aTeamId = intent.getExtras().getString("aTeamId");
        sticky = intent.getExtras().getString("isSticky");
        recordingStatus = intent.getExtras().getString("isRecording");
        aTeamNames = intent.getExtras().getStringArray("aTeamNames");
        aTeamIds = intent.getExtras().getStringArray("aTeamIds");
        itsHandler2 = new Handler();
        // preparing hashmap & spinner list
        spinner2 = (Spinner) findViewById(R.id.spinner1);
        List<String> listSpinner = new ArrayList<String>();
        // list.add("Team 1");
        // list.add("Team 2");
        // list.add("Team 3");
        // list.add("Team 4");
        // list.add("Team 5");
        // list.add("Team 6");
        listSpinner.add("Select Team");
        for (int i = 0; i < aTeamNames.length; i++) {
            hmapTeamId.put(aTeamNames[i], Integer.parseInt(aTeamIds[i]));
            if (!aTeamNames[i].equals(aTeamName)) {
                listSpinner.add(aTeamNames[i] + "");
            }
        }

        textView.setText(aTeamName);

        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnStop = (Button) findViewById(R.id.btnStop);
        textDuration = (TextView) findViewById(R.id.txtDuration);
        spinnerTime = (Spinner) findViewById(R.id.timeList);
        List<String> listSpinnerTime = new ArrayList<String>();
        listSpinnerTime.add("Select Duration");
        listSpinnerTime.add("5 min");
        listSpinnerTime.add("15 min");
        listSpinnerTime.add("30 min");
        listSpinnerTime.add("45 min");
        listSpinnerTime.add("60 min");
        listSpinnerTime.add("90 min");
        listSpinnerTime.add("120 min");
        listSpinnerTime.add("150 min");
        ArrayAdapter<String> dataAdapterTime = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listSpinnerTime);
        dataAdapterTime
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(dataAdapterTime);

        btnRecord.setVisibility(View.GONE);
        textDuration.setVisibility(View.GONE);
        spinnerTime.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);

        if (sticky.equals("true")) {
            btnRecord.setVisibility(View.VISIBLE);
            textDuration.setVisibility(View.VISIBLE);
            spinnerTime.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.VISIBLE);
        }
        if (recordingStatus.equals("true")) {
            btnRecord.setText("Recording");
        } else {
            btnRecord.setText("Record");
        }
        btnRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (recordingStatus.equals("true") || recordStatus == true) {
                    Toast.makeText(MMPActivity.this,
                            "Already Recording is InProgress",
                            Toast.LENGTH_SHORT).show();
                } else if (selectedDuration.equals("Select Duration")) {
                    Toast.makeText(MMPActivity.this, "Please select Duration",
                            Toast.LENGTH_SHORT).show();
                } else {

                    btnRecord.setText("Recording");
                    recordStatus = true;

                    MyAppConstants.recording = true;

                    String str = String.valueOf(recordStatus);

                    updateRecording(str, true);

                }

            }
        });

        spinnerTime.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedDuration = spinnerTime.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        btnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (recordingStatus.equals("true") || recordStatus == true) {

                    btnRecord.setText("Record");
                    recordStatus = false;

                    MyAppConstants.recording = false;

                    String str = String.valueOf(recordStatus);
                    updateRecording(str, false);
                } else {
                    Toast.makeText(MMPActivity.this, "No recording to stop",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        list = (ListView) findViewById(R.id.listView1);
        btnBack = (Button) findViewById(R.id.btnDone);
        btnMove = (Button) findViewById(R.id.btnMovee);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnAddMember = (Button) findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent aAddTeamMemberIntent = new Intent().setClass(context,
                        ContactsActivity.class);
                // aAddTeamMemberIntent.putExtra("teamActivity",
                // "addTeamMember");
                // aAddTeamMemberIntent.putExtra("parentActivity",
                // "Add Team Member");
                aAddTeamMemberIntent.putExtra("teamName", aTeamName);
                startActivity(aAddTeamMemberIntent);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // int ht = UIUtil.getHeight(context);
        // Log.d("niaz..", "niaz..==" + ht);
        // list.setLayoutParams(new LinearLayout.LayoutParams(
        // LayoutParams.FILL_PARENT, ht - 500));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // calling Edit Profile
                Intent myProfileIntent = new Intent().setClass(context,
                        EpActivity.class);
                myProfileIntent.putExtra("aMemberPhone",
                        teammembers.get(position));
                myProfileIntent.putExtra("aFollowerId",
                        teammembersId.get(position));
                startActivity(myProfileIntent);

            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listSpinner);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);

        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedVal = spinner2.getSelectedItem().toString();

                if (!selectedVal.equals(null)
                        && !selectedVal.equals("Select Team")) {
                    selectedteamId = hmapTeamId.get(selectedVal);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        btnMove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedVal.equals("Select Team")) {
                    Toast.makeText(MMPActivity.this, "Please select team",
                            Toast.LENGTH_SHORT).show();
                } else {
                    getSelectedMembers("Move");
                }

            }

        });
        btnCopy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedVal.equals("Select Team")) {
                    Toast.makeText(MMPActivity.this, "Please select team",
                            Toast.LENGTH_SHORT).show();
                } else {
                    getSelectedMembers("Copy");

                }

            }

        });

        getTeamMembers();

    }

    private void getSelectedMembers(String mode) {
        ArrayList<MemberFollowersDto> selFollowerDtos = new ArrayList<MemberFollowersDto>();
        // for (MemberFollowersDto followerDto : memberDtos) {
        for (MemberFollowersDto followerDto : arrayFollowersDto) {
            if (followerDto.isValue()) {
                selFollowerDtos.add(followerDto);
            }
        }

        if (selFollowerDtos.size() == 0) {
            Toast.makeText(this, "No Teams Selected", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            JSONArray aFollowersArray = new JSONArray();
            for (MemberFollowersDto followerDto : selFollowerDtos) {
                JSONObject aContactJSONObject = new JSONObject();
                aContactJSONObject.put("name", followerDto.getName());
                aContactJSONObject.put("followerId", followerDto.getMemberId());
                aFollowersArray.put(aContactJSONObject);
            }
            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");
            // String aTeamId = itsSharedPreference.getString(
            // MyAppConstants.TEAM_ID, "");

            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.FROM_TEAM_ID, aTeamId);
            aRequestJson.put(MyAppConstants.TO_TEAM_ID, selectedteamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            aRequestJson.put(MyAppConstants.ACTION, mode);
            aRequestJson.put(MyAppConstants.MEMBERS, aFollowersArray);

            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.MOVE_TEAM_MEMBER,
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
                            Toast.makeText(MMPActivity.this,
                                    "Member updated successfully.",
                                    Toast.LENGTH_LONG).show();
                            // MyAppConstants.IS_MAP_SHOW_ALL = true;
                            // MyAppConstants.IS_FOLLOW_ME = false;
                            finish();

                        } else if (aResponseStatus
                                .equals(MyAppConstants.FAILURE_STATUS))
                            Toast.makeText(MMPActivity.this, aResponseMsg,
                                    Toast.LENGTH_LONG).show();
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(MMPActivity.this,
                                    MyAppConstants.CONNECTION_ERROR,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MMPActivity.this, aResponseMsg,
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
            Toast.makeText(MMPActivity.this, MyAppConstants.JSON_ERROR_REQ,
                    Toast.LENGTH_LONG).show();
        }

    }

    public void moveTeammembers(JSONArray aMembersArray, String mode) {
        // Kala

        try {
            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");

            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.FROM_TEAM_ID, aTeamId);
            aRequestJson.put(MyAppConstants.TO_TEAM_ID, aTeamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            aRequestJson.put(MyAppConstants.ACTION, mode);
            aRequestJson.put(MyAppConstants.MEMBERS, aMembersArray);

            AppAsyncTask aAsyncTask = new AppAsyncTask(context,
                    MyAppConstants.MOVE_TEAM_MEMBER,
                    MyAppConstants.API_POST_TYPE, MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                String aResponseStatus, aResponseMsg, aUserId;

                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theResult) {
                    try {
                        // MyAppConstants.IS_MAP_SHOW_ALL = true;
                        // MyAppConstants.IS_FOLLOW_ME = false;
                        Log.d("Response", theResult);
                        JSONObject aResponseJson = new JSONObject(theResult);

                        aResponseStatus = aResponseJson
                                .getString(MyAppConstants.STATUS);
                        aResponseMsg = aResponseJson
                                .getString(MyAppConstants.MESSAGE);

                        Log.d("Response", theResult);
                        if (aResponseStatus
                                .equals(MyAppConstants.SUCCESS_STATUS)) {
                            // Toast.makeText(
                            // context,
                            // "Team " + aTeamNameEt.getText().toString()
                            // + " Created Successfully",
                            // Toast.LENGTH_LONG).show();

                            ((Activity) context).finish();

                        }
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(context,
                                    MyAppConstants.CONNECTION_ERROR,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, aResponseMsg,
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
            Toast.makeText(context, MyAppConstants.JSON_ERROR_REQ,
                    Toast.LENGTH_LONG).show();
        }

    }

    public void updateRecording(String recordStatus, final boolean val) {
        try {
            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");

            // Constructing request JSON
            JSONObject aRequestJson = new JSONObject();

            if (MyAppConstants.hmapMarkerId.get(aTeamId) != null) {
                aRequestJson.put(MyAppConstants.MARKER_ID,
                        MyAppConstants.hmapMarkerId.get(aTeamId));
            } else {
                aRequestJson.put(MyAppConstants.MARKER_ID, 0);
            }

            if (val) {
                aRequestJson.put("duration", selectedDuration
                        .replace("min", "").trim());
            }

            aRequestJson.put(MyAppConstants.TEAM_ID, aTeamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            aRequestJson.put(MyAppConstants.START_TIME, new Date().getTime());
            if (MyAppConstants.NAME_MARKER != null)
                aRequestJson.put(MyAppConstants.MARKER_NAME, MyAppConstants.NAME_MARKER);
            else
                aRequestJson.put(MyAppConstants.MARKER_NAME, "");
            aRequestJson.put(MyAppConstants.NAME, aTeamName);
            aRequestJson.put(MyAppConstants.LAT, MyAppConstants.LAT_USER);
            aRequestJson.put(MyAppConstants.LNG, MyAppConstants.LNG_USER);
            aRequestJson.put(MyAppConstants.MARKER_LAT,
                    MyAppConstants.LAT_MARKER);
            aRequestJson.put(MyAppConstants.MARKER_LNG,
                    MyAppConstants.LNG_MARKER);
            aRequestJson.put("status", recordStatus);

            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.UPDATE_RECORD, MyAppConstants.API_POST_TYPE,
                    MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theMemberInfo) {

                    // if (theMemberInfo != null) {

                    // } else {
                    // Toast.makeText(getApplicationContext(),
                    // MyAppConstants.CONNECTION_ERROR,
                    // Toast.LENGTH_LONG).show();
                    // }
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
    public void onResume() {
        Log.d("TeamFragment", "calling team fragment..");
        getTeamMembers();
        super.onResume();
    }

    private void getTeamMembers() {
        try {
            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");
            // Constructing request JSON
            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(MyAppConstants.TEAM_ID, aTeamId);
            aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.GET_TEAM_MEMBERS,
                    MyAppConstants.API_POST_TYPE,
                    MyAppConstants.TEAM_MEMBERS_LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theMemberInfo) {
                    if (theMemberInfo != null) {
                        memberDtos.clear();
                        iterateAndDrawMarkers(theMemberInfo);

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

    private void iterateAndDrawMarkers(String theMemberInfo) {
        teammembers.clear();
        memberDtos.clear();
        teammembersId.clear();
        try {
            JSONObject aTeamMembersJson = new JSONObject(theMemberInfo);

            // Retrieving status from response JSON
            String aResponseStatus = aTeamMembersJson
                    .getString(MyAppConstants.STATUS);

            // If API response status is success
            if (aResponseStatus.equals(MyAppConstants.SUCCESS_STATUS)) { // Get
                // team
                // members
                // array

                JSONArray aTeamMemberArray = aTeamMembersJson
                        .getJSONArray(MyAppConstants.RESULT);

                for (int index = 0; index < aTeamMemberArray.length(); index++) {
                    JSONObject aMemberJson;
                    aMemberJson = aTeamMemberArray.getJSONObject(index);

                    MemberFollowersDto membersDto = new MemberFollowersDto();

                    membersDto.setName(aMemberJson
                            .getString(MyAppConstants.ALIAS));

                    membersDto.setMemberId(aMemberJson
                            .getString(MyAppConstants.MEMBER_ID));

                    memberDtos.add(membersDto);

                    // teammembers
                    // .add(aMemberJson.getString(MyAppConstants.ALIAS));
                    teammembersId.add(aMemberJson
                            .getString(MyAppConstants.MEMBER_ID));
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

        list.setFastScrollEnabled(true);

        arrayFollowersDto = new MemberFollowersDto[memberDtos.size()];
        arrayFollowersDto = memberDtos.toArray(arrayFollowersDto);
        for (MemberFollowersDto followerDto : arrayFollowersDto)
            System.out.println(followerDto);

        AdapterMMP adapter = new AdapterMMP(MMPActivity.this, arrayFollowersDto);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

    }

    public void removeTeamMember(int position) {

        try {
            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");

            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(TeamBuilder.TEAMID, aTeamId);
            aRequestJson.put(TeamBuilder.MEMBERID, aMemberId);
            aRequestJson.put("followerId", teammembersId.get(position));
            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.REMOVE_TEAM_MEMBER,
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

                            getTeamMembers();
                            Toast.makeText(context,
                                    "Members has been removed successfully.",
                                    Toast.LENGTH_LONG).show();
                            // ((Activity) context).finish();

                        }
                    } catch (JSONException theJSONException) {
                        theJSONException.printStackTrace();
                        Log.e(this.getClass().getName(),
                                "JSON Exception while retrieving response from login webservice");
                        if (aResponseMsg == null || aResponseMsg.equals("")) {
                            Toast.makeText(context,
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
            Toast.makeText(context, MyAppConstants.JSON_ERROR_REQ,
                    Toast.LENGTH_LONG).show();
        }

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

    }

    public void getRecordingInfo() {
        try {

            itsSharedPreference = context.getApplicationContext()
                    .getSharedPreferences(MyAppConstants.APP_PREFERENCE,
                            Context.MODE_PRIVATE);
            String aMemberId = itsSharedPreference.getString(
                    MyAppConstants.MEMBER_ID, "");

            JSONObject aRequestJson = new JSONObject();
            aRequestJson.put(TeamBuilder.TEAMID, aTeamId);
            aRequestJson.put(TeamBuilder.MEMBERID, aMemberId);

            AppAsyncTask aAsyncTask = new AppAsyncTask(this,
                    MyAppConstants.GET_RECORDINGS,
                    MyAppConstants.API_POST_TYPE, MyAppConstants.LOADING);
            aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
                @Override
                public void onPreExecuteConcluded() {
                }

                @Override
                public void onPostExecuteConcluded(String theRecordingInfo) {
                    if (theRecordingInfo != null) {
                        try {
                            JSONObject aRecordingJson = new JSONObject(
                                    theRecordingInfo);

                            // Retrieving status from response JSON
                            String aResponseStatus = aRecordingJson
                                    .getString(MyAppConstants.STATUS);

                            // If API response status is success
                            if (aResponseStatus
                                    .equals(MyAppConstants.SUCCESS_STATUS)) { // Get
                                // team
                                // members
                                // array

                                JSONArray aRecordingArray = aRecordingJson
                                        .getJSONArray(MyAppConstants.RESULT);

                                JSONObject aRecordJson;
                                aRecordJson = aRecordingArray.getJSONObject(0);
                                RecordingDto recordingDto = new RecordingDto();

                                recordingDto.setStartTime(aRecordJson
                                        .getString("startTime"));
                                recordingDto.setEndTime(aRecordJson
                                        .getString("endTime"));
                                recordingDto.setMarkerId(aRecordJson
                                        .getString("markerId"));
                                recordingDto.setTeamId(aRecordJson
                                        .getString("sTeamId"));

                                Date start = new Date(Long.valueOf(recordingDto
                                        .getStartTime()) * 1000);
                                start_time = sdf.format(start);
                                Date end = new Date(Long.valueOf(recordingDto
                                        .getEndTime()) * 1000);
                                end_time = sdf.format(end);

                                System.out.println("START:: " + start_time);
                                System.out.println("END :; " + end_time);

                                if (mHandlerTask2 != null)
                                    itsHandler2.removeCallbacks(mHandlerTask2);
                                // Create a new handler to run for every n
                                // minutes
                                mHandlerTask2 = new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("tmf",
                                                "calling mHandlerTask.......");
                                        // Get and set team members location in
                                        // map
                                        long epochTime = System
                                                .currentTimeMillis() / 1000;
                                        int epochTimeInt = (int) epochTime;
                                        Date currentTime = new Date(Long
                                                .valueOf(epochTimeInt) * 1000);
                                        String current_time = sdf
                                                .format(currentTime);
                                        System.out.println("StrtTIme:  "
                                                + current_time);
                                        System.out.println("End TIme:  "
                                                + end_time);
                                        if (current_time.equals(end_time)
                                                || (current_time
                                                .compareTo(end_time) > 0)) {
                                            // stopRefreshTimer();
                                            btnRecord.setText("Record");
                                            updateRecording("false", false);

                                        }
                                        itsHandler2.postDelayed(mHandlerTask2,
                                                1 * 1000);
                                    }
                                };
                                mHandlerTask2.run();

                                //

                            }
                            // If API response status is failure
                            else if (aResponseStatus
                                    .equals(MyAppConstants.FAILURE_STATUS)) {
                                String aResponseMsg = aRecordingJson
                                        .getString(MyAppConstants.MESSAGE);
                            }

                        } catch (JSONException theJSONException) {
                            theJSONException.printStackTrace();
                            Log.e(this.getClass().getName(),
                                    "JSONException when retrieving members information from getMyTeamMembers webservice");
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

}