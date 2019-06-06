package com.noted.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noted.R;
import com.noted.models.Voice;

import java.util.ArrayList;

import static com.noted.utils.AccountUtil.getUid;

public class VoiceRecyclerAdapter extends RecyclerView.Adapter<VoiceRecyclerAdapter.VoiceViewHolder> {

    private ArrayList<Voice> nVoiceList;

    private DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference nStorage = FirebaseStorage.getInstance().getReference();

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
        final String pushkey = voice.getPUSHKEY();
        final String title = voice.getTITLE();
        final String timestamp = voice.getTIMESTAMP();
        final String url = voice.getURL();

        Log.i("Which is null? ", "nUID: " + nUID + " key: " + pushkey);

        // populate view holder
        holder.nTitle.setText(title);
        holder.nTimestamp.setText("Recorded " + timestamp);

        holder.nCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "Delete Recording?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nStorage.child(url).child(title + ".3gp").delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.v("delete recording", "Success");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.i("path", url);
                                                Log.v("read error", exception.getMessage());
                                            }
                                        });

                                nDatabase.child("audio").child(nUID).child(pushkey).removeValue();

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