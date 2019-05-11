package com.noted.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.noted.interfaces.Memo;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Note implements Memo {
    private String UID;
    private String TITLE;
    private String CONTENT;
    private String TIMESTAMP;

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Note(String uid, String title, String content,
                String timestamp) {
        this.UID = uid;
        this.TITLE = title;
        this.CONTENT = content;
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

    public String getCONTENT() {
        return CONTENT;
    }
    public void setCONTENT(String content) {this.CONTENT = content; }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }
    public void setTIMESTAMP(String timestamp) {this.TIMESTAMP = timestamp; }

    @Override
    public int getType() {
        return Memo.TYPE_NOTE;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", UID);
        result.put("title", TITLE);
        result.put("content", CONTENT);
        result.put("timestamp", TIMESTAMP);

        return result;
    }
}
