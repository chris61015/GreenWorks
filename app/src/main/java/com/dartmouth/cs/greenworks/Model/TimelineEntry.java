package com.dartmouth.cs.greenworks.Model;

/**
 * Created by Xiaolei on 3/3/17.
 */

public class TimelineEntry {
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
}
