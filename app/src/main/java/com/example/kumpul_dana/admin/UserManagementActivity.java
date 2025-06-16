package com.example.kumpul_dana.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem; // Import untuk Toolbar
import android.widget.Toast;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.adapter.UserAdapter; // NEW: Adapter baru

import java.util.List;
import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<String[]> userList; // Menggunakan String[] karena DatabaseHelper mengembalikan String[]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Sembunyikan title default
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Tampilkan tombol kembali
        }

        dbHelper = new DatabaseHelper(this);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        loadUserData();
    }

    // Handle tombol kembali di Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Kembali ke activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserData() {
        userList = dbHelper.getAllUsersData(); // Ambil semua data user
        if (userList.isEmpty()) {
            Toast.makeText(this, "Belum ada pengguna yang terdaftar.", Toast.LENGTH_SHORT).show();
        }

        adapter = new UserAdapter(this, userList);
        recyclerViewUsers.setAdapter(adapter);
    }
}