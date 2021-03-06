package com.noted.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Note {
    private String TITLE;
    private String CONTENT;
    private String TIMESTAMP;
    private String PUSHKEY;

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Note(String title, String content,
                String timestamp, String pushkey) {
        this.TITLE = title;
        this.CONTENT = content;
        this.TIMESTAMP = timestamp;
        this.PUSHKEY = pushkey;
    }

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

    public String getPUSHKEY() {
        return PUSHKEY;
    }
    public void setPUSHKEY(String pushkey) {this.PUSHKEY = pushkey; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", TITLE);
        result.put("content", CONTENT);
        result.put("timestamp", TIMESTAMP);
        result.put("pushkey", PUSHKEY);

        return result;
    }
}
