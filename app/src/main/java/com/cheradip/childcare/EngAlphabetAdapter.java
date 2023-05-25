package com.cheradip.childcare;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EngAlphabetAdapter extends BaseAdapter {

    private final Context context;
    private final String[] imageItems;
    private MediaPlayer mediaPlayer;

    public EngAlphabetAdapter(Context context, String[] imageItems) {
        this.context = context;
        this.imageItems = imageItems;
    }

    @Override
    public int getCount() {
        return imageItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Inside the getView() method in AlphabetAdapter.java
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }

        // Set image resource
        int imageResource = context.getResources().getIdentifier(imageItems[position], "drawable", context.getPackageName());
        imageView.setImageResource(imageResource);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(position);
            }
        });

        return imageView;
    }
    private void playSound(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        int soundResource = context.getResources().getIdentifier("n" + position, "raw", context.getPackageName());
        mediaPlayer = MediaPlayer.create(context, soundResource);
        mediaPlayer.start();
    }

}

