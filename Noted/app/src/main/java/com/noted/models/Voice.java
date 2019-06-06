package com.noted.models;


import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Voice {
    private String KEY;
    private String TITLE;
    private String TIMESTAMP;
    private String URL;

    public Voice() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Voice(String key, String title, String url,
                String timestamp) {
        this.KEY = key;
        this.TITLE = title;
        this.TIMESTAMP = timestamp;
        this.URL = url;
    }

    public String getKEY() {
        return KEY;
    }
    public void setUID(String key) {this.KEY = key; }

    public String getTITLE() {
        return TITLE;
    }
    public void setTITLE(String title) {this.TITLE = title; }


    public String getURL( ) {
        return URL;
    }


    public String getTIMESTAMP() {
        return TIMESTAMP;
    }
    public void setTIMESTAMP(String timestamp) {this.TIMESTAMP = timestamp; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", KEY);
        result.put("title", TITLE);
        result.put("url", URL);
        result.put("timestamp", TIMESTAMP);

        return result;
    }
}

