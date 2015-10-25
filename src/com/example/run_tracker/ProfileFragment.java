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
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

public class ProfileFragment extends Fragment implements OnClickListener
{
    private String TAG = "profile";
    private ProgressDialog pDialog;
    private Button mSubmit;
    private EditText mUsername;
    private EditText mPassword1;
    private EditText mPassword2;
    private EditText mName;
    private EditText mSurname;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState)
    {

	View rootView = inflater.inflate(R.layout.profilefragment, container,
		false);

	mSubmit = (Button) rootView.findViewById(R.id.submit);
	mUsername = (EditText) rootView.findViewById(R.id.username);
	mPassword1 = (EditText) rootView.findViewById(R.id.newpassword1);
	mPassword2 = (EditText) rootView.findViewById(R.id.newpassword2);
	mName = (EditText) rootView.findViewById(R.id.name);
	mSurname = (EditText) rootView.findViewById(R.id.surname);
	mSubmit.setOnClickListener(this);
	mUsername.setEnabled(false);
	Make_get_profile_request();

	return rootView;

    }

    private void Make_get_profile_request()
    {

	JSONObject credentials = null;
	try
	{
	    credentials = new JSONObject();
	    credentials.put("token", ((MainActivity) getActivity()).getToken());

	} catch (JSONException e)
	{
	    e.printStackTrace();
	}

	JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
		URLS.URL_PROFILE, credentials,
		new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {
			try
			{
			    mUsername.setText(response.getString("username"));
			    mName.setText(response.getString("name"));
			    mSurname.setText(response.getString("surname"));

			} catch (JSONException e)
			{
			   

			    try
			    {
				Toast.makeText(getActivity(),
					response.getString("error"),
					Toast.LENGTH_LONG).show();
			    } catch (JSONException e1)
			    {
				e1.printStackTrace();
			    }
			    e.printStackTrace();
			}

			Log.v(TAG, response.toString());
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
	    /**
	     * Passing some request headers token and content type
	     * */
	    @Override
	    public Map<String, String> getHeaders() throws AuthFailureError
	    {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-access-token",
			((MainActivity) getActivity()).getToken());
		return headers;
	    }
	};

	// Adding request to request queue
	AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);

	// Cancelling request
	// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    // edit profile request
    private void Make_edit_profile_request(String old_password,
	    String new_password)
    {

	JSONObject credentials = null;
	try
	{
	    credentials = new JSONObject();
	    credentials.put("password", old_password);
	    credentials.put("username", mUsername.getText());
	    credentials.put("password_r", new_password);
	    credentials.put("name", mName.getText());
	    credentials.put("surname", mSurname.getText());

	} catch (JSONException e)
	{
	    e.printStackTrace();
	}

	JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.PUT,
		URLS.URL_PROFILE, credentials,
		new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {
			try
			{
			    Toast.makeText(getActivity(),
				    response.getString("success"),
				    Toast.LENGTH_LONG).show();
			} catch (JSONException e)
			{
			    try
			    {
				Toast.makeText(getActivity(),
				    response.getString("error"),
					Toast.LENGTH_LONG).show();
			    } catch (JSONException e1)
			    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    }
			    e.printStackTrace();
			}
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
	    /**
	     * Passing some request headers token and content type
	     * */
	    @Override
	    public Map<String, String> getHeaders() throws AuthFailureError
	    {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-access-token",
			((MainActivity) getActivity()).getToken());
		return headers;
	    }
	};

	// Adding request to request queue
	AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);

	// Cancelling request
	// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
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
    public void onClick(View arg0)
    {
	Log.v(TAG, "click");
	AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

	alert.setTitle("Enter Password");
	alert.setMessage("Please enter your password");

	// Set an EditText view to get user input
	final EditText input = new EditText(getActivity());
	input.setInputType(InputType.TYPE_CLASS_TEXT
		| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	alert.setView(input);

	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
	{
	    public void onClick(DialogInterface dialog, int whichButton)
	    {
		String password = input.getText().toString();
		String new_password = null;
		if (!mPassword1.getText().toString()
			.equals(mPassword2.getText().toString()))
		{

		    Toast.makeText(getActivity(), "Passwords dont match",
			    Toast.LENGTH_LONG).show();
		} else
		{
		    if (isEmpty(mPassword1) || (isEmpty(mPassword2)))
		    {
			new_password = password;
		    } else
		    {
			new_password = mPassword1.getText().toString();
		    }
		    Make_edit_profile_request(password, new_password);
		}

	    }
	});

	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
	{
	    public void onClick(DialogInterface dialog, int whichButton)
	    {
		// Canceled.
	    }
	});

	alert.show();

    }

    private boolean isEmpty(EditText etText)
    {
	return etText.getText().toString().trim().length() == 0;
    }
}