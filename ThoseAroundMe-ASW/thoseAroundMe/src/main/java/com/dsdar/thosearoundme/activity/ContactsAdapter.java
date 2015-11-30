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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dsdar.thosearoundme.ContactsActivity;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.dto.ContactDto;

/**
 * Activity to view User's followers.
 * 
 * @author Bharath_Murali
 */

public class ContactsAdapter extends ArrayAdapter<ContactDto> implements
		SectionIndexer {
	private final ContactsActivity context;
	SharedPreferences aSharedPreference;
	static ContactDto[] arrayContactDto;
	String TAG = "TAG";
	HashMap<String, Integer> azIndexer;
	String[] sections;

	public ContactsAdapter(Activity context, ContactDto[] arrayContactDto2) {
		super(context, R.layout.contact_li, arrayContactDto2);
		this.context = (ContactsActivity) context;
		arrayContactDto = arrayContactDto2;
		azIndexer = new HashMap<String, Integer>(); // stores the positions for
													// the start of each letter

		int size = arrayContactDto.length;
		for (int i = size - 1; i >= 0; i--) {
			ContactDto element = arrayContactDto[i];
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
		ContactDto contactDto = arrayContactDto[position];
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.contact_li, null, true);

		TextView name = (TextView) rowView.findViewById(R.id.name);
		name.setText(contactDto.getName());

		TextView phone = (TextView) rowView.findViewById(R.id.phone);
		phone.setText(contactDto.getPhone());
		// ImageView qcb = (ImageView) rowView.findViewById(R.id.qcb);
		//
		// if (contactDto.getPhoto() != null) {
		// Log.d(TAG, "contactDto.getPhoto()=" + contactDto.getPhoto());
		// Uri photo_uri = Uri.parse(contactDto.getPhoto());
		// qcb.setImageURI(photo_uri);
		// // new ImageDownloaderTask(qcb).execute(contactDto.getPhoto());
		// }

		CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox1);
		if (arrayContactDto[position].isValue())
			cb.setChecked(true);
		else
			cb.setChecked(false);

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (arrayContactDto[position].isValue()) {
					arrayContactDto[position].setValue(false);
					context.contactDtos.get(position).setValue(false);
				} else {
					arrayContactDto[position].setValue(true);
					context.contactDtos.get(position).setValue(true);
				}

			}
		});
		if (position % 2 == 0)
			rowView.setBackgroundResource(R.drawable.listview_selector_odd);
		else
			rowView.setBackgroundResource(R.drawable.listview_selector_even);
		return rowView;
	}

	@Override
	public Object[] getSections() {
		return sections; // to string will be called to display the letter
	}

	@Override
	public int getSectionForPosition(int position) {
		Log.v("getSectionForPosition", "called");
		return 0;
	}

	@Override
	public int getPositionForSection(int section) {
		String letter = sections[section];
		return azIndexer.get(letter);
	}

}