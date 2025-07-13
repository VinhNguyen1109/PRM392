package com.example.familynoteapp.feture.interaction;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExtraPhotoAdapter extends RecyclerView.Adapter<ExtraPhotoAdapter.PhotoViewHolder> {

    private static final String TAG = "ExtraPhotoAdapter";
    private final Context context;
    private final List<Uri> photoUris = new ArrayList<>();

    // callback để activity/fragment xử lý khi xoá
    public interface OnPhotoRemoveListener {
        void onRemove(int position);
    }

    private OnPhotoRemoveListener removeListener;

    public void setOnPhotoRemoveListener(OnPhotoRemoveListener listener) {
        this.removeListener = listener;
    }

    public ExtraPhotoAdapter(Context context, List<String> uriStrings) {
        this.context = context;
        setDataFromStrings(uriStrings);
    }

    private void setDataFromStrings(List<String> uriStrings) {
        photoUris.clear();
        if (uriStrings != null) {
            for (String s : uriStrings) {
                if (s != null && !s.trim().isEmpty()) {
                    Uri parsed = parseSafeUri(s);
                    if (parsed != null) {
                        photoUris.add(parsed);
                    } else {
                        Log.e(TAG, "Cannot parse uri: " + s);
                    }
                }
            }
        }
    }

    public void updateData(List<String> newUriStrings) {
        setDataFromStrings(newUriStrings);
        notifyDataSetChanged();
    }

    private Uri parseSafeUri(String s) {
        try {
            // Trường hợp content provider của Google Photos: giải mã phần content
            if (s.startsWith("content://com.google.android.apps.photos.contentprovider")) {
                int start = s.indexOf("content%3A");
                int end = s.indexOf("/ORIGINAL");
                if (start != -1 && end != -1 && end > start) {
                    String encodedPart = s.substring(start, end);
                    String decoded = URLDecoder.decode(encodedPart, StandardCharsets.UTF_8.name());
                    return Uri.parse(decoded);
                }
            }
            return Uri.parse(s);
        } catch (Exception e) {
            Log.e(TAG, "parseSafeUri error: " + e.getMessage());
            return null;
        }
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_extra_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = photoUris.get(position);
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imgExtraPhoto);

        holder.btnRemovePhoto.setOnClickListener(v -> {
            if (removeListener != null) removeListener.onRemove(position);
        });
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgExtraPhoto;
        ImageButton btnRemovePhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgExtraPhoto = itemView.findViewById(R.id.imgExtraPhoto);
            btnRemovePhoto = itemView.findViewById(R.id.btnRemovePhoto);
        }
    }
}
