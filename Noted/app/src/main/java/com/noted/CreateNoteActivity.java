package com.noted;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateNoteActivity extends AppCompatActivity {

    // EditTexts
    private EditText nEditTitle;
    private EditText nEditContent;

    // Fab
    private FloatingActionButton nFabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        nEditTitle = findViewById(R.id.noteEditTitle);
        nEditContent = findViewById(R.id.noteEditContent);

        nFabSave = findViewById(R.id.noteFabSave);
        nFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Save to database", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
}
