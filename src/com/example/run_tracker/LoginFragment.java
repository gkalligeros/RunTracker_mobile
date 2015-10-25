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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginFragment extends Fragment implements OnClickListener
{
    private String TAG = "Login";
    Button mLogin;
    EditText mUsername, mPassword;
    private ProgressDialog pDialog;
    private CheckBox mRememberme;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState)
    {

	View rootView = inflater.inflate(R.layout.loginfragment, container,	false);
	mLogin = (Button) rootView.findViewById(R.id.login);
	mUsername = (EditText) rootView.findViewById(R.id.username);
	mPassword = (EditText) rootView.findViewById(R.id.password);
	mRememberme = (CheckBox) rootView.findViewById(R.id.rememberme);
	mLogin.setOnClickListener(this);
	SharedPreferences credentials = getActivity().getPreferences(Context.MODE_PRIVATE);
	mUsername.setText(credentials.getString("username", ""));
	mPassword.setText(credentials.getString("password", ""));
	return rootView;

    }
    public void onDestroy()
    {
	super.onDestroy();
	Log.v(TAG, "onDestroy");
	AppController.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onClick(View v)
    {
	// TODO Auto-generated method stub
	if (isEmpty(mUsername) || isEmpty(mPassword))
	{
	    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_LONG)
		    .show();
	} else
	{
	    // request here
	    Make_login_request();

	}
    }

    private boolean isEmpty(EditText etText)
    {
	return etText.getText().toString().trim().length() == 0;
    }

    private void Make_login_request()
    {
	pDialog = new ProgressDialog(getActivity());
	pDialog.setMessage("Login in ");
	showProgressDialog();
	JSONObject credentials = null;
	try
	{
	    credentials = new JSONObject();
	    credentials.put("username", mUsername.getText().toString());
	    credentials.put("password",mPassword.getText().toString());

	} catch (JSONException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST,
		URLS.URL_LOGIN, credentials,
		new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {
			/*
			 * response.getString("token");
			 * 
			 * (and save the token for future requests) else we got
			 * an error
			 */
			Log.d(TAG, response.toString());
			try
			{
			    response.getString("token");
			    if (mRememberme.isChecked())
			    {
				Log.v(TAG, "saving token");
				// save username and password for future use ins
				// shared preferences
				SharedPreferences credentials = getActivity()
					.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = credentials
					.edit();
				editor.putString("username", mUsername
					.getText().toString());
				editor.putString("password", mPassword
					.getText().toString());
				editor.commit();
			    }
			    Launch_app(response.getString("token"));

			} catch (JSONException e)
			{
			    // TODO Auto-generated catch block
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
			}

			hideProgressDialog();
		    }
		}, new Response.ErrorListener()
		{

		    @Override
		    public void onErrorResponse(VolleyError error)
		    {
			VolleyLog.d(TAG, "Error: " + error.getMessage());
			Launch_app("delete me ");

			hideProgressDialog();
		    }
		})
	{

	};

	// Adding request to request queue
	AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);

	// Cancelling request
	// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    private void Launch_app(String token)
    {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		intent.putExtra("token", token);
		intent.putExtra("Username", mUsername.getText().toString());
		startActivity(intent);
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
}
