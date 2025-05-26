package com.example.kumpul_dana.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.kumpul_dana.model.Project;
import com.example.kumpul_dana.model.UserDonation;
import com.example.kumpul_dana.R; // Pastikan ini diimport, asumsi R.drawable.project_1 ada
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "kumpul_dana.db";
    // PENTING: UBAH VERSI DATABASE INI KE 4 ATAU LEBIH TINGGI DARI VERSI SEBELUMNYA
    // Agar onCreate/onUpgrade terpanggil dan kolom 'role' ditambahkan.
    private static final int DATABASE_VERSION = 4; // <<< UBAH INI (Jika sebelumnya 3, naikkan jadi 4)

    // Tabel Pengguna
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role"; // Kolom role untuk admin/user

    // Tabel Proyek Donasi
    public static final String TABLE_PROJECTS = "projects";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_PROJECT_TITLE = "title";
    public static final String COLUMN_PROJECT_DESCRIPTION = "description";
    public static final String COLUMN_PROJECT_IMAGE_PATH = "image_path";
    public static final String COLUMN_PROJECT_TARGET_AMOUNT = "target_amount";
    public static final String COLUMN_PROJECT_COLLECTED_AMOUNT = "collected_amount";
    public static final String COLUMN_PROJECT_TIME_LEFT = "time_left";

    // Tabel Donasi
    public static final String TABLE_DONATIONS = "donations";
    public static final String COLUMN_DONATION_ID = "id";
    public static final String COLUMN_DONATION_USER_ID = "user_id";
    public static final String COLUMN_DONATION_PROJECT_ID = "project_id";
    public static final String COLUMN_DONATION_AMOUNT = "amount";
    public static final String COLUMN_DONATION_DATE = "donation_date";
    public static final String COLUMN_DONATION_STATUS = "status"; // misal: "Pending", "Diverifikasi", "Ditolak"
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_PROOF_IMAGE_PATH = "proof_image_path";


    // SQL untuk membuat tabel pengguna (SUDAH DIUBAH)
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT UNIQUE," +
            COLUMN_EMAIL + " TEXT UNIQUE," +
            COLUMN_PHONE + " TEXT," +
            COLUMN_PASSWORD + " TEXT NOT NULL," +
            COLUMN_USER_ROLE + " TEXT DEFAULT 'user'" + // Kolom role ditambahkan dengan default 'user'
            ")";

    // SQL untuk membuat tabel proyek (Tidak Berubah)
    private static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + TABLE_PROJECTS + "(" +
            COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PROJECT_TITLE + " TEXT NOT NULL," +
            COLUMN_PROJECT_DESCRIPTION + " TEXT," +
            COLUMN_PROJECT_IMAGE_PATH + " TEXT," +
            COLUMN_PROJECT_TARGET_AMOUNT + " REAL NOT NULL," +
            COLUMN_PROJECT_COLLECTED_AMOUNT + " REAL DEFAULT 0," +
            COLUMN_PROJECT_TIME_LEFT + " TEXT" +
            ")";

    // SQL untuk membuat tabel donasi (Tidak Berubah)
    private static final String CREATE_TABLE_DONATIONS = "CREATE TABLE " + TABLE_DONATIONS + "(" +
            COLUMN_DONATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_DONATION_USER_ID + " INTEGER," +
            COLUMN_DONATION_PROJECT_ID + " INTEGER," +
            COLUMN_DONATION_AMOUNT + " REAL NOT NULL," +
            COLUMN_DONATION_DATE + " TEXT NOT NULL," +
            COLUMN_DONATION_STATUS + " TEXT NOT NULL," +
            COLUMN_PAYMENT_METHOD + " TEXT," +
            COLUMN_PROOF_IMAGE_PATH + " TEXT," +
            "FOREIGN KEY(" + COLUMN_DONATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")," +
            "FOREIGN KEY(" + COLUMN_DONATION_PROJECT_ID + ") REFERENCES " + TABLE_PROJECTS + "(" + COLUMN_PROJECT_ID + ")" +
            ")";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_DONATIONS);
        Log.d(TAG, "All tables created successfully.");

        addInitialProjects(db); // Tambahkan proyek awal
        addDummyUser(db); // Tambahkan user dummy
        addDummyAdmin(db); // Tambahkan admin dummy
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Penting: Hapus tabel dalam urutan yang benar jika ada FOREIGN KEY
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS); // Tabel users terakhir karena direferensikan
        onCreate(db); // Buat ulang semua tabel
    }

    // --- Metode untuk Pengguna & Admin Login ---

    public long createUser(String username, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USER_ROLE, "user"); // Default role saat membuat user baru

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        Log.d(TAG, "User created with ID: " + id);
        return id;
    }

    // Metode baru untuk mendapatkan role pengguna setelah login berhasil
    public String getUserRole(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String userRole = null;

        String selection = "(" + COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?) AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {identifier, identifier, password};

        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_USER_ID, COLUMN_USER_ROLE}, // Ambil ID dan ROLE
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                userRole = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE));
                Log.d(TAG, "User " + identifier + " logged in with role: " + userRole);
            } else {
                Log.d(TAG, "Login failed for identifier: " + identifier);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login attempt: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return userRole; // Mengembalikan 'admin', 'user', atau null jika gagal login
    }

    // Metode baru untuk mendapatkan User ID setelah login berhasil
    public int getUserId(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;

        String selection = "(" + COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?) AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {identifier, identifier, password};

        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return userId;
    }

    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String username = null;
        try {
            String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                Log.d(TAG, "Username for ID " + userId + ": " + username);
            } else {
                Log.d(TAG, "No username found for ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting username: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return username;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            String query = "SELECT " + COLUMN_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
                Log.d(TAG, "Email " + email + " already exists.");
            } else {
                Log.d(TAG, "Email " + email + " does not exist.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking email existence: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
                Log.d(TAG, "Username " + username + " already exists.");
            } else {
                Log.d(TAG, "Username " + username + " does not exist.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking username existence: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    // Metode untuk menambahkan dummy user
    private void addDummyUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "testuser");
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PHONE, "081234567890");
        values.put(COLUMN_PASSWORD, "password123");
        values.put(COLUMN_USER_ROLE, "user"); // Tetapkan role default 'user'
        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            Log.d(TAG, "Dummy user added with ID: " + id);
        } else {
            Log.e(TAG, "Failed to add dummy user. It might already exist (unique constraint).");
        }
    }

    // Metode untuk menambahkan dummy admin
    private void addDummyAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_EMAIL, "admin@kumpuldana.com");
        values.put(COLUMN_PHONE, "081122334455");
        values.put(COLUMN_PASSWORD, "admin123"); // Password sederhana untuk testing
        values.put(COLUMN_USER_ROLE, "admin"); // Set role sebagai 'admin'
        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            Log.d(TAG, "Dummy admin added with ID: " + id);
        } else {
            Log.e(TAG, "Failed to add dummy admin. It might already exist (unique constraint).");
        }
    }

    // --- Metode untuk Proyek ---

    // Metode ini hanya dipanggil dari onCreate, jadi private dan menerima db
    private long insertProjectInternal(SQLiteDatabase db, String title, String description, String imagePath, double targetAmount, double collectedAmount, String timeLeft) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, title);
        values.put(COLUMN_PROJECT_DESCRIPTION, description);
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath);
        values.put(COLUMN_PROJECT_TARGET_AMOUNT, targetAmount);
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, collectedAmount);
        values.put(COLUMN_PROJECT_TIME_LEFT, timeLeft);
        long id = db.insert(TABLE_PROJECTS, null, values);
        Log.d(TAG, "Inserted project (internal): " + title + " with ID: " + id);
        return id;
    }

    // Metode ini untuk menambahkan proyek baru dari AddEditProjectActivity
    public long insertProject(String title, String description, String imagePath, double targetAmount, String timeLeft) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, title);
        values.put(COLUMN_PROJECT_DESCRIPTION, description);
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath);
        values.put(COLUMN_PROJECT_TARGET_AMOUNT, targetAmount);
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, 0.0); // Proyek baru selalu mulai dari 0
        values.put(COLUMN_PROJECT_TIME_LEFT, timeLeft);
        long id = db.insert(TABLE_PROJECTS, null, values);
        db.close(); // Tutup database setelah operasi selesai
        Log.d(TAG, "Inserted project (public): " + title + " with ID: " + id);
        return id;
    }

    private void addInitialProjects(SQLiteDatabase db) {
        String packageName = context.getPackageName();

        // Menggunakan resource ID secara langsung, bukan path string
        long projectId1 = insertProjectInternal(db, "Bantuan Korban Banjir", "Membantu korban banjir yang kehilangan tempat tinggal dan harta benda yang membutuhkan bantuan secepatnya.",
                "android.resource://" + packageName + "/" + R.drawable.project_1, 5000000.0, 0.0, "15 hari lagi");
        long projectId2 = insertProjectInternal(db, "Beasiswa Anak Yatim", "Memberikan beasiswa pendidikan untuk anak-anak yatim piatu agar mereka bisa meraih cita-cita dan memiliki masa depan yang cerah.",
                "android.resource://" + packageName + "/" + R.drawable.project_1, 10000000.0, 0.0, "30 hari lagi");
        long projectId3 = insertProjectInternal(db, "Pembangunan Masjid Desa", "Mengumpulkan dana untuk pembangunan masjid di desa terpencil yang membutuhkan tempat ibadah layak dan pusat komunitas.",
                "android.resource://" + packageName + "/" + R.drawable.project_1, 25000000.0, 0.0, "60 hari lagi");

        if (projectId1 != -1) Log.d(TAG, "Initial project 1 added.");
        if (projectId2 != -1) Log.d(TAG, "Initial project 2 added.");
        if (projectId3 != -1) Log.d(TAG, "Initial project 3 added.");
    }

    public List<Project> getAllProjects() {
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_PROJECTS,
                    null, // Semua kolom
                    null, null, null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH));
                    double targetAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TARGET_AMOUNT));
                    double collectedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_COLLECTED_AMOUNT));
                    String timeLeft = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TIME_LEFT));

                    Project project = new Project(id, title, description, imagePath, targetAmount, collectedAmount, timeLeft);
                    projectList.add(project);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Fetched " + projectList.size() + " projects from database.");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all projects: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Tutup database setelah selesai
        }
        return projectList;
    }

    public Project getProjectById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Project project = null;
        try {
            cursor = db.query(
                    TABLE_PROJECTS,
                    null,
                    COLUMN_PROJECT_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH));
                double targetAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TARGET_AMOUNT));
                double collectedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_COLLECTED_AMOUNT));
                String timeLeft = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TIME_LEFT));

                project = new Project(id, title, description, imagePath, targetAmount, collectedAmount, timeLeft);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting project by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return project;
    }

    public boolean updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, project.getTitle());
        values.put(COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        values.put(COLUMN_PROJECT_IMAGE_PATH, project.getImagePath());
        values.put(COLUMN_PROJECT_TARGET_AMOUNT, project.getTargetAmount());
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, project.getCollectedAmount());
        values.put(COLUMN_PROJECT_TIME_LEFT, project.getTimeLeft());

        int rowsAffected = db.update(TABLE_PROJECTS, values, COLUMN_PROJECT_ID + " = ?",
                new String[]{String.valueOf(project.getId())});
        db.close();
        Log.d(TAG, "Updated project ID " + project.getId() + ". Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }

    public boolean deleteProject(int projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PROJECTS, COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
        db.close();
        return rowsAffected > 0;
    }


    // --- Metode untuk Donasi ---

    public long createDonation(int userId, int projectId, double amount, String donationDate, String status, String paymentMethod, String proofImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DONATION_USER_ID, userId);
        values.put(COLUMN_DONATION_PROJECT_ID, projectId);
        values.put(COLUMN_DONATION_AMOUNT, amount);
        values.put(COLUMN_DONATION_DATE, donationDate);
        values.put(COLUMN_DONATION_STATUS, status);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_PROOF_IMAGE_PATH, proofImagePath);

        long id = db.insert(TABLE_DONATIONS, null, values);

        if (id != -1) {
            // Setelah donasi dicatat, perbarui jumlah terkumpul di tabel proyek
            boolean updatedProject = updateProjectCollectedAmount(db, projectId, amount); // db_instance passed
            if (updatedProject) {
                Log.d(TAG, "Donation created with ID: " + id + " and project collected amount updated for project ID: " + projectId);
            } else {
                Log.e(TAG, "Donation created with ID: " + id + ", but failed to update project collected amount for project ID: " + projectId);
            }
        } else {
            Log.e(TAG, "Failed to create donation.");
        }

        db.close(); // Tutup database setelah operasi selesai
        return id;
    }

    // Metode untuk mendapatkan daftar donasi pengguna beserta judul dan deskripsi proyek terkait
    public List<UserDonation> getDonationsByUserIdWithProjectTitle(int userId) {
        List<UserDonation> donationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT " +
                    "T1." + COLUMN_DONATION_ID + ", " +
                    "T1." + COLUMN_DONATION_USER_ID + ", " +
                    "T1." + COLUMN_DONATION_PROJECT_ID + ", " +
                    "T2." + COLUMN_PROJECT_TITLE + ", " +
                    "T2." + COLUMN_PROJECT_DESCRIPTION + ", " +
                    "T1." + COLUMN_DONATION_AMOUNT + ", " +
                    "T1." + COLUMN_DONATION_DATE + ", " +
                    "T1." + COLUMN_DONATION_STATUS + ", " +
                    "T1." + COLUMN_PAYMENT_METHOD + ", " +
                    "T1." + COLUMN_PROOF_IMAGE_PATH +
                    " FROM " + TABLE_DONATIONS + " AS T1" +
                    " INNER JOIN " + TABLE_PROJECTS + " AS T2" +
                    " ON T1." + COLUMN_DONATION_PROJECT_ID + " = T2." + COLUMN_PROJECT_ID +
                    " WHERE T1." + COLUMN_DONATION_USER_ID + " = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int donationId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_ID));
                    int currentUserId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_USER_ID));
                    int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_PROJECT_ID));
                    String projectTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TITLE));
                    String projectDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION));

                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DONATION_AMOUNT));
                    String donationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONATION_DATE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONATION_STATUS));
                    String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD));
                    String proofImagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROOF_IMAGE_PATH));

                    UserDonation userDonation = new UserDonation(
                            donationId,
                            currentUserId,
                            projectId,
                            projectTitle,
                            projectDescription,
                            amount,
                            donationDate,
                            status,
                            paymentMethod,
                            proofImagePath
                    );
                    donationList.add(userDonation);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Fetched " + donationList.size() + " donations for user ID: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting donations by user ID with project title: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return donationList;
    }

    // Metode ini hanya dipanggil dari createDonation, dan menerima db yang sudah terbuka
    private boolean updateProjectCollectedAmount(SQLiteDatabase db, int projectId, double amountToAdd) {
        double currentCollectedAmount = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PROJECTS,
                    new String[]{COLUMN_PROJECT_COLLECTED_AMOUNT},
                    COLUMN_PROJECT_ID + " = ?",
                    new String[]{String.valueOf(projectId)},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                currentCollectedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_COLLECTED_AMOUNT));
                Log.d(TAG, "Current collected amount for project ID " + projectId + ": " + currentCollectedAmount);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving current collected amount: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Jangan menutup DB di sini, karena DB dikelola oleh pemanggil (createDonation)
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, currentCollectedAmount + amountToAdd);

        int rowsAffected = db.update(TABLE_PROJECTS, values, COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
        Log.d(TAG, "Updated project ID " + projectId + " collected amount by " + amountToAdd + ". Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }


    public int getTotalDonationsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // ... (sisanya dari metode ini jika ada, pastikan ditutup db-nya)
        // Contoh implementasi untuk getTotalDonationsForUser:
        int totalDonations = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_DONATIONS + " WHERE " + COLUMN_DONATION_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                totalDonations = cursor.getInt(0); // Ambil nilai dari kolom pertama (COUNT)
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting total donations for user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return totalDonations;
    }
}