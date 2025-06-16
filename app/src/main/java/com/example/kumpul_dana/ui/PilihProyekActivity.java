package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.adapter.ProjectAdapter;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.model.Project;

import java.util.List;
import java.util.ArrayList;

public class PilihProyekActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProjects;
    private ProjectAdapter projectAdapter;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_proyek);

        dbHelper = new DatabaseHelper(this);

        recyclerViewProjects = findViewById(R.id.recyclerViewProjects);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User ID tidak ditemukan. Mohon login kembali.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(PilihProyekActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));

        projectList = dbHelper.getAllProjects();
        if (projectList == null || projectList.isEmpty()) {
            Toast.makeText(this, "Tidak ada proyek donasi yang tersedia saat ini.", Toast.LENGTH_LONG).show();
            projectList = new ArrayList<>();
        }

        projectAdapter = new ProjectAdapter(this, projectList);

        projectAdapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Project project) {
                Intent intent = new Intent(PilihProyekActivity.this, BuatDonasiActivity.class);
                intent.putExtra("PROJECT_ID", project.getId());

                intent.putExtra("projectTitle", project.getTitle());
                intent.putExtra("targetAmount", project.getTargetAmount());
                intent.putExtra("collectedAmount", project.getCollectedAmount());
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                Toast.makeText(PilihProyekActivity.this, "Anda memilih proyek: " + project.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Project project) {

            }
        });

        recyclerViewProjects.setAdapter(projectAdapter);

        ImageView backButton = findViewById(R.id.buttonBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null && projectList != null && projectAdapter != null) {
            List<Project> updatedProjectList = dbHelper.getAllProjects();
            projectList.clear();
            if (updatedProjectList != null) {
                projectList.addAll(updatedProjectList);
            }
            projectAdapter.notifyDataSetChanged();
        }
    }
}