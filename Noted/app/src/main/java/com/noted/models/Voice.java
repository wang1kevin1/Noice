package com.noted.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Voice {
    private String PUSHKEY;
    private String TITLE;
    private String TIMESTAMP;
    private String URL;

    public Voice() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Voice(String pushkey, String title, String url,
                String timestamp) {
        this.PUSHKEY = pushkey;
        this.TITLE = title;
        this.TIMESTAMP = timestamp;
        this.URL = url;
    }

    public String getPUSHKEY() {
        return PUSHKEY;
    }
    public void setPUSHKEY(String pushkey) {this.PUSHKEY = pushkey; }

    public String getTITLE() {
        return TITLE;
    }
    public void setTITLE(String title) {this.TITLE = title; }


    public String getURL() {
        return URL;
    }
    public void setURL(String url) {this.URL = url; }


    public String getTIMESTAMP() { return TIMESTAMP; }
    public void setTIMESTAMP(String timestamp) {this.TIMESTAMP = timestamp; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pushkey", PUSHKEY);
        result.put("title", TITLE);
        result.put("timestamp", TIMESTAMP);
        result.put("url", URL);

        return result;
    }
}

