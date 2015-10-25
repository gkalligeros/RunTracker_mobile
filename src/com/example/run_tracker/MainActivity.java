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

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.run_tracker.TrackingService.LocalBinder;

/*Main activity starts a tracking service that is running constantly and can be accessed from fragments*/
public class MainActivity extends FragmentActivity implements
	android.app.ActionBar.TabListener
{

    Intent serviceintent;
    private String TAG = "MTrackerr";
    private String Username;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private android.app.ActionBar actionBar;
    private String[] tabs =
    { "run", "myRuns", "profile" };
    private String token;
    TrackingService myService;
    boolean isBound = false;

    public String getToken()
    {
	return token;
    }

    public void setToken(String token)
    {
	this.token = token;
    }

    public String getUsername()
    {
	return Username;
    }

    public void setUsername(String username)
    {
	Username = username;
    }

    public TrackingService getMyService()
    {
	return myService;
    }

    public void setMyService(TrackingService myService)
    {
	this.myService = myService;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	Bundle extras = getIntent().getExtras();
	//get the username for display and token for authorized requests 
	if (extras != null)
	{
	    setToken(extras.getString("token"));
	    Username = extras.getString("Username");
	    Log.v(TAG, Username);
	}
	viewPager = (ViewPager) findViewById(R.id.pager);
	actionBar = getActionBar();
	mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

	viewPager.setAdapter(mAdapter);
	actionBar.setHomeButtonEnabled(false);
	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	actionBar.setTitle("Welcome " + Username);
	for (String tab_name : tabs)
	{
	    actionBar.addTab(actionBar.newTab().setText(tab_name)
		    .setTabListener(this));

	}
	viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
	{

	    @Override
	    public void onPageSelected(int position)
	    {
		// on changing the page
		// make respected tab selected
		actionBar.setSelectedNavigationItem(position);
	    }
	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2)
	    {
	    }
	    @Override
	    public void onPageScrollStateChanged(int arg0)
	    {
	    }
	});
    }

    public void onPause()
    {
	super.onPause();
	Log.v(TAG, "onPause");

    }

    protected void onResume()
    {
	super.onResume();
	// bind (and start the service)
	Intent intent = new Intent(this, TrackingService.class);
	bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	Log.v(TAG, "onCreateOptionsMenu");
	return true;
    }

    public void onStart()
    {
	Log.v(TAG, "onStart");
	super.onStart();
	

    }

    public void onStop()
    {

	super.onStop();
	Log.v(TAG, "onStop");

    }

    public void onDestroy()
    {

	super.onDestroy();
	Log.v(TAG, "onDestroy");
	
	//unbind and destroy service 
	unbindService(myConnection);

	

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

    private ServiceConnection myConnection = new ServiceConnection()
    {
	// on connection return the service to activity so we can access it
	// directly
	public void onServiceConnected(ComponentName className, IBinder service)
	{
	    LocalBinder binder = (LocalBinder) service;
	    myService = binder.getService();
	    isBound = true;
	}
	public void onServiceDisconnected(ComponentName arg0)
	{
	    isBound = false;
	}
    };

    @Override
    public void onTabReselected(android.app.ActionBar.Tab arg0,
	    android.app.FragmentTransaction arg1)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab arg0,
	    android.app.FragmentTransaction arg1)
    {
	// TODO Auto-generated method stub
	viewPager.setCurrentItem(arg0.getPosition());

    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab arg0,
	    android.app.FragmentTransaction arg1)
    {
	// TODO Auto-generated method stub

    }

}
