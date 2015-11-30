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
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dsdar.thosearoundme.R;
import com.dsdar.util.TeamBuilder;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class AdapterInvitation extends ArrayAdapter<String> {
	private final InvitationListActivity context;
	private final String[] web, teamname;
	SharedPreferences aSharedPreference;

	public AdapterInvitation(Activity context, String[] web,
			String[] itsInvitationMemberIdArray,
			SharedPreferences aSharedPreference) {
		super(context, R.layout.li_switch, web);
		this.context = (InvitationListActivity) context;
		this.web = web;
		this.teamname = itsInvitationMemberIdArray;
		this.aSharedPreference = aSharedPreference;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final String itsLoginUserId = aSharedPreference.getString(
				TeamBuilder.MEMBERID, "");

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.li_switch, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(web[position]);
//		rowView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent myProfileIntent = new Intent().setClass(context,
//						EpActivity.class);
//				myProfileIntent.putExtra("aMemberPhone",
//						context.itsInvitationArray[position]);
//				myProfileIntent.putExtra("aFollowerId",
//						context.itsInvitationMemberIdArray[position]);
//				context.startActivity(myProfileIntent);
//			}
//
//		});

		Button btnAccept = (Button) rowView.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String reqMemberId = context.itsInvitationMemberIdArray[position];
				String teamID=context.itsInvitationTeamIdArray[position];
				// Toast.makeText(
				// context.getApplicationContext(),
				// "ACCEPT:::::memberId=" + itsLoginUserId + ", reqMemberId="
				// + reqMemberId, Toast.LENGTH_LONG).show();
				context.updateInvitation(reqMemberId,teamID, true);
			}
		});

		Button btnReject = (Button) rowView.findViewById(R.id.btnReject);
		btnReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String reqMemberId = context.itsInvitationMemberIdArray[position];
				String teamID=context.itsInvitationTeamIdArray[position];
				// Toast.makeText(
				// context.getApplicationContext(),
				// "REJECT:::::memberId=" + itsLoginUserId + ", reqMemberId="
				// + reqMemberId, Toast.LENGTH_LONG).show();
				context.updateInvitation(reqMemberId,teamID, false);
			}
		});

		if (position % 2 == 0)
			rowView.setBackgroundResource(R.drawable.listview_selector_odd);
		else
			rowView.setBackgroundResource(R.drawable.listview_selector_even);

		return rowView;
	}
}