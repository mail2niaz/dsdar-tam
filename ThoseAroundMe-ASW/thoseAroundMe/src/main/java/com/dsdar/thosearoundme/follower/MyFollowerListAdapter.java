package com.dsdar.thosearoundme.follower;

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
import com.dsdar.thosearoundme.tab.FollowerFragment;

public class MyFollowerListAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] web;
	FollowerFragment fragment;

	public MyFollowerListAdapter(Activity context, String[] web,
			FollowerFragment fragment) {
		super(context, R.layout.follower_li, web);
		this.context = context;
		this.web = web;
		this.fragment = fragment;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.follower_li, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(web[position]);
		txtTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myProfileIntent = new Intent().setClass(context,
						EpActivity.class);
				myProfileIntent.putExtra("aMemberPhone",
						fragment.itsFollowersArray[position]);
				myProfileIntent.putExtra("aFollowerId",
						fragment.itsFollowersMemberIdArray[position]);
				context.startActivity(myProfileIntent);
			}

		});
		return rowView;
	}
}