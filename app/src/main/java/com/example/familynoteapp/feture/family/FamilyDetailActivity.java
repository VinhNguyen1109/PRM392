package com.example.familynoteapp.feture.family;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.MainViewModel;
import com.example.familynoteapp.R;
import com.example.familynoteapp.feture.interaction.AddInteractionActivity;
import com.example.familynoteapp.feture.interaction.InteractionAdapter;
import com.example.familynoteapp.feture.interaction.InteractionViewModel;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.model.Interaction;
import com.example.familynoteapp.util.Converters;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.*;

public class FamilyDetailActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtName, txtRelation, txtDateFilter, btnClearFilter;
    private SearchView searchView;
    private RecyclerView recyclerInteractions;
    private FloatingActionButton btnAddInteraction;

    private InteractionAdapter interactionAdapter;
    private InteractionViewModel interactionViewModel;
    private MainViewModel mainViewModel;
    private FamilyMember member;

    private final List<Interaction> allInteractions = new ArrayList<>();
    private String keyword = "", selectedType = "", selectedDate = "";

    private ActivityResultLauncher<Intent> addInteractionLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_detail);

        initViews();
        initViewModels();
        setupRecyclerView();
        setupAddInteractionLauncher();
        setupPickImageLauncher();
        setupListeners();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtRelation = findViewById(R.id.txtRelation);
        txtDateFilter = findViewById(R.id.txtDateFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        searchView = findViewById(R.id.searchView);
        recyclerInteractions = findViewById(R.id.recyclerInteractions);
        btnAddInteraction = findViewById(R.id.btnAddInteraction);
    }

    private void initViewModels() {
        interactionViewModel = new ViewModelProvider(this).get(InteractionViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        int memberId = getIntent().getIntExtra("member_id", -1);
        if (memberId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người thân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        interactionViewModel.getMemberById(memberId).observe(this, m -> {
            if (m != null) {
                member = m;
                txtName.setText(member.name);
                txtRelation.setText(member.relationship);
                loadMemberAvatar();
            }
        });

        interactionViewModel.getInteractionsForMember(memberId).observe(this, list -> {
            allInteractions.clear();
            if (list != null) allInteractions.addAll(list);
            applyFilters();
        });
    }

    private void loadMemberAvatar() {
        if (member.photoUri == null || member.photoUri.isEmpty()) {
            imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
            return;
        }

        Log.d("vinhnc", "Loading photoUri: " + member.photoUri);

        try {
            if (member.photoUri.startsWith("/")) {
                Glide.with(this).load(new File(member.photoUri)).into(imgAvatar);
            } else if (member.photoUri.startsWith("content://")) {
                if (member.photoUri.contains("com.google.android.apps.photos.contentprovider")) {
                    Glide.with(this).load(Uri.parse(member.photoUri)).into(imgAvatar);
                } else {
                    String localPath = Converters.saveImageToInternalStorage(
                            this, Uri.parse(member.photoUri),
                            "member_" + member.id + "_" + System.currentTimeMillis() + ".jpg");
                    if (localPath != null) {
                        member.photoUri = localPath;
                        mainViewModel.updateMember(member);
                        Glide.with(this).load(new File(localPath)).into(imgAvatar);
                    } else {
                        Glide.with(this).load(Uri.parse(member.photoUri)).into(imgAvatar);
                    }
                }
            } else {
                Glide.with(this).load(member.photoUri).into(imgAvatar);
            }
        } catch (Exception e) {
            Log.e("vinhnc", "Error loading avatar", e);
            imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
        }
    }

    private void setupRecyclerView() {
        interactionAdapter = new InteractionAdapter(this, interactionViewModel);
        recyclerInteractions.setLayoutManager(new LinearLayoutManager(this));
        recyclerInteractions.setAdapter(interactionAdapter);
    }

    private void setupAddInteractionLauncher() {
        addInteractionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int newId = result.getData().getIntExtra("interaction_id", -1);
                        if (newId != -1) {
                            // Luôn lấy lại từ DB để chắc chắn có ảnh mới hoặc dữ liệu mới
                            interactionViewModel.getInteractionById(newId).observe(this, newItem -> {
                                if (newItem != null) {
                                    boolean updated = false;
                                    for (int i = 0; i < allInteractions.size(); i++) {
                                        if (allInteractions.get(i).id == newItem.id) {
                                            allInteractions.set(i, newItem);
                                            updated = true;
                                            break;
                                        }
                                    }
                                    if (!updated) {
                                        allInteractions.add(0, newItem); // thêm mới đầu danh sách
                                    }
                                    applyFilters();
                                }
                            });
                        }
                    }
                }
        );
    }

    private void setupPickImageLauncher() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;

                    try {
                        if (uri.toString().contains("com.google.android.apps.photos.contentprovider")) {
                            member.photoUri = uri.toString();
                            mainViewModel.updateMember(member);
                            Glide.with(this).load(uri).into(imgAvatar);
                        } else {
                            String localPath = Converters.saveImageToInternalStorage(
                                    this, uri, "member_" + member.id + "_" + System.currentTimeMillis() + ".jpg");
                            if (localPath != null) {
                                member.photoUri = localPath;
                                mainViewModel.updateMember(member);
                                Glide.with(this).load(new File(localPath)).into(imgAvatar);
                            } else {
                                Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("vinhnc", "Error picking image", e);
                        Toast.makeText(this, "Có lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        btnAddInteraction.setOnClickListener(v -> {
            if (member != null) {
                Intent intent = new Intent(this, AddInteractionActivity.class);
                intent.putExtra("memberId", member.id);
                addInteractionLauncher.launch(intent);
            }
        });

        imgAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                keyword = query;
                applyFilters();
                return true;
            }
            public boolean onQueryTextChange(String newText) {
                keyword = newText;
                applyFilters();
                return true;
            }
        });

        txtDateFilter.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                txtDateFilter.setText(selectedDate);
                applyFilters();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnClearFilter.setOnClickListener(v -> {
            keyword = selectedType = selectedDate = "";
            searchView.setQuery("", false);
            txtDateFilter.setText("Chọn ngày");
            applyFilters();
        });
    }

    private void applyFilters() {
        List<Interaction> filtered = new ArrayList<>();
        for (Interaction item : allInteractions) {
            boolean matches = (keyword.isEmpty() || (item.note != null && item.note.toLowerCase().contains(keyword.toLowerCase())))
                    && (selectedType.isEmpty() || selectedType.equals(item.type))
                    && (selectedDate.isEmpty() || selectedDate.equals(item.date));
            if (matches) filtered.add(item);
        }
        interactionAdapter.submitList(filtered);
    }
}
