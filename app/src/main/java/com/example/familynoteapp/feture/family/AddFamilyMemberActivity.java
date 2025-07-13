package com.example.familynoteapp.feture.family;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.util.Converters;

import java.io.File;

public class AddFamilyMemberActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private EditText edtName, edtRelation, edtBirthday;

    private Uri selectedImageUri = null; // URI file local hoặc fallback URI gốc
    private String selectedImageLocalPath = null; // Đường dẫn file local trong internal storage

    private FamilyMember editingMember = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            // Copy ảnh từ URI gốc sang internal storage và lấy đường dẫn file local
                            String localPath = Converters.saveImageToInternalStorage(
                                    AddFamilyMemberActivity.this,
                                    uri,
                                    "member_" + System.currentTimeMillis() + ".jpg"
                            );
                            if (localPath != null) {
                                selectedImageLocalPath = localPath;
                                selectedImageUri = Uri.fromFile(new File(localPath));
                                Glide.with(AddFamilyMemberActivity.this).load(selectedImageUri).into(imgAvatar);
                            } else {
                                // Nếu copy thất bại, fallback load URI gốc (có thể lỗi permission)
                                selectedImageUri = uri;
                                Glide.with(AddFamilyMemberActivity.this).load(uri).into(imgAvatar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Fallback load URI gốc
                            selectedImageUri = uri;
                            Glide.with(AddFamilyMemberActivity.this).load(uri).into(imgAvatar);
                        }
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

        // Lấy dữ liệu chỉnh sửa nếu có
        editingMember = (FamilyMember) getIntent().getSerializableExtra("edit_member");
        if (editingMember != null) {
            edtName.setText(editingMember.name);
            edtRelation.setText(editingMember.relationship);
            edtBirthday.setText(editingMember.birthday);

            if (editingMember.photoUri != null && !editingMember.photoUri.isEmpty()) {
                File localFile = new File(editingMember.photoUri);
                if (localFile.exists()) {
                    selectedImageLocalPath = editingMember.photoUri;
                    selectedImageUri = Uri.fromFile(localFile);
                    Glide.with(this).load(selectedImageUri).into(imgAvatar);
                } else {
                    // Nếu đường dẫn không phải file local thì fallback load URI
                    try {
                        selectedImageUri = Uri.parse(editingMember.photoUri);
                        Glide.with(this).load(selectedImageUri).into(imgAvatar);
                    } catch (Exception e) {
                        imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
                    }
                }
            } else {
                imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
        }
    }

    private void onAction() {
        imgAvatar.setOnClickListener(v -> {
            // Mở picker chọn ảnh
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickIntent);
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String relation = edtRelation.getText().toString().trim();
            String birthday = edtBirthday.getText().toString().trim();

            if (name.isEmpty() || relation.isEmpty()) {
                if (name.isEmpty()) edtName.setError("Bắt buộc");
                if (relation.isEmpty()) edtRelation.setError("Bắt buộc");
                return;
            }

            if (editingMember != null && editingMember.id != 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận cập nhật")
                        .setMessage("Bạn có chắc muốn cập nhật thông tin người thân này?")
                        .setPositiveButton("Cập nhật", (dialog, which) -> saveMember(name, relation, birthday))
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

        // Ưu tiên lưu đường dẫn file local, nếu không có thì lưu URI gốc
        if (selectedImageLocalPath != null && !selectedImageLocalPath.isEmpty()) {
            editingMember.photoUri = selectedImageLocalPath;
        } else if (selectedImageUri != null) {
            editingMember.photoUri = selectedImageUri.toString();
        } else {
            editingMember.photoUri = "";
        }

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
