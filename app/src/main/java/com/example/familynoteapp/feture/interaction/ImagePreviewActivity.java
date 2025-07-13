package com.example.familynoteapp.feture.interaction;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity {
    private ImageView imgFullScreen;
    private RecyclerView recyclerThumbnails;
    private ThumbnailAdapter adapter;
    private List<String> photoUriStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imgFullScreen = findViewById(R.id.imgFullScreen);
        recyclerThumbnails = findViewById(R.id.recyclerThumbnails);

        // Lấy danh sách ảnh truyền qua intent
        photoUriStrings = getIntent().getStringArrayListExtra("photo_uris");
        String initialUri = getIntent().getStringExtra("initial_uri");

        if (photoUriStrings == null) photoUriStrings = new ArrayList<>();

        // Hiển thị ảnh đầu tiên
        if (initialUri != null) {
            Glide.with(this).load(Uri.parse(initialUri)).into(imgFullScreen);
        } else if (!photoUriStrings.isEmpty()) {
            Glide.with(this).load(Uri.parse(photoUriStrings.get(0))).into(imgFullScreen);
        }

        adapter = new ThumbnailAdapter(this, photoUriStrings, uri -> {
            // Khi click thumbnail: đổi ảnh lớn
            Glide.with(this).load(uri).into(imgFullScreen);
        });

        recyclerThumbnails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerThumbnails.setAdapter(adapter);
    }
}
