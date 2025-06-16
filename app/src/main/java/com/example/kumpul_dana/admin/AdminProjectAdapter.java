package com.example.kumpul_dana.admin;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.model.Project;

import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class AdminProjectAdapter extends RecyclerView.Adapter<AdminProjectAdapter.ProjectViewHolder> {

    private List<Project> projectList;
    private Context context;
    private OnProjectClickListener listener;

    public AdminProjectAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.listener = listener;
    }

    public interface OnProjectClickListener {
        void onProjectClick(int projectId);
        void onEditClick(int projectId);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        if (project.getImagePath() != null && !project.getImagePath().isEmpty()) {
            Uri imageUri = Uri.parse(project.getImagePath());
            holder.imageViewProjectImage.setImageURI(imageUri);
        } else {
            holder.imageViewProjectImage.setImageResource(R.drawable.image_background);
        }

        holder.textViewProjectTitle.setText(project.getTitle());
        holder.textViewProjectDescription.setText(project.getDescription());

        // Format collectedAmount dan targetAmount ke mata uang
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String collectedFormatted = formatRupiah.format(project.getCollectedAmount()).replace(",00", "");
        String targetFormatted = formatRupiah.format(project.getTargetAmount()).replace(",00", "");
        holder.textViewCollectedAmount.setText(collectedFormatted + " dari " + targetFormatted);

        // Penanganan ProgressBar
        if (holder.progressBarProject != null) {
            if (project.getTargetAmount() > 0) {
                int progress = (int) ((project.getCollectedAmount() / project.getTargetAmount()) * 100);
                holder.progressBarProject.setProgress(progress);
            } else {
                holder.progressBarProject.setProgress(0);
            }
        }

        // Tampilkan sisa waktu yang sudah diformat dari objek Project
        holder.textViewTimeLeft.setText(project.getFormattedTimeLeft());

        // Set listener untuk item klik keseluruhan (opsional, jika ingin ada aksi klik pada item)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(project.getId());
            }
        });

        holder.buttonEditProject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(project.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProjectImage;
        TextView textViewProjectTitle;
        TextView textViewProjectDescription;
        TextView textViewCollectedAmount;
        TextView textViewTimeLeft;
        ProgressBar progressBarProject; // Deklarasikan ProgressBar
        Button buttonEditProject;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProjectImage = itemView.findViewById(R.id.imageViewProjectImage);
            textViewProjectTitle = itemView.findViewById(R.id.textViewProjectTitle);
            textViewProjectDescription = itemView.findViewById(R.id.textViewProjectDescription);
            textViewCollectedAmount = itemView.findViewById(R.id.textViewCollectedAmount);
            textViewTimeLeft = itemView.findViewById(R.id.textViewTimeLeft);
            progressBarProject = itemView.findViewById(R.id.progressBarProject); // Inisialisasi ProgressBar
            buttonEditProject = itemView.findViewById(R.id.buttonEditProject);
        }
    }
}