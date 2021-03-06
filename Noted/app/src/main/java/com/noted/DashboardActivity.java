package com.noted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noted.adapters.NoteRecyclerAdapter;
import com.noted.adapters.VoiceRecyclerAdapter;
import com.noted.models.Note;
import com.noted.models.User;
import com.noted.models.Voice;

import java.util.ArrayList;
import java.util.HashMap;

import static com.noted.utils.AccountUtil.getDatabaseReference;
import static com.noted.utils.AccountUtil.getUid;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // initialize database
    private DatabaseReference dDatabase;
    private String dUID;

    // RecyclerView
    private RecyclerView dNoteRecycler;
    private RecyclerView dVoiceRecycler;

    // SearchView
    private SearchView dSearchNotes;
    private SearchView dSearchVoice;

    // Adapter + layout manager
    private NoteRecyclerAdapter dNoteAdapter;
    private VoiceRecyclerAdapter dVoiceAdapter;

    LinearLayoutManager layoutManager;


    ArrayList<Note> dNoteList;
    ArrayList<Voice> dVoiceList;

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
        dDatabase = getDatabaseReference();
        dDatabase.keepSynced(true);

        // initialize views
        dTextEmpty = findViewById(R.id.dashboardTextEmpty);
        dNoteRecycler = findViewById(R.id.dashboardNoteRecyclerView);
        dVoiceRecycler = findViewById(R.id.dashboardVoiceRecyclerView);

        //initialize searchviews
        dSearchNotes = findViewById(R.id.dashboardSearchNotes);
        dSearchNotes.clearFocus();
        dSearchNotes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchNotes(query);

                dSearchNotes.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchNotes("");
                }
                return false;
            }
        });

        dSearchVoice = findViewById(R.id.dashboardSearchVoice);
        dSearchVoice.clearFocus();
        dSearchVoice.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchVoice(query);

                dSearchVoice.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchVoice("");
                }
                return false;
            }
        });

        // default focus is notes
        setTitle("My Notes");
        displayNotes();
    }

    private void displayNotes() {
        dSearchVoice.setVisibility(View.GONE);
        dVoiceRecycler.setVisibility(View.GONE);

        dDatabase.child("notes").child(dUID).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dNoteList = new ArrayList<Note>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Note note = postSnapshot.getValue(Note.class);
                    dNoteList.add(0, note);
                }

                // set empty note list text
                if (dNoteList.isEmpty()) {
                    dNoteRecycler.setVisibility(View.GONE);
                    dSearchNotes.setVisibility(View.GONE);

                    dTextEmpty.setText("No notes to display.");
                    dTextEmpty.setVisibility(View.VISIBLE);

                } else {
                    dTextEmpty.setVisibility(View.GONE);
                    dSearchNotes.setVisibility(View.VISIBLE);
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

    private void displaySortedNotes() {
        dSearchVoice.setVisibility(View.GONE);
        dVoiceRecycler.setVisibility(View.GONE);

        dDatabase.child("notes").child(dUID).orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dNoteList = new ArrayList<Note>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Note note = postSnapshot.getValue(Note.class);
                    dNoteList.add(note);
                }

                // set empty note list text
                if (dNoteList.isEmpty()) {
                    dNoteRecycler.setVisibility(View.GONE);
                    dSearchNotes.setVisibility(View.GONE);

                    dTextEmpty.setText("No notes to display.");
                    dTextEmpty.setVisibility(View.VISIBLE);

                } else {
                    dTextEmpty.setVisibility(View.GONE);
                    dSearchNotes.setVisibility(View.VISIBLE);
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

    public void searchNotes(String query) {

        final String keyword = query.toLowerCase();

        dDatabase.child("notes").child(dUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dNoteList = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Note note = postSnapshot.getValue(Note.class);

                    if (note.getTITLE().toLowerCase().contains(keyword) ||
                            note.getCONTENT().toLowerCase().contains(keyword)) {
                        dNoteList.add(note);
                    }
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
        dSearchNotes.setVisibility(View.GONE);
        dNoteRecycler.setVisibility(View.GONE);

        dDatabase.child("audio").child(dUID).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dVoiceList = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Voice voice = postSnapshot.getValue(Voice.class);
                    dVoiceList.add(0,voice);
                }

                // set empty note list text
                if (dVoiceList.isEmpty()) {
                    dVoiceRecycler.setVisibility(View.GONE);
                    dSearchVoice.setVisibility(View.GONE);

                    dTextEmpty.setText("No recordings to display.");
                    dTextEmpty.setVisibility(View.VISIBLE);

                } else {
                    dTextEmpty.setVisibility(View.GONE);
                    dSearchVoice.setVisibility(View.VISIBLE);
                    dVoiceRecycler.setVisibility(View.VISIBLE);
                }

                // specify an adapter
                dVoiceAdapter = new VoiceRecyclerAdapter(dVoiceList, getApplication());

                // use a linear layout manager
                layoutManager = new LinearLayoutManager(DashboardActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                dVoiceRecycler.setLayoutManager(layoutManager);

                // set adapter
                dVoiceRecycler.setAdapter(dVoiceAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("read error", databaseError.getMessage());
            }
        });
    }

    private void displaySortedVoice() {
        dSearchNotes.setVisibility(View.GONE);
        dNoteRecycler.setVisibility(View.GONE);

        dDatabase.child("audio").child(dUID).orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dVoiceList = new ArrayList<Voice>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Voice voice = postSnapshot.getValue(Voice.class);
                    dVoiceList.add(voice);
                }

                // set empty note list text
                if (dVoiceList.isEmpty()) {
                    dVoiceRecycler.setVisibility(View.GONE);
                    dSearchVoice.setVisibility(View.GONE);

                    dTextEmpty.setText("No recordings to display.");
                    dTextEmpty.setVisibility(View.VISIBLE);

                } else {
                    dTextEmpty.setVisibility(View.GONE);
                    dSearchVoice.setVisibility(View.VISIBLE);
                    dVoiceRecycler.setVisibility(View.VISIBLE);
                }

                // specify an adapter
                dVoiceAdapter = new VoiceRecyclerAdapter(dVoiceList, getApplication());

                // use a linear layout manager
                layoutManager = new LinearLayoutManager(DashboardActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                dVoiceRecycler.setLayoutManager(layoutManager);

                // set adapter
                dVoiceRecycler.setAdapter(dVoiceAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("read error", databaseError.getMessage());
            }
        });
    }

    public void searchVoice(String query) {

        final String keyword = query.toLowerCase();

        dDatabase.child("audio").child(dUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dVoiceList = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Voice voice = postSnapshot.getValue(Voice.class);

                    if (voice.getTITLE().toLowerCase().contains(keyword)) {
                        dVoiceList.add(voice);
                    }
                }

                // specify an adapter
                dVoiceAdapter = new VoiceRecyclerAdapter(dVoiceList, getApplication());

                // use a linear layout manager
                layoutManager = new LinearLayoutManager(DashboardActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                dVoiceRecycler.setLayoutManager(layoutManager);

                // set adapter
                dVoiceRecycler.setAdapter(dVoiceAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("read error", databaseError.getMessage());
            }
        });
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
        if (id == R.id.action_sort_atoz) {
            if (dNoteRecycler.getVisibility() == View.VISIBLE) {
                displaySortedNotes();
            } else if (dVoiceRecycler.getVisibility() == View.VISIBLE) {
                displaySortedVoice();
            } else {
                Toast.makeText(this, "Nothing to sort.", Toast.LENGTH_SHORT).show();
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_date) {
            if (dNoteRecycler.getVisibility() == View.VISIBLE) {
                displayNotes();
            } else if (dVoiceRecycler.getVisibility() == View.VISIBLE) {
                displayVoice();
            } else {
                Toast.makeText(this, "Nothing to sort.", Toast.LENGTH_SHORT).show();
            }
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
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(redirectToLogin);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
