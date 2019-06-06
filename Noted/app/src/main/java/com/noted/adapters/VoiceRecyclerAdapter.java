package com.noted.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noted.PlayVoiceActivity;
import com.noted.R;
import com.noted.models.Voice;

import java.util.ArrayList;

import static com.noted.utils.AccountUtil.getUid;

public class VoiceRecyclerAdapter extends RecyclerView.Adapter<VoiceRecyclerAdapter.VoiceViewHolder> {

    private ArrayList<Voice> nVoiceList;

    private DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference();

    Context context;

    String nUID;

    public static class VoiceViewHolder extends RecyclerView.ViewHolder {

        public CardView nCardView;
        public TextView nTitle;
        public TextView nTimestamp;

        public VoiceViewHolder(View v) {
            super(v);
            nCardView = v.findViewById(R.id.voice_card_view);
            nTitle = v.findViewById(R.id.voiceTitle);
            nTimestamp = v.findViewById(R.id.voiceTimestamp);
        }
    }

    public VoiceRecyclerAdapter(ArrayList<Voice> voiceList, Context context) {
        nVoiceList = voiceList;
        this.context = context;
        nUID = getUid();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VoiceRecyclerAdapter.VoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_voice, parent, false);

        VoiceViewHolder viewHolder = new VoiceViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VoiceViewHolder holder, final int position) {
        // get item from list
        Voice voice = nVoiceList.get(position);

        // get item attributes
        final String title = voice.getTITLE();
        final String timestamp = voice.getTIMESTAMP();
        final String key = voice.getKEY();
        final String url = voice.getURL();

        // populate view holder
        holder.nTitle.setText(title);
        holder.nTimestamp.setText(timestamp);

        holder.nCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayVoiceActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("key", key);
                intent.putExtra("url", url);
                view.getContext().startActivity(intent);
            }
        });

        holder.nCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "Delete Recording?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nDatabase.child("audio").child(nUID).child(key).removeValue();
                                nVoiceList.remove(position);
                            }
                        }).show();
                return true;
            }
        });
    }

    // Return the size of itemList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (nVoiceList == null) {
            return 0;
        } else {
            return nVoiceList.size();
        }
    }
}