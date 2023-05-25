package com.cheradip.childcare;

import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EngCaAlphabetActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable releaseRunnable;
    private GridView gridView;
    private EngAlphabetAdapter alphabetAdapter;

    private final String[] imageItems = {
            "p4/p1/a",
            "p4/p1/b",
            "p4/p1/c",
            "p4/p1/d",
            "p4/p1/e",
            "p4/p1/f",
            "p4/p1/g",
            "p4/p1/h",
            "p4/p1/i",
            "p4/p1/j",
            "p4/p1/k",
            "p4/p1/l",
            "p4/p1/m",
            "p4/p1/n",
            "p4/p1/o",
            "p4/p1/p",
            "p4/p1/q",
            "p4/p1/r",
            "p4/p1/s",
            "p4/p1/t",
            "p4/p1/u",
            "p4/p1/v",
            "p4/p1/w",
            "p4/p1/x",
            "p4/p1/y",
            "p4/p1/z"
    };

//    private String[] soundItems = {"p4_letters_audios/letters"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        gridView = findViewById(R.id.recyclerView);
        alphabetAdapter = new EngAlphabetAdapter(this, imageItems);
        gridView.setAdapter(alphabetAdapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(5);
        } else {
            gridView.setNumColumns(3);
        }

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                playSound(position);
//            }
//        });

//        gridView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = 3; // Example position value, change as needed
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Calculate the starting position in milliseconds
                int startTime = position * 1807;

                playAudio(startTime);

                // Schedule the release of media player after 1800 milliseconds
                handler = new Handler();
                releaseRunnable = new Runnable() {
                    @Override
                    public void run() {
                        releaseMediaPlayer();
                    }
                };
                handler.postDelayed(releaseRunnable, 1807);
            }
        });
    }

//    private void playSound(int position) {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//        }
//
//        int soundResource = getResources().getIdentifier(soundItems[position], "raw", getPackageName());
//        if (soundResource != 0) {
//            mediaPlayer = MediaPlayer.create(this, soundResource);
//            mediaPlayer.start();
//        } else {
//            Toast.makeText(this, "Sound file not found", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void playAudio(int startTime) {
        // Initialize the MediaPlayer with the audio file
        try {
            AssetFileDescriptor afd = getAssets().openFd("p4/audios/letters.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.prepare();
            mediaPlayer.seekTo(startTime);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Remove the releaseRunnable from the handler's queue
        if (handler != null && releaseRunnable != null) {
            handler.removeCallbacks(releaseRunnable);
        }
    }
}

