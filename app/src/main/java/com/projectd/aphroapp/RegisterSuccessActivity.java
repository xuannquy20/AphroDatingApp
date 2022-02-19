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
        Handler h = new Handler();
        Intent i = getIntent();
        t.setText(i.getStringExtra("warning"));

        Intent home = new Intent(this, HomeActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(home);
            }
        }, 2000);
    }
}