package com.dsdar.thosearoundme;

import java.util.Timer;
import java.util.TimerTask;

import com.dsdar.util.TeamBuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

public class SignupThanksActivity extends Activity {
	private long splashDelay = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup_thanks);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();

				SharedPreferences aTeamSettings = getSharedPreferences(
						"TamPreferences", MODE_PRIVATE);

				Intent aTeamIntent = new Intent().setClass(
						SignupThanksActivity.this, TeamViewActivity.class);
				aTeamIntent.putExtra(TeamBuilder.MEMBERID,
						aTeamSettings.getString(TeamBuilder.MEMBERID, ""));
				startActivity(aTeamIntent);

				// Intent mapViewIntent = new
				// Intent().setClass(SignupThanksActivity.this,
				// MapViewActivity.class);
				// startActivity(mapViewIntent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
}
