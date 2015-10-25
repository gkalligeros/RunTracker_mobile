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

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class IntroActivity extends Activity implements OnClickListener
{

    private boolean state = false;
    private TextView mBottomText;

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_intro);
	mBottomText = (TextView) findViewById(R.id.bottom_text);
	// check if orientation changed 
	if (savedInstanceState != null)
	{
	    state = savedInstanceState.getBoolean("STATE");
	} else
	{
	    state = false;
	}
	mBottomText.setOnClickListener(this);
	if (!state)
	{
	    LoginFragment login = new LoginFragment();
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    ft.replace(R.id.container, login); 
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.commit();
	    mBottomText.setText(R.string.RegisterPrompt);

	} else
	{
	    RegisterFragment register = new RegisterFragment();
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    ft.replace(R.id.container, register);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.commit();
	    mBottomText.setText(R.string.LoginPrompt);

	}
	mBottomText.setTextColor(Color.BLUE);

    }

    public void onSaveInstanceState(Bundle savedInstanceState)
    {
	//
	// Save the user's current fragment (login or register)
	savedInstanceState.putBoolean("STATE", state);

	// Always call the superclass so it can save the view hierarchy state
	super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.intro, menu);
	return true;
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

	//change between two fragments 
    @Override
    public void onClick(View v)
    {
	switch (v.getId())
	{
	case R.id.bottom_text:
	{
	   // Toast.makeText(this, "click on text", Toast.LENGTH_LONG).show();
		//check witch fragment is currently in use 
	    if (state)
	    {
		LoginFragment login = new LoginFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, login);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
		mBottomText.setText(R.string.RegisterPrompt);
		state = false;
	    } else
	    {
		RegisterFragment register = new RegisterFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, register);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
		mBottomText.setText(R.string.LoginPrompt);
		state = true;
	    }

	    break;
	}
	
	default:
	{
	    Toast.makeText(this, "click on something", Toast.LENGTH_LONG)
		    .show();
	}
	}

    }
}
