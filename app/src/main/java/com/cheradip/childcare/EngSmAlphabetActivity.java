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

public class EngSmAlphabetActivity extends AppCompatActivity {

    private GridView gridView;
    private EngAlphabetAdapter alphabetAdapter;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable releaseRunnable;

    private final String[] imageItems = {
            "p4/sletters/images/a",
            "p4/sletters/images/b",
            "p4/sletters/images/c",
            "p4/sletters/images/d",
            "p4/sletters/images/e",
            "p4/sletters/images/f",
            "p4/sletters/images/g",
            "p4/sletters/images/h",
            "p4/sletters/images/i",
            "p4/sletters/images/j",
            "p4/sletters/images/k",
            "p4/sletters/images/l",
            "p4/sletters/images/m",
            "p4/sletters/images/n",
            "p4/sletters/images/o",
            "p4/sletters/images/p",
            "p4/sletters/images/q",
            "p4/sletters/images/r",
            "p4/sletters/images/s",
            "p4/sletters/images/t",
            "p4/sletters/images/u",
            "p4/sletters/images/v",
            "p4/sletters/images/w",
            "p4/sletters/images/x",
            "p4/sletters/images/y",
            "p4/sletters/images/z"
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
//    }

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
            AssetFileDescriptor afd = getAssets().openFd("p4/letters/audios/letters.mp3");
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

