package com.cheradip.childcare;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

public class EngActivity extends AppCompatActivity {

    private GridView gridView;
    private EngAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        gridView = findViewById(R.id.recyclerView);
        imageAdapter = new EngAdapter(this);
        gridView.setAdapter(imageAdapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(4);
        } else {
            gridView.setNumColumns(1);
        }
    }
}

