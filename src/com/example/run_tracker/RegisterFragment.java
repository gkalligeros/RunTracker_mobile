package com.example.run_tracker;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

public class RegisterFragment extends Fragment implements OnClickListener
{
    EditText mUsername, mPassword1, mPassword2, mName, mSurname;
    Button mRegister;
    private Dialog pDialog;
    private String TAG = "Register";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState)
    {

	View rootView = inflater.inflate(R.layout.registerfragment, container,
		false);
	mRegister = (Button) rootView.findViewById(R.id.register);
	mUsername = (EditText) rootView.findViewById(R.id.username);
	mPassword1 = (EditText) rootView.findViewById(R.id.password1);
	mPassword2 = (EditText) rootView.findViewById(R.id.password2);
	mName = (EditText) rootView.findViewById(R.id.name);
	mSurname = (EditText) rootView.findViewById(R.id.surname);
	mRegister.setOnClickListener(this);
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
	if (!mPassword1.getText().toString()
		.equals(mPassword2.getText().toString()))
	{

	    Toast.makeText(getActivity(), "Passwords dont match",
		    Toast.LENGTH_LONG).show();
	} else if (isEmpty(mPassword1) || isEmpty(mPassword2)
		|| isEmpty(mUsername) || isEmpty(mName) || isEmpty(mSurname))
	{

	    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_LONG)
		    .show();
	} else
	{
	    // post request
	    Make_register_request();

	}

    }

    private boolean isEmpty(EditText etText)
    {
	return etText.getText().toString().trim().length() == 0;
    }

    private void Make_register_request()
    {
	pDialog = new ProgressDialog(getActivity());

	showProgressDialog();
	JSONObject credentials = null;
	try
	{
	    credentials = new JSONObject();
	    credentials.put("username", mUsername.getText().toString());
	    credentials.put("password", mPassword1.getText().toString());
	    credentials.put("name", mName.getText().toString());
	    credentials.put("surname", mSurname.getText().toString());

	} catch (JSONException e)
	{
	    e.printStackTrace();
	}

	JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST,
		URLS.URL_PROFILE, credentials,
		new Response.Listener<JSONObject>()
		{

		    @Override
		    public void onResponse(JSONObject response)
		    {
			hideProgressDialog();
			try
			{
			    response.getString("ok");
			    Toast.makeText(getActivity(),
				    "Registration completed please login",
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
