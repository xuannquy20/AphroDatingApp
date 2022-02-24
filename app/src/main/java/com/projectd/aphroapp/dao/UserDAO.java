package com.projectd.aphroapp.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserDAO {
    public static String CURRENT_USER_ID = null;
    public static User CURRENT_USER = new User();
    public static int ORDER_NUMBER = -1;
    public static String GENDER = "";
    public static String GENDER_FINDING = "";
    public static Bitmap imageBitmap = null;
    public static List<ReactUser> takedLike = new ArrayList<>();
    public static List<ReactUser> givedLike = new ArrayList<>();
    public static List<Integer> listCanFind = new ArrayList<>();

    public static int randomPosition = -1;
    public static List<User> userFound = new ArrayList<>();
    public static List<Bitmap> imageUserFound = new ArrayList<>();

    public static DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    public static DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    public static DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");

    public static void getDataUser() {
        refCheck.get().addOnCompleteListener(task -> {
            if (task.getResult().hasChild(CURRENT_USER_ID)) {
                CURRENT_USER.setId(CURRENT_USER_ID);
                ORDER_NUMBER = task.getResult().child(CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                GENDER = task.getResult().child(CURRENT_USER_ID + "/gender").getValue(String.class);
            }
        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                refUser.child(GENDER + "/" + ORDER_NUMBER + "/profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        CURRENT_USER = task.getResult().getValue(User.class);
                        StorageReference storeRef = InternetDAO.storage.child(CURRENT_USER_ID);
                        try {
                            File localFile = File.createTempFile(CURRENT_USER_ID, "png");
                            storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                imageBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            });
                        } catch (Exception e) {
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (CURRENT_USER.isGender()) {
                            GENDER_FINDING += "male";
                        } else {
                            GENDER_FINDING += "female";
                        }
                        refCheck.child(CURRENT_USER_ID + "/react/take" + GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                    takedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                }
                            }
                        });
                        refCheck.child(CURRENT_USER_ID + "/react/give" + GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                    givedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                }
                            }
                        });
                    }
                }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        refTotalUser.child(GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                int max = task.getResult().getValue(Integer.class);
                                max--;
                                if(givedLike.size() > 0) {
                                    for (int i = 0; i < givedLike.size(); i++) {
                                        if (max == givedLike.get(i).getOrderNumber()) {
                                            i = 0;
                                            max--;
                                        } else if (i == givedLike.size() - 1) {
                                            listCanFind.add(max);
                                            max--;
                                            if (max >= 0) {
                                                i = 0;
                                            }
                                        }
                                    }
                                }
                                else{
                                    for(int i = 0; i <= max; i++){
                                        listCanFind.add(i);
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                findRandomUser(0);
                            }
                        });
                    }
                });
            }
        });
    }

    public static void findRandomUser(int position) {
        if (listCanFind.size() > 0) {
            Random rd = new Random();
            randomPosition = rd.nextInt(listCanFind.size());
            int orderNumberRandom = listCanFind.get(randomPosition);
            listCanFind.remove(randomPosition);
            refUser.child(GENDER_FINDING + "/" + orderNumberRandom + "/profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    userFound.add(task.getResult().getValue(User.class));
                }
            }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    StorageReference storeRef = InternetDAO.storage.child(userFound.get(position).getImage());
                    try {
                        File localFile = File.createTempFile(userFound.get(position).getImage(), "png");
                        storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            imageUserFound.add(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                        });
                    } catch (Exception e) {}
                }
            });
        }
    }
}
