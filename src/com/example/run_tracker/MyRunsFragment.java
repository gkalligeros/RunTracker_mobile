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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.run_tracker.CustomMapFragment.OnMapReadyListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

public class MyRunsFragment extends Fragment implements OnMapReadyListener,
	OnItemClickListener,OnClickListener
{
    private CustomMapFragment mMapFragment;
    private List<Track> mTracks = new ArrayList<Track>();
    private CustomListAdapter adapter;
    private ListView listView;
    private String TAG = "myruns";
    private ProgressDialog pDialog;
    private GoogleMap mMap;
    private Button mRefresh;
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
	Log.v(TAG, "onActivityCreated");

	super.onActivityCreated(savedInstanceState);

	if (savedInstanceState != null)
	{
	    mTracks = savedInstanceState.getParcelableArrayList("tracklist");
	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
	Log.v(TAG, "onSaveInstanceState");

	super.onSaveInstanceState(outState);
	outState.putParcelableArrayList("tracklist",
		(ArrayList<? extends Parcelable>) mTracks);
	// Save the fragment's state here
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState)
    {

	Log.v(TAG, "onCreateView");

	View rootView = inflater.inflate(R.layout.myrunfragment, container,
		false);

	listView = (ListView) rootView.findViewById(R.id.list);
	mRefresh = (Button) rootView.findViewById(R.id.refresh);
	listView.setOnItemClickListener(this);
	mMapFragment = CustomMapFragment.newInstance();
	getChildFragmentManager().beginTransaction()
		.replace(R.id.map_container, mMapFragment).commit();
	mRefresh.setOnClickListener(this);
	return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	Log.v(TAG, "onCreate");

    }

    public void onDestroy()
    {
	super.onDestroy();
	Log.v(TAG, "onDestroy");
	AppController.getInstance().cancelPendingRequests(TAG);
    }

    public void onStart()
    {
	Log.v(TAG, "onStart");

	super.onStart();
    }

    public void onResume()
    {
	Log.v(TAG, "onResume");
	adapter = new CustomListAdapter(this.getActivity(), mTracks,
		((MainActivity) getActivity()).getToken());
	adapter.notifyDataSetChanged();
	listView.setAdapter(adapter);
	if (mTracks.isEmpty())
	{

	    Make_track_request();
	}

	super.onResume();
    }

    private void Make_track_request()
    {
	Log.v(TAG, "Make_track_request");

	pDialog = new ProgressDialog(getActivity());
	pDialog.setMessage("Getting your tracks");
	showProgressDialog();
	// Creating volley request obj
	JsonArrayRequest trackReq = new JsonArrayRequest(URLS.URL_TRACKS,
		new Response.Listener<JSONArray>()
		{
		    @Override
		    public void onResponse(JSONArray response)
		    {
			// Parsing json
			mTracks.clear();
			for (int i = 0; i < response.length(); i++)
			{
			    try
			    {
				JSONObject obj = response.getJSONObject(i);
				mTracks.add(new Track(obj.getString("date"),
					obj.getString("time"), obj
						.getString("track"), obj
						.getString("_id"), obj
						.getString("distance")));
				Log.v(TAG,""+mTracks.get(i).getDate());
			    } catch (JSONException e)
			    {
				e.printStackTrace();
			    }
			}

			// notifying list adapter about data changes
			// so that it renders the list view with updated data
			adapter.notifyDataSetChanged();
			hideProgressDialog();

		    }
		}, new Response.ErrorListener()
		{
		    @Override
		    public void onErrorResponse(VolleyError error)
		    {
			VolleyLog.d(TAG, "Error: " + error.getMessage());
			hideProgressDialog();
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
		headers.put("x-access-token",((MainActivity) getActivity()).getToken());
		return headers;
	    }
	};
	AppController.getInstance().addToRequestQueue(trackReq, TAG);

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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	    long arg3)
    {
	Log.v(TAG, "" + position);
	Log.v(TAG, "click");
	PolylineOptions options = new PolylineOptions();
	options.width(3);
	options.color(Color.GREEN);
	List<LatLng> track = PolyUtil.decode(mTracks.get(position).getTrack());
	options.addAll(track);
	LatLngBounds.Builder b = new LatLngBounds.Builder();
	for (LatLng m : track)
	{
	    b.include(m);
	}
	if (mMap != null)
	{
	    mMap.clear();
	    LatLngBounds bounds = b.build();
	    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100,
		    100, 1);
	    mMap.animateCamera(cu);
	    mMap.addPolyline(options);
	}
    }

    @Override
    public void onClick(View arg0)
    {
	Make_track_request();
    }
}
