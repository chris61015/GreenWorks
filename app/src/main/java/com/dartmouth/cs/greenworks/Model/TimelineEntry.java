package com.dartmouth.cs.greenworks.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Xiaolei on 3/3/17.
 */

public class TimelineEntry implements Parcelable {
    // properties.
    public long timelineId;
    public long treeId;  // foreign key.
    public long dateTime;
    public String name;  // person who updated the tree
    public String regId; // identify user.
    public String photo;
    public String comment;

    // constructor
    public TimelineEntry(long _timelineId, long _treeId, long _dateTime, String _name,
                         String _regId, String _photo, String _comment) {
        timelineId = _timelineId;
        treeId = _treeId;
        dateTime = _dateTime;
        name = _name;
        regId = _regId;
        photo = _photo;
        comment = _comment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timelineId);
        dest.writeLong(this.treeId);
        dest.writeLong(this.dateTime);
        dest.writeString(this.name);
        dest.writeString(this.regId);
        dest.writeString(this.photo);
        dest.writeString(this.comment);
    }

    protected TimelineEntry(Parcel in) {
        this.timelineId = in.readLong();
        this.treeId = in.readLong();
        this.dateTime = in.readLong();
        this.name = in.readString();
        this.regId = in.readString();
        this.photo = in.readString();
        this.comment = in.readString();
    }

    public static final Parcelable.Creator<TimelineEntry> CREATOR = new Parcelable.Creator<TimelineEntry>() {
        @Override
        public TimelineEntry createFromParcel(Parcel source) {
            return new TimelineEntry(source);
        }

        @Override
        public TimelineEntry[] newArray(int size) {
            return new TimelineEntry[size];
        }
    };
}
