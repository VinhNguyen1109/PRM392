package com.example.familynoteapp.feture.schedule;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familynoteapp.R;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.model.ScheduleTask;

import java.util.List;

public class ScheduleAdapter extends ListAdapter<ScheduleTask, ScheduleAdapter.ScheduleViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(ScheduleTask task);
    }

    private OnItemLongClickListener longClickListener;
    private List<FamilyMember> members;

    public ScheduleAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setFamilyMembers(List<FamilyMember> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleTask task = getItem(position);
        holder.bind(task);
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle, txtDescription, txtTime, txtStatus, txtMemberName;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtMemberName = itemView.findViewById(R.id.txtMemberName);
        }

        public void bind(ScheduleTask task) {
            txtTitle.setText(task.getTitle());
            txtDescription.setText(task.getDescription());
            txtTime.setText(task.getDateTime());

            FamilyMember member = findMemberById(task.getMemberId());
            if (member != null) {
                txtMemberName.setText("Người thân: " + member.name);
            } else {
                txtMemberName.setText("Người thân: Không xác định");
            }

            if (task.isCompleted()) {
                txtStatus.setText("Trạng thái: Đã hoàn thành");
                txtStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                txtStatus.setText("Trạng thái: Chưa hoàn thành");
                txtStatus.setTextColor(Color.parseColor("#FF5722"));
            }

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) longClickListener.onItemLongClick(task);
                return true;
            });
        }

        private FamilyMember findMemberById(int id) {
            if (members == null) return null;
            for (FamilyMember m : members) {
                if (m.id == id) return m;
            }
            return null;
        }
    }

    public static final DiffUtil.ItemCallback<ScheduleTask> DIFF_CALLBACK = new DiffUtil.ItemCallback<ScheduleTask>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScheduleTask oldItem, @NonNull ScheduleTask newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScheduleTask oldItem, @NonNull ScheduleTask newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getDateTime().equals(newItem.getDateTime()) &&
                    oldItem.isCompleted() == newItem.isCompleted() &&
                    oldItem.getMemberId() == newItem.getMemberId();
        }
    };
}
