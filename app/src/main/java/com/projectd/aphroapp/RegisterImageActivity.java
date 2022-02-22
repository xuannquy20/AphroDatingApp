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
import java.util.Random;

public class RegisterImageActivity extends AppCompatActivity {
    private TextView txtInfo;
    private ImageView selectImage;
    private Button btnNext;
    private Uri uri;
    DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");

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

        btnNext.setOnClickListener(v -> {
                    LoadingDialog loading = new LoadingDialog(this);
                    loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    loading.show();
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
                        ref.setValue(UserDAO.CURRENT_USER).addOnSuccessListener(unused -> {
                            StorageReference upImage = InternetDAO.storage.child(UserDAO.CURRENT_USER_ID);
                            upImage.putFile(uri).addOnSuccessListener(task -> {
                                refCount.child(finalGender).setValue(count[0] + 1).addOnSuccessListener(unused1 -> {
                                    refSavePeople.child(UserDAO.CURRENT_USER_ID).child("order_number").setValue(count[0]);
                                    refSavePeople.child(UserDAO.CURRENT_USER_ID).child("gender").setValue(finalGender).addOnSuccessListener(unused2 -> {
                                        loading.cancel();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterImageActivity.this);
                                        builder.setMessage("Chúc bạn tìm được một nửa của mình với Aphro!")
                                                .setTitle("Hồ sơ đã hoàn thành")
                                                .create().show();
                                        getData();
                                        new Handler().postDelayed(() -> {
                                            loading.show();
                                            if(UserDAO.imageUserFound.size() > 0){
                                                Intent i = new Intent(this, HomeActivity.class);
                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(this, "Mạng yếu, quay lại sau", Toast.LENGTH_LONG).show();
                                                Handler handler1 = new Handler();
                                                handler1.postDelayed(() -> System.exit(0), 1800);
                                            }
                                        }, 10000);
                                    });
                                });
                            }).addOnFailureListener(e -> {
                                loading.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterImageActivity.this);
                                builder.setMessage("Đã xảy ra lỗi trong quá trình tạo tài khoản, vui lòng kiểm tra lại đường truyền mạng!")
                                        .setTitle("Lỗi không xác định")
                                        .create().show();
                                new Handler().postDelayed(() -> {
                                    Intent i = new Intent(RegisterImageActivity.this, RegisterNameActivity.class);
                                    startActivity(i);
                                }, 2000);
                            });
                        });
                    });
                }
        );
    }

    private void getData() {
        refTotalUser.get().addOnCompleteListener(task1 -> {
            String gender = "";
            if (UserDAO.CURRENT_USER.isGenderFinding()) {
                gender += "male";
            } else {
                gender += "female";
            }
            UserDAO.maxGenderFinding = task1.getResult().child(gender).getValue(Integer.class);
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
                    if ((UserDAO.CURRENT_USER.isGenderFinding() == UserDAO.CURRENT_USER.isGender()) && orderNumberFound == UserDAO.ORDER_NUMBER) {
                        continue;
                    } else if (UserDAO.givedLike.size() == 0) {
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
                                if (UserDAO.userFound.size() < 1) {
                                    UserDAO.userFound.add(task.getResult().child("profile").getValue(User.class));
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                refUser.child(finalGender + "/" + orderNumberFound + "/profile/image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (UserDAO.imageUserFound.size() < 1) {
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