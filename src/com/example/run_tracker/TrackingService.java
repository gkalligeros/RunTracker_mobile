package com.example.run_tracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackingService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final String TAG = "ServiceTrackerr";
	private List<LatLng> ls = new ArrayList<LatLng>();// list of points
	private final IBinder mBinder = new LocalBinder();
	private boolean mTracking, mStarted = false;
	private int mDistance=0;
	PolylineOptions myLine = new PolylineOptions();
	private LocationClient mLocationClient;

	public List<LatLng> getLs() {
		return ls;
	}

	public void setLs(List<LatLng> ls) {
		this.ls = ls;
	}

	public boolean isTracking() {
		return mTracking;
	}

	public void setIsTracking(boolean isTracking) {
		mTracking = isTracking;
	}

	public class LocalBinder extends Binder {
		TrackingService getService() {
			return TrackingService.this;
		}
	}

	public void onCreate() {

		Log.v(TAG, "onCreate");
		mLocationClient = new LocationClient(this, this, this);

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
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
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		/*
		 * if (!mLocationClient.isConnected() ||
		 * !mLocationClient.isConnecting()) { mLocationClient.connect(); }
		 */
		Log.v(TAG, "onBind");
		if (!mLocationClient.isConnected() || !mLocationClient.isConnecting()) {
			mLocationClient.connect();
		}
		return mBinder;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		sendlocation();
		Log.v(TAG, "onLocationChanged");
		if (mTracking) {
			myLine.add(new LatLng(arg0.getAltitude(), arg0.getLongitude()))
					.width(2).color(Color.GREEN);
			mDistance+=5;
		} else {
			myLine.add(new LatLng(arg0.getAltitude(), arg0.getLongitude()))
					.width(2).color(Color.RED);
		}
		// TODO Auto-generated method stub

	}

	public PolylineOptions getMyLine() {
		return myLine;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.v(TAG, "onConnected");
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(5000);
		mLocationRequest.setSmallestDisplacement(5);
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private void sendlocation() {
		Intent intent = new Intent("location_changed");

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public boolean isStarted() {
		return mStarted;
	}

	public void setStarted(boolean started) {
		mStarted = started;
	}

	public int getDistance() {
		return mDistance;
	}

	public void setDistance(int distance) {
		mDistance = distance;
	}

}
