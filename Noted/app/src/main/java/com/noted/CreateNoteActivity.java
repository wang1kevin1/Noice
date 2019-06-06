package com.noted;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.noted.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.noted.utils.AccountUtil.getUid;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String REQUIRED = "Required";

    // Database
    private DatabaseReference nDatabase;
    private String nUID;

    // EditTexts
    private EditText nEditTitle;
    private EditText nEditContent;

    // Strings
    private String nTitle;
    private String nContent;
    private String nTimestamp;

    // Lists
    private List<String> readAccess;
    private List<String> writeAccess;

    // Fab
    private FloatingActionButton nFabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // initialize database
        nDatabase = FirebaseDatabase.getInstance().getReference();

        // get userID
        nUID = getUid();
        //nUID = AccountUtil.getCurrentUser().getUid();

        // initialize note form
        nEditTitle = findViewById(R.id.noteEditTitle);
        nEditContent = findViewById(R.id.noteEditContent);

        // initialize save button
        nFabSave = findViewById(R.id.noteFabSave);
        nFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) { // check if network available

                    newNote(); // creates a newNote and saves it

                    finish(); // close activity

                } else { // network unavailable
                    Snackbar.make(view, "Network Unavailable", Snackbar.LENGTH_LONG)
                            .setAction("Wi-Fi Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // opens wifi settings
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            }).show();
                }
            }
        });
    }

    // creates a new Note
    private void newNote() {
        // get text from form
        nTitle = nEditTitle.getText().toString().trim();
        nContent = nEditContent.getText().toString().trim();

        // both fields are required
        if (TextUtils.isEmpty(nTitle)) {
            nTitle = "Untitled Note";
            return;
        }
        if (TextUtils.isEmpty(nContent)) {
            nEditContent.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        // get timestamp at latest moment
        nTimestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        // get push key
        String key = nDatabase.child("notes").child(nUID).push().getKey();

        // initialize new note
        Note note = new Note(nTitle, nContent, nTimestamp, key);

        saveNote(note, key);

        // Re-enable buttons
        setEditingEnabled(true);

        return;
    }

    // Saves a note to the database
    private void saveNote(Note note, String key) {
        // note fields are valid
        Toast.makeText(CreateNoteActivity.this, "Saving...", Toast.LENGTH_LONG).show();

        // map note
        Map<String, Object> postValues = note.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        // set db structure
        childUpdates.put("/notes/" + nUID + "/" + key, postValues);

        // update db
        nDatabase.updateChildren(childUpdates);

        // note saved
        Toast.makeText(CreateNoteActivity.this, "Saved", Toast.LENGTH_LONG).show();
    }

    // checks if connected to network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // disables editing to prevent multiple posts on button spam
    private void setEditingEnabled(boolean enabled) {
        nEditTitle.setEnabled(enabled);
        nEditContent.setEnabled(enabled);
        nFabSave.setEnabled(enabled);
    }
}
