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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noted.models.Note;
import com.noted.utils.AccountUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class CreateVoiceActivity extends AppCompatActivity {

    private EditText nameOfMemo;
    private Button Record;
    private Button Stop;
    private MediaRecorder Recorder;
    private SimpleDateFormat simpleDateFormat;
    private String pathSave;
    private String DateStamp; // these variabels to put in data base
    private String Title;
    final int REQUEST_PERMISSION_CODE = 1000;

    private StorageReference mStorage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_voice);

        if(!Permission())
        {
            requestPermission();
        }
        nameOfMemo = findViewById(R.id.NameOfRecord);
        Record = findViewById(R.id.RECORD);
        Stop = findViewById(R.id.STOP);
        mStorage = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);

            // setting up the record button listener
            Record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Permission())
                    {
                        // get the path to the audio
                        pathSave = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() +"/" + UUID.randomUUID().toString()+"_audio_record.3gp";

                        if(!pathSave.isEmpty())
                        {
                            System.out.println("the path is this!!!!!!!!!!!!!!!!!!!!!!!!!!" +pathSave);
                        }
                        Stop.setEnabled(true);
                        Record.setEnabled(false);
                        MediaRecorderFunction(pathSave);
                        // starting the recorder
                        try{
                            Recorder.prepare();
                            Recorder.start();
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                    Toast.makeText(CreateVoiceActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    }else {
                        requestPermission();
                    }
                    }
            });
            // setting the Stop button to stop recording
            Stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recorder.stop();
                    Recorder.release();
                    Recorder = null;
                    System.out.println("Stopped Recording, this is the path: !!!!!!!!!!!!!!!!!!!!!!!!!!" +pathSave);
                    Stop.setEnabled(false);
                    Record.setEnabled(true);
                    uploadAudio();
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    DateStamp = simpleDateFormat.format(new Date());
                    Title = nameOfMemo.getText().toString().trim();
                    nameOfMemo.setText("");
                    Toast.makeText(CreateVoiceActivity.this, "Stopped Recording...", Toast.LENGTH_SHORT).show();
                }
            });



    }
// seting up the recorder
    public void MediaRecorderFunction(String path)
    {
        Recorder = new MediaRecorder();
        Recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        Recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        Recorder.setOutputFile(path);

    }
    // checking if there is permission, other wise requesting it
    private boolean Permission()
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
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

    }

    private void uploadAudio() {
         dialog.setMessage("uploading...");
         dialog.show();
         System.out.println("this is the title: " + Title);
         StorageReference path = mStorage.child("Audio").child(Title+".3gp");
         Uri uri = Uri.fromFile(new File(pathSave));
         path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();

             }
         });
    }


}
