package com.cheradip.childcare;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EngNumberActivity extends AppCompatActivity {

    private GridView gridView;
    private EngNumberAdapter numberAdapter;
    private MediaPlayer mediaPlayer;

    private final String[] imageItems = {
            "p4/numbers/images/n1",
            "p4/numbers/images/n2",
            "p4/numbers/images/n3",
            "p4/numbers/images/n4",
            "p4/numbers/images/n5",
            "p4/numbers/images/n6",
            "p4/numbers/images/n7",
            "p4/numbers/images/n8",
            "p4/numbers/images/n9",
            "p4/numbers/images/n10"
    };

    private final String[] soundItems = {
            "p4_numbers_audios/n1",
            "p4_numbers_audios/n2",
            "p4_numbers_audios/n3",
            "p4_numbers_audios/n4",
            "p4_numbers_audios/n5",
            "p4_numbers_audios/n6",
            "p4_numbers_audios/n7",
            "p4_numbers_audios/n8",
            "p4_numbers_audios/n9",
            "p4_numbers_audios/n10"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        gridView = findViewById(R.id.recyclerView);
        numberAdapter = new EngNumberAdapter(this, imageItems);
        gridView.setAdapter(numberAdapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(3); // Display 3 items in each row
        } else {
            gridView.setNumColumns(5); // Display 5 items in each row
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSound(position);
            }
        });
    }

    private void playSound(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        int soundResource = getResources().getIdentifier(soundItems[position], "raw", getPackageName());
        if (soundResource != 0) {
            mediaPlayer = MediaPlayer.create(this, soundResource);
            mediaPlayer.start();
        } else {
            Toast.makeText(this, "Sound file not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private Bitmap decodeSampledBitmapFromResource(String resourceName, int reqWidth, int reqHeight) {
        int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(), resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
