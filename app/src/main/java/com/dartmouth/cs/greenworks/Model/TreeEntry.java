package com.dartmouth.cs.greenworks.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xiaolei on 3/3/17.
 */

public class TreeEntry implements Parcelable {
    // Properties.
    public long treeId;
    public long dateTime; // millisec since 1970.
    public LatLng location;
    public String name; // person who planted the tree. Entered by user
    public String city; // where is the tree.  Entered by user.
    public String regId; // unique Id to identify use. Hack for login.
    public String photo;
    public String comment;
    // only for sorting after retrieved from datastore.

    // Constructor.
    public TreeEntry(long _treeId, long _dateTime, LatLng _location,
                     String _name, String _city, String _regId,
                     String _photo, String _comment) {
        treeId = _treeId;
        dateTime = _dateTime;
        location = _location;
        name = _name;
        city = _city;
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
        dest.writeLong(this.treeId);
        dest.writeLong(this.dateTime);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.name);
        dest.writeString(this.city);
        dest.writeString(this.regId);
        dest.writeString(this.photo);
        dest.writeString(this.comment);
    }

    protected TreeEntry(Parcel in) {
        this.treeId = in.readLong();
        this.dateTime = in.readLong();
        this.location = in.readParcelable(LatLng.class.getClassLoader());
        this.name = in.readString();
        this.city = in.readString();
        this.regId = in.readString();
        this.photo = in.readString();
        this.comment = in.readString();
    }

    public static final Parcelable.Creator<TreeEntry> CREATOR = new Parcelable.Creator<TreeEntry>() {
        @Override
        public TreeEntry createFromParcel(Parcel source) {
            return new TreeEntry(source);
        }

        @Override
        public TreeEntry[] newArray(int size) {
            return new TreeEntry[size];
        }
    };
}
