package com.example.notesproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NoteAudioFragment extends Fragment {

    Button btnPlay, btnStop, btnRecord, btnStopRecord;
    String pathSave = "";
    String audioPath = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    NoteClass notes;

    final int REQUEST_PERMISSION_CODE = 1000;

    final private static String RECORDED_FILE = "/audio.3gp";


    public NoteAudioFragment(NoteClass notes) {
        this.notes = notes;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_audio_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // set the volume of played media to maximum.
        audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

        btnPlay = view.findViewById(R.id.btn_play);
        btnRecord = view.findViewById(R.id.btn_record);
        btnStop = view.findViewById(R.id.btn_stop);
        btnStopRecord = view.findViewById(R.id.btn_stop_record);

        // check the permission
        if (!checkPermissionDevice())
            requestPermission();



        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionDevice()) {

                    pathSave = getContext().getExternalCacheDir().getAbsolutePath()
                            + RECORDED_FILE;

                    setUpMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException ise) {

                        ise.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnRecord.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Recording...", Toast.LENGTH_SHORT).show();
                } else
                    requestPermission();
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(true);
                btnRecord.setVisibility(View.VISIBLE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(false);
                btnPlay.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnStop.setVisibility(View.GONE);
                        btnPlay.setVisibility(View.VISIBLE);
                        btnRecord.setEnabled(true);
                    }
                });

                mediaPlayer.start();
                Toast.makeText(getContext(), "Playing...", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnRecord.setEnabled(true);
                btnPlay.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setUpMediaRecorder();
                }
            }
        });


    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
