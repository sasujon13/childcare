package com.cheradip.childcare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EngAdapter extends BaseAdapter {

    private final Context context;
    private final String[] imageItems = {"A.svg", "a.svg", "n1.svg", "quiz.svg"};
    private final String[] textItems = {"Capital Letters", "Small Letter", "Numbers and Signs", "Quiz"};
    public EngAdapter(Context context) {
        this.context = context;
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

        // Set click listener
        if (position == 0) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EngCaAlphabetActivity.class); //it was FourthActivity
                    context.startActivity(intent);
                }
            });
        } if (position == 1) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EngSmAlphabetActivity.class); //it was FourthActivity
                    context.startActivity(intent);
                }
            });
        } if (position == 2) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EngNumberActivity.class); //it was FourthActivity
                    context.startActivity(intent);
                }
            });
        } else if(position == 3) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UnderDevelopmentActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        return imageView;

        //  return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}


