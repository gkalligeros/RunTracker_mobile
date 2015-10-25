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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

public class CustomListAdapter extends BaseAdapter implements OnClickListener
{
    private static final String TAG = "myruns";
    private Activity activity;
    private LayoutInflater inflater;
    private List<Track> TrackItems;
    private String mToken;
    private String hack_id;
    private int hack_position;

    public CustomListAdapter(Activity activity, List<Track> TrackItems,
	    String Token)
    {
	this.mToken = Token;
	this.activity = activity;
	this.TrackItems = TrackItems;
    }

    @Override
    public int getCount()
    {
	return TrackItems.size();
    }

    @Override
    public Object getItem(int location)
    {
	return TrackItems.get(location);
    }

    @Override
    public long getItemId(int position)
    {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

	if (inflater == null)
	{
	    inflater = (LayoutInflater) activity
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	if (convertView == null)
	{
	    convertView = inflater.inflate(R.layout.list_row, null);
	}

	TextView date = (TextView) convertView.findViewById(R.id.date);
	TextView time = (TextView) convertView.findViewById(R.id.time);
	TextView distance = (TextView) convertView.findViewById(R.id.distance);
	ImageButton delete = (ImageButton) convertView
		.findViewById(R.id.delete_btn);
	delete.setOnClickListener(this);
	delete.setTag(position);// set the tag for recognizing each row
	// getting track data for the row
	Track m = TrackItems.get(position);
	date.setText("Date: " + m.getDate());
	time.setText("Time: " + m.getTime() + "min");
	distance.setText("Distance: " + m.getDistance());
	return convertView;
    }

    @Override
    public void onClick(View arg0)
    {
	String track_id = TrackItems.get((int) arg0.getTag()).getId();
	int track_position = (int) arg0.getTag();
	Log.v(TAG, "" + track_position);

	// shity hacks
	hack_position = track_position;
	hack_id = track_id;
	// ------------
	show_confirm();

    }

    private void Make_delete_request(String id)
    {

	JSONObject packet = new JSONObject();
	try
	{
	    packet.put("id", id);

	} catch (JSONException e)
	{
	    e.printStackTrace();
	}
	Log.v(TAG, "" + packet);
	JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.DELETE,
		URLS.URL_TRACKS, packet, new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {
			Log.v(TAG, "responce: " + response);
			TrackItems.remove(hack_position);

			notifyDataSetChanged();

		    }
		}, new Response.ErrorListener()
		{

		    @Override
		    public void onErrorResponse(VolleyError error)
		    {
			VolleyLog.d(TAG, "Error: " + error.getMessage());
		    }
		})
	{
	    @Override
	    public Map<String, String> getHeaders() throws AuthFailureError
	    {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-access-token", mToken);
		headers.put("id", hack_id);
		return headers;
	    }

	};

	// Adding request to request queue
	AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);

	// Cancelling request
	// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
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
		    // Yes button clicked save and clear
		    Make_delete_request(hack_id);

		    break;
		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked do nothing
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	builder.setMessage("Are you sure?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
    }

}