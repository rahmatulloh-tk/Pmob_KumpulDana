package com.example.kumpul_dana.ui; // Sesuaikan dengan package UI Anda jika berbeda

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.admin.AdminDashboardActivity;
import com.example.kumpul_dana.ui.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextIdentifier;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewCreateAccount;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        editTextIdentifier = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount);

        buttonLogin.setOnClickListener(v -> {
            String identifier = editTextIdentifier.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (identifier.isEmpty()) {
                editTextIdentifier.setError("Username atau Email tidak boleh kosong");
                Toast.makeText(LoginActivity.this, "Username atau Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password tidak boleh kosong");
                Toast.makeText(LoginActivity.this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Panggil metode getUserRole dari DatabaseHelper yang baru
            String role = dbHelper.getUserRole(identifier, password);
            Log.d("LoginActivity", "Login attempt for: " + identifier + ", result role: " + role);

            if (role != null) {
                // Login berhasil, dapatkan juga User ID
                int userId = dbHelper.getUserId(identifier, password); // Panggil metode getUserId yang baru

                // Simpan user_id di SharedPreferences setelah login berhasil
                SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("user_id", userId);
                editor.putString("user_role", role); // Simpan juga role
                editor.apply();

                Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                if (role.equals("admin")) {
                    // Redirect ke Admin Dashboard
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish(); // Tutup LoginActivity
                } else if (role.equals("user")) {
                    // Redirect ke User Home
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish(); // Tutup LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "Peran pengguna tidak dikenal.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Username/Email atau Password salah.", Toast.LENGTH_SHORT).show();
                editTextPassword.setText("");
            }
        });

        textViewCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrasiActivity.class);
            startActivity(intent);
        });
    }
}