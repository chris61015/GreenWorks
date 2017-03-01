package com.dartmouth.cs.greenworks.backend.datastores;

/**
 * Created by Xiaolei on 2/26/17.
 */

public class PhotoEntry {
    private long photoId;
    private byte[] photo;  // use blob to save photo
                           // client needs to to photo conversions.

    // constructor.
    public PhotoEntry(long _photoId, byte[] _photo) {
        photoId = _photoId;
        photo = _photo;
    }
}
