package com.example.kumpul_dana.model;

public class UserDonation {
    private int id;
    private int userId;
    private int projectId;
    private String projectTitle;
    private String projectDescription;
    private double amount;
    private String donationDate;
    private String status;
    private String paymentMethod;
    private String proofImagePath;
    private String userName; // NEW: Field untuk menyimpan username donor

    // Konstruktor untuk membuat donasi baru (misalnya dari UI)
    // Digunakan saat donasi baru dibuat tanpa projectTitle atau projectDescription awal
    public UserDonation(int userId, int projectId, double amount, String donationDate, String status, String paymentMethod, String proofImagePath) {
        this.userId = userId;
        this.projectId = projectId;
        this.amount = amount;
        this.donationDate = donationDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.proofImagePath = proofImagePath;
        // userName, projectTitle, projectDescription akan null atau diisi belakangan jika diperlukan
    }

    // Konstruktor untuk mengambil donasi dari database (DENGAN DETAIL PROYEK DAN USERNAME)
    // Ini adalah konstruktor yang akan digunakan oleh DatabaseHelper.getAllDonationsWithProjectAndUserInfo
    public UserDonation(int id, int userId, int projectId, String projectTitle, String projectDescription,
                        double amount, String donationDate, String status, String paymentMethod,
                        String proofImagePath, String userName) { // NEW: Tambahkan userName di sini
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.amount = amount;
        this.donationDate = donationDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.proofImagePath = proofImagePath;
        this.userName = userName; // NEW: Inisialisasi userName
    }

    // Overload konstruktor lama untuk kompatibilitas jika masih ada kode yang memanggilnya
    // Ini memastikan bahwa jika Anda memanggil konstruktor lama, userName akan diatur ke null
    public UserDonation(int id, int userId, int projectId, String projectTitle, String projectDescription,
                        double amount, String donationDate, String status, String paymentMethod, String proofImagePath) {
        this(id, userId, projectId, projectTitle, projectDescription, amount, donationDate, status, paymentMethod, proofImagePath, null); // Panggil konstruktor baru dengan userName = null
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public double getAmount() {
        return amount;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getProofImagePath() {
        return proofImagePath;
    }

    public String getUserName() { // NEW: Getter untuk userName
        return userName;
    }

    // --- Setters (Jika diperlukan) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setProofImagePath(String proofImagePath) {
        this.proofImagePath = proofImagePath;
    }

    public void setUserName(String userName) { // NEW: Setter untuk userName
        this.userName = userName;
    }
}