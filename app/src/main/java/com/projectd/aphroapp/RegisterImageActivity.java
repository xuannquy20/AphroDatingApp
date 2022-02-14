package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

        btnNext.setOnClickListener(v -> {
            String fileName = uri.getPath();
            int index = fileName.lastIndexOf('.');
            UserDAO.CURRENT_USER.setImage(UserDAO.CURRENT_USER_ID + fileName.substring(index));
            DatabaseReference ref = InternetDAO.database.getReference().child("user/"+UserDAO.CURRENT_USER_ID+"/profile");
            ref.setValue(UserDAO.CURRENT_USER);

            StorageReference upImage = InternetDAO.storage.getReference().child(UserDAO.CURRENT_USER_ID);
            upImage.putFile(uri).addOnSuccessListener(task -> {
                Intent i = new Intent(RegisterImageActivity.this, RegisterSuccessActivity.class);
                startActivity(i);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                selectImage.setImageURI(uri);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectImage, 1200, 800, false);
        animationIntro(btnNext, -1200, 1000, false);
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