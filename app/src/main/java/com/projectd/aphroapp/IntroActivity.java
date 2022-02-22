package com.projectd.aphroapp;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
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
    DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");


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
                refCheck.get().addOnCompleteListener(task -> {
                    if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                        UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                        UserDAO.ORDER_NUMBER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                        UserDAO.GENDER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/gender").getValue(String.class);
                        refUser.get().addOnCompleteListener(task1 -> UserDAO.CURRENT_USER = task1.getResult().child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").getValue(User.class))
                                .addOnSuccessListener(dataSnapshot -> {
                                    StorageReference storeRef = InternetDAO.storage.child(UserDAO.CURRENT_USER.getImage());
                                    try {
                                        File localFile = File.createTempFile(UserDAO.CURRENT_USER.getImage(), "png");
                                        storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                            UserDAO.imageBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        });
                                    } catch (Exception e) {
                                    }
                                }).addOnSuccessListener(dataSnapshot -> refTotalUser.get().addOnCompleteListener(task1 -> {
                            String gender = "";
                            if (UserDAO.CURRENT_USER.isGenderFinding()) {
                                gender += "male";
                            } else {
                                gender += "female";
                            }
                            UserDAO.maxGenderFinding = task1.getResult().child(gender).getValue(Integer.class);
                        })).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                refCheck.child(UserDAO.CURRENT_USER_ID + "/collection/take").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        for (DataSnapshot ds : task.getResult().getChildren()) {
                                            UserDAO.takedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        refCheck.child(UserDAO.CURRENT_USER_ID + "/collection/give").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                                    UserDAO.givedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                                }
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                            @Override
                                            public void onSuccess(DataSnapshot dataSnapshot) {
                                                String gender = "";
                                                if (UserDAO.CURRENT_USER.isGenderFinding()) {
                                                    gender += "male";
                                                } else {
                                                    gender += "female";
                                                }
                                                boolean loop = true;
                                                while (loop) {
                                                    Random rd = new Random();
                                                    int orderNumberFound = rd.nextInt(UserDAO.maxGenderFinding);
                                                    if((UserDAO.CURRENT_USER.isGenderFinding() == UserDAO.CURRENT_USER.isGender()) && orderNumberFound == UserDAO.ORDER_NUMBER){
                                                        continue;
                                                    }
                                                    else if (UserDAO.givedLike.size() == 0) {
                                                        loop = false;
                                                    } else {
                                                        for (int i = 0; i < UserDAO.givedLike.size(); i++) {
                                                            if (orderNumberFound == UserDAO.givedLike.get(i).getOrderNumber()) {
                                                                break;
                                                            } else if (i == UserDAO.givedLike.size() - 1) {
                                                                loop = false;
                                                            }
                                                        }
                                                    }
                                                    if (loop == false) {
                                                        String finalGender = gender;
                                                        refUser.child(gender + "/" + orderNumberFound).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                if (UserDAO.userFound.size() < 2) {
                                                                    UserDAO.userFound.add(task.getResult().child("profile").getValue(User.class));
                                                                }
                                                            }
                                                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DataSnapshot dataSnapshot) {
                                                                refUser.child(finalGender + "/" + orderNumberFound + "/profile/image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                        if (UserDAO.imageUserFound.size() < 2) {
                                                                            String image = task.getResult().getValue(String.class);
                                                                            StorageReference storeRef = InternetDAO.storage.child(image);
                                                                            try {
                                                                                File localFile = File.createTempFile(image, "png");
                                                                                storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                                                                    UserDAO.imageUserFound.add(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                                                                                });
                                                                            } catch (Exception e) {
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
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
        } else {
            Intent i = new Intent(IntroActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(imgLogo, 0f, 0.8f, 0);
        animationIntro(imgLogo, 0f, 0.8f, 1);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
            animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
            animationIntro(imgLogo, 1f, 0f, 2);
            handler.postDelayed(() -> {
                if (UserDAO.CURRENT_USER.getId() != null && (UserDAO.imageBitmap == null || UserDAO.imageUserFound.size() < 1)) {
                    LoadingDialog loadingDialog = new LoadingDialog(this);
                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    loadingDialog.show();
                    new Handler().postDelayed(() -> {
                        Log.i("checkdata", UserDAO.userFound.size() + "");
                        if (UserDAO.imageUserFound.size() < 1) {
                            Toast.makeText(IntroActivity.this, "Mạng yếu, thử lại sau", Toast.LENGTH_LONG).show();
                            Handler handler1 = new Handler();
                            handler1.postDelayed(() -> System.exit(0), 1800);
                        } else {
                            loadingDialog.cancel();
                            nextActivity();
                        }
                    }, 10000);
                } else {
                    nextActivity();
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