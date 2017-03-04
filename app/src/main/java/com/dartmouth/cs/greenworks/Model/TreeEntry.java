package com.dartmouth.cs.greenworks.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xiaolei on 3/3/17.
 */

public class TreeEntry {
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
}
