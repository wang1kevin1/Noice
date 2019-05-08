package com.noted.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {

    private String EMAIL;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.EMAIL = email;
    }

    public String getEMAIL() { return EMAIL; }
    public void setEMAIL(String email) {this.EMAIL = email; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", EMAIL);

        return result;
    }
}
