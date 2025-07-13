package com.example.familynoteapp.feture.interaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VerticalPhotoAdapter extends RecyclerView.Adapter<VerticalPhotoAdapter.PhotoViewHolder> {

    private static final String TAG = "Vinhnc";
    private final Context context;
    private final String mainPhotoUriString;  // Ảnh đại diện
    private final List<String> extraPhotoUriStrings = new ArrayList<>(); // Chỉ các extra photos
    private final List<Uri> extraPhotoUris = new ArrayList<>();

    public VerticalPhotoAdapter(Context context, String mainPhotoUriString, List<String> extraPhotoUriStrings) {
        this.context = context;
        this.mainPhotoUriString = mainPhotoUriString;
        setDataFromStrings(extraPhotoUriStrings);
    }

    private void setDataFromStrings(List<String> uriStrings) {
        extraPhotoUris.clear();
        extraPhotoUriStrings.clear();
        if (uriStrings != null) {
            for (String s : uriStrings) {
                if (s != null && !s.trim().isEmpty()) {
                    Uri parsed = parseSafeUri(s);
                    if (parsed != null) {
                        extraPhotoUris.add(parsed);
                        extraPhotoUriStrings.add(s);
                    } else {
                        Log.e(TAG, "Cannot parse uri: " + s);
                    }
                }
            }
        }
    }

    private Uri parseSafeUri(String s) {
        try {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_vertical_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = extraPhotoUris.get(position);
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imgVerticalPhoto);

        holder.imgVerticalPhoto.setOnClickListener(v -> {
            ArrayList<String> fullList = new ArrayList<>();
            if (mainPhotoUriString != null && !mainPhotoUriString.isEmpty()) {
                fullList.add(mainPhotoUriString); // Thêm ảnh đại diện vào đầu
            }
            fullList.addAll(extraPhotoUriStrings); // Thêm extra photos

            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putExtra("initial_uri", extraPhotoUriStrings.get(position)); // uri đang click
            intent.putStringArrayListExtra("photo_uris", fullList); // toàn bộ list (có cả ảnh đại diện)
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return extraPhotoUris.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVerticalPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVerticalPhoto = itemView.findViewById(R.id.imgVerticalPhoto);
        }
    }
}
