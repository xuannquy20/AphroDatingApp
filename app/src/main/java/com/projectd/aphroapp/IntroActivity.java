package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.List;
import java.util.Random;

public class IntroActivity extends AppCompatActivity {
    private TextView imgLogo;
    private boolean login = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        imgLogo = findViewById(R.id.logoView);
        checkAccount();
    }

    public void checkAccount() {
        if (InternetDAO.isNetworkAvailable(IntroActivity.this)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(IntroActivity.this);
            if (account != null || isFacebookLoggedIn()) {
                login = true;
                if (account != null) {
                    UserDAO.CURRENT_USER_ID = account.getId();
                } else {
                    UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                }
                UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                UserDAO.getDataUser();
            }
        } else {
            Toast.makeText(IntroActivity.this, "Không có kết nối mạng, thử lại sau", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> System.exit(0), 1800);
        }
    }

    private void nextActivity() {
        if (UserDAO.ORDER_NUMBER == -1 && !login) {
            Intent i = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (UserDAO.ORDER_NUMBER == -1 && login) {
            Intent i = new Intent(IntroActivity.this, RegisterNameActivity.class);
            startActivity(i);
            finish();
        } else if(UserDAO.CURRENT_USER.getAge() >= 18){
            Intent i = new Intent(IntroActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(IntroActivity.this, AccountUnder18Activity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(imgLogo, 0f, 1f, 0);
        animationIntro(imgLogo, 0f, 1f, 1);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
            animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
            animationIntro(imgLogo, 1f, 0f, 2);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog loadingDialog = new LoadingDialog(IntroActivity.this);
                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    loadingDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    if (UserDAO.CURRENT_USER.getId() != null && !UserDAO.getDataComplete) {
                                        Thread.sleep(100);
                                    } else {
                                        loadingDialog.cancel();
                                        nextActivity();
                                        break;
                                    }
                                } catch (Exception e) {}
                            }
                        }
                    }).start();
                }
            }, 1000);
        }, 2000);
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