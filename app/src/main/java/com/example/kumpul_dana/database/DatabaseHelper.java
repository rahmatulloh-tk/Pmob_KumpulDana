package com.example.kumpul_dana.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.kumpul_dana.model.Project;
import com.example.kumpul_dana.model.UserDonation;
import com.example.kumpul_dana.R;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "kumpul_dana.db";
    private static final int DATABASE_VERSION = 5; // <--- UPDATE KE VERSI 5 (ATAU LEBIH TINGGI)

    // Tabel Pengguna
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";
    public static final String COLUMN_USER_BALANCE = "balance"; // <--- KOLOM BARU UNTUK SALDO

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
    public static final String COLUMN_DONATION_STATUS = "status";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_PROOF_IMAGE_PATH = "proof_image_path";


    // SQL untuk membuat tabel pengguna (DITAMBAH KOLOM BALANCE)
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT UNIQUE," +
            COLUMN_EMAIL + " TEXT UNIQUE," +
            COLUMN_PHONE + " TEXT," +
            COLUMN_PASSWORD + " TEXT NOT NULL," +
            COLUMN_USER_ROLE + " TEXT DEFAULT 'user'," +
            COLUMN_USER_BALANCE + " REAL DEFAULT 0.0" + // <--- PENAMBAHAN KOLOM SALDO
            ")";

    // SQL untuk membuat tabel proyek (Tidak ada perubahan di sini)
    private static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + TABLE_PROJECTS + "(" +
            COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PROJECT_TITLE + " TEXT NOT NULL," +
            COLUMN_PROJECT_DESCRIPTION + " TEXT," +
            COLUMN_PROJECT_IMAGE_PATH + " TEXT," +
            COLUMN_PROJECT_TARGET_AMOUNT + " REAL NOT NULL," +
            COLUMN_PROJECT_COLLECTED_AMOUNT + " REAL DEFAULT 0," +
            COLUMN_PROJECT_TIME_LEFT + " TEXT" +
            ")";

    // SQL untuk membuat tabel donasi (Tidak ada perubahan di sini)
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

        addInitialProjects(db);
        addDummyUser(db);
        addDummyAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Jika hanya menambah kolom, disarankan ALTER TABLE.
        // Karena tugas tidak mau rumit dan sering reset DB, DROP dan CREATE ulang bisa diterima
        // TAPI INGAT: Ini akan menghapus SEMUA data saat update versi DB!
        // Jika ingin data tetap, Anda harus melakukan ALTER TABLE, bukan DROP.
        if (oldVersion < 5) { // Misalnya dari versi 4 ke 5
            Log.d(TAG, "Adding COLUMN_USER_BALANCE to " + TABLE_USERS);
            // Tambahkan kolom 'balance' jika belum ada
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_BALANCE + " REAL DEFAULT 0.0;");
        }
        // Contoh untuk upgrade yang lebih komprehensif, tapi ini akan menghapus data:
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // onCreate(db);
    }

    // --- Metode untuk Pengguna & Admin Login ---

    public long createUser(String username, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USER_ROLE, "user");
        values.put(COLUMN_USER_BALANCE, 0.0); // <--- INISIALISASI SALDO 0 UNTUK USER BARU

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        Log.d(TAG, "User created with ID: " + id);
        return id;
    }

    public String getUserRole(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String userRole = null;

        String selection = "(" + COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?) AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {identifier, identifier, password};

        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_USER_ID, COLUMN_USER_ROLE},
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
        return userRole;
    }

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

    // Metode untuk menambahkan dummy user (DITAMBAH INISIALISASI SALDO)
    private void addDummyUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "testuser");
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PHONE, "081234567890");
        values.put(COLUMN_PASSWORD, "password123");
        values.put(COLUMN_USER_ROLE, "user");
        values.put(COLUMN_USER_BALANCE, 50000.0);
        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            Log.d(TAG, "Dummy user added with ID: " + id);
        } else {
            Log.e(TAG, "Failed to add dummy user. It might already exist (unique constraint).");
        }
    }

    // Metode untuk menambahkan dummy admin (DITAMBAH INISIALISASI SALDO)
    private void addDummyAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_EMAIL, "admin@kumpuldana.com");
        values.put(COLUMN_PHONE, "081122334455");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_USER_ROLE, "admin");
        values.put(COLUMN_USER_BALANCE, 0.0); // <--- Admin tidak punya saldo
        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            Log.d(TAG, "Dummy admin added with ID: " + id);
        } else {
            Log.e(TAG, "Failed to add dummy admin. It might already exist (unique constraint).");
        }
    }

    // --- Metode untuk Proyek ---
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

    public long insertProject(String title, String description, String imagePath, double targetAmount, String timeLeft) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, title);
        values.put(COLUMN_PROJECT_DESCRIPTION, description);
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath);
        values.put(COLUMN_PROJECT_TARGET_AMOUNT, targetAmount);
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, 0.0);
        values.put(COLUMN_PROJECT_TIME_LEFT, timeLeft);
        long id = db.insert(TABLE_PROJECTS, null, values);
        db.close();
        Log.d(TAG, "Inserted project (public): " + title + " with ID: " + id);
        return id;
    }

    private void addInitialProjects(SQLiteDatabase db) {
        String packageName = context.getPackageName();

        long projectId1 = insertProjectInternal(db, "Penanaman Hutan Papua", "Membantu penanaman hutan papua yang gundul untuk menjaga kelestarian alam.",
                "android.resource://" + packageName + "/" + R.drawable.hutan_papua, 10000000.0, 900000.0, "2025-12-31");
        long projectId2 = insertProjectInternal(db, "Donasi Buku untuk Perpustakaan Keliling", "Proyek Anniv NCTZen membantu perbaikan perpustakaan keliling.",
                "android.resource://" + packageName + "/" + R.drawable.donasi_buku, 2000000.0, 1500000.0, "2026-01-15");
        long projectId3 = insertProjectInternal(db, "Steril Kucing Massal", "Mengumpulkan dana untuk steril kucing liar massal di daerah Surabaya.",
                "android.resource://" + packageName + "/" + R.drawable.steril_kucing, 12000000.0, 100000.0, "2026-03-01");

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
                    null,
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
            db.close();
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
            boolean updatedProject = updateProjectCollectedAmount(db, projectId, amount);
            // Tambahkan juga pengurangan saldo user di sini jika donasi langsung dari saldo
            // Misal: boolean updatedBalance = decreaseUserBalance(db, userId, amount);
            if (updatedProject) {
                Log.d(TAG, "Donation created with ID: " + id + " and project collected amount updated for project ID: " + projectId);
            } else {
                Log.e(TAG, "Donation created with ID: " + id + ", but failed to update project collected amount for project ID: " + projectId);
            }
        } else {
            Log.e(TAG, "Failed to create donation.");
        }

        db.close();
        return id;
    }

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
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_COLLECTED_AMOUNT, currentCollectedAmount + amountToAdd);

        int rowsAffected = db.update(TABLE_PROJECTS, values, COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
        Log.d(TAG, "Updated project ID " + projectId + " collected amount by " + amountToAdd + ". Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }


    public int getTotalDonationsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalDonations = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_DONATIONS + " WHERE " + COLUMN_DONATION_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                totalDonations = cursor.getInt(0);
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

    // --- NEW: Metode untuk Saldo Pengguna ---

    // Metode untuk mendapatkan saldo user
    public double getUserBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double balance = 0.0;
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_USER_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                int balanceIndex = cursor.getColumnIndex(COLUMN_USER_BALANCE);
                if (balanceIndex != -1) {
                    balance = cursor.getDouble(balanceIndex);
                    Log.d(TAG, "User ID " + userId + " balance: " + balance);
                } else {
                    Log.e(TAG, "Kolom '" + COLUMN_USER_BALANCE + "' tidak ditemukan di tabel " + TABLE_USERS + ".");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user balance for ID " + userId + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return balance;
    }

    // Metode untuk mengupdate saldo user (bisa untuk top-up atau pengurangan)
    public boolean updateUserBalance(int userId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_BALANCE, amount); // Mengupdate saldo menjadi nilai 'amount' yang diberikan

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        if (rowsAffected > 0) {
            Log.d(TAG, "Updated user ID " + userId + " balance to: " + amount);
        } else {
            Log.e(TAG, "Failed to update user ID " + userId + " balance.");
        }
        return rowsAffected > 0;
    }

    // Metode untuk menambah saldo user (top-up)
    public boolean addBalance(int userId, double amountToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        double currentBalance = getUserBalance(userId); // Dapatkan saldo saat ini (memanggil fungsi yang akan menutup DB)
        // Buka kembali database untuk operasi update
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_BALANCE, currentBalance + amountToAdd);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        if (rowsAffected > 0) {
            Log.d(TAG, "Added " + amountToAdd + " to user ID " + userId + ". New balance: " + (currentBalance + amountToAdd));
        } else {
            Log.e(TAG, "Failed to add balance to user ID " + userId + ".");
        }
        return rowsAffected > 0;
    }

    // Metode untuk mengurangi saldo user (donasi via saldo)
    public boolean decreaseBalance(int userId, double amountToDecrease) {
        SQLiteDatabase db = this.getWritableDatabase();
        double currentBalance = getUserBalance(userId); // Dapatkan saldo saat ini
        // Buka kembali database untuk operasi update
        db = this.getWritableDatabase();

        if (currentBalance >= amountToDecrease) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_BALANCE, currentBalance - amountToDecrease);

            int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.close();
            if (rowsAffected > 0) {
                Log.d(TAG, "Decreased " + amountToDecrease + " from user ID " + userId + ". New balance: " + (currentBalance - amountToDecrease));
            } else {
                Log.e(TAG, "Failed to decrease balance from user ID " + userId + ".");
            }
            return rowsAffected > 0;
        } else {
            Log.w(TAG, "Insufficient balance for user ID " + userId + ". Current: " + currentBalance + ", Attempted to decrease: " + amountToDecrease);
            db.close();
            return false; // Saldo tidak cukup
        }
    }

    public boolean checkUserPassword(int userId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean passwordCorrect = false;
        try {
            String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                if (storedPassword.equals(password)) { // Perbandingan langsung, untuk tugas oke
                    passwordCorrect = true;
                    Log.d(TAG, "Password for user ID " + userId + " is correct.");
                } else {
                    Log.d(TAG, "Password for user ID " + userId + " is incorrect.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking user password: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return passwordCorrect;
    }
    // Mengambil semua donasi (untuk Riwayat Donasi Admin)
    public List<UserDonation> getAllDonationsWithProjectAndUserInfo() {
        List<UserDonation> donationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT " +
                    "D." + COLUMN_DONATION_ID + ", " +
                    "D." + COLUMN_DONATION_USER_ID + ", " +
                    "U." + COLUMN_USERNAME + ", " + // Tambahkan username
                    "D." + COLUMN_DONATION_PROJECT_ID + ", " +
                    "P." + COLUMN_PROJECT_TITLE + ", " + // Tambahkan judul proyek
                    "D." + COLUMN_DONATION_AMOUNT + ", " +
                    "D." + COLUMN_DONATION_DATE + ", " +
                    "D." + COLUMN_DONATION_STATUS + ", " +
                    "D." + COLUMN_PAYMENT_METHOD + ", " +
                    "D." + COLUMN_PROOF_IMAGE_PATH +
                    " FROM " + TABLE_DONATIONS + " AS D" +
                    " INNER JOIN " + TABLE_USERS + " AS U ON D." + COLUMN_DONATION_USER_ID + " = U." + COLUMN_USER_ID +
                    " INNER JOIN " + TABLE_PROJECTS + " AS P ON D." + COLUMN_DONATION_PROJECT_ID + " = P." + COLUMN_PROJECT_ID +
                    " ORDER BY D." + COLUMN_DONATION_DATE + " DESC"; // Urutkan berdasarkan tanggal terbaru

            cursor = db.rawQuery(query, null); // Tidak ada selectionArgs karena mengambil semua

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int donationId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)); // Ambil username
                    int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONATION_PROJECT_ID));
                    String projectTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_TITLE)); // Ambil judul proyek
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DONATION_AMOUNT));
                    String donationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONATION_DATE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONATION_STATUS));
                    String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD));
                    String proofImagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROOF_IMAGE_PATH));

                    // Perlu model UserDonation yang bisa menampung username dan projectTitle
                    // Jika model UserDonation yang sudah ada tidak punya ini, kita akan perlu menyesuaikan
                    // atau membuat model baru untuk admin view. Untuk sementara, kita asumsikan UserDonation
                    // bisa menampung projectTitle. Tambahkan username juga di konstruktor UserDonation
                    // atau gunakan UserDonation versi lain / Map.

                    // Untuk sekarang, kita akan tambahkan ke UserDonation.
                    // Pastikan model UserDonation Anda diperbarui untuk menampung `username` (opsional)
                    // dan `projectTitle` (sudah ada).

                    UserDonation userDonation = new UserDonation(
                            donationId,
                            userId,
                            projectId,
                            projectTitle, // Sudah ada
                            null, // projectDescription - tidak diambil di query ini, bisa diisi null atau ambil jika perlu
                            amount,
                            donationDate,
                            status,
                            paymentMethod,
                            proofImagePath
                    );
                    // Jika Anda ingin menampilkan username, Anda perlu menambahkan setter di model UserDonation
                    // atau membuat konstruktor/model khusus untuk admin view.
                    // Contoh: userDonation.setUsername(username);
                    donationList.add(userDonation);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Fetched " + donationList.size() + " total donations from database.");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all donations for admin: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return donationList;
    }

    // Mengambil semua user (untuk Manajemen Pengguna Admin)
    public List<String[]> getAllUsersData() {
        List<String[]> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Ambil ID, Username, Email, Phone, Role, dan Balance
            String query = "SELECT " +
                    COLUMN_USER_ID + ", " +
                    COLUMN_USERNAME + ", " +
                    COLUMN_EMAIL + ", " +
                    COLUMN_PHONE + ", " +
                    COLUMN_USER_ROLE + ", " +
                    COLUMN_USER_BALANCE +
                    " FROM " + TABLE_USERS +
                    " ORDER BY " + COLUMN_USERNAME + " ASC";

            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Masukkan data ke array string, atau buat model User jika ada
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE));
                    double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_BALANCE));

                    // Untuk kesederhanaan, kembalikan String[]
                    // Jika Anda memiliki model User, lebih baik mengembalikan List<User>
                    userList.add(new String[]{id, username, email, phone, role, String.valueOf(balance)});
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Fetched " + userList.size() + " total users from database.");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users for admin: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return userList;
    }

}