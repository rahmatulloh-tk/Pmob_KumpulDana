package com.example.kumpul_dana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kumpul_dana.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<String[]> userList; // Menggunakan List<String[]> sesuai dengan DatabaseHelper

    public UserAdapter(Context context, List<String[]> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String[] userData = userList.get(position); // userData: [id, username, email, phone, role, balance]

        holder.textViewUserId.setText("ID: #" + userData[0]);
        holder.textViewUsername.setText("Username: " + userData[1]);
        holder.textViewUserEmail.setText("Email: " + userData[2]);
        holder.textViewUserPhone.setText("Phone: " + userData[3]);
        holder.textViewUserRole.setText("Role: " + userData[4]);

        try {
            double balance = Double.parseDouble(userData[5]);
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            String formattedBalance = formatRupiah.format(balance).replace(",00", "");
            holder.textViewUserBalance.setText("Saldo: " + formattedBalance);
        } catch (NumberFormatException e) {
            holder.textViewUserBalance.setText("Saldo: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserId;
        TextView textViewUsername;
        TextView textViewUserEmail;
        TextView textViewUserPhone;
        TextView textViewUserRole;
        TextView textViewUserBalance;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmail);
            textViewUserPhone = itemView.findViewById(R.id.textViewUserPhone);
            textViewUserRole = itemView.findViewById(R.id.textViewUserRole);
            textViewUserBalance = itemView.findViewById(R.id.textViewUserBalance);
        }
    }
}