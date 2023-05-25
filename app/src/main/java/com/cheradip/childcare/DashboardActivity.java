package com.cheradip.childcare;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

public class DashboardActivity extends AppCompatActivity {

    private final String[] titles = {
            "friends.svg", "arabic.svg", "bengali.svg", "english.svg", "math.svg",
            "iq.svg", "animals.svg", "fruits.svg", "vegetables.svg", "protein.svg",
            "hbody.svg", "khata.svg", "quiz.svg", "games.svg", "shorts.svg"
    };

    private final String[] imageNames = {
            "Friends-বন্ধু", "Arabic-আরবি", "Bengali-বাংলা", "English-ইংরেজি", "Math-গণিত",
            "IQ-বুদ্ধিমত্তা", "Animals-প্রাণী", "Fruits-ফলমূল", "Vegetables-সবজি", "Protein-প্রোটিন",
            "H Body-মানব দেহ", "Khata-খাতা", "Quiz-কুইজ", "Games-খেলা", "Shorts-শর্টস"
    };

    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        recyclerView = findViewById(R.id.recyclerView);

        // Determine the span count based on the device's orientation
        int spanCount = getSpanCount();

        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        Drawable[] drawables = null; // Declare and initialize the variable

        try {
            drawables = loadSVGImages(); // Load SVG images from the "res/drawable/images" folder
        } catch (IOException | SVGParseException e) {
            e.printStackTrace(); // Print the stack trace
        }

        dashboardAdapter = new DashboardAdapter(titles, drawables); // Use the variable 'drawables'
        recyclerView.setAdapter(dashboardAdapter);
    }

    private int getSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        int gridSize;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridSize = 3;
        } else {
            gridSize = 5;
        }

        return gridSize;
    }

    private Drawable[] loadSVGImages() throws IOException, SVGParseException {
        Drawable[] drawables = new Drawable[titles.length];
        int gridSize = getSpanCount();

        for (int i = 0; i < titles.length; i++) {
            String imageName = imageNames[i];
            InputStream inputStream = getAssets().open("images/" + imageName + ".svg");
            SVG svg = SVG.getFromInputStream(inputStream);

            // Render the SVG to a bitmap
            Bitmap bitmap = Bitmap.createBitmap(gridSize, gridSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawRGB(255, 255, 255); // Set background color if needed
            svg.renderToCanvas(canvas);

            // Convert the bitmap to a drawable
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);

            // Calculate the desired width and height of the image
            int cellWidth = recyclerView.getWidth() / gridSize;
            int cellHeight = recyclerView.getHeight() / gridSize;

            // Resize the drawable to fit the grid cell
            drawable.setBounds(0, 0, cellWidth, cellHeight);
            drawables[i] = drawable;
        }

        return drawables;
    }
}
