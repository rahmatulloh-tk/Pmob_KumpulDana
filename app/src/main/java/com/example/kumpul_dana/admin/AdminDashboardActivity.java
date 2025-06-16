package com.example.kumpul_dana.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.ui.LoginActivity;
// Import Activity baru yang akan dibuat
import com.example.kumpul_dana.admin.AdminDonationHistoryActivity; // NEW
import com.example.kumpul_dana.admin.UserManagementActivity; // NEW

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcomeAdmin;
    private TextView textViewAdminRoleInfo;
    private Button buttonManajemenProjects;
    private Button buttonVerifyDonations; // Akan digunakan untuk Riwayat Donasi Admin
    private Button buttonManajemenUsers; // Akan digunakan untuk Manajemen User (Read-only)
    private Button buttonLogoutHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textViewWelcomeAdmin = findViewById(R.id.textViewWelcomeAdmin);
        textViewAdminRoleInfo = findViewById(R.id.textViewAdminRoleInfo);

        buttonManajemenProjects = findViewById(R.id.buttonManajemenProjects);
        buttonVerifyDonations = findViewById(R.id.buttonVerifyDonations); // Menggunakan ID yang sama
        buttonManajemenUsers = findViewById(R.id.buttonManajemenUsers); // Menggunakan ID yang sama
        buttonLogoutHeader = findViewById(R.id.btnLogoutAdminHeader);

        int adminId = getIntent().getIntExtra("USER_ID", -1); // Admin ID dari LoginActivity
        if (adminId != -1) {
            // Bisa ambil username admin dari DB kalau mau dinamis
            textViewWelcomeAdmin.setText("Hai, ADMIN");
            textViewAdminRoleInfo.setText("Panel Kontrol Utama");
        } else {
            textViewWelcomeAdmin.setText("Selamat Datang, Admin!");
            textViewAdminRoleInfo.setText("Pastikan Anda login sebagai admin.");
            // Mungkin juga langsung logout jika tidak ada adminId
            // performLogout();
        }

        buttonManajemenProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Manajemen Proyek", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminDashboardActivity.this, ProjectActivity.class);
                startActivity(intent);
            }
        });

        // Mengarahkan ke AdminDonationHistoryActivity
        buttonVerifyDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Riwayat Donasi Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminDashboardActivity.this, AdminDonationHistoryActivity.class);
                startActivity(intent);
            }
        });

        // Mengarahkan ke UserManagementActivity (read-only)
        buttonManajemenUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Data Pengguna", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
                startActivity(intent);
            }
        });

        buttonLogoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }

    private void performLogout() {
        Toast.makeText(AdminDashboardActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}