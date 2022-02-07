package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgLogo = findViewById(R.id.logoView);
        animationIntro(imgLogo, 0f, 0.8f, 0);
        animationIntro(imgLogo, 0f, 0.8f, 1);

        if(InternetDAO.isNetworkAvailable(MainActivity.this)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
                    animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
                    animationIntro(imgLogo, 1f, 0f, 2);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

                            if (account != null || isFacebookLoggedIn()) {
                                if (account != null) {
                                    UserDAO.CURRENT_USER_ID = account.getId();
                                } else {
                                    UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                                }
                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        }
                    }, 800);
                }
            }, 2000);
        }
        else{
            Toast.makeText(MainActivity.this, "Không có kết nối mạng, thử lại sau", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1800);
        }
    }

    public boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(800);
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if(thing==0){
                    view.setScaleX(progress);
                }
                else if(thing == 1){
                    view.setScaleY(progress);
                }
                else{
                    view.setAlpha(progress);
                }
            }
        });
        move.start();
    }
}