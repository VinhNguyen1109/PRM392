package com.example.familynoteapp.feture.interaction;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InteractionDetailActivity extends AppCompatActivity {
    private static final String TAG = "InteractionDetail";

    private InteractionViewModel viewModel;
    private TextView txtType, txtNote, txtDate;
    private ImageView imgPhoto;
    private RecyclerView recyclerExtraPhotos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_detail);

        initViews();

        viewModel = new ViewModelProvider(this).get(InteractionViewModel.class);

        int interactionId = getIntent().getIntExtra("interaction_id", -1);
        if (interactionId == -1) {
            Log.e(TAG, "interaction_id not found in intent, finish activity");
            finish();
            return;
        }

        Log.d(TAG, "Received interaction_id: " + interactionId);

        // Lấy dữ liệu Interaction theo id và quan sát thay đổi
        viewModel.getInteractionById(interactionId).observe(this, interaction -> {
            if (interaction == null) {
                Log.e(TAG, "Interaction not found in database, finish activity");
                finish();
            } else {
                bindData(interaction);
            }
        });
    }

    private void initViews() {
        txtType = findViewById(R.id.txtTypeDetail);
        txtNote = findViewById(R.id.txtNoteDetail);
        txtDate = findViewById(R.id.txtDateDetail);
        imgPhoto = findViewById(R.id.imgPhotoDetail);
        recyclerExtraPhotos = findViewById(R.id.recyclerExtraPhotosDetail);

        recyclerExtraPhotos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void bindData(Interaction interaction) {
        txtType.setText("Loại: " + interaction.type);
        txtNote.setText("Ghi chú: " + interaction.note);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDate.setText("Ngày: " + sdf.format(interaction.date));

        // Load ảnh chính với Glide
        if (interaction.photoUri != null && !interaction.photoUri.trim().isEmpty()) {
            Uri photoUri = Uri.parse(interaction.photoUri);
            Log.d(TAG, "Loading photoUri: " + photoUri);
            Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
                                                    @NonNull Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide failed: " + model, e);
                            return false; // cho Glide xử lý hiển thị placeholder/error
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model,
                                                       @NonNull Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide loaded: " + model);
                            return false;
                        }
                    })
                    .into(imgPhoto);
        } else {
            imgPhoto.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Load ảnh bổ sung nếu có
        List<String> extraPhotos = (interaction.extraPhotoUris != null) ? interaction.extraPhotoUris : new ArrayList<>();
        recyclerExtraPhotos.setAdapter(new ExtraPhotoAdapter(this, extraPhotos));
    }
}
