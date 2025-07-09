package com.example.familynoteapp.feture.interaction;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class InteractionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_detail);

        Interaction interaction = (Interaction) getIntent().getSerializableExtra("interaction_detail");
        if (interaction == null) {
            finish();
            return;
        }

        TextView txtType = findViewById(R.id.txtTypeDetail);
        TextView txtNote = findViewById(R.id.txtNoteDetail);
        TextView txtDate = findViewById(R.id.txtDateDetail);
        ImageView imgPhoto = findViewById(R.id.imgPhotoDetail);

        txtType.setText("Loại: " + interaction.type);
        txtNote.setText("Ghi chú: " + interaction.note);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDate.setText("Ngày: " + sdf.format(interaction.date));

        if (interaction.photoUri != null) {
            Glide.with(this).load(interaction.photoUri).into(imgPhoto);
        } else {
            imgPhoto.setImageResource(R.drawable.ic_image_placeholder);
        }
    }
}
