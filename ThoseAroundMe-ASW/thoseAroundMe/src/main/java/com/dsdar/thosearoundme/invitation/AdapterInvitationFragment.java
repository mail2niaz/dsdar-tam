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
package com.dsdar.thosearoundme.invitation;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.ep.EpActivity;
import com.dsdar.thosearoundme.tab.InvitationFragment;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class AdapterInvitationFragment extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] web;
	InvitationFragment fragment;

	public AdapterInvitationFragment(Activity context, String[] web,
			InvitationFragment fragment) {
		super(context, R.layout.li_switch, web);
		this.context = context;
		this.web = web;
		this.fragment = fragment;

	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.li_switch, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(web[position]);
		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myProfileIntent = new Intent().setClass(context,
						EpActivity.class);
				myProfileIntent.putExtra("aMemberPhone",
						fragment.itsInvitationArray[position]);
				myProfileIntent.putExtra("aFollowerId",
						fragment.itsInvitationMemberIdArray[position]);
				context.startActivity(myProfileIntent);
			}

		});
		return rowView;
	}
}