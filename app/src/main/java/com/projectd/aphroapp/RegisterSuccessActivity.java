package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;

public class RegisterSuccessActivity extends AppCompatActivity {
    Intent i = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView t = findViewById(R.id.notification);
        t.setText("Vui lòng chờ");
        DatabaseReference ref = InternetDAO.database.getReference().child("user");
        Handler h = new Handler();
        ref.get().addOnCompleteListener(task -> {
            if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                t.setText("Thành công");
                i = new Intent(RegisterSuccessActivity.this, HomeActivity.class);
            } else {
                t.setText("Lỗi mạng");
                i = new Intent(RegisterSuccessActivity.this, LoginActivity.class);
            }
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                }
            }, 2000);
        });
    }
}