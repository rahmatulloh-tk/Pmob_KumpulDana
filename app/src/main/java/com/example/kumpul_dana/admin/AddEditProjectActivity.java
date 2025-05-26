// File: AddEditProjectActivity.java
package com.example.kumpul_dana.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.model.Project;

import java.text.ParseException; // Import untuk ParseException
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; // Import untuk Date
import java.util.Locale;

public class AddEditProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription, editTextProjectTargetAmount, editTextProjectDeadline;
    private ImageView imageViewProjectPreview;
    private Button buttonSaveProject, buttonDeleteProject;
    private RelativeLayout layoutImageUpload;

    private DatabaseHelper databaseHelper;
    private int projectId = -1;
    private boolean isEditMode = false;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_project);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEditProject);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inisialisasi Views
        editTextProjectTitle = findViewById(R.id.editTextProjectTitle);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);
        editTextProjectTargetAmount = findViewById(R.id.editTextProjectTargetAmount);
        editTextProjectDeadline = findViewById(R.id.editTextProjectDeadline);
        imageViewProjectPreview = findViewById(R.id.imageViewProjectPreview);
        buttonSaveProject = findViewById(R.id.buttonSaveProject);
        buttonDeleteProject = findViewById(R.id.buttonDeleteProject);
        layoutImageUpload = findViewById(R.id.layoutImageUpload);

        // Tangani klik pada area upload gambar
        layoutImageUpload.setOnClickListener(v -> openImageChooser());

        // Tangani klik pada EditText Deadline untuk menampilkan DatePicker
        editTextProjectDeadline.setOnClickListener(v -> showDatePickerDialog());

        // Cek apakah ini mode edit atau tambah baru
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);
        isEditMode = (projectId != -1);

        if (isEditMode) {
            toolbar.setTitle("Edit Proyek");
            buttonSaveProject.setText("Update Proyek");
            buttonDeleteProject.setVisibility(View.VISIBLE);
            loadProjectData(projectId);
        } else {
            toolbar.setTitle("Tambah Proyek Baru");
            buttonSaveProject.setText("Tambah Proyek");
            buttonDeleteProject.setVisibility(View.GONE);
        }

        buttonSaveProject.setOnClickListener(v -> saveProject());
        buttonDeleteProject.setOnClickListener(v -> showConfirmationDialog("Hapus Proyek", "Apakah Anda yakin ingin menghapus proyek ini?", true));
    }

    private void loadProjectData(int id) {
        Project project = databaseHelper.getProjectById(id);
        if (project != null) {
            editTextProjectTitle.setText(project.getTitle());
            editTextProjectDescription.setText(project.getDescription());
            editTextProjectTargetAmount.setText(String.format(Locale.getDefault(), "%.0f", project.getTargetAmount()));

            // Ambil deadline yang tersimpan (format yyyy-MM-dd) dari objek Project
            String storedDeadline = project.getTimeLeft();
            // Format ulang untuk ditampilkan di EditText (dd MMMM yyyy)
            SimpleDateFormat storedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            try {
                Date deadlineDate = storedFormat.parse(storedDeadline);
                if (deadlineDate != null) {
                    editTextProjectDeadline.setText(displayFormat.format(deadlineDate));
                } else {
                    editTextProjectDeadline.setText("Tanggal tidak valid");
                }
            } catch (ParseException e) {
                e.printStackTrace();
                editTextProjectDeadline.setText("Tanggal tidak valid"); // Tangani jika ada kesalahan parsing
            }

            if (project.getImagePath() != null && !project.getImagePath().isEmpty()) {
                selectedImageUri = Uri.parse(project.getImagePath());
                imageViewProjectPreview.setImageURI(selectedImageUri);
                imageViewProjectPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageViewProjectPreview.setImageResource(R.drawable.image_background);
                imageViewProjectPreview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        } else {
            Toast.makeText(this, "Proyek tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveProject() {
        String title = editTextProjectTitle.getText().toString().trim();
        String description = editTextProjectDescription.getText().toString().trim();
        String targetAmountStr = editTextProjectTargetAmount.getText().toString().trim();
        String deadlineDisplayStr = editTextProjectDeadline.getText().toString().trim(); // Ini adalah "dd MMMM yyyy"
        String imagePath = (selectedImageUri != null) ? selectedImageUri.toString() : "";

        if (title.isEmpty() || description.isEmpty() || targetAmountStr.isEmpty() || deadlineDisplayStr.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua bidang.", Toast.LENGTH_SHORT).show();
            return;
        }

        double targetAmount;
        try {
            targetAmount = Double.parseDouble(targetAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah Target Donasi tidak valid.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Perubahan Penting di Sini: Konversi format tanggal untuk penyimpanan ---
        String deadlineToStore;
        // Format yang ditampilkan di EditText
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        // Format yang akan disimpan di database
        SimpleDateFormat storedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = displayFormat.parse(deadlineDisplayStr);
            if (parsedDate != null) {
                deadlineToStore = storedFormat.format(parsedDate); // Konversi ke format yyyy-MM-dd
            } else {
                Toast.makeText(this, "Format tanggal tidak valid. Mohon pilih ulang tanggal.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Kesalahan parsing tanggal. Mohon pilih ulang tanggal.", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- Akhir Perubahan Penting ---

        Project project;
        if (isEditMode) {
            // Untuk mode edit, gunakan ID yang sudah ada
            project = new Project(projectId, title, description, imagePath, 0.0, targetAmount, deadlineToStore); // Kirim deadlineToStore
            boolean updated = databaseHelper.updateProject(project);
            if (updated) {
                Toast.makeText(this, "Proyek berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Gagal memperbarui proyek.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Untuk mode tambah baru
            long newId = databaseHelper.insertProject(title, description, imagePath, targetAmount, deadlineToStore); // Kirim deadlineToStore
            if (newId != -1) {
                Toast.makeText(this, "Proyek berhasil ditambahkan.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Gagal menambahkan proyek.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showConfirmationDialog(String title, String message, final boolean isDeleteAction) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (isDeleteAction) {
                        deleteProject();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteProject() {
        if (isEditMode) {
            boolean deleted = databaseHelper.deleteProject(projectId);
            if (deleted) {
                Toast.makeText(this, "Proyek berhasil dihapus.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Gagal menghapus proyek.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageViewProjectPreview.setImageURI(selectedImageUri);
            imageViewProjectPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
        }
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    c.set(selectedYear, selectedMonth, selectedDay);
                    // Format ini HANYA untuk menampilkan di EditText
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    editTextProjectDeadline.setText(sdf.format(c.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }
}