package com.example.familynoteapp.feture.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.familynoteapp.R;
import com.example.familynoteapp.dao.MemberInteractionCount;
import com.example.familynoteapp.model.FamilyMember;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtTotalInteractions, txtTopMember;
    private DashboardViewModel viewModel;
    private PieChart pieChart; // Đổi BarChart -> PieChart
    private BarChart barChart;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Ánh xạ view
        txtTotalInteractions = findViewById(R.id.txtTotalInteractions);
        txtTopMember = findViewById(R.id.txtTopMember);
        pieChart = findViewById(R.id.pieChart); // Sửa từ View sang PieChart


        int textColor = getColor(R.color.orange); // Hoặc màu text bạn muốn (có thể lấy từ theme)
        pieChart.getLegend().setTextColor(textColor);
        pieChart.getDescription().setTextColor(textColor);
        pieChart.setEntryLabelColor(textColor);

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
        barChart = findViewById(R.id.barChart);

// Quan sát danh sách thành viên từ ViewModel (giả sử đã có hàm)
        viewModel.getAllFamilyMembers().observe(this, members -> {
            Map<Integer, Integer> monthCount = new HashMap<>();

            for (FamilyMember member : members) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(member.birthday));
                    int month = calendar.get(Calendar.MONTH); // 0 = Jan

                    int count = monthCount.getOrDefault(month, 0);
                    monthCount.put(month, count + 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            List<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                int count = monthCount.getOrDefault(i, 0);
                if (count > 0) {
                    entries.add(new BarEntry(i, count));
                }
            }
            BarDataSet dataSet = new BarDataSet(entries, "Số người theo tháng sinh");
            dataSet.setValueTextSize(12f);
            BarData barData = new BarData(dataSet);
            barChart.setData(barData);

            String[] months = {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"};
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(12);
            xAxis.setDrawGridLines(false);
            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setTextColor(Color.DKGRAY);
            yAxisLeft.setTextSize(12f);
            yAxisLeft.setGranularity(1f);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getDescription().setEnabled(false);
            barChart.animateY(1000);
            barChart.invalidate();
        });
    }
}