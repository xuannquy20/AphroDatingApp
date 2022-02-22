package com.projectd.aphroapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.Random;

public class HomeFragment extends Fragment {
    private ImageView imageShow, imageHide;
    private CardView btnSkip, btnLike;
    private LinearLayout layoutMain;
    private RelativeLayout layoutInfomation;
    private boolean checkInformation = false;
    private TextView tutorialCheckInfomation;
    DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");

    private void bindingView() {
        imageShow = getView().findViewById(R.id.image_show);
        btnLike = getView().findViewById(R.id.button_like);
        btnSkip = getView().findViewById(R.id.button_skip);
        layoutMain = getView().findViewById(R.id.layout_main_match);
        layoutInfomation = getView().findViewById(R.id.layout_infomation);
        imageHide = getView().findViewById(R.id.image_hide_infomation);
        tutorialCheckInfomation = getView().findViewById(R.id.txt_tutorial_infomation);
    }

    private void bindingAction(){
        layoutMain.setOnClickListener(v -> {
            if(!checkInformation){
                animationIntro(layoutMain, 1, 0, 1, 300);
                checkInformation = true;
            }
            else{
                animationIntro(layoutMain, 0, 1, 1, 300);
                checkInformation = false;
            }
            tutorialCheckInfomation.setVisibility(View.GONE);
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageShow.setImageBitmap(UserDAO.imageUserFound.get(1));
                UserDAO.userFound.remove(0);
                UserDAO.imageUserFound.remove(0);
                getData();
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageShow.setImageBitmap(UserDAO.imageUserFound.get(1));
                UserDAO.userFound.remove(0);
                UserDAO.imageUserFound.remove(0);
                getData();
            }
        });
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
                                if (UserDAO.userFound.size() < 3) {
                                    UserDAO.userFound.add(task.getResult().child("profile").getValue(User.class));
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                refUser.child(finalGender + "/" + orderNumberFound + "/profile/image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (UserDAO.imageUserFound.size() < 3) {
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        String gender = "";
        if(UserDAO.CURRENT_USER.isGenderFinding()){
            gender+="male";
        }
        else{
            gender+="female";
        }
        refTotalUser.child(gender).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserDAO.maxGenderFinding = snapshot.getValue(Integer.class);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        getData();
    }

    @Override
    public void onPause() {
        checkInformation = false;
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(() -> animationIntro(tutorialCheckInfomation, 0, 1, 1, 300), 2000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindingView();
        bindingAction();
        imageShow.setImageBitmap(UserDAO.imageUserFound.get(0));
        imageHide.setImageBitmap(UserDAO.imageUserFound.get(0));
        layoutInfomation.setVisibility(View.GONE);
        animationIntro(layoutMain, 0, 1, 1, 500);
        new Handler().postDelayed(() -> layoutInfomation.setVisibility(View.VISIBLE),1000);
        animationIntro(btnSkip, -500f, 0, 0, 1000);
        animationIntro(btnLike, 500f, 0, 0, 1000);
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing, int time) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(time);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing == 0) {
                view.setTranslationX(progress);
            }else if(thing == 1){
                view.setAlpha(progress);
            }
        });
        move.start();
    }
}