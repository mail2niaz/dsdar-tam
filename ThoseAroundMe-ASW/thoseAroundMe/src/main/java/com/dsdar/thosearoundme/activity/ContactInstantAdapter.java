package com.dsdar.thosearoundme.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dsdar.thosearoundme.R;

public class ContactInstantAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final ArrayList<String> teamDto;

	public ContactInstantAdapter(Activity context,
			ArrayList<String> arrayTeamsDto) {
		super(context, R.layout.t_li, arrayTeamsDto);
		this.context = context;
		this.teamDto = arrayTeamsDto;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final String dto = teamDto.get(position);
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.t_li, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(dto);

		Button btnRemove = (Button) rowView.findViewById(R.id.button1);

		// Remove Button

		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				teamDto.remove(position);
				notifyDataSetChanged();
			}
		});

		if (position % 2 == 0)
			rowView.setBackgroundResource(R.drawable.listview_selector_odd);
		else
			rowView.setBackgroundResource(R.drawable.listview_selector_even);
		return rowView;
	}

	public int getSectionForPosition(int position) {
		Log.v("getSectionForPosition", "called");
		return 0;
	}
}