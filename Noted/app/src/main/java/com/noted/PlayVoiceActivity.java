package com.noted;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class PlayVoiceActivity extends AppCompatActivity {

    // Buttons
    private Button fPlayButton;
    private Button fBackButton;

    // Edit Text
    private TextView fTextViewTitle;
    private CircleLineVisualizer mVisualizer;

    // Strings
    private String fUrl;
    private String fKey;
    private String fTitle;

    // Storage
    private StorageReference fStorage;

    // media player
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_voice);

        mVisualizer = findViewById(R.id.circleline);

        fTextViewTitle = findViewById(R.id.voiceTitle);

        Intent intent = getIntent();

        // Get intent Strings
        fTitle = intent.getStringExtra("title");
        fUrl = intent.getStringExtra("url");
        fKey = intent.getStringExtra("pushkey");

        fTextViewTitle.setText(fTitle);

        fStorage = FirebaseStorage.getInstance().getReference();

        fPlayButton = findViewById(R.id.playVoice);
        fPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStorage.child(fUrl).child(fTitle + ".3gp").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        player = new MediaPlayer();
                        // audio visualizer
                        int audioSessionId = player.getAudioSessionId();
                        if (audioSessionId != -1)
                            mVisualizer.setAudioSessionId(audioSessionId);

                        try {
                            player.setDataSource(uri.toString());
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if (mVisualizer != null)
                                        mVisualizer.hide();
                                }
                            });
                            player.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                Toast.makeText(PlayVoiceActivity.this, "Playing " + fTitle, Toast.LENGTH_LONG).show();
            }
        });

        fBackButton = findViewById(R.id.back);
        fBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(player.isPlaying())
                {
                    player.stop();
                    player.release();
                }

                finish();
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mVisualizer != null)
            mVisualizer.release();
    }

}
