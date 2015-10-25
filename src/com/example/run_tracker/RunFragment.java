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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.run_tracker.CustomMapFragment.OnMapReadyListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

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
    AlertDialog mConfirm_stop;
    private Timer mTimer = new Timer();;
    private CustomMapFragment mMapFragment;
    PolylineOptions options=new PolylineOptions(); ;
    private String mToken;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
	@Override
	// When we receive e location change from TrackingService
	public void onReceive(Context context, Intent intent)
	{
	    Log.v(TAG, "onReceive");
	    mDistanceText.setText(((MainActivity) getActivity()).getMyService()
		    .getDistance() + " m");
	    // update distance and map line
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
    private ProgressDialog pDialog;

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
	mToken = ((MainActivity) getActivity()).getToken();
	mDistanceText = (TextView) rootView.findViewById(R.id.dst);
	mStatus = (TextView) rootView.findViewById(R.id.status);
	mStatus.setTextColor(Color.RED);
	mStop = (Button) rootView.findViewById(R.id.stop);
	Chronometer mCronometer = (Chronometer) rootView
		.findViewById(R.id.chronometer1);
	mTimer.setTimer(mCronometer);
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
	// stop listening to TrackingService for location changes
	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
		mMessageReceiver);
	Log.v(TAG, "onPause");

    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
	super.onSaveInstanceState(state);
	Log.v(TAG, "onSaveInstanceState");
    }

    public void onResume()
    {
	// when the user returns to RunFragment we must bring everything back
	// from TrackingService
	super.onResume();
	// redraw the line by getting the list of waypoints from service
	if (((MainActivity) getActivity()).getMyService() != null)

	{
	    if (((MainActivity) getActivity()).getMyService().isStarted())
	    {
		mStart.setEnabled(false);
		// get the list of point from service and redraw the line
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
		mStatus.setText(R.string.Running);
		mStatus.setTextColor(Color.GREEN);
	    }

	}
	// start listening to location changes
	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
		mMessageReceiver, new IntentFilter("location_changed"));
    }

    public void onStart()
    {
	super.onStart();

    }

    public void onDestroy()
    {
		super.onDestroy();
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
    // button click listeners
    public void onClick(View v)
    {
	switch (v.getId())
	{
	case R.id.start:
	{
	    ((MainActivity) getActivity()).getMyService().setStarted(true);
	    mStatus.setText(R.string.Running);
	    mTimer.startTimer();
	    mStart.setEnabled(false);
	    mStop.setEnabled(true);
	    mStatus.setTextColor(Color.GREEN);
	    break;
	}
	case R.id.stop:

	    // mConfirm_stop.show();
	    show_confirm();
	    break;
	}

    }

    @Override
    // when map fragment is ready take the map object from it
    public void onMapReady()
    {
	mMap = mMapFragment.getMap();
	if (mMap != null)
	{
	    mMap.setMyLocationEnabled(true);
	}

    }

    // clear everything after stop is pressed
    private void reset_fragment()
    {
	((MainActivity) getActivity()).getMyService().reset_all();
	mMapFragment.getMap().clear();
	mDistanceText.setText("0 m");
	mStatus.setText(R.string.Stoped);
	mStatus.setTextColor(Color.RED);
	mTimer.stopTimer();
	mStart.setEnabled(true);
	mStop.setEnabled(false);
	options =new PolylineOptions();
    }

    private void save_track()
    {
	pDialog = new ProgressDialog(getActivity());
	pDialog.setMessage("Saving track ");
	showProgressDialog();
	String line = PolyUtil.encode(((MainActivity) getActivity())
		.getMyService().getCoursePoints());

	Calendar c = Calendar.getInstance();
	String time = (String) mTimer.getTimer().getText();
	String date = c.get(Calendar.DATE) + "-" + (1 + c.get(Calendar.MONTH))
		+ "-" + c.get(Calendar.YEAR);
	String distance = (String) mDistanceText.getText();
	

	JSONObject new_run = new JSONObject();
	try
	{
	    new_run.put("track", line);
	    new_run.put("date", date);
	    new_run.put("distance", distance);
	    new_run.put("time", time);
	} catch (JSONException e)
	{
	    e.printStackTrace();
	}

	JsonObjectRequest save_req = new JsonObjectRequest(Method.POST,
		URLS.URL_TRACKS, new_run, new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {

			hideProgressDialog();
			reset_fragment();
			try
			{
			    if (response.get("error") != null)
			    {
				Toast.makeText(getActivity(),
					(CharSequence) response.get("error"),
					Toast.LENGTH_LONG).show();
			    }
			} catch (JSONException e)
			{

			    Toast.makeText(getActivity(), "Track saved",
				    Toast.LENGTH_LONG).show();
			}
		    }
		}, new Response.ErrorListener()
		{

		    @Override
		    public void onErrorResponse(VolleyError arg0)
		    {
			hideProgressDialog();
			Toast.makeText(getActivity(), "Error",
				Toast.LENGTH_LONG).show();
		    }

		})
	{

	    /**
	     * Passing some request headers token and content type
	     * */
	    @Override
	    public Map<String, String> getHeaders() throws AuthFailureError
	    {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-access-token", mToken);
		return headers;
	    }

	};
	AppController.getInstance().addToRequestQueue(save_req, TAG);
    }

    private void showProgressDialog()
    {
	if (!pDialog.isShowing())
	    pDialog.show();
    }

    private void hideProgressDialog()
    {
	if (pDialog.isShowing())
	    pDialog.hide();
    }

    private void show_confirm()
    {
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
	{
	    @Override
	    public void onClick(DialogInterface dialog, int which)
	    {
		switch (which)
		{
		case DialogInterface.BUTTON_POSITIVE:

		    save_track();
		    // Yes button clicked save and clear
		    break;

		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked do nothing
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	builder.setMessage("Are you sure?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
    }
}
