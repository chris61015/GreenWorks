package com.dartmouth.cs.greenworks.backend.datastores;


/**
 * Created by Xiaolei on 2/26/17.
 */

public class TimelineEntry {


    // kind info
    public static final String PARENT_KIND = "timeline parent kind";
    public static final String PARENT_IDENTIFIER = "timeline parent identifier";
    public static final String ENTRY_ENTITY_KIND = "timeline entry";

    // entity property keys
    public static final String PROPERTY_TIMELINE_ID ="Timeline ID";
    public static final String PROPERTY_TREE_ID = "Tree ID";
    public static final String PROPERTY_DATETIME = "Date Time";
    public static final String PROPERTY_NAME = "Name";
    public static final String PROPERTY_REG_ID = "Registration ID";
    public static final String PROPERTY_PHOTO = "Photo";
    public static final String PROPERTY_COMMENT = "Comment";


    // properties.
    public long timelineId;
    public long treeId;  // foreign key.
    public long dateTime;
    public String name;  // person who updated the tree
    public String regId; // identify user.
    public String photo; // base64
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
}

