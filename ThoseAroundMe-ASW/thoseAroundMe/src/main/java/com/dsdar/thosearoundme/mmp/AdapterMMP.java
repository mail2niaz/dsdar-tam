package com.dsdar.thosearoundme.mmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.MemberFollowersDto;

public class AdapterMMP extends ArrayAdapter<MemberFollowersDto> {
	private final MMPActivity context;
	static MemberFollowersDto[] arrayFollowersDto;
	String TAG = "TAG";
	HashMap<String, Integer> azIndexer;
	String[] sections;

	public AdapterMMP(Activity context, MemberFollowersDto[] arrayFollowersDto2) {

		super(context, R.layout.mmp_li, arrayFollowersDto2);
		this.context = (MMPActivity) context;
		arrayFollowersDto = arrayFollowersDto2;
		azIndexer = new HashMap<String, Integer>();
		int size = arrayFollowersDto.length;
		for (int i = size - 1; i >= 0; i--) {
			MemberFollowersDto element = arrayFollowersDto[i];
			// We store the first letter of the word, and its index.
			if ((element.getName() == null)
					|| (element.getName().trim().equals(""))) {
				continue;
			}

			azIndexer.put(element.getName().substring(0, 1), i);
		}

		Set<String> keys = azIndexer.keySet(); // set of letters

		Iterator<String> it = keys.iterator();
		ArrayList<String> keyList = new ArrayList<String>();

		while (it.hasNext()) {
			String key = it.next();
			keyList.add(key);
		}
		Collections.sort(keyList);// sort the keylist
		sections = new String[keyList.size()]; // simple conversion to array
		keyList.toArray(sections);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		MemberFollowersDto followerDto = arrayFollowersDto[position];
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mmp_li, null, true);

		CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox1);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

		if (followerDto.getName() != null) {
			txtTitle.setText(followerDto.getName());
		}

		if (arrayFollowersDto[position].isValue())
			cb.setChecked(true);
		else
			cb.setChecked(false);

		// ImageView qcb = (ImageView) rowView.findViewById(R.id.qcb);
		//
		// if (contactDto.getPhoto() != null) {
		// Log.d(TAG, "contactDto.getPhoto()=" + contactDto.getPhoto());
		// Uri photo_uri = Uri.parse(contactDto.getPhoto());
		// qcb.setImageURI(photo_uri);
		// // new ImageDownloaderTask(qcb).execute(contactDto.getPhoto());
		// }

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (arrayFollowersDto[position].isValue()) {
					arrayFollowersDto[position].setValue(false);
					context.memberDtos.get(position).setValue(false);
				} else {
					arrayFollowersDto[position].setValue(true);
					context.memberDtos.get(position).setValue(true);
				}

			}
		});

		Button btnRemove = (Button) rowView.findViewById(R.id.btnRemove);
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
										context.removeTeamMember(position);
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

	public Object[] getSections() {
		return sections; // to string will be called to display the letter
	}

	public int getSectionForPosition(int position) {
		Log.v("getSectionForPosition", "called");
		return 0;
	}

	public int getPositionForSection(int section) {
		String letter = sections[section];
		return azIndexer.get(letter);
	}

}