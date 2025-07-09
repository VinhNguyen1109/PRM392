package com.example.familynoteapp.feture.interaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddInteractionActivity extends AppCompatActivity {

    private Spinner spinnerType;
    private EditText edtNote, edtCustomType;
    private ImageView imgPhoto;
    private TextView txtDate;
    private Button btnSave;

    private Uri selectedImageUri = null;
    private Date selectedDate = new Date();
    private final Calendar calendar = Calendar.getInstance();
    private int memberId;
    private Interaction editingInteraction;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(imgPhoto);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interaction);

        editingInteraction = (Interaction) getIntent().getSerializableExtra("edit_interaction");

        if (editingInteraction != null) {
            memberId = editingInteraction.memberId;
        } else {
            memberId = getIntent().getIntExtra("memberId", -1);
        }

        if (memberId == -1) {
            Toast.makeText(this, "Không tìm thấy người thân để ghi tương tác", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        onBinding();
        onAction();
    }

    private void onBinding() {
        spinnerType = findViewById(R.id.spinnerType);
        edtNote = findViewById(R.id.edtNote);
        edtCustomType = findViewById(R.id.edtCustomType);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtDate = findViewById(R.id.txtDate);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerType.getSelectedItem().toString();
                if (selected.equalsIgnoreCase("Khác")) {
                    edtCustomType.setVisibility(View.VISIBLE);
                } else {
                    edtCustomType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                edtCustomType.setVisibility(View.GONE);
            }
        });

        updateDateText();

        if (editingInteraction != null) {
            spinnerType.setSelection(getSpinnerIndex(spinnerType, editingInteraction.type));
            edtNote.setText(editingInteraction.note);
            selectedDate = editingInteraction.date;
            updateDateText();
            if (editingInteraction.photoUri != null) {
                selectedImageUri = Uri.parse(editingInteraction.photoUri);
                Glide.with(this).load(selectedImageUri).into(imgPhoto);
            }
        }
    }

    private void onAction() {
        txtDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        imgPhoto.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickImage);
        });

        btnSave.setOnClickListener(v -> {
            String selectedType = spinnerType.getSelectedItem().toString();
            String typeToSave = selectedType;

            if (selectedType.equalsIgnoreCase("Khác")) {
                typeToSave = edtCustomType.getText().toString().trim();
                if (typeToSave.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập loại tương tác tùy chọn", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            final String finalType = typeToSave;
            String note = edtNote.getText().toString().trim();
            String photoUri = selectedImageUri != null ? selectedImageUri.toString() : null;

            InteractionViewModel viewModel = new ViewModelProvider(this).get(InteractionViewModel.class);

            if (editingInteraction != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận cập nhật")
                        .setMessage("Bạn có chắc muốn cập nhật tương tác này?")
                        .setPositiveButton("Cập nhật", (dialog, which) -> {
                            editingInteraction.type = finalType;
                            editingInteraction.note = note;
                            editingInteraction.photoUri = photoUri;
                            editingInteraction.date = selectedDate;

                            viewModel.update(editingInteraction);
                            Toast.makeText(this, "Đã cập nhật tương tác", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            } else {
                Interaction interaction = new Interaction();
                interaction.memberId = memberId;
                interaction.type = finalType;
                interaction.note = note;
                interaction.photoUri = photoUri;
                interaction.date = selectedDate;

                viewModel.insert(interaction);
                Toast.makeText(this, "Đã ghi tương tác", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate = calendar.getTime();
        updateDateText();
    };

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDate.setText(sdf.format(selectedDate));
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
