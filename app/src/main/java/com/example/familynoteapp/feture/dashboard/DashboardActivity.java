package com.example.familynoteapp.feture.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.familynoteapp.R;
import com.example.familynoteapp.dao.MemberInteractionCount;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtTotalInteractions, txtTopMember;
    private DashboardViewModel viewModel;
    private PieChart pieChart; // Đổi BarChart -> PieChart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Ánh xạ view
        txtTotalInteractions = findViewById(R.id.txtTotalInteractions);
        txtTopMember = findViewById(R.id.txtTopMember);
        pieChart = findViewById(R.id.pieChart); // Sửa từ View sang PieChart

        // ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Tổng số tương tác
        viewModel.getTotalInteractionCount().observe(this, count ->
                txtTotalInteractions.setText("Tổng số tương tác: " + count)
        );

        // Người tương tác nhiều nhất
        viewModel.getTopInteractedMemberName().observe(this, name ->
                txtTopMember.setText("Người tương tác nhiều nhất: " + name)
        );

        // Biểu đồ tròn dữ liệu thực tế
        viewModel.getInteractionCountsPerMember().observe(this, data -> {
            List<PieEntry> entries = new ArrayList<>();
            for (MemberInteractionCount item : data) {
                entries.add(new PieEntry(item.count, item.name));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Tương tác theo người thân");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData pieData = new PieData(dataSet);
            pieData.setValueTextSize(14f);
            pieData.setValueTextColor(Color.BLACK);

            pieChart.setUsePercentValues(true);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setEntryLabelTextSize(12f);
            pieChart.animateY(1000);
            pieChart.invalidate(); // Refresh lại biểu đồ
        });
    }
}
