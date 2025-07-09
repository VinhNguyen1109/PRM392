package com.example.familynoteapp.feture.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.familynoteapp.MainViewModel;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.model.ScheduleTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditScheduleActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private TextView txtDateTime;
    private CheckBox chkCompleted;
    private Spinner spinnerMembers;
    private Button btnSave;
    private ScheduleViewModel viewModel;
    private MainViewModel mainViewModel;
    private ScheduleTask editingTask;
    private List<FamilyMember> members;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_schedule);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        txtDateTime = findViewById(R.id.txtDateTime);
        chkCompleted = findViewById(R.id.chkCompleted);
        btnSave = findViewById(R.id.btnSaveSchedule);
        spinnerMembers = findViewById(R.id.spinnerMembers);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getAllFamily().observe(this, familyMembers -> {
            members = familyMembers;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            for (FamilyMember member : members) {
                adapter.add(member.name);
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMembers.setAdapter(adapter);
        });

        txtDateTime.setOnClickListener(v -> {
            DatePickerDialog dateDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    txtDateTime.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dateDialog.show();
        });

        editingTask = (ScheduleTask) getIntent().getSerializableExtra("schedule_task");
        if (editingTask != null) {
            edtTitle.setText(editingTask.getTitle());
            edtDescription.setText(editingTask.getDescription());
            txtDateTime.setText(editingTask.getDateTime());
            chkCompleted.setChecked(editingTask.isCompleted());
        }

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();
            String dateTime = txtDateTime.getText().toString().trim();
            boolean completed = chkCompleted.isChecked();

            int position = spinnerMembers.getSelectedItemPosition();
            int memberId = members.get(position).id;

            if (editingTask != null) {
                editingTask.setTitle(title);
                editingTask.setDescription(desc);
                editingTask.setDateTime(dateTime);
                editingTask.setCompleted(completed);
                editingTask.setMemberId(memberId);
                viewModel.update(editingTask);
            } else {
                ScheduleTask newTask = new ScheduleTask();
                newTask.setTitle(title);
                newTask.setDescription(desc);
                newTask.setDateTime(dateTime);
                newTask.setCompleted(completed);
                newTask.setMemberId(memberId);
                viewModel.insert(newTask);
            }

            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
