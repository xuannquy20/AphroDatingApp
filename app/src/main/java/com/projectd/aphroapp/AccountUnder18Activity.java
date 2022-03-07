package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.projectd.aphroapp.dao.UserDAO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AccountUnder18Activity extends AppCompatActivity {
    private TextView textDay;
    private Button btnQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_under18);
        textDay = findViewById(R.id.text_day);
        btnQuit = findViewById(R.id.button_exit);

        btnQuit.setOnClickListener(v -> System.exit(0));

        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString2 = UserDAO.CURRENT_USER.getDay() + " " + UserDAO.CURRENT_USER.getMonth() + " " + (UserDAO.CURRENT_USER.getYear() + 18);

        try {
            Date date1 = Calendar.getInstance().getTime();
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            textDay.setText("Mời bạn quay lại sau " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " ngày!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}