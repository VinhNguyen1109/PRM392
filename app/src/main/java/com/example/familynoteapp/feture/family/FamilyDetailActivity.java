package com.example.familynoteapp.feture.family;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.feture.dashboard.DashboardActivity;
import com.example.familynoteapp.feture.interaction.AddInteractionActivity;
import com.example.familynoteapp.feture.interaction.InteractionAdapter;
import com.example.familynoteapp.feture.interaction.InteractionViewModel;
import com.example.familynoteapp.model.FamilyMember;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FamilyDetailActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtName, txtRelation;
    private RecyclerView recyclerInteractions;
//    private BottomNavigationView bottomNavigation;

    private InteractionAdapter adapter;
    private InteractionViewModel viewModel;
    private FamilyMember member;

    private FloatingActionButton btnAddInteraction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_detail);

        int memberId = getIntent().getIntExtra("member_id", -1);
        if (memberId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người thân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        onBinding();

        viewModel = new ViewModelProvider(this).get(InteractionViewModel.class);

        // Lấy dữ liệu member từ DB
        viewModel.getMemberById(memberId).observe(this, result -> {
            if (result == null) {
                Toast.makeText(this, "Không tìm thấy người thân", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                member = result;
                bindMemberInfo();
            }
        });

        onAction();
    }

    private void onBinding() {
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtRelation = findViewById(R.id.txtRelation);
        recyclerInteractions = findViewById(R.id.recyclerInteractions);
//        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnAddInteraction = findViewById(R.id.btnAddInteraction);
        adapter = new InteractionAdapter(this, new ViewModelProvider(this).get(InteractionViewModel.class));
        recyclerInteractions.setLayoutManager(new LinearLayoutManager(this));
        recyclerInteractions.setAdapter(adapter);
    }

    private void bindMemberInfo() {
        txtName.setText(member.name);
        txtRelation.setText(member.relationship);

        Glide.with(this)
                .load(member.photoUri)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imgAvatar);

        viewModel.getInteractionsForMember(member.id).observe(this, list -> {
            adapter.submitList(list);
        });

        Log.d("FamilyDetailDebug", "Đã bind member: " + member.name);
    }

    private void onAction() {

        btnAddInteraction.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddInteractionActivity.class);
            intent.putExtra("memberId", member.id);
            startActivity(intent);
        });


//        bottomNavigation.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (member == null) return false;
//            if (itemId == R.id.nav_home) {
//                Intent backToMain = new Intent(this, com.example.familynoteapp.MainActivity.class);
//                backToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(backToMain);
//                return true;
//            } else if (itemId == R.id.nav_dashboard) {
//                Intent dashboardIntent = new Intent(this, DashboardActivity.class);
//                startActivity(dashboardIntent);
//                return true;
//            }
//
//            return false;
//        });
//
//        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}
