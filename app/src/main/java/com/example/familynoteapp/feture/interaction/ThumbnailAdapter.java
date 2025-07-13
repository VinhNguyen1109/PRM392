package com.example.familynoteapp.feture.interaction;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    private final Context context;
    private final List<String> photoUris;
    private final OnThumbnailClickListener listener;

    public interface OnThumbnailClickListener {
        void onClick(Uri uri);
    }

    public ThumbnailAdapter(Context context, List<String> photoUris, OnThumbnailClickListener listener) {
        this.context = context;
        this.photoUris = photoUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uriStr = photoUris.get(position);
        Uri uri = Uri.parse(uriStr);
        Glide.with(context).load(uri).placeholder(R.drawable.ic_image_placeholder).into(holder.imgThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onClick(uri));
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}