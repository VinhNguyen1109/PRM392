package com.example.familynoteapp.feture.interaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddInteractionActivity extends AppCompatActivity {

    private Spinner spinnerType;
    private EditText edtNote;
    private ImageView imgPhoto;
    private TextView txtDate;
    private Button btnSave, btnAddExtraPhotos;
    private RecyclerView recyclerExtraPhotos;

    private Uri mainImageUri;
    private final List<Uri> extraImageUris = new ArrayList<>();
    private Date selectedDate = new Date();

    private ExtraPhotoAdapter extraPhotoAdapter;
    private Interaction editingInteraction;
    private int memberId;
    private InteractionViewModel viewModel;
    private int editingInteractionId = -1;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri pickedUri = result.getData().getData();
                    if (pickedUri != null) {
                        String localPath = copyUriToLocalFile(pickedUri);
                        if (localPath != null) {
                            mainImageUri = Uri.fromFile(new File(localPath));
                        } else {
                            mainImageUri = pickedUri;
                        }
                        Glide.with(this).load(mainImageUri).into(imgPhoto);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> multiImagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            String localPath = copyUriToLocalFile(uri);
                            if (localPath != null) {
                                extraImageUris.add(Uri.fromFile(new File(localPath)));
                            }
                        }
                    } else if (data.getData() != null) {
                        Uri uri = data.getData();
                        String localPath = copyUriToLocalFile(uri);
                        if (localPath != null) {
                            extraImageUris.add(Uri.fromFile(new File(localPath)));
                        }
                    }
                    updateExtraPhotos();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interaction);

        viewModel = new ViewModelProvider(this).get(InteractionViewModel.class);

        editingInteractionId = getIntent().getIntExtra("edit_interaction_id", -1);
        if (editingInteractionId != -1) {
            // sửa: load từ DB
            viewModel.getInteractionById(editingInteractionId).observe(this, item -> {
                if (item != null) {
                    editingInteraction = item;
                    memberId = editingInteraction.memberId;
                    bindDataToUI(editingInteraction);
                }
            });
        } else {
            memberId = getIntent().getIntExtra("memberId", -1);
        }

        if (memberId == -1) {
            Toast.makeText(this, "Không tìm thấy thành viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        spinnerType = findViewById(R.id.spinnerType);
        edtNote = findViewById(R.id.edtNote);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtDate = findViewById(R.id.txtDate);
        btnSave = findViewById(R.id.btnSave);
        btnAddExtraPhotos = findViewById(R.id.btnAddExtraPhotos);
        recyclerExtraPhotos = findViewById(R.id.recyclerExtraPhotos);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.interaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        recyclerExtraPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        extraPhotoAdapter = new ExtraPhotoAdapter(this, new ArrayList<>());
        recyclerExtraPhotos.setAdapter(extraPhotoAdapter);

        // callback xoá ảnh phụ
        extraPhotoAdapter.setOnPhotoRemoveListener(position -> {
            if (position >= 0 && position < extraImageUris.size()) {
                extraImageUris.remove(position);
                updateExtraPhotos();
            }
        });

        txtDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate));
    }

    private void setupListeners() {
        imgPhoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pick);
        });

        btnAddExtraPhotos.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            multiImagePickerLauncher.launch(pick);
        });

        btnSave.setOnClickListener(v -> saveOrUpdateInteraction());

        txtDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                selectedDate = cal.getTime();
                txtDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void bindDataToUI(Interaction interaction) {
        edtNote.setText(interaction.note);
        selectedDate = interaction.date;
        txtDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate));

        if (interaction.photoUri != null) {
            File file = new File(interaction.photoUri);
            if (file.exists()) {
                mainImageUri = Uri.fromFile(file);
            } else {
                mainImageUri = Uri.parse(interaction.photoUri);
            }
            Glide.with(this).load(mainImageUri).into(imgPhoto);
        }

        if (interaction.extraPhotoUris != null) {
            extraImageUris.clear();
            for (String uriStr : interaction.extraPhotoUris) {
                File file = new File(uriStr);
                if (file.exists()) {
                    extraImageUris.add(Uri.fromFile(file));
                } else {
                    extraImageUris.add(Uri.parse(uriStr));
                }
            }
            updateExtraPhotos();
        }
    }

    private void saveOrUpdateInteraction() {
        String type = spinnerType.getSelectedItem().toString();
        String note = edtNote.getText().toString().trim();
        String mainUriStr = (mainImageUri != null) ? mainImageUri.toString() : null;

        List<String> extraUrisStr = new ArrayList<>();
        for (Uri uri : extraImageUris) extraUrisStr.add(uri.toString());

        if (editingInteraction != null) {
            editingInteraction.type = type;
            editingInteraction.note = note;
            editingInteraction.photoUri = mainUriStr;
            editingInteraction.date = selectedDate;
            editingInteraction.extraPhotoUris = extraUrisStr;

            viewModel.update(editingInteraction);
            returnResultAndFinish(editingInteraction);
        } else {
            Interaction newInteraction = new Interaction();
            newInteraction.memberId = memberId;
            newInteraction.type = type;
            newInteraction.note = note;
            newInteraction.photoUri = mainUriStr;
            newInteraction.date = selectedDate;
            newInteraction.extraPhotoUris = extraUrisStr;

            viewModel.insert(newInteraction);
            returnResultAndFinish(newInteraction);
        }
    }

    private void returnResultAndFinish(Interaction interaction) {
        Intent intent = new Intent();
        intent.putExtra("new_interaction", interaction);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateExtraPhotos() {
        List<String> list = new ArrayList<>();
        for (Uri uri : extraImageUris) list.add(uri.toString());
        extraPhotoAdapter.updateData(list);
    }

    private String copyUriToLocalFile(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            File file = new File(getCacheDir(), "photo_" + System.currentTimeMillis() + ".jpg");
            try (OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("AddInteraction", "copyUriToLocalFile error: " + e.getMessage());
            return null;
        }
    }
}
