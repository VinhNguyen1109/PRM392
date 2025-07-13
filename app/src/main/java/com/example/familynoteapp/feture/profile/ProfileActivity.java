package com.example.familynoteapp.feture.profile;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familynoteapp.R;

import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave, btnAddField;
    private LinearLayout dynamicFieldsContainer;
    private SharedPreferences sharedPreferences;

    // Lưu label và EditText động
    private ArrayList<String> fieldLabels = new ArrayList<>();
    private ArrayList<EditText> fieldValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnAddField = findViewById(R.id.btnAddField);
        dynamicFieldsContainer = findViewById(R.id.dynamicFieldsContainer);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        loadProfile();

        btnAddField.setOnClickListener(v -> showAddFieldDialog());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void showAddFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập tên trường");

        final EditText input = new EditText(this);
        input.setHint("Ví dụ: Mật khẩu email, Facebook...");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String fieldName = input.getText().toString().trim();
            if (!fieldName.isEmpty()) {
                addNewField(fieldName, null);
            } else {
                Toast.makeText(this, "Tên trường thông tin không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addNewField(String label, String value) {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextSize(16f);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackgroundColor(0x00000000);

        EditText edtValue = new EditText(this);
        edtValue.setHint("Nhập " + label);
        edtValue.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        if (value != null) edtValue.setText(value);
        // Khởi tạo mặc định là ẩn
        edtValue.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ImageView ivToggle = new ImageView(this);
        ivToggle.setImageResource(R.drawable.ic_eye_closed);
        ivToggle.setPadding(16, 16, 16, 16);

        final boolean[] isHidden = {true};
        ivToggle.setOnClickListener(v -> {
            if (isHidden[0]) {
                edtValue.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggle.setImageResource(R.drawable.ic_eye_open);
            } else {
                edtValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggle.setImageResource(R.drawable.ic_eye_closed);
            }
            isHidden[0] = !isHidden[0];
            edtValue.setSelection(edtValue.length());
        });

        LinearLayout valueLayout = new LinearLayout(this);
        valueLayout.setOrientation(LinearLayout.HORIZONTAL);
        valueLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        valueLayout.addView(edtValue);
        valueLayout.addView(ivToggle);

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá thông tin \"" + label + "\"?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        dynamicFieldsContainer.removeView(headerLayout);
                        dynamicFieldsContainer.removeView(valueLayout);
                        fieldLabels.remove(label);
                        fieldValues.remove(edtValue);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        headerLayout.addView(tvLabel);
        headerLayout.addView(btnDelete);

        dynamicFieldsContainer.addView(headerLayout);
        dynamicFieldsContainer.addView(valueLayout);

        fieldLabels.add(label);
        fieldValues.add(edtValue);
    }

    private void loadProfile() {
        edtName.setText(sharedPreferences.getString("name", ""));
        edtEmail.setText(sharedPreferences.getString("email", ""));
        edtPhone.setText(sharedPreferences.getString("phone", ""));

        int extraCount = sharedPreferences.getInt("extra_count", 0);
        for (int i = 0; i < extraCount; i++) {
            String label = sharedPreferences.getString("extra_label_" + i, "");
            String value = sharedPreferences.getString("extra_value_" + i, "");
            if (!label.isEmpty()) addNewField(label, value);
        }
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", edtName.getText().toString().trim());
        editor.putString("email", edtEmail.getText().toString().trim());
        editor.putString("phone", edtPhone.getText().toString().trim());

        editor.putInt("extra_count", fieldLabels.size());
        for (int i = 0; i < fieldLabels.size(); i++) {
            editor.putString("extra_label_" + i, fieldLabels.get(i));
            editor.putString("extra_value_" + i, fieldValues.get(i).getText().toString().trim());
        }

        editor.apply();
        Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
    }
}
