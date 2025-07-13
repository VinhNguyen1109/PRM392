package com.example.familynoteapp.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.familynoteapp.MainActivity;
import com.example.familynoteapp.R;
import com.example.familynoteapp.feture.dashboard.DashboardActivity;
import com.example.familynoteapp.feture.schedule.ScheduleActivity;
import com.example.familynoteapp.feture.theme.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavFragment extends Fragment {

    public BottomNavFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        BottomNavigationView bottomNavigation = view.findViewById(R.id.bottomNavigation);

        // Gán sự kiện chọn tab
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Context context = requireContext();

            if (id == R.id.nav_home) {
                if (!(context instanceof MainActivity)) {
                    context.startActivity(new Intent(context, MainActivity.class));
                }
                return true;
            } else if (id == R.id.nav_dashboard) {
                if (!(context instanceof DashboardActivity)) {
                    context.startActivity(new Intent(context, DashboardActivity.class));
                }
                return true;
            } else if (id == R.id.nav_schedule) {
                if (!(context instanceof ScheduleActivity)) {
                    context.startActivity(new Intent(context, ScheduleActivity.class));
                }
                return true;
            }else if (id == R.id.nav_settings) {
                if (!(context instanceof SettingsActivity)) {
                    context.startActivity(new Intent(context, SettingsActivity.class));
                }
                return true;
            }

            return false;
        });

        // 👉 Đặt tab đang hoạt động được hover
        if (getActivity() instanceof MainActivity) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else if (getActivity() instanceof DashboardActivity) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        } else if (getActivity() instanceof ScheduleActivity) {
            bottomNavigation.setSelectedItemId(R.id.nav_schedule);
        }else if (getActivity() instanceof SettingsActivity) {
            bottomNavigation.setSelectedItemId(R.id.nav_settings);
        }

    }

}
