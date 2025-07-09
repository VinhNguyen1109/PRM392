package com.example.familynoteapp.feture.family;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.familynoteapp.MainViewModel;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.FamilyMember;
import java.util.Objects;

public class FamilyAdapter extends ListAdapter<FamilyMember, FamilyAdapter.FamilyViewHolder> {
    private final Context context;
    private final MainViewModel viewModel;
    private OnItemActionListener listener;

    public FamilyAdapter(Context context, MainViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.viewModel = viewModel;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    public interface OnItemActionListener {
        void onEditClicked(FamilyMember member);
        void onDeleteClicked(FamilyMember member);
        void onItemClicked(FamilyMember member);
    }

    private static final DiffUtil.ItemCallback<FamilyMember> DIFF_CALLBACK = new DiffUtil.ItemCallback<FamilyMember>() {
        @Override
        public boolean areItemsTheSame(@NonNull FamilyMember oldItem, @NonNull FamilyMember newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FamilyMember oldItem, @NonNull FamilyMember newItem) {
            return oldItem.id == newItem.id &&
                    Objects.equals(oldItem.name, newItem.name) &&
                    Objects.equals(oldItem.relationship, newItem.relationship) &&
                    Objects.equals(oldItem.birthday, newItem.birthday) &&
                    Objects.equals(oldItem.photoUri, newItem.photoUri);
        }
    };

    @NonNull
    @Override
    public FamilyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family_member, parent, false);
        return new FamilyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyViewHolder holder, int position) {
        FamilyMember member = getItem(position);
        if (member == null) return;

        holder.txtName.setText(member.name);
        holder.txtRelation.setText(member.relationship);

        Glide.with(context)
                .load(member.photoUri)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(holder.imgAvatar);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(member);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(member);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                Log.e("FamilyAdapter", "chuyen huong");
                listener.onItemClicked(member);
            } else {
                Log.e("FamilyAdapter", "OnItemActionListener is null. Cannot handle item click.");
            }
        });

    }

    static class FamilyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtRelation;
        ImageView btnEdit, btnDelete;

        public FamilyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtRelation = itemView.findViewById(R.id.txtRelation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
