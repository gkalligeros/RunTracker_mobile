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

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends ActionBarActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private String TAG = "Trackerr";
	private GoogleMap mMap;
	private LocationClient mLocationClient;
	boolean is_tracking = false;
	Location prev = null;
	int mDistance = 0;// distance covered
	private TextView mDistanceText;
	private TextView mStatus;
    private Chronometer mTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		mDistanceText = (TextView) findViewById(R.id.dst);
		mStatus = (TextView) findViewById(R.id.status);
		mTimer = (Chronometer) findViewById(R.id.chronometer1);
		setUpMapIfNeeded();
		mLocationClient = new LocationClient(this, this, this);
		mMap.setMyLocationEnabled(true);
		if (is_tracking) {
			Log.v(TAG, "is_tracking");

		} else {
			Log.v(TAG, "is_not_tracking");
		}
		Log.v(TAG, "onCreate");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Log.v(TAG, "onCreateOptionsMenu");

		return true;
	}

	public void onStart() {
		Log.v(TAG, "onStart");

		super.onStart();
		if (!mLocationClient.isConnected()) {
			mLocationClient.connect();
		}

	}

	public void onDestroy() {
		Log.v(TAG, "onDestroy");

		super.onDestroy();
		mLocationClient.disconnect();

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
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.

		}

	}

	// Button Listeners ---------------------------------------------------

	// when Start is Clicked
	public void onStartB(View view) {
		Log.v(TAG, "onStartB");
		is_tracking = true; // Start tracking TODO add timer and start timer
		mStatus.setText(R.string.Running);

	}

	// when Stop is Clicked

	public void onStopB(View view) {
		// TODO save run (confirm first)
		onLocationChanged(mLocationClient.getLastLocation());
		mStatus.setText(R.string.Stoped);
		Log.v(TAG, "onStopB");

	}

	// When Pause is Clicked
	public void onPauseB(View view) {
		Log.v(TAG, "onPauseB");
		is_tracking = false; // Stop tracking TODO add timer and stop timer
		mStatus.setText(R.string.Paused);

	}

	// Button Listeners ---------------------------------------------------

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onConnectionFailed");

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onConnected");
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(5000);
		mLocationRequest.setSmallestDisplacement(5);
		mLocationClient.requestLocationUpdates(mLocationRequest, this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDisconnected");

	}

	// Location Listeners Methods --------------------------------------
	@Override
	public void onLocationChanged(Location arg0) {
		Log.v(TAG, "onLocationChanged");
		Toast.makeText(this, "onLocationChanged", Toast.LENGTH_SHORT).show();
		// TODO (maybe i will save all locations to a list )

		if (prev != null) {

			if (is_tracking) {
				mMap.addPolyline(new PolylineOptions()
						.add(new LatLng(prev.getLatitude(), prev.getLongitude()),
								new LatLng(arg0.getLatitude(), arg0
										.getLongitude())).width(2)
						.color(Color.RED));
				prev = arg0;
				mDistance += 5;
				mDistanceText.setText(mDistance + " m");
			}

		} else {
			if (is_tracking) {
				Toast.makeText(this, "first_waypoint", Toast.LENGTH_SHORT)
						.show();
				Log.v(TAG, "first_waypoint");
				prev = arg0;// we are at the first point of run
			}
		}

	}

	// Location Listener Methods --------------------------------------
}
