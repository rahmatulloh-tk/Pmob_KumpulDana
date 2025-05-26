// File: ProjectAdapter.java
package com.example.kumpul_dana.adapter; // PASTIKAN INI BENAR SESUAI LOKASI FILE

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
import com.example.kumpul_dana.model.Project; // Pastikan ini mengarah ke file model Project Anda

import java.text.NumberFormat;
import java.text.ParseException; // Tetap diperlukan
import java.text.SimpleDateFormat; // Tetap diperlukan untuk format tanggal lain jika ada
import java.util.Calendar; // Tetap diperlukan
import java.util.Date; // Tetap diperlukan
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit; // Tetap diperlukan

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;
    private OnItemClickListener listener;

    // Interface untuk menangani klik item dan tombol Edit
    public interface OnItemClickListener {
        void onItemClick(Project project); // Untuk klik keseluruhan item (opsional)
        void onEditClick(Project project); // Untuk tombol Edit
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false); // Pastikan item_project adalah layout untuk user
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        // Set gambar proyek
        if (project.getImagePath() != null && !project.getImagePath().isEmpty()) {
            Uri imageUri = Uri.parse(project.getImagePath());
            holder.imageViewProject.setImageURI(imageUri);
        } else {
            // Set gambar placeholder jika tidak ada gambar
            holder.imageViewProject.setImageResource(R.drawable.image_background); // Ganti dengan placeholder Anda
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
                holder.progressBarProject.setProgress(0); // Jika target 0 atau belum ada, set progress ke 0
            }
        }

        // Tampilkan sisa waktu yang sudah diformat dari objek Project
        holder.textViewTimeLeft.setText(project.getFormattedTimeLeft()); // PERBAIKAN: Memanggil getFormattedTimeLeft()

        // Set listener untuk tombol Donasi Sekarang (jika ada di layout user)
        if (holder.buttonDonateNow != null) {
            holder.buttonDonateNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(project); // Atau onDonateClick(project) jika ada method terpisah
                }
            });
        }

        // Jika Anda memiliki buttonEditProject di layout user, pastikan disembunyikan
        if (holder.buttonEditProject != null) {
            holder.buttonEditProject.setVisibility(View.GONE);
            // Atau jika ProjectAdapter ini juga digunakan admin, Anda bisa mengatur visibilitas berdasarkan peran
            // if (userRole.equals("admin")) holder.buttonEditProject.setVisibility(View.VISIBLE);
            // else holder.buttonEditProject.setVisibility(View.GONE);
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
        TextView textViewTargetAmount;
        Button buttonDonateNow; // Mungkin ada di layout user
        Button buttonEditProject; // Mungkin ada di layout admin, perlu disembunyikan jika ini adapter user

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProject = itemView.findViewById(R.id.imageViewProjectImage); // Asumsi ID sama dengan admin_project_item
            textViewProjectTitle = itemView.findViewById(R.id.textViewProjectTitle);
            textViewTimeLeft = itemView.findViewById(R.id.textViewTimeLeft);
            textViewProjectDescription = itemView.findViewById(R.id.textViewProjectDescription);
            progressBarProject = itemView.findViewById(R.id.progressBarProject);
            textViewCollectedAmount = itemView.findViewById(R.id.textViewCollectedAmount);
            textViewTargetAmount = itemView.findViewById(R.id.textViewTargetAmount);
            buttonDonateNow = itemView.findViewById(R.id.buttonDonateNow); // Inisialisasi jika ada
            buttonEditProject = itemView.findViewById(R.id.buttonEditProject); // Inisialisasi jika ada
        }
    }
}