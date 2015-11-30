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

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.dto.MarkerDto;
import com.dsdar.thosearoundme.dto.TeamCode;
import com.dsdar.thosearoundme.util.MyAppConstants;

import java.util.ArrayList;

//import com.google.android.gms.drive.internal.ac;


public class StickyMarkerBehaviorDialog extends Dialog {
    Dialog aMemberDialog = null;
    TeamViewActivity activity = null;
    ArrayList<TeamCode> teamCodes;
    MarkerDto markerDto;
    String markerCode = null, markerName = null;
    String markerCodeDesc = null;
    String markerDesc = null;
    EditText et_description, et_name;
    TextView textViewCodeDesc;

    public StickyMarkerBehaviorDialog(TeamViewActivity activity) {
        super(activity, R.style.DialogSlideAnim);
        aMemberDialog = this;
        this.activity = activity;
        teamCodes = (ArrayList<TeamCode>) activity.teamCodes;
        markerDto = activity.itsTeamMapFragment.markerDto;
        initialize();
    }

    private void initialize() {
        // TODO_CONCERN Auto-generated method stub

        // Sticky Marker
        aMemberDialog.setContentView(R.layout.stickymarker_dialog);
        aMemberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        aMemberDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        aMemberDialog.getWindow().setGravity(Gravity.BOTTOM);
        aMemberDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);


        //Marker Description
        et_description = (EditText) aMemberDialog.findViewById(R.id.et_desc);
        et_description.clearFocus();
        et_description.setText(markerDto.getMarkerDesc());
        et_description.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO_CONCERN Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO_CONCERN Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO_CONCERN Auto-generated method stub
                markerDesc = et_description.getText().toString();
            }
        });


        //Marker Name
        et_name = (EditText) aMemberDialog.findViewById(R.id.et_name);
        et_name.clearFocus();
        et_name.setText(markerDto.getMarkerName());
        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO_CONCERN Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO_CONCERN Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO_CONCERN Auto-generated method stub
                markerName = et_name.getText().toString();
            }
        });


        textViewCodeDesc = (TextView) aMemberDialog
                .findViewById(R.id.textViewCodeDesc);
        if (markerDto.getMarkerCode() != null)
            textViewCodeDesc.setText(markerDto.getMarkerCode() + "-"
                    + markerDto.getMarkerCodeDesc());

        final String[] items = new String[teamCodes.size()];
        for (int i = 0; i < teamCodes.size(); i++) {
            items[i] = teamCodes.get(i).getCode() + " - "
                    + teamCodes.get(i).getDescription();
        }

        ListView listView1 = (ListView) aMemberDialog
                .findViewById(R.id.listView1);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                textViewCodeDesc.setText(items[position]);
                String[] text = items[position].split("-");
                markerCode = text[0];
                markerCodeDesc = text[1];
                submitBehavior();
            }

        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, items);

        listView1.setAdapter(adapter);

        ImageView aCancelButton = (ImageView) aMemberDialog
                .findViewById(R.id.bCancelMemberDialog);
        aCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                aMemberDialog.dismiss();
            }
        });

        ImageView aSendButton = (ImageView) aMemberDialog
                .findViewById(R.id.btnSend);
        aSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitBehavior();
            }

        });

    }

    private void submitBehavior() {

        Log.d("STICKYTEST", markerCode + ":::" + markerCodeDesc + ":::"
                + markerDesc + ":::"
                + markerName);
        if ((activity.itsTeamMapFragment.markerDto != null)
                && !((markerName == null) && (markerCode == null) && (markerCodeDesc == null) && (markerDesc == null))) {
            activity.itsTeamMapFragment.markerDto
                    .setMarkerCode(markerCode);
            activity.itsTeamMapFragment.markerDto
                    .setMarkerCode(markerCodeDesc);
            activity.itsTeamMapFragment.markerDto
                    .setMarkerDesc(markerDesc);
            activity.itsTeamMapFragment.markerDto
                    .setMarkerName(markerName);
            activity.insertStickyTeamMarkerInfo(
                    MyAppConstants.myStickyMarker.getPosition().latitude,
                    MyAppConstants.myStickyMarker.getPosition().longitude,
                    markerCode, markerCodeDesc, markerDesc, markerName);
            Toast.makeText(activity, "Successfully updated the changes made for Target behavior!",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(activity, "No changes made for Target behavior!",
                    Toast.LENGTH_SHORT).show();
        }

        aMemberDialog.dismiss();
    }

}
