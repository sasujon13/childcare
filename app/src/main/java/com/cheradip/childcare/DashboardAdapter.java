package com.cheradip.childcare;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.GridViewHolder> {

    private final String[] titles;
    private final Drawable[] drawables;

    public DashboardAdapter(String[] titles, Drawable[] drawables) {
        this.titles = titles;
        this.drawables = drawables;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Drawable drawable = drawables[position];
        holder.imageView.setImageDrawable(drawable);
        holder.textViewTitle.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewTitle;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }
}