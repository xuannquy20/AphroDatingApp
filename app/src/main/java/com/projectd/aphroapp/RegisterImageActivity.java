package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

public class RegisterImageActivity extends AppCompatActivity {
    private TextView txtInfo;
    private ImageView selectImage;
    private Button btnNext;
    private Uri uri;

    protected void bindingView() {
        txtInfo = findViewById(R.id.txtInfo);
        selectImage = findViewById(R.id.select_image);
        btnNext = findViewById(R.id.btnNext);
    }

    protected void bindingAciton() {
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, 1);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog loadingDialog = new LoadingDialog(RegisterImageActivity.this);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.show();

                UserDAO.CURRENT_USER.setImage(UserDAO.CURRENT_USER_ID);
                String gender = "";
                if (UserDAO.CURRENT_USER.isGender()) {
                    gender = "male";
                } else {
                    gender = "female";
                }
                DatabaseReference refCount = InternetDAO.database.child("total_user");
                DatabaseReference refSavePeople = InternetDAO.database.child("data_user");
                final int[] count = {0};
                String finalGender = gender;
                refCount.child(gender).get().addOnCompleteListener(task -> count[0] = task.getResult().getValue(Integer.class)).addOnSuccessListener(dataSnapshot -> {
                    DatabaseReference ref = InternetDAO.database.child("user/" + finalGender + "/" + count[0] + "/profile");
                    UserDAO.CURRENT_USER.setOrderNumber(count[0]);
                    UserDAO.ORDER_NUMBER = count[0];
                    ref.setValue(UserDAO.CURRENT_USER);
                    StorageReference upImage = InternetDAO.storage.child(UserDAO.CURRENT_USER_ID);
                    upImage.putFile(uri);
                    refCount.child(finalGender).setValue(count[0] + 1);
                    refSavePeople.child(UserDAO.CURRENT_USER_ID).child("order_number").setValue(count[0]);
                    refSavePeople.child(UserDAO.CURRENT_USER_ID).child("gender").setValue(finalGender).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            UserDAO.getOrderNumberCanFind();
                            new Thread(() -> {
                                try{
                                    while(true){
                                        if (UserDAO.getDataComplete) {
                                            loadingDialog.cancel();
                                            Intent i;
                                            if (UserDAO.age >= 18) {
                                                i = new Intent(RegisterImageActivity.this, RegisterSuccessActivity.class);
                                            } else {
                                                i = new Intent(RegisterImageActivity.this, AccountUnder18Activity.class);
                                            }
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                            break;
                                        }
                                        else{
                                            Thread.sleep(100);
                                        }
                                    }
                                }
                                catch (Exception e){e.printStackTrace();}
                            }).start();
                        }
                    });
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                selectImage.setImageURI(uri);
                selectImage.buildDrawingCache();
                UserDAO.imageBitmap = selectImage.getDrawingCache();
            }
        }
        btnNext.setEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_image);

        bindingView();
        bindingAciton();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectImage, 1200, 800, false);
        animationIntro(btnNext, -1200, 1000, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void animationIntro(View view, float infoNum1, int timeMove, boolean thing) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, 0);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(timeMove);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing) {
                view.setTranslationY(progress);
            } else {
                view.setTranslationX(progress);
            }
        });
        move.start();
    }
}