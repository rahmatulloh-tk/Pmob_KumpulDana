package com.example.kumpul_dana.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.model.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ProjectActivity extends AppCompatActivity implements AdminProjectAdapter.OnProjectClickListener { // Implementasikan interface

    private RecyclerView recyclerViewProjects;
    private AdminProjectAdapter projectAdapter;
    private List<Project> projectList;
    private DatabaseHelper databaseHelper;

    private static final int ADD_EDIT_PROJECT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_project);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAdminProjects);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewProjects = findViewById(R.id.recyclerViewProjects);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));

        loadProjects(); // Muat proyek saat activity dibuat

        FloatingActionButton fabAddProject = findViewById(R.id.fabAddProject);
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, AddEditProjectActivity.class);
                startActivityForResult(intent, ADD_EDIT_PROJECT_REQUEST_CODE);
            }
        });
    }

    private void loadProjects() {
        projectList = databaseHelper.getAllProjects();
        projectAdapter = new AdminProjectAdapter(this, projectList);
        projectAdapter.setOnProjectClickListener(this); // Set listener
        recyclerViewProjects.setAdapter(projectAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_PROJECT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh daftar proyek jika ada perubahan
            loadProjects();
        }
    }

    @Override
    public void onProjectClick(int projectId) {
        // Ini bisa digunakan untuk melihat detail proyek (opsional)
        // Toast.makeText(this, "Project Clicked: " + projectId, Toast.LENGTH_SHORT).show();
        // Intent detailIntent = new Intent(ProjectActivity.this, ProjectDetailActivity.class);
        // detailIntent.putExtra("PROJECT_ID", projectId);
        // startActivity(detailIntent);
    }

    @Override
    public void onEditClick(int projectId) {
        // Handle klik tombol edit
        Intent intent = new Intent(ProjectActivity.this, AddEditProjectActivity.class);
        intent.putExtra("PROJECT_ID", projectId); // Kirim ID proyek ke AddEditProjectActivity
        startActivityForResult(intent, ADD_EDIT_PROJECT_REQUEST_CODE);
    }
}