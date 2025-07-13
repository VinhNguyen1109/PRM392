package com.example.familynoteapp.feture.interaction;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InteractionDetailActivity extends AppCompatActivity {
    private static final String TAG = "Vinhnc";

    private InteractionViewModel viewModel;
    private TextView txtType, txtNote, txtDate;
    private ImageView imgPhoto;
    private RecyclerView recyclerVerticalPhotos;

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
        recyclerVerticalPhotos = findViewById(R.id.recyclerVerticalPhotos);
        recyclerVerticalPhotos.setLayoutManager(new LinearLayoutManager(this)); // chiều dọc
    }

    private void bindData(Interaction interaction) {
        txtType.setText("Loại: " + interaction.type);
        txtNote.setText("Ghi chú: " + interaction.note);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDate.setText("Ngày: " + sdf.format(interaction.date));

        // Load ảnh đại diện ở ImageView to
        if (interaction.photoUri != null && !interaction.photoUri.trim().isEmpty()) {
            Uri photoUri = Uri.parse(interaction.photoUri);
            Log.d(TAG, "Loading photoUri: " + photoUri);
            Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(imgPhoto);
        } else {
            imgPhoto.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Tạo list mới để hiển thị trong recyclerView: thêm ảnh đại diện vào đầu list
        List<String> extraPhotos = (interaction.extraPhotoUris != null) ? interaction.extraPhotoUris : new ArrayList<>();
        recyclerVerticalPhotos.setAdapter(new VerticalPhotoAdapter(this, interaction.photoUri, extraPhotos));
    }

}
