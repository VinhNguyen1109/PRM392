package com.example.familynoteapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

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
    private SearchView searchView;

    private final ActivityResultLauncher<Intent> addMemberLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            refreshFamilyList();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startScheduleService();
        initViewModel();
        bindViews();
        setupRecyclerView();
        setupActions();
        observeFamilyList();
    }

    private void startScheduleService() {
        Intent intent = new Intent(this, ScheduleForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MainActivity", "Starting ScheduleForegroundService in foreground");
            startForegroundService(intent);
        } else {
            Log.d("MainActivity", "Starting ScheduleForegroundService in background");
            startService(intent);
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void bindViews() {
        faAdd = findViewById(R.id.fabAdd);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        adapter = new FamilyAdapter(this, viewModel);
        adapter.setOnItemActionListener(new FamilyAdapter.OnItemActionListener() {
            @Override
            public void onEditClicked(FamilyMember member) {
                editMember(member);
            }

            @Override
            public void onDeleteClicked(FamilyMember member) {
                confirmDeleteMember(member);
            }

            @Override
            public void onItemClicked(FamilyMember member) {
                openMemberDetail(member);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupActions() {
        faAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFamilyMemberActivity.class);
            addMemberLauncher.launch(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFamilyList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFamilyList(newText);
                return true;
            }
        });
    }

    private void observeFamilyList() {
        viewModel.getAllFamily().observe(this, list -> {
            adapter.submitList(new ArrayList<>(list));
        });
    }

    private void refreshFamilyList() {
        viewModel.getAllFamily().observe(this, list -> {
            adapter.submitList(new ArrayList<>(list));
        });
    }

    private void filterFamilyList(String keyword) {
        viewModel.getAllFamily().observe(this, list -> {
            if (list == null) return;
            ArrayList<FamilyMember> filtered = new ArrayList<>();
            for (FamilyMember member : list) {
                if (member.name.toLowerCase().contains(keyword.toLowerCase())
                        || member.relationship.toLowerCase().contains(keyword.toLowerCase())) {
                    filtered.add(member);
                }
            }
            adapter.submitList(filtered);
        });
    }

    private void openMemberDetail(FamilyMember member) {
        if (member == null) {
            Log.e("MainActivity", "onItemClicked: member is null");
            return;
        }
        Intent intent = new Intent(this, FamilyDetailActivity.class);
        intent.putExtra("member_id", member.id);
        startActivity(intent);
    }

    private void editMember(FamilyMember member) {
        Intent intent = new Intent(this, AddFamilyMemberActivity.class);
        intent.putExtra("edit_member", member);
        startActivity(intent);
    }

    private void confirmDeleteMember(FamilyMember member) {
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
}
