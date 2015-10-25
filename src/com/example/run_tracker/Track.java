package com.example.run_tracker;

import android.os.Parcel;
import android.os.Parcelable;

//model class for a single run 
public class Track implements Parcelable
{
    private String mTime, mTrack, mId, mDate;
    private String mDistance;

    public Track(String date, String time, String track, String id, String distance)
    {
	super();
	mDate = date;
	mTime = time;
	mTrack = track;
	mId = id;
	mDistance = distance;
    }

    public Track()
    {
    }

    public String getDate()
    {
	return mDate;
    }

    public void setDate(String date)
    {
	mDate = date;
    }

    public String getTime()
    {
	return mTime;
    }

    public void setTime(String time)
    {
	mTime = time;
    }

    public String getTrack()
    {
	return mTrack;
    }

    public void setTrack(String track)
    {
	mTrack = track;
    }

    public String getId()
    {
	return mId;
    }

    public void setId(String id)
    {
	mId = id;
    }

    public String getDistance()
    {
	return mDistance;
    }

    public void setDistance(String distance)
    {
	mDistance = distance;
    }

    @Override
    public int describeContents()
    {
	return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1)
    {
	arg0.writeString(mDate);
	arg0.writeString(mTime);
	arg0.writeString(mTrack);
	arg0.writeString(mId);
	arg0.writeString(mDistance);
    }

    private static Track readFromParcel(Parcel in)
    {
	String date = in.readString();
	String time = in.readString();
	String track = in.readString();
	String id = in.readString();
	String distance = in.readString();
	return new Track(date, time, track, id, distance);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
	public Track createFromParcel(Parcel in)
	{
	    return readFromParcel(in);
	}

	public Track[] newArray(int size)
	{ // (7)
	    return new Track[size];
	}
    };

}
