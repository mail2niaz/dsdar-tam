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

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.util.MyAppConstants;

/**
 * Activity for splash screen. Class used to load splash screen for few seconds
 * 
 * @author senthil_kumaran
 */
public class SplashActivity extends Activity {
	
	private String registration_token = null;
	
	@Override
	public void onCreate(Bundle theSavedInstanceState) {
		super.onCreate(theSavedInstanceState);
		Fabric.with(this, new Crashlytics());

		Log.i(this.getClass().getName(), MyAppConstants.SPLASH_LOAD_INFO);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		Uri parameter = getIntent().getData();
		if(parameter != null)
		{
		String key = parameter.getQueryParameter("token");
		registration_token = key;
		}
		
		SharedPreferences aSharedPreference = getSharedPreferences(
				MyAppConstants.APP_PREFERENCE, MODE_PRIVATE);
		SharedPreferences.Editor aEditor = aSharedPreference.edit();
		aEditor.putString(MyAppConstants.SERVER_URL, MyAppConstants.API_URL);
		aEditor.commit();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent logInIntent = new Intent().setClass(SplashActivity.this,
						WelcomeActivity.class);
				logInIntent.putExtra("reg_token", registration_token);
				startActivity(logInIntent);
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, MyAppConstants.SPLASH_DURATION);
	}
}