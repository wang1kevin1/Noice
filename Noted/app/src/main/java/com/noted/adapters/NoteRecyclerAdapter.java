package com.noted.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noted.R;
import com.noted.models.Note;

import java.util.ArrayList;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {

    private ArrayList<Note> nNoteList;

    Context context;

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView nTitle;
        public TextView nContent;
        public TextView nTimestamp;

        public NoteViewHolder(View v) {
            super(v);
            nTitle = v.findViewById(R.id.nTitle);
            nContent = v.findViewById(R.id.nContent);
            nTimestamp = v.findViewById(R.id.nTimestamp);
        }
    }

    public NoteRecyclerAdapter(ArrayList<Note> noteList, Context context) {
        nNoteList = noteList;
        this.context = context;
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
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // get item from list
        Note note = nNoteList.get(position);

        // get item attributes
        String title = note.getTITLE();
        String content = note.getCONTENT();
        String timestamp = note.getTIMESTAMP();

        // populate view holder
        holder.nTitle.setText(title);
        holder.nContent.setText(content);
        holder.nTimestamp.setText(timestamp);
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