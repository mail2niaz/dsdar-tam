package com.dsdar.thosearoundme.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		Log.d("TEST", "index==" + index);
		switch (index) {
		case 0:
			return new TeamFragment();
//		case 1:
//			return new FollowerFragment();
//		case 2:
//			return new InvitationFragment();

		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 1;
	}

	@Override
	public int getItemPosition(Object object) {
		if (object instanceof FollowerFragment) {
			((FollowerFragment) object).getAndLoadFollowers();
		} else if (object instanceof InvitationFragment) {
			((InvitationFragment) object).getAndLoadInvitaion();
		}
		// don't return POSITION_NONE, avoid fragment recreation.
		return super.getItemPosition(object);
	}
}
