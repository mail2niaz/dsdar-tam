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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.ep.EpActivity;
import com.dsdar.util.TeamBuilder;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class AdapterFollower extends ArrayAdapter<String> {
	// private final Activity context;
	private final String[] web, teamname,phonenumber;
	FollowersListActivity context;
	SharedPreferences aSharedPreference;

	public AdapterFollower(Activity context, String[] web,
			String[] itsFollowersMemberIdArray,String[] itsPhoneNumberArray,
			SharedPreferences aSharedPreference) {
		super(context, R.layout.follower_li, web);
		this.context = (FollowersListActivity) context;
		this.web = web;
		this.aSharedPreference = aSharedPreference;
		this.teamname = itsFollowersMemberIdArray;
		this.phonenumber=itsPhoneNumberArray;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final String itsLoginUserId = aSharedPreference.getString(
				TeamBuilder.MEMBERID, "");
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.follower_li, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		// TextView txtTeam = (TextView) rowView.findViewById(R.id.txtTeam);
		txtTitle.setText(web[position]);
		// txtTeam.setText(teamname[position]);

		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myProfileIntent = new Intent().setClass(context,
						EpActivity.class);
				myProfileIntent.putExtra("aTeamName",web[position]);
				myProfileIntent.putExtra("aMemberPhone",
						context.itsPhoneNumberArray[position]);
				myProfileIntent.putExtra("aFollowerId",
						context.itsFollowersMemberIdArray[position]);
				context.startActivity(myProfileIntent);
			}

		});

		Button btnRemove = (Button) rowView.findViewById(R.id.btnRemove);
		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String reqMemberId = context.itsFollowersMemberIdArray[position];

				Toast.makeText(
						context.getApplicationContext(),
						"memberId=" + itsLoginUserId + ", reqMemberId="
								+ context.itsFollowersMemberIdArray[position],
						Toast.LENGTH_LONG).show();
				context.updateFollwers(reqMemberId);
			}
		});

		if (position % 2 == 0)
			rowView.setBackgroundResource(R.drawable.listview_selector_odd);
		else
			rowView.setBackgroundResource(R.drawable.listview_selector_even);

		

		return rowView;
	}
}