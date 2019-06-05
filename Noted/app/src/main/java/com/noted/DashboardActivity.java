package com.noted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noted.adapters.NoteRecyclerAdapter;
import com.noted.models.Note;
import com.noted.models.Voice;

import java.util.ArrayList;

import static com.noted.utils.AccountUtil.getUid;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // initialize database
    private DatabaseReference dDatabase;
    private String dUID;

    // RecyclerView
    private RecyclerView dNoteRecycler;
    private RecyclerView dVoiceRecycler;

    // Adapter + layout manager
    private NoteRecyclerAdapter dNoteAdapter;
    private LinearLayoutManager layoutManager;

    private ArrayList<Note> dNoteList;
    private ArrayList<Voice> dVoiceList;

    private TextView dTextEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // initialize the navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // initialize database stuff
        dUID = getUid();
        dDatabase = FirebaseDatabase.getInstance().getReference();
        dDatabase.keepSynced(true);

        // initialize views
        dTextEmpty = findViewById(R.id.dashboardTextEmpty);
        dNoteRecycler = findViewById(R.id.dashboardNoteRecyclerView);
        dVoiceRecycler = findViewById(R.id.dashboardVoiceRecyclerView);

        // default focus is notes
        setTitle("My Notes");
        displayNotes();
    }

    private void displayNotes() {
        dVoiceRecycler.setVisibility(View.GONE);

        dDatabase.child("notes").child(dUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dNoteList = new ArrayList<Note>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Note note = postSnapshot.getValue(Note.class);
                    dNoteList.add(note);

                    String postKey = postSnapshot.getKey();
                }
                // set empty note list text
                if (dNoteList.isEmpty()) {
                    dNoteRecycler.setVisibility(View.GONE);
                    dTextEmpty.setText("No notes to display.");
                    dTextEmpty.setVisibility(View.VISIBLE);
                } else {
                    dTextEmpty.setVisibility(View.GONE);
                    dNoteRecycler.setVisibility(View.VISIBLE);
                }

                // specify an adapter
                dNoteAdapter = new NoteRecyclerAdapter(dNoteList, getApplication());

                // use a linear layout manager
                layoutManager = new LinearLayoutManager(DashboardActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                dNoteRecycler.setLayoutManager(layoutManager);

                // set adapter
                dNoteRecycler.setAdapter(dNoteAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("read error", databaseError.getMessage());
            }
        });
    }

    private void displayVoice() {
        dNoteRecycler.setVisibility(View.GONE);

        dVoiceList = new ArrayList<Voice>();

        if (dVoiceList.isEmpty()) {
            dVoiceRecycler.setVisibility(View.GONE);
            dTextEmpty.setText("No recordings to display.");
            dTextEmpty.setVisibility(View.VISIBLE);
        } else {
            dTextEmpty.setVisibility(View.GONE);
            dVoiceRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_date) {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboardNote) {
            setTitle("My Notes");
            displayNotes();
        } else if (id == R.id.nav_dashboardAddNote) {
            Intent myIntent =
                    new Intent(DashboardActivity.this, CreateNoteActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.nav_dashboardVoice) {
            setTitle("My Recordings");
            displayVoice();
        } else if (id == R.id.nav_dashboardAddVoice) {
            Intent myIntent =
                    new Intent(DashboardActivity.this, CreateVoiceActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.nav_dashboardAccountSettings) {

        } else if (id == R.id.nav_dashboardLogout) {
            FirebaseAuth.getInstance().signOut();
            Intent redirectToLogin = new Intent(this, LoginActivity.class);
            startActivity(redirectToLogin);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
