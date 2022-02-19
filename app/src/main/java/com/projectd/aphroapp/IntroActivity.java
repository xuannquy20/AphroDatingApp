package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

public class IntroActivity extends AppCompatActivity {
    private TextView imgLogo;
    DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imgLogo = findViewById(R.id.logoView);
        animationIntro(imgLogo, 0f, 0.8f, 0);
        animationIntro(imgLogo, 0f, 0.8f, 1);

        if (InternetDAO.isNetworkAvailable(IntroActivity.this)) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
                animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
                animationIntro(imgLogo, 1f, 0f, 2);
                handler.postDelayed(() -> {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(IntroActivity.this);
                    if (account != null || isFacebookLoggedIn()) {
                        if (account != null) {
                            UserDAO.CURRENT_USER_ID = account.getId();
                        } else {
                            UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                        }
                        refCheck.get().addOnCompleteListener(task -> {
                            UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                            if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                                UserDAO.ORDER_NUMBER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                                UserDAO.GENDER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/gender").getValue(String.class);
                                refUser.get().addOnCompleteListener(task1 -> UserDAO.CURRENT_USER = task1.getResult().child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").getValue(User.class)).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        Intent i = new Intent(IntroActivity.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            } else {
                                Intent i = new Intent(this, RegisterNameActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                    } else {
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 800);
            }, 2000);
        } else {
            Toast.makeText(IntroActivity.this, "Không có kết nối mạng, thử lại sau", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1800);
        }
    }

    public boolean isFacebookLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(1000);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing == 0) {
                view.setScaleX(progress);
            } else if (thing == 1) {
                view.setScaleY(progress);
            } else {
                view.setAlpha(progress);
            }
        });
        move.start();
    }
}