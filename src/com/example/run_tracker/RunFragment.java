/*Copyright (C) 2014 Yiorgos Kalligeros

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/
package com.example.run_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunFragment extends Fragment implements OnClickListener,
	OnMapReadyListener
{
    private Button mStart, mStop;
    private String TAG = "RunFragment";
    private float ZOOM_LEVEL = 17;

    private GoogleMap mMap;
    int mDistance = 0;// distance covered
    private TextView mDistanceText;
    private TextView mStatus;
    private Timer mTimer = new Timer();;
    private CustomMapFragment mMapFragment;
    PolylineOptions options = new PolylineOptions();;

    boolean isBound = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
	@Override
	public void onReceive(Context context, Intent intent)
	{
	    mDistanceText.setText(((MainActivity) getActivity()).getMyService()
		    .getDistance() + " m");
	    if (mMap != null)
	    {
		options.add(((MainActivity) getActivity()).getMyService()
			.getLastPoint());
		options.width(3);
		options.color(Color.GREEN);
		mMap.addPolyline(options);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
			((MainActivity) getActivity()).getMyService()
				.getLastPoint(), ZOOM_LEVEL));

	    }
	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	Log.v(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState)
    {

	View rootView = inflater
		.inflate(R.layout.runfragment, container, false);
	mDistanceText = (TextView) rootView.findViewById(R.id.dst);
	mStatus = (TextView) rootView.findViewById(R.id.status);
	mStop = (Button) rootView.findViewById(R.id.stop);
	mTimer.setTimer((Chronometer) rootView.findViewById(R.id.chronometer1));
	mStart = (Button) rootView.findViewById(R.id.start);
	mStart.setOnClickListener(this);
	mStop.setOnClickListener(this);
	mMapFragment = CustomMapFragment.newInstance();
	getChildFragmentManager().beginTransaction()
		.replace(R.id.myfragment, mMapFragment).commit();
	if (!mTimer.getFirstStart())
	{
	    mTimer.startTimer();
	}
	Log.v(TAG, "onCreateView");

	return rootView;

    }

    public void onPause()
    {
	super.onPause();
	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
		mMessageReceiver);
	Log.v(TAG, "onPause");
	// getActivity().getIntent().p;

    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
	super.onSaveInstanceState(state);
	Log.v(TAG, "onSaveInstanceState");
    }

    public void onResume()
    {
	super.onResume();
	if (((MainActivity) getActivity()).getMyService() != null)

	{
	    if (!((MainActivity) getActivity()).getMyService()
		    .getCoursePoints().isEmpty())
	    {
		Log.v(TAG, "InIf1");

		options = new PolylineOptions();
		options.addAll(((MainActivity) getActivity()).getMyService()
			.getCoursePoints());
		options.width(3);
		options.color(Color.GREEN);
		mMap.addPolyline(options);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
			((MainActivity) getActivity()).getMyService()
				.getLastPoint(), ZOOM_LEVEL));
		mDistanceText.setText(((MainActivity) getActivity())
			.getMyService().getDistance() + " m");
	    }
	    if (((((MainActivity) getActivity()).getMyService().isStarted())))
	    {
		Log.v(TAG, "InIf2");

		mStatus.setText(R.string.Running);

	    }
	    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
		    mMessageReceiver, new IntentFilter("location_changed"));
	}

	Log.v(TAG, "onResume");

    }

    public void onStart()
    {
	Log.v(TAG, "onStart");

	super.onStart();

    }

    public void onDestroy()
    {

	super.onDestroy();
	// unbindService(myConnection);
	Log.v(TAG, "onDestroy");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();
	if (id == R.id.action_settings)
	{
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
	// TODO Auto-generated method stub
	switch (v.getId())
	{
	case R.id.start:
	{
	    ((MainActivity) getActivity()).getMyService().setStarted(true);
	    mStatus.setText(R.string.Running);
	    mTimer.startTimer();
	    Log.v(TAG, "onStartB");
	    break;
	}
	case R.id.stop:
	    mStatus.setText(R.string.Stoped);
	    Log.v(TAG, "onStopB");
	    break;
	}

    }

    @Override
    public void onMapReady()
    {
	// TODO Auto-generated method stub
	mMap = mMapFragment.getMap();
	if (mMap != null)
	{
	    mMap.setMyLocationEnabled(true);
	}

    }

}
