package com.dsdar.thosearoundme.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.TeamDto;

public class TeamListAdapter extends ArrayAdapter<TeamDto> {
	private final Activity context;
	TeamFragment teamFragment;
	private final TeamDto[] teamDto;

	public TeamListAdapter(Activity context, TeamDto[] arrayTeamsDto,
			TeamFragment teamFragment) {
		super(context, R.layout.team_li, arrayTeamsDto);
		this.context = context;
		this.teamDto = arrayTeamsDto;
		this.teamFragment = teamFragment;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final TeamDto dto = teamDto[position];
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.team_li, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(dto.getTeamName() + " - " + dto.getOwnerName());
		if (dto.getIsSticky().equals("true")) {
			txtTitle.setTextColor(Color.RED);
		}
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dto.getIsStickyOwner().equals("true")) {
					teamFragment.openTeam(position);
				} else {
					Toast.makeText(context, "You don't have access permission",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		// Lock
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imgLock);
		if (dto.getIsSticky().equals("true")
				&& !dto.getIsStickyOwner().equals("true")) {
			imageView.setVisibility(View.VISIBLE);
		}

		Button btnRemove = (Button) rowView.findViewById(R.id.button1);
		if (dto.getTeamName().equals("Home")) {
			btnRemove.setVisibility(View.INVISIBLE);
		}

		// Remove Button

		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setTitle("Those Around Me");
				alertDialogBuilder
						.setMessage("Confirm Deletion !")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										teamFragment.removeTeam(position);
									}
								})

						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

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