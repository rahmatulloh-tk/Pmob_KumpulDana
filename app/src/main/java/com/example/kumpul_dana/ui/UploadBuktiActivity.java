package com.example.kumpul_dana.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadBuktiActivity extends AppCompatActivity {

    private FrameLayout frameLayoutAddImage;
    private ImageView imageViewUploadedProof;
    private TextView textViewAddImage;
    private Button buttonSubmitDonasi;
    private ImageView backButtonUpload;

    private DatabaseHelper dbHelper;
    private Uri selectedImageUri;

    // Data donasi yang akan diterima dari Intent
    private int userId;
    private int projectId;
    private double amount;
    private String paymentMethod;
    private String message;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti);

        dbHelper = new DatabaseHelper(this);

        frameLayoutAddImage = findViewById(R.id.frameLayoutAddImage);
        imageViewUploadedProof = findViewById(R.id.imageViewUploadedProof);
        textViewAddImage = findViewById(R.id.textViewAddImage);
        buttonSubmitDonasi = findViewById(R.id.buttonSubmitDonasi);
        backButtonUpload = findViewById(R.id.backButtonUpload);

        // Ambil data donasi dari Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        amount = getIntent().getDoubleExtra("AMOUNT", 0.0);
        paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        message = getIntent().getStringExtra("MESSAGE");

        // Inisialisasi ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imageViewUploadedProof.setImageURI(selectedImageUri);
                        imageViewUploadedProof.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        textViewAddImage.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(UploadBuktiActivity.this, "Pemilihan gambar dibatalkan.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Listener tombol kembali
        backButtonUpload.setOnClickListener(v -> finish());

        frameLayoutAddImage.setOnClickListener(v -> openImagePicker());

        buttonSubmitDonasi.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(UploadBuktiActivity.this, "Mohon unggah bukti pembayaran!", Toast.LENGTH_SHORT).show();
            } else {
                submitDonation();
            }
        });
    }

    private void openImagePicker() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Meminta izin READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                return;
            }
        }
        pickImageLauncher.launch("image/*");
    }

    // Handle hasil permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Kode permintaan izin kita
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, coba lagi membuka picker gambar
                openImagePicker();
            } else {
                // Izin ditolak
                Toast.makeText(this, "Izin membaca penyimpanan ditolak. Tidak dapat memilih gambar.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void submitDonation() {
        // Ambil tanggal donasi saat ini
        String donationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String status = "Pending";
        String proofImagePath = (selectedImageUri != null) ? selectedImageUri.toString() : null;

        long result = dbHelper.createDonation(
                userId,
                projectId,
                amount,
                donationDate,
                status,
                paymentMethod,
                proofImagePath
        );

        if (result != -1) {
            Toast.makeText(this, "Donasi berhasil disubmit!", Toast.LENGTH_LONG).show();
            // Kembali ke HomeActivity atau RiwayatDonasiActivity
            Intent intent = new Intent(UploadBuktiActivity.this, HomeActivity.class);
            // Flags ini bagus untuk membersihkan back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan donasi. Coba lagi.", Toast.LENGTH_SHORT).show();
            Log.e("UploadBuktiActivity", "Gagal menyimpan donasi ke DB. UserId: " + userId + ", ProjId: " + projectId + ", Amount: " + amount);
        }
    }
}