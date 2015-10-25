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

import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

public class Timer
{

    private Chronometer mTimer;
    private Boolean mFirstStart = true;
    private long mTimeWhenStopped;
    private String TAG = "Timer";
    private long base;

    public Chronometer getTimer()
    {
	return mTimer;
    }

    public void setTimer(Chronometer timer)
    {
	mTimer = timer;
    }

    public void startTimer()
    {
	if (mFirstStart)
	{
	    base = SystemClock.elapsedRealtime();
	    mTimer.setBase(base);
	    mTimer.start();
	    mFirstStart = false;
	    Log.v(TAG, "startTimer mFirstStart" + mTimer.getBase());

	} else
	{
	    Log.v(TAG, "base " + base);
	    mTimer.setBase(base);
	    mTimer.start();
	    Log.v(TAG, "startTimer not_mFirstStart " + mTimer.getBase());

	}

    }

    public Boolean getFirstStart()
    {
        return mFirstStart;
    }

    public void setFirstStart(Boolean firstStart)
    {
        mFirstStart = firstStart;
    }

    public void stopTimer()
    {
	mFirstStart=true;
	mTimer.stop();
	mTimer.setText("00:00");
    }

    public void resetTimer()
    {
	mTimer.stop();
	mFirstStart=true;
    }

    public long getTimeWhenStopped()
    {
	return mTimeWhenStopped;
    }

    public void setTimeWhenStopped(long timeWhenStopped)
    {
	mTimeWhenStopped = timeWhenStopped;
    }

    public long getBase()
    {
	return mTimer.getBase();
    }
}
