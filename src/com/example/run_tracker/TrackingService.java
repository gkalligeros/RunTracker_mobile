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

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class TrackingService extends Service implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener
{
    private static final String TAG = "ServiceTrackerr";
    private List<LatLng> mCoursePoints;
    private LatLng mLastPoint;
    // points

    private final IBinder mBinder = new LocalBinder();
    private boolean mStarted = false;
    private int mDistance = 0;
    private LocationClient mLocationClient;

    public class LocalBinder extends Binder
    {
	TrackingService getService()
	{
	    return TrackingService.this;
	}
    }

    public void onCreate()
    {
	Log.v(TAG, "onCreate");
	mLocationClient = new LocationClient(this, this, this);
    }

    public void onDestroy()
    {
	super.onDestroy();
	// unbindService(myConnection);
	Log.v(TAG, "onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
	super.onStartCommand(intent, flags, startId);
	Log.v(TAG, "onStartCommand");
	/*
	 * Log.v("LocalService", "Received start id " + startId + ": " +
	 * intent);
	 * 
	 * if (!mLocationClient.isConnected() ||
	 * !mLocationClient.isConnecting()) { mLocationClient.connect(); }
	 */
	return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent arg0)
    {
	Log.v(TAG, "onBind");
	if (!mLocationClient.isConnected() && !mLocationClient.isConnecting())
	{
	    mLocationClient.connect();
	}
	return mBinder;
    }

    @Override
    public void onLocationChanged(Location arg0)
    {
	Log.v(TAG, "onLocationChanged");
	// add the next point to list and broadcast the message
	if (mStarted)
	{
	    // add the new point to the list
	    if (!mCoursePoints.isEmpty())
	    {
		Location prev_point = new Location("point A"); // previous location
		prev_point.setLatitude(get_last_point(mCoursePoints).latitude);
		prev_point.setLongitude(get_last_point(mCoursePoints).longitude);
		// sometimes when we lose gps signal the first from second
		// location are 3000km apart or more
		// so we check this in order to be more precise
		if (arg0.distanceTo(prev_point) < 15)
		{
		    mDistance += arg0.distanceTo(prev_point);
		} 
	    } else
	    {
		mDistance = 0;
	    }
	    mCoursePoints.add(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
	    mLastPoint = new LatLng(arg0.getLatitude(), arg0.getLongitude());
	    // broadcast message ;
	    sendlocation();
	}
    }

    public List<LatLng> getCoursePoints()
    {
	return mCoursePoints;
    }

    public void setCoursePoints(List<LatLng> coursePoints)
    {
	mCoursePoints = coursePoints;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {

    }

    @Override
    public void onConnected(Bundle arg0)
    {
	Log.v(TAG, "onConnected");

    }

    @Override
    public void onDisconnected()
    {
	Log.v(TAG, "onDisconnected");
    }

    // this method broadcasts the location change
    private void sendlocation()
    {
	// notify for location change anyone who listens
	Log.v(TAG, "sendlocation");
	Intent intent = new Intent("location_changed");
	LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // does the service listen for location changes ?
    public boolean isStarted()
    {
	return mStarted;
    }

    // start listen for location changes
    public void setStarted(boolean started)
    {
	Log.v(TAG, "setStarted");

	if (mStarted != started)
	{
	    mStarted = started;
	    if (started)
	    {
		// start listening to location changes
		mCoursePoints = new ArrayList<LatLng>();
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(500);// check for location change every 0.5 sec
		mLocationRequest.setSmallestDisplacement(5);// 5 meters min displacement
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		Log.v(TAG, "Listening for location changes");

	    }
	}
    }

    public int getDistance()
    {
	return mDistance;
    }

    public LatLng getLastPoint()
    {
	return mLastPoint;
    }

    public void setLastPoint(LatLng lastPoint)
    {
	mLastPoint = lastPoint;
    }

    public void setDistance(int distance)
    {
	mDistance = distance;
    }

    public void reset_all()
    {
	// stop tracking and reset service
	Log.v(TAG, "" + mCoursePoints);
	mCoursePoints.clear();
	mDistance = 0;
	mStarted = false;
	mLocationClient.removeLocationUpdates(this);
	Log.v(TAG, "reset_all");
	Log.v(TAG, "" + mCoursePoints);
    }

    private LatLng get_last_point(List<LatLng> Points)
    {
	return Points.get(Points.size() - 1);
    }
}
