package com.dsdar.thosearoundme.ep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.MemberFollowersDto;

public class AdapterEp extends ArrayAdapter<MemberFollowersDto> {
	private final EpActivity context;
	// private final String[] web;
	private String[] aArrayListMemberFollowers;
	static MemberFollowersDto[] arrayFollowersDto;
	String TAG = "TAG";
	HashMap<String, Integer> azIndexer;
	String[] sections;

	public AdapterEp(Activity context, MemberFollowersDto[] arrayFollowersDto2,
			String[] aTeamNameArray) {
		super(context, R.layout.ep_li, arrayFollowersDto2);
		this.context = (EpActivity) context;
		aArrayListMemberFollowers = aTeamNameArray;
		arrayFollowersDto = arrayFollowersDto2;
		azIndexer = new HashMap<String, Integer>(); // stores the positions for
													// the start of each letter

		int size = arrayFollowersDto.length;
		for (int i = size - 1; i >= 0; i--) {
			MemberFollowersDto element = arrayFollowersDto[i];
			// We store the first letter of the word, and its index.
			if ((element.getTeamName() == null)
					|| (element.getTeamName().trim().equals(""))) {
				continue;

			}
			for (int j = 0; j < aArrayListMemberFollowers.length; j++) {
				if (element.getTeamName().equals(aArrayListMemberFollowers[j])) {
					arrayFollowersDto[i].setValue(true);
				}

			}

			azIndexer.put(element.getTeamName().substring(0, 1), i);
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

	// @Override
	// public View getView(int position, View view, ViewGroup parent) {
	// LayoutInflater inflater = context.getLayoutInflater();
	// View rowView = inflater.inflate(R.layout.ep_li, null, true);
	// TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
	// CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox1);
	// txtTitle.setText(web[position]);
	// for (int i = 0; i < aArrayListMemberFollowers.length; i++) {
	// if (web[position].equals(aArrayListMemberFollowers[i])) {
	// checkBox.setChecked(true);
	// }
	//
	// }
	//
	// return rowView;
	// }

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		MemberFollowersDto followerDto = arrayFollowersDto[position];
		System.out.println("FOLLOWERDTO  :: " + followerDto);
		System.out.println("FOLLOWERDTO  :: " + followerDto.getTeamName());
		LayoutInflater inflater = context.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.ep_li, null, true);
		CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox1);
		TextView txtTeamName = (TextView) rowView.findViewById(R.id.txt);

		if (followerDto.getTeamName() != null) {
			txtTeamName.setText(followerDto.getTeamName());
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
					context.followerDtos.get(position).setValue(false);
				} else {
					arrayFollowersDto[position].setValue(true);
					context.followerDtos.get(position).setValue(true);
				}

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