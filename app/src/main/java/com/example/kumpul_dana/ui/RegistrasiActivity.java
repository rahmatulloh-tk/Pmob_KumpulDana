package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Tambahkan import ini untuk Log
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;

public class RegistrasiActivity extends AppCompatActivity {

    private TextInputEditText editTextUsernameReg;
    private TextInputEditText editTextEmailReg;
    private TextInputEditText editTextMobileReg;
    private TextInputEditText editTextPasswordReg;
    private TextInputEditText editTextConfirmPasswordReg;
    private Button buttonRegister;
    private TextView textViewLoginLink;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi EditText dan lainnya
        editTextUsernameReg = findViewById(R.id.editTextUsernameReg);
        editTextEmailReg = findViewById(R.id.editTextEmailReg);
        editTextMobileReg = findViewById(R.id.editTextMobileReg);
        editTextPasswordReg = findViewById(R.id.editTextPasswordReg);
        editTextConfirmPasswordReg = findViewById(R.id.editTextConfirmPasswordReg);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonRegister.setOnClickListener(v -> {
            // Ambil teks dari EditText dan terapkan .trim()
            String username = editTextUsernameReg.getText().toString().trim();
            String email = editTextEmailReg.getText().toString().trim();
            String mobile = editTextMobileReg.getText().toString().trim(); // Ini adalah nomor telepon
            String password = editTextPasswordReg.getText().toString().trim(); // Ini adalah password
            String confirmPassword = editTextConfirmPasswordReg.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegistrasiActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegistrasiActivity.this, "Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show();
            } else {
                // Gunakan metode checkEmailExists dan checkUsernameExists terlebih dahulu
                if (dbHelper.checkEmailExists(email)) {
                    Toast.makeText(RegistrasiActivity.this, "Email sudah terdaftar. Gunakan Email lain.", Toast.LENGTH_LONG).show();
                } else if (dbHelper.checkUsernameExists(username)) {
                    Toast.makeText(RegistrasiActivity.this, "Username sudah dipakai. Gunakan Username lain.", Toast.LENGTH_LONG).show();
                } else {
                    // Panggil createUser dengan urutan parameter yang BENAR
                    // Metode createUser mengembalikan 'long' (ID baris), bukan 'boolean'
                    long newUserId = dbHelper.createUser(username, email, mobile, password); // <<< PERBAIKAN DI SINI

                    Log.d("RegistrasiActivity", "Attempted to create user. Result ID: " + newUserId);

                    if (newUserId != -1) { // Jika ID tidak -1, berarti user berhasil dibuat
                        Toast.makeText(RegistrasiActivity.this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Menutup RegistrasiActivity
                    } else {
                        // Ini akan terpicu jika ada error lain saat insert (misalnya, batasan UNIQUE lain)
                        Toast.makeText(RegistrasiActivity.this, "Registrasi Gagal! Terjadi kesalahan tak terduga.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        textViewLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}