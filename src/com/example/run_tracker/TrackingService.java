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
    private List<LatLng> mCoursePoints = new ArrayList<LatLng>();
    private LatLng mLastPoint; 
    // points

    private final IBinder mBinder = new LocalBinder();
    private boolean  mStarted = false;
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
	if (!mLocationClient.isConnected() || !mLocationClient.isConnecting())
	{
	    mLocationClient.connect();
	}
	return mBinder;

    }

    @Override
    public void onLocationChanged(Location arg0)
    {
	Log.v(TAG, "onLocationChanged");
	if (mStarted)
	{
	    mCoursePoints.add(new LatLng(arg0.getLatitude(), arg0
		    .getLongitude()));
	    mDistance += 5;
	    mLastPoint = new LatLng(arg0.getLatitude(), arg0.getLongitude());
	    sendlocation();
	}
	// TODO Auto-generated method stub

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
	// TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0)
    {
	Log.v(TAG, "onConnected");
	LocationRequest mLocationRequest = LocationRequest.create();
	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	mLocationRequest.setInterval(5000);
	mLocationRequest.setSmallestDisplacement(5);
	mLocationClient.requestLocationUpdates(mLocationRequest, this);
	// TODO Auto-generated method stub

    }

    @Override
    public void onDisconnected()
    {
	// TODO Auto-generated method stub

    }

    private void sendlocation()
    {
	Intent intent = new Intent("location_changed");

	LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public boolean isStarted()
    {
	return mStarted;
    }

  

    public void setStarted(boolean started)
    {	
	Log.v(TAG, "setStarted");

        mStarted = started;
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

}
