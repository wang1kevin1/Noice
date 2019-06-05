package com.noted.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noted.R;
import com.noted.models.Note;
import com.noted.utils.AccountUtil;

import java.util.ArrayList;

import static com.noted.utils.AccountUtil.getUid;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {

    private ArrayList<Note> nNoteList;

    private DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference();

    Context context;

    String nUID;

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public CardView nCardView;
        public TextView nTitle;
        public TextView nContent;
        public TextView nTimestamp;

        public NoteViewHolder(View v) {
            super(v);
            nCardView = v.findViewById(R.id.note_card_view);
            nTitle = v.findViewById(R.id.nTitle);
            nContent = v.findViewById(R.id.nContent);
            nTimestamp = v.findViewById(R.id.nTimestamp);
        }
    }

    public NoteRecyclerAdapter(ArrayList<Note> noteList, Context context) {
        nNoteList = noteList;
        this.context = context;
        nUID = getUid();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteRecyclerAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_note, parent, false);

        NoteViewHolder viewHolder = new NoteViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position) {
        // get item from list
        Note note = nNoteList.get(position);

        // get item attributes
        final String title = note.getTITLE();
        final String content = note.getCONTENT();
        final String timestamp = note.getTIMESTAMP();
        final String pushkey = note.getPUSHKEY();

        // populate view holder
        holder.nTitle.setText(title);
        holder.nContent.setText(content);
        holder.nTimestamp.setText(timestamp);

        holder.nCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "Delete Note?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nDatabase.child("notes").child(nUID).child(pushkey).removeValue();

                                nNoteList.remove(position);
                            }
                        }).show();
                return true;
            }
        });
    }

    // Return the size of itemList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (nNoteList == null) {
            return 0;
        } else {
            return nNoteList.size();
        }
    }
}