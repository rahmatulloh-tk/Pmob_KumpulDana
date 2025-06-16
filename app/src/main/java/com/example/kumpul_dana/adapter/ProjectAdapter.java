package com.example.kumpul_dana.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log; // Pastikan ini diimport!
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private static final String TAG = "ProjectAdapter"; // Tag untuk Logcat
    private Context context;
    private List<Project> projectList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Project project);
        void onEditClick(Project project);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProjectAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        Log.d(TAG, "onCreateViewHolder: Inflating item_project.xml");
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        // --- DEBUGGING LOGS ---
        if (holder.imageViewProject == null) {
            Log.e(TAG, "ERROR: imageViewProject is NULL in onBindViewHolder for project: " + project.getTitle());
            // Jika ini terjadi, berarti masalahnya ada di ProjectViewHolder constructor
            // atau ada RecyclerView yang menggunakan adapter ini dengan layout yang berbeda.
            // Bisa tambahkan return; di sini untuk mencegah crash, tapi item ini akan kosong.
            return;
        } else {
            Log.d(TAG, "imageViewProject is NOT NULL for project: " + project.getTitle());
        }
        // --- AKHIR DEBUGGING LOGS ---

        // Set gambar proyek
        if (project.getImagePath() != null && !project.getImagePath().isEmpty()) {
            Uri imageUri = Uri.parse(project.getImagePath());
            holder.imageViewProject.setImageURI(imageUri); // Baris 63 kamu
        } else {
            holder.imageViewProject.setImageResource(R.drawable.image_background);
        }

        holder.textViewProjectTitle.setText(project.getTitle());
        holder.textViewProjectDescription.setText(project.getDescription());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String collectedFormatted = formatRupiah.format(project.getCollectedAmount()).replace(",00", "");
        String targetFormatted = formatRupiah.format(project.getTargetAmount()).replace(",00", "");
        holder.textViewCollectedAmount.setText(collectedFormatted); // Menampilkan collected amount saja
        holder.textViewTargetAmount.setText("dari " + targetFormatted); // Menampilkan target amount secara terpisah

        // ProgressBar
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

        // Set listener untuk tombol Donasi Sekarang
        if (holder.buttonDonateNow != null) {
            holder.buttonDonateNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(project);
                }
            });
        }

        if (holder.buttonEditProject != null) {
            holder.buttonEditProject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProject;
        TextView textViewProjectTitle;
        TextView textViewTimeLeft;
        TextView textViewProjectDescription;
        ProgressBar progressBarProject;
        TextView textViewCollectedAmount;
        TextView textViewTargetAmount; // Ini benar ada di item_project.xml
        Button buttonDonateNow;
        Button buttonEditProject;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            // --- INI PERBAIKAN UTAMA DAN LOG UNTUK KONFIRMASI ---
            imageViewProject = itemView.findViewById(R.id.imageViewProject);
            if (imageViewProject == null) {
                Log.e(TAG, "ERROR: imageViewProject (R.id.imageViewProject) NOT FOUND in ViewHolder constructor!");
            } else {
                Log.d(TAG, "imageViewProject (R.id.imageViewProject) FOUND in ViewHolder constructor.");
            }
            // --- AKHIR LOG ---

            textViewProjectTitle = itemView.findViewById(R.id.textViewProjectTitle);
            textViewTimeLeft = itemView.findViewById(R.id.textViewTimeLeft);
            textViewProjectDescription = itemView.findViewById(R.id.textViewProjectDescription);
            progressBarProject = itemView.findViewById(R.id.progressBarProject);
            textViewCollectedAmount = itemView.findViewById(R.id.textViewCollectedAmount);
            textViewTargetAmount = itemView.findViewById(R.id.textViewTargetAmount); // Ini sudah benar ada di item_project.xml
            buttonDonateNow = itemView.findViewById(R.id.buttonDonateNow);
            buttonEditProject = itemView.findViewById(R.id.buttonEditProject);
        }
    }
}