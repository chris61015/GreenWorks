package com.dartmouth.cs.greenworks.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TimeLineModel implements Parcelable {

    private String mMessage;
    private String mDate;
    public TimeLineModel() {
    }

    public TimeLineModel(String mMessage, String mDate) {
        this.mMessage = mMessage;
        this.mDate = mDate;
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
    }

    protected TimeLineModel(Parcel in) {
        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
    }

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}

