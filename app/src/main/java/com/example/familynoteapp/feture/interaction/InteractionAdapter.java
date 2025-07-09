package com.example.familynoteapp.feture.interaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familynoteapp.R;
import com.example.familynoteapp.model.Interaction;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class InteractionAdapter extends ListAdapter<Interaction, InteractionAdapter.InteractionViewHolder> {

    private final Context context;
    private final InteractionViewModel viewModel;

    public InteractionAdapter(Context context, InteractionViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.viewModel = viewModel;
    }

    private static final DiffUtil.ItemCallback<Interaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Interaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Interaction oldItem, @NonNull Interaction newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Interaction oldItem, @NonNull Interaction newItem) {
            return oldItem.id == newItem.id &&
                    oldItem.type.equals(newItem.type) &&
                    oldItem.note.equals(newItem.note) &&
                    String.valueOf(oldItem.photoUri).equals(String.valueOf(newItem.photoUri)) &&
                    oldItem.date.equals(newItem.date);
        }
    };

    @NonNull
    @Override
    public InteractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interaction, parent, false);
        return new InteractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InteractionViewHolder holder, int position) {
        Interaction interaction = getItem(position);

        holder.txtType.setText("Loại: " + interaction.type);
        holder.txtNote.setText("Ghi chú: " + interaction.note);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtDate.setText("Ngày: " + sdf.format(interaction.date));

        if (interaction.photoUri != null) {
            Glide.with(holder.itemView.getContext())
                    .load(interaction.photoUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InteractionDetailActivity.class);
            intent.putExtra("interaction_detail", interaction);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showInteractionOptions(interaction);
            return true;
        });
    }

    private void showInteractionOptions(Interaction interaction) {
        new AlertDialog.Builder(context)
                .setTitle("Tùy chọn tương tác")
                .setItems(new CharSequence[]{"Sửa", "Xoá"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent editIntent = new Intent(context, AddInteractionActivity.class);
                        editIntent.putExtra("edit_interaction", interaction);
                        context.startActivity(editIntent);
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Xác nhận xoá")
                                .setMessage("Bạn có chắc chắn muốn xoá tương tác này?")
                                .setPositiveButton("Xoá", (d, w) -> {
                                    viewModel.delete(interaction);
                                    Toast.makeText(context, "Đã xoá tương tác", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Huỷ", null)
                                .show();
                    }
                })
                .show();
    }


    static class InteractionViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtNote, txtDate;
        ImageView imgPhoto;

        public InteractionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtType);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
