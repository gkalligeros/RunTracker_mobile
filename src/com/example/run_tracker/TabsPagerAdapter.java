package com.example.run_tracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {

        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new RunFragment();
        case 1:
            // Games fragment activity
            return new StatsFragment();
        case 2:
            // Movies fragment activity
            return new ProfileFragment();
        }
 
        return null;// TODO Auto-generated method stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
