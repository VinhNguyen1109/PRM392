package com.example.familynoteapp.feture.family;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;

public class AddFamilyMemberActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private EditText edtName, edtRelation, edtBirthday;
    private Uri selectedImageUri = null;
    private FamilyMember editingMember = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(AddFamilyMemberActivity.this).load(selectedImageUri).into(imgAvatar);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);
        onBinding();
        onAction();
    }

    private void onBinding() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtRelation = findViewById(R.id.edtRelation);
        edtBirthday = findViewById(R.id.edtBirthday);

        // Nhận dữ liệu nếu đang chỉnh sửa
        editingMember = (FamilyMember) getIntent().getSerializableExtra("edit_member");
        if (editingMember != null) {
            edtName.setText(editingMember.name);
            edtRelation.setText(editingMember.relationship);
            edtBirthday.setText(editingMember.birthday);

            if (editingMember.photoUri != null) {
                selectedImageUri = Uri.parse(editingMember.photoUri);
                Glide.with(this).load(selectedImageUri).into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_user_placeholder); // ảnh mặc định
            }
        }

    }

    private void onAction() {
        imgAvatar.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickIntent);
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String relation = edtRelation.getText().toString().trim();
            String birthday = edtBirthday.getText().toString().trim();

            if (name.isEmpty() || relation.isEmpty()) {
                edtName.setError("Bắt buộc");
                edtRelation.setError("Bắt buộc");
                return;
            }

            if (editingMember != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận cập nhật")
                        .setMessage("Bạn có chắc muốn cập nhật thông tin người thân này?")
                        .setPositiveButton("Cập nhật", (dialog, which) -> {
                            saveMember(name, relation, birthday);
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            } else {
                saveMember(name, relation, birthday);
            }
        });


    }

    private void saveMember(String name, String relation, String birthday) {
        if (editingMember == null) {
            editingMember = new FamilyMember();
        }

        editingMember.name = name;
        editingMember.relationship = relation;
        editingMember.birthday = birthday;
        editingMember.photoUri = selectedImageUri != null ? selectedImageUri.toString() : "";

        if (editingMember.id == 0) {
            AppDatabaseSingleton.getInstance(getApplicationContext())
                    .familyMemberDao().insert(editingMember);
        } else {
            AppDatabaseSingleton.getInstance(getApplicationContext())
                    .familyMemberDao().update(editingMember);
        }

        setResult(RESULT_OK);
        finish();
    }

}
