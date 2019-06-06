package com.noted;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noted.models.Voice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.noted.utils.AccountUtil.getUid;


public class CreateVoiceActivity extends AppCompatActivity {

    private EditText nameOfMemo;
    private Button Record;
    private Button Stop;
    private MediaRecorder Recorder;
    private SimpleDateFormat simpleDateFormat;
    private String pathSave;
    private String DateStamp; // these variabels to put in data base
    private String Title;

    private DatabaseReference nDatabase;
    private String nUID;
    final int REQUEST_PERMISSION_CODE = 1000;

    private StorageReference mStorage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_voice);

        if(!hasPermission())
        {
            requestPermission();
        }
        nameOfMemo = findViewById(R.id.NameOfRecord);
        Record = findViewById(R.id.RECORD);
        Stop = findViewById(R.id.STOP);
        mStorage = FirebaseStorage.getInstance().getReference();
        nUID = getUid();
        dialog = new ProgressDialog(this);
        nDatabase = FirebaseDatabase.getInstance().getReference();

        // setting up the record button listener
        Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission()) {
                    Title = nameOfMemo.getText().toString().trim();
                    if (TextUtils.isEmpty(Title)) {
                        nameOfMemo.setError("Required");
                        return;
                    }
                    // get the path to the audio
                    pathSave = getExternalCacheDir().getAbsolutePath() + "_audio_record.3gp";

                    Stop.setEnabled(true);
                    Record.setEnabled(false);
                    mediaRecorderFunction(pathSave);
                    // starting the recorder
                    try{
                        Recorder.prepare();
                        Recorder.start();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Toast.makeText(CreateVoiceActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }
            }
        });
            // setting the Stop button to stop recording
            Stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Recorder.stop();
                        Recorder.release();
                        Recorder = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Stop.setEnabled(false);
                    Record.setEnabled(true);

                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");

                    DateStamp = simpleDateFormat.format(new Date());
                    Title = nameOfMemo.getText().toString().trim();
                    String key = nDatabase.child("audio").push().getKey();
                    uploadAudio(key);
                    String url = "/audio/" + nUID + "/" + key;
                    Voice voice = new Voice(key, Title, url, DateStamp);
                    saveVoice(voice, key);
                    nameOfMemo.setText("");
                    finish();
                }
            });



    }
// seting up the recorder
    public void mediaRecorderFunction(String path)
    {
        Recorder = new MediaRecorder();
        Recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        Recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        Recorder.setOutputFile(path);

    }
    // checking if there is permission, other wise requesting it
    private boolean hasPermission()
    {
        int writeResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE); // check if can write
        int recordResult = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        boolean granted = false;
        if(writeResult== PackageManager.PERMISSION_GRANTED && recordResult ==PackageManager.PERMISSION_GRANTED)
        {
            granted = true;
        }
        return granted;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String [] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "hasPermission Granted", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(this, "hasPermission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

    }

    private void uploadAudio(String key) {
         Toast.makeText(this,"uploading...", Toast.LENGTH_SHORT).show();
         System.out.println("this is the title: " + Title);
         StorageReference path = mStorage.child("audio").child(nUID).child(key).child(Title + ".3gp");
         Uri uri = Uri.fromFile(new File(pathSave));
         path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

             }
         });
    }

    private void saveVoice(Voice voice, String key) {
        // map note
        Map<String, Object> postValues = voice.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        // set db structure
        childUpdates.put("/audio/" + nUID + "/" + key, postValues);

        // update db
        nDatabase.updateChildren(childUpdates);

        // voice saved
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }


}
