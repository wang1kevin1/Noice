package com.noted.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.noted.interfaces.Memo;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Voice implements Memo {
    private String UID;
    private String TITLE;
    // private something MEDIA;
    private String TIMESTAMP;

    public Voice() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Voice(String uid, String title, String media,
                String timestamp) {
        this.UID = uid;
        this.TITLE = title;
        //this.MEDIA = media;
        this.TIMESTAMP = timestamp;
    }

    public String getUID() {
        return UID;
    }
    public void setUID(String uid) {this.UID = uid; }

    public String getTITLE() {
        return TITLE;
    }
    public void setTITLE(String title) {this.TITLE = title; }

    /*
    public ______ getMEDIA() {
        return MEDIA;
    }
    public void setMEDIA(______ media) {this.MEDIA = media; }
    */

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }
    public void setTIMESTAMP(String timestamp) {this.TIMESTAMP = timestamp; }

    @Override
    public int getType() {
        return Memo.TYPE_VOICE;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", UID);
        result.put("title", TITLE);
        //result.put("media", MEDIA);
        result.put("timestamp", TIMESTAMP);

        return result;
    }
}

