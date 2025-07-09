package com.example.familynoteapp.feture.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.familynoteapp.R;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.ScheduleTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ScheduleViewModel viewModel;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recyclerViewSchedule);
        fabAdd = findViewById(R.id.fabAddSchedule);

        adapter = new ScheduleAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        viewModel.getAllSchedules().observe(this, schedules -> adapter.submitList(schedules));

        AppDatabaseSingleton.getInstance(this).familyMemberDao().getAll().observe(this, members -> {
            adapter.setFamilyMembers(members);
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditScheduleActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemLongClickListener(task -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn muốn chỉnh sửa hay xoá lịch trình này?")
                    .setPositiveButton("Sửa", (dialog, which) -> {
                        Intent intent = new Intent(ScheduleActivity.this, AddEditScheduleActivity.class);
                        intent.putExtra("schedule_task", task);
                        startActivity(intent);
                    })
                    .setNegativeButton("Xoá", (dialog, which) -> {
                        viewModel.delete(task);
                        Toast.makeText(this, "Đã xoá lịch trình", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Huỷ", null)
                    .show();
        });
    }
}
