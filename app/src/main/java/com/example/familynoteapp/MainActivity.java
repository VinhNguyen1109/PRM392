package com.example.familynoteapp;



import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familynoteapp.feture.family.AddFamilyMemberActivity;
import com.example.familynoteapp.feture.family.FamilyAdapter;
import com.example.familynoteapp.feture.family.FamilyDetailActivity;
import com.example.familynoteapp.feture.schedule.service.ScheduleForegroundService;
import com.example.familynoteapp.model.FamilyMember;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FamilyAdapter adapter;
    private MainViewModel viewModel;

    private FloatingActionButton faAdd;

//    private BottomNavigationView bottomNav;
    private final ActivityResultLauncher<Intent> addMemberLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    viewModel.getAllFamily().observe(this,
                            list -> adapter.submitList(new ArrayList<>(list)));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ScheduleForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("vinhnc", "Starting ScheduleForegroundService in foreground");
            startForegroundService(intent);
        } else {
            Log.d("vinhnc", "Starting ScheduleForegroundService in background 2");
            startService(intent);
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        onBinding();
        onAction();
    }

    private void onBinding() {
        faAdd = findViewById(R.id.fabAdd);
//        bottomNav = findViewById(R.id.bottomNavigation);
        adapter = new FamilyAdapter(this, viewModel);
        adapter.setOnItemActionListener(new FamilyAdapter.OnItemActionListener() {
            @Override
            public void onEditClicked(FamilyMember member) {
                showEditConfirmation(member);
            }

            @Override
            public void onDeleteClicked(FamilyMember member) {
                showDeleteConfirmation(member);
            }

            @Override
            public void onItemClicked(FamilyMember member) {
                if (member == null) {
                    Log.e("MainActivity", "onItemClicked: member is null");
                    return;
                }

                Log.d("MainActivity", "onItemClicked: " + member.name);

                Intent intent = new Intent(MainActivity.this, FamilyDetailActivity.class);
                intent.putExtra("member_id", member.id);  // chỉ gửi ID
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel.getAllFamily().observe(this,
                list -> adapter.submitList(new ArrayList<>(list)));
    }

    private void onAction() {
        faAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFamilyMemberActivity.class);
            addMemberLauncher.launch(intent);
        });
    }

    private void showEditConfirmation(FamilyMember member) {
        Intent intent = new Intent(this, AddFamilyMemberActivity.class);
        intent.putExtra("edit_member", member);
        startActivity(intent);
    }

    private void showDeleteConfirmation(FamilyMember member) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá người thân này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    viewModel.deleteMember(member);
                    Toast.makeText(this, "Đã xoá: " + member.name, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "✅ Đã được cấp quyền POST_NOTIFICATIONS");
            } else {
                Log.d("Permission", "❌ Người dùng từ chối quyền POST_NOTIFICATIONS");
            }
        }
    }

}
