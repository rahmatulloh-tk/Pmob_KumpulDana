package com.example.kumpul_dana.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.ui.LoginActivity; // Untuk logout

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcomeAdmin;
    private TextView textViewAdminRoleInfo; // Tambahkan ini
    private Button buttonManajemenProjects; // Ganti dari btnManageProjects
    private Button buttonVerifyDonations;   // Ganti dari btnVerifyDonations
    private Button buttonManajemenUsers;    // Ganti dari btnManageUsers
    private Button buttonLogoutHeader; // Tambahkan jika logout di header
    private Button buttonLogoutBottom; // Jika ada tombol logout di bawah juga

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Sembunyikan ActionBar default jika desain header custom
        }

        textViewWelcomeAdmin = findViewById(R.id.textViewWelcomeAdmin);
        textViewAdminRoleInfo = findViewById(R.id.textViewAdminRoleInfo); // Inisialisasi

        buttonManajemenProjects = findViewById(R.id.buttonManajemenProjects); // ID baru
        buttonVerifyDonations = findViewById(R.id.buttonVerifyDonations);     // ID baru
        buttonManajemenUsers = findViewById(R.id.buttonManajemenUsers);       // ID baru
        buttonLogoutHeader = findViewById(R.id.btnLogoutAdminHeader); // Inisialisasi

        // Optional: Jika Anda masih ingin tombol logout di bawah juga
        // buttonLogoutBottom = findViewById(R.id.btnLogoutAdmin); // Jika ID ini masih ada di layout

        int adminId = getIntent().getIntExtra("USER_ID", -1);
        if (adminId != -1) {
            textViewWelcomeAdmin.setText("Hai, ADMIN");
            textViewAdminRoleInfo.setText("Panel Kontrol Utama"); // Set teks info role
        } else {
            textViewWelcomeAdmin.setText("Selamat Datang, Admin!");
            textViewAdminRoleInfo.setText("Pastikan Anda login sebagai admin.");
        }

        buttonManajemenProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Manajemen Proyek", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminDashboardActivity.this, ProjectActivity.class);
                startActivity(intent);
            }
        });

        buttonVerifyDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Verifikasi Donasi (Belum Diimplementasikan)", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(AdminDashboardActivity.this, AdminDonationHistoryActivity.class);
                // startActivity(intent);
            }
        });

        buttonManajemenUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menuju Manajemen Pengguna (Belum Diimplementasikan)", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
                // startActivity(intent);
            }
        });

        // Set listener untuk tombol logout di header
        buttonLogoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        // Jika Anda masih ingin tombol logout di bagian bawah, uncomment ini
        // if (buttonLogoutBottom != null) {
        //     buttonLogoutBottom.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View v) {
        //             performLogout();
        //         }
        //     });
        // }
    }

    private void performLogout() {
        Toast.makeText(AdminDashboardActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}