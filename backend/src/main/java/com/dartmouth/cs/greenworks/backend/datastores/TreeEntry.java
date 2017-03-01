package com.dartmouth.cs.greenworks.backend.datastores;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.GeoPt;



/**
 * Created by Xiaolei on 2/26/17.
 */

public class TreeEntry {

    // kind info
    public static final String PARENT_KIND = "tree parent kind";
    public static final String PARENT_IDENTIFIER = "tree parent identifier";
    public static final String ENTRY_ENTITY_KIND = "tree entry";

    // entity property keys
    public static final String PROPERTY_TREE_ID ="Tree ID";
    public static final String PROPERTY_DATETIME = "Date Time";
    public static final String PROPERTY_LOCATION = "Location";
    public static final String PROPERTY_NAME = "Name";
    public static final String PROPERTY_CITY = "City";
    public static final String PROPERTY_REG_ID = "Registration ID";
    public static final String PROPERTY_PHOTO = "Photo";
    public static final String PROPERTY_COMMENT = "Comment";

    // Properties.
    public long treeId;
    public long dateTime; // millisec since 1970.
    public GeoPt location;
    public String name; // person who planted the tree. Entered by user
    public String city; // where is the tree.  Entered by user.
    public String regId; // unique Id to identify use. Hack for login.
    public Blob photo;
    public String comment;

    // Constructor.
    public TreeEntry(long _treeId, long _dateTime, GeoPt _location,
                     String _name, String _city, String _regId,
                     Blob _photo, String _comment) {
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
