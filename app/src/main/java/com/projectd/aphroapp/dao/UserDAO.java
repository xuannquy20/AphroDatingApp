package com.projectd.aphroapp.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.HomeFragment;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.Messenger;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UserDAO {
    public static String CURRENT_USER_ID = null;
    public static User CURRENT_USER = new User();
    public static int ORDER_NUMBER = -1;
    public static String GENDER = "";
    public static String GENDER_FINDING = "";
    public static Bitmap imageBitmap = null;
    public static int age = -1;

    public static List<ReactUser> takedLike = new ArrayList<>();
    public static List<ReactUser> givedLike = new ArrayList<>();
    public static List<Integer> listCanFind = new ArrayList<>();

    public static LinkedList<ChatBox> listChat = new LinkedList<>();

    public static int randomPosition = -1;
    public static List<User> userFound = new ArrayList<>();
    public static List<Bitmap> imageUserFound = new ArrayList<>();

    public static boolean getDataComplete = false;
    public static boolean isGoogle = true;

    public static DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    public static DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    public static DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");
    public static DatabaseReference refChatBox = FirebaseDatabase.getInstance().getReference().child("chat_box");

    public static void getDataUser(Context context) {
        try {
            refCheck.get().addOnCompleteListener(task -> {
                if (task.getResult().hasChild(CURRENT_USER_ID)) {
                    CURRENT_USER.setId(CURRENT_USER_ID);
                    ORDER_NUMBER = task.getResult().child(CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                    GENDER = task.getResult().child(CURRENT_USER_ID + "/gender").getValue(String.class);
                }
                if (ORDER_NUMBER != -1) {
                    refUser.child(GENDER + "/" + ORDER_NUMBER + "/profile").get().addOnCompleteListener(task1 -> {
                        CURRENT_USER = task1.getResult().getValue(User.class);
                        StorageReference storeRef = InternetDAO.storage.child(CURRENT_USER_ID);
                        try {
                            File localFile = File.createTempFile(CURRENT_USER_ID, "png");
                            storeRef.getFile(localFile);
                            imageBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        } catch (Exception e) {
                        }
                        if (CURRENT_USER.isGenderFinding()) {
                            GENDER_FINDING += "male";
                        } else {
                            GENDER_FINDING += "female";
                        }
                        refCheck.child(CURRENT_USER_ID + "/chat_room").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.getResult().getChildrenCount() > 0) {
                                    for (DataSnapshot ds : task.getResult().getChildren()) {
                                        ChatBox chatBox = new ChatBox();
                                        chatBox.setIdRoom(ds.child("idRoom").getValue(String.class));
                                        chatBox.setIdUser(ds.child("idUser").getValue(String.class));
                                        chatBox.setNameUser(ds.child("nameUser").getValue(String.class));
                                        chatBox.setReaded(ds.child("readed").getValue(Boolean.class));
                                        chatBox.setFirst(true);
                                        refChatBox.child(chatBox.getIdRoom()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                                    Messenger ms = new Messenger();
                                                    ms.setIdUser(ds.child("idUser").getValue(String.class));
                                                    ms.setText(ds.child("text").getValue(String.class));
                                                    ms.setDate(ds.child("date").getValue(Date.class));
                                                    chatBox.getMessengers().add(0, ms);
                                                }
                                                listChat.add(chatBox);
                                            }
                                        });
                                    }
                                }
                                getOrderNumberCanFind();
                            }
                        });
                    });
                } else {
                    findRandomUser(0);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOrderNumberCanFind() {
        refCheck.child(CURRENT_USER_ID + "/react/take/" + GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    ReactUser reactUser = new ReactUser();
                    reactUser.setOrderNumber(ds.child("orderNumber").getValue(Integer.class));
                    reactUser.setGender(ds.child("gender").getValue(Boolean.class));
                    takedLike.add(reactUser);
                }
                final int[] size = {takedLike.size()};
                refCheck.child(CURRENT_USER_ID + "/react/take/" + GENDER_FINDING).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(size[0] > 0){
                            size[0]--;
                        }
                        else{
                            refCheck.child(CURRENT_USER_ID + "/react/take/" + GENDER_FINDING+ "/" + snapshot.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    ReactUser reactUser = new ReactUser();
                                    reactUser.setOrderNumber(task.getResult().child("orderNumber").getValue(Integer.class));
                                    reactUser.setGender(task.getResult().child("gender").getValue(Boolean.class));
                                    takedLike.add(reactUser);
                                }
                            });
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                refCheck.child(CURRENT_USER_ID + "/react/give/" + GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            ReactUser reactUser = new ReactUser();
                            reactUser.setOrderNumber(ds.child("orderNumber").getValue(Integer.class));
                            reactUser.setGender(ds.child("gender").getValue(Boolean.class));
                            givedLike.add(reactUser);
                        }
                        if (CURRENT_USER.isGenderFinding()) {
                            GENDER_FINDING = "male";
                        } else {
                            GENDER_FINDING = "female";
                        }

                        if (CURRENT_USER.isGender()) {
                            GENDER = "male";
                        } else {
                            GENDER = "female";
                        }
                        refTotalUser.child(GENDER_FINDING).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                int max = task.getResult().getValue(Integer.class);
                                max--;
                                if (givedLike.size() > 0) {
                                    for (int i = 0; i < givedLike.size(); i++) {
                                        if ((max == ORDER_NUMBER && CURRENT_USER.isGender() == CURRENT_USER.isGenderFinding()) || max == givedLike.get(i).getOrderNumber()) {
                                            i = 0;
                                            max--;
                                        } else if (i == givedLike.size() - 1 && max >= 0) {
                                            listCanFind.add(max);
                                            max--;
                                            if (max >= 0) {
                                                i = 0;
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = 0; i <= max; i++) {
                                        if (!(i == ORDER_NUMBER && (CURRENT_USER.isGender() == CURRENT_USER.isGenderFinding() || !CURRENT_USER.isGender() == !CURRENT_USER.isGenderFinding()))) {
                                            listCanFind.add(i);
                                        }
                                    }
                                }
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
                    StorageReference storeRef = InternetDAO.storage.child(userFound.get(position).getImage());
                    try {
                        File localFile = File.createTempFile(userFound.get(position).getImage(), "png");
                        storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            imageUserFound.add(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            getDataComplete = true;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            getDataComplete = true;
        }
    }
}
