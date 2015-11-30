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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.util.MyAppConstants;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class MyProfileActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	SharedPreferences aSharedPreference;
	Button btnBack,btnLocationSettings;
	CheckBox checkBoxAccuracy, checkBoxTime;
	boolean booleanAccuracy, booleanTime;
	MyProfileActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myprofile);
		activity = this;
		aSharedPreference = getSharedPreferences(MyAppConstants.APP_PREFERENCE,
				MODE_PRIVATE);

		btnBack = (Button) findViewById(R.id.buttonDone);
		btnBack.setOnClickListener(this);
		btnLocationSettings = (Button) findViewById(R.id.button_location_setings);
		btnLocationSettings.setOnClickListener(this);

		checkBoxAccuracy = (CheckBox) findViewById(R.id.checkBoxAccuracy);
		checkBoxTime = (CheckBox) findViewById(R.id.checkBoxTime);
		checkBoxAccuracy.setOnCheckedChangeListener(this);
		checkBoxTime.setOnCheckedChangeListener(this);

		booleanAccuracy = aSharedPreference.getBoolean(
				MyAppConstants.USER_SEL_ACCURACY, false);
		booleanTime = aSharedPreference.getBoolean(
				MyAppConstants.USER_SEL_TIME, false);

		checkBoxAccuracy.setChecked(booleanAccuracy);
		checkBoxTime.setChecked(booleanTime);

	}

	@Override
	public void onClick(View theView) {
		switch (theView.getId()) {
		case R.id.buttonDone:
			this.onBackPressed();
			break;

		case R.id.button_location_setings:
//			startActivity(
//					new Intent(Settings.ACTION_SETTINGS));
			startActivity(new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				break;

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences.Editor aEditor = aSharedPreference.edit();

		switch (buttonView.getId()) {
		case R.id.checkBoxAccuracy:
			Log.d("TAG", "checkBoxAccuracy Value =" + isChecked);
			aEditor.putBoolean(MyAppConstants.USER_SEL_ACCURACY, isChecked);
			break;
		case R.id.checkBoxTime:
			Log.d("TAG", "checkBoxTime Value =" + isChecked);
			aEditor.putBoolean(MyAppConstants.USER_SEL_TIME, isChecked);
			break;
		}
		aEditor.commit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		MyAppConstants.isProfileChanges = true;
		super.onBackPressed();
	}

}
