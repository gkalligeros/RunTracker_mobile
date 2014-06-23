package com.example.run_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.run_tracker.CustomMapFragment.OnMapReadyListener;
import com.google.android.gms.maps.GoogleMap;

public class RunFragment extends Fragment implements OnClickListener,
		OnMapReadyListener {
	private Button mStart, mStop, mPause;
	private String TAG = "RunFragment";
	private GoogleMap mMap;
	boolean is_tracking = false;
	int mDistance = 0;// distance covered
	private TextView mDistanceText;
	private TextView mStatus;
	private Timer mTimer;

	boolean isBound = false;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.v(TAG, "onReceive");
			if (mMap != null) {
				mMap.clear();
				mMap.addPolyline(((MainActivity) getActivity()).getMyService()
						.getMyLine());
				mDistanceText.setText(((MainActivity) getActivity())
						.getMyService().getDistance() + " m");
			}

		}
	};
	private CustomMapFragment mMapFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.runfragment, container, false);
		mDistanceText = (TextView) rootView.findViewById(R.id.dst);
		mStatus = (TextView) rootView.findViewById(R.id.status);
		mTimer = new Timer();
		mTimer.setTimer((Chronometer) rootView.findViewById(R.id.chronometer1));
		// setUpMapIfNeeded();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver, new IntentFilter("location_changed"));
		Log.v(TAG, "onCreateView");
		mStart = (Button) rootView.findViewById(R.id.start);
		mStop = (Button) rootView.findViewById(R.id.stop);
		mPause = (Button) rootView.findViewById(R.id.pause);
		mStart.setOnClickListener(this);
		mStop.setOnClickListener(this);
		mPause.setOnClickListener(this);
		mMapFragment = CustomMapFragment.newInstance();
	    getChildFragmentManager().beginTransaction().replace(R.id.myfragment, mMapFragment).commit();
		setUpMapIfNeeded();
		return rootView;

	}

	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");

	}

	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");

	}

	public void onStart() {
		Log.v(TAG, "onStart");

		super.onStart();
		/*
		 * if (!mLocationClient.isConnected()) { mLocationClient.connect(); }
		 */

	}

	public void onDestroy() {

		super.onDestroy();
		// unbindService(myConnection);
		Log.v(TAG, "onDestroy");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMapIfNeeded() {

		Log.v(TAG, "setUpMapIfNeeded");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.start: {
			((MainActivity) getActivity()).getMyService().setStarted(true);
			((MainActivity) getActivity()).getMyService().setIsTracking(true);
			mStatus.setText(R.string.Running);
			mTimer.startTimer();
			Log.v(TAG, "onStartB");
			break;
		}

		case R.id.pause: {
			Log.v(TAG, "onPauseB");
			((MainActivity) getActivity()).getMyService().setIsTracking(false);
			mStatus.setText(R.string.Paused);
			mTimer.stopTimer();
			break;

		}

		case R.id.stop:
			mStatus.setText(R.string.Stoped);
			Log.v(TAG, "onStopB");

			break;

		}

	}

	@Override
	public void onMapReady() {
		// TODO Auto-generated method stub
		mMap = mMapFragment.getMap();

	}

}
