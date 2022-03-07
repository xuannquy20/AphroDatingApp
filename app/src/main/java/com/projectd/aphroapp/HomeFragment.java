package com.projectd.aphroapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class HomeFragment extends Fragment {
    private ImageView imageShow, imageHide, imageCheckInfomation, imageWarning;
    private CardView btnSkip, btnLike;
    private LinearLayout layoutMain;
    private RelativeLayout layoutInfomation;
    private boolean checkInformation = false;
    private TextView tutorialCheckInfomation, nameUser, nameUserHide, cityUser, addressUserHide, descriptionUser;

    private void bindingView() {
        imageShow = getView().findViewById(R.id.image_show);
        btnLike = getView().findViewById(R.id.button_like);
        btnSkip = getView().findViewById(R.id.button_skip);
        layoutMain = getView().findViewById(R.id.layout_main_match);
        layoutInfomation = getView().findViewById(R.id.layout_infomation);
        imageHide = getView().findViewById(R.id.image_hide_infomation);
        tutorialCheckInfomation = getView().findViewById(R.id.txt_tutorial_infomation);
        nameUser = getView().findViewById(R.id.user_name_found);
        nameUserHide = getView().findViewById(R.id.user_name_found_hide);
        cityUser = getView().findViewById(R.id.user_city_found);
        addressUserHide = getView().findViewById(R.id.user_address_found_hide);
        descriptionUser = getView().findViewById(R.id.user_description_found_hide);
        imageCheckInfomation = getView().findViewById(R.id.image_icon_check_infomation);
        imageWarning = getView().findViewById(R.id.image_warning);
    }

    private void bindingAction() {
        layoutMain.setOnClickListener(v -> {
            if (!checkInformation) {
                animationIntro(layoutMain, 1, 0, 1, 300);
                checkInformation = true;
            } else {
                animationIntro(layoutMain, 0, 1, 1, 300);
                checkInformation = false;
            }
            tutorialCheckInfomation.setVisibility(View.GONE);
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickReact(false);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLikeToDatabase(UserDAO.userFound.get(0), UserDAO.CURRENT_USER_ID);
                clickReact(true);
            }
        });
    }

    private void clickReact(boolean isLike) {
        btnLike.setEnabled(false);
        btnSkip.setEnabled(false);
        clickAnimation(isLike);
    }

    private void clickAnimation(boolean isLike) {
        //Animation
        animationIntro(btnLike, 1f, 0.2f, 1, 500);
        animationIntro(btnSkip, 1f, 0.2f, 1, 500);

        View view = null;
        if (isLike) {
            imageWarning.setImageResource(R.drawable.ic_heart);
            view = btnLike;
        } else {
            imageWarning.setImageResource(R.drawable.ic_skip);
            view = btnSkip;
        }
        animationIntro(view, 1, 1.2f, 2, 200);
        animationIntro(view, 1, 1.2f, 3, 200);
        View finalView = view;
        new Handler().postDelayed(() -> {
            animationIntro(finalView, 1.2f, 1, 2, 200);
            animationIntro(finalView, 1.2f, 1, 3, 200);
        }, 200);

        imageWarning.setVisibility(View.VISIBLE);
        animationIntro(imageWarning, 0f, 1f, 2, 300);
        animationIntro(imageWarning, 0f, 1f, 3, 300);
        animationIntro(imageWarning, 800f, 0f, 4, 300);
        new Handler().postDelayed(() -> {
            animationIntro(imageWarning, 1f, 0f, 2, 300);
            animationIntro(imageWarning, 1f, 0f, 3, 300);
            animationIntro(imageWarning, 0f, 800f, 4, 300);
            if (isLike) {
                animationIntro(imageShow, 0, 40, 5, 300);
                animationIntro(imageShow, 0f, 1500f, 0, 300);
            } else {
                animationIntro(imageShow, 0, -40, 5, 300);
                animationIntro(imageShow, 0f, -1500f, 0, 300);
            }
            animationIntro(imageShow, 1f, 0f, 1, 300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (UserDAO.imageUserFound.size() > 1) {
                        imageShow.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageShow.setImageBitmap(UserDAO.imageUserFound.get(1));
                        imageHide.setImageBitmap(UserDAO.imageUserFound.get(1));
                        int age = Calendar.getInstance().get(Calendar.YEAR) - UserDAO.userFound.get(1).getYear();
                        if (UserDAO.userFound.get(1).getMonth() < Calendar.getInstance().get(Calendar.MONTH)) {
                            age--;
                        } else if (UserDAO.userFound.get(1).getMonth() == Calendar.getInstance().get(Calendar.MONTH) && UserDAO.userFound.get(1).getDay() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                            age--;
                        }
                        String nameAge = UserDAO.userFound.get(1).getName() + ", " + age;
                        nameUser.setText(nameAge);
                        nameUserHide.setText(nameAge);
                        cityUser.setText(UserDAO.userFound.get(1).getCity().substring(3));
                        String address = UserDAO.userFound.get(1).getWard().substring(6) + ", " + UserDAO.userFound.get(1).getDistrict().substring(4) + ", " + cityUser.getText().toString();
                        addressUserHide.setText(address);
                        descriptionUser.setText(UserDAO.userFound.get(1).getDescription());
                    }
                    if (UserDAO.imageUserFound.size() > 0) {
                        UserDAO.userFound.remove(0);
                        UserDAO.imageUserFound.remove(0);
                    }
                    if (UserDAO.listCanFind.size() > 0) {
                        UserDAO.findRandomUser(1);
                    }
                    if (UserDAO.userFound.size() == 0) {
                        imageShow.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageShow.setImageResource(R.drawable.empty_match);
                        nameUser.setText("");
                        cityUser.setText("");
                        layoutMain.setEnabled(false);
                        tutorialCheckInfomation.setVisibility(View.GONE);
                        imageCheckInfomation.setVisibility(View.GONE);
                    }
                    if (!isLike) {
                        animationIntro(imageShow, -40, 0, 5, 300);
                        animationIntro(imageShow, -1500f, 0f, 0, 300);
                    } else {
                        animationIntro(imageShow, 40, 0, 5, 300);
                        animationIntro(imageShow, 1500f, 0f, 0, 300);
                    }
                    animationIntro(imageShow, 0f, 1f, 1, 500);
                    animationEnableButton();
                }
            }, 300);
        }, 1000);
    }

    private void saveLikeToDatabase(User userFound, String id) {
        DatabaseReference giveUser = UserDAO.refCheck.child(id + "/react/give/" + UserDAO.GENDER_FINDING);
        ReactUser newAdd = new ReactUser(userFound.getOrderNumber(), UserDAO.CURRENT_USER.isGenderFinding());
        UserDAO.givedLike.add(newAdd);
        final long[] count = {0};
        giveUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = snapshot.getChildrenCount();
                giveUser.child(count[0] + "").setValue(newAdd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        String[] idWhoTake = {""};
        DatabaseReference userTake = UserDAO.refUser.child(UserDAO.GENDER_FINDING + "/" + userFound.getOrderNumber() + "/profile");
        userTake.get().addOnCompleteListener(task -> {
            idWhoTake[0] = task.getResult().child("id").getValue(String.class);
            int[] size = {-1};
            DatabaseReference saveTake = UserDAO.refCheck.child(idWhoTake[0] + "/react/take/" + UserDAO.GENDER);
            saveTake.get().addOnCompleteListener(task1 -> {
                size[0] = (int) task1.getResult().getChildrenCount();
                ReactUser ru = new ReactUser(UserDAO.CURRENT_USER.getOrderNumber(), UserDAO.CURRENT_USER.isGender());
                saveTake.child(size[0] + "").setValue(ru);
            });
        });

        if (checkIsMatch(userFound)) {
            UUID uuid = UUID.randomUUID();
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    long size = task.getResult().getChildrenCount();
                    ChatBox chatBox = new ChatBox();
                    chatBox.setIdRoom(uuid.toString());
                    chatBox.setIdUser(userFound.getId());
                    chatBox.setNameUser(userFound.getName());
                    chatBox.setReaded(false);
                    UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + size + "/idRoom").setValue(uuid.toString());
                    UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + size + "/idUser").setValue(userFound.getId());
                    UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + size + "/nameUser").setValue(userFound.getName());
                    UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + size + "/readed").setValue(false);
                    UserDAO.listChat.add(chatBox);
                    Collections.sort(UserDAO.listChat, (o1, o2) -> Boolean.compare(o2.isReaded(), o1.isReaded()));
                    if(ChatListFragment.adapter != null){
                        ChatListFragment.adapter.notifyItemInserted(UserDAO.listChat.size() - 1);
                    }
                    UserDAO.refCheck.child(userFound.getId() + "/chat_room").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            long size = task.getResult().getChildrenCount();
                            UserDAO.refCheck.child(userFound.getId() + "/chat_room/" + size + "/idRoom").setValue(uuid.toString());
                            UserDAO.refCheck.child(userFound.getId() + "/chat_room/" + size + "/idUser").setValue(UserDAO.CURRENT_USER_ID);
                            UserDAO.refCheck.child(userFound.getId() + "/chat_room/" + size + "/nameUser").setValue(UserDAO.CURRENT_USER.getName());
                            UserDAO.refCheck.child(userFound.getId() + "/chat_room/" + size + "/readed").setValue(false);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Intent i = new Intent(getActivity(), MatchSuccessActivity.class);
                            startActivity(i);
                        }
                    });
                }
            });
        }
    }

    private boolean checkIsMatch(User userFound) {
        for (int i = 0; i < UserDAO.takedLike.size(); i++) {
            if (userFound.getOrderNumber() == UserDAO.takedLike.get(i).getOrderNumber()) {
                return true;
            }
        }
        return false;
    }

    private void animationEnableButton() {
        try {
            if ((UserDAO.imageUserFound.size() == 2) || (UserDAO.imageUserFound.size() == 1 && UserDAO.listCanFind.size() == 0)) {
                btnLike.setEnabled(true);
                btnSkip.setEnabled(true);
                animationIntro(btnLike, 0.2f, 1f, 1, 500);
                animationIntro(btnSkip, 0.2f, 1f, 1, 500);
            } else if (UserDAO.imageUserFound.size() == 0) {
                return;
            } else {
                Thread.sleep(100);
                animationEnableButton();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        UserDAO.findRandomUser(1);
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
        if (UserDAO.imageUserFound.size() > 0) {
            imageShow.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageShow.setImageBitmap(UserDAO.imageUserFound.get(0));
            imageHide.setImageBitmap(UserDAO.imageUserFound.get(0));
            int age = Calendar.getInstance().get(Calendar.YEAR) - UserDAO.userFound.get(0).getYear();
            if (UserDAO.userFound.get(0).getMonth() < Calendar.getInstance().get(Calendar.MONTH)) {
                age--;
            } else if (UserDAO.userFound.get(0).getMonth() == Calendar.getInstance().get(Calendar.MONTH) && UserDAO.userFound.get(0).getDay() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
            String nameAge = UserDAO.userFound.get(0).getName() + ", " + age;
            nameUser.setText(nameAge);
            nameUserHide.setText(nameAge);
            cityUser.setText(UserDAO.userFound.get(0).getCity().substring(3));
            String address = UserDAO.userFound.get(0).getWard().substring(6) + ", " + UserDAO.userFound.get(0).getDistrict().substring(4) + ", " + cityUser.getText().toString();
            addressUserHide.setText(address);
            descriptionUser.setText(UserDAO.userFound.get(0).getDescription());
        } else {
            imageShow.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageShow.setImageResource(R.drawable.empty_match);
            layoutMain.setEnabled(false);
            nameUser.setText("");
            cityUser.setText("");
            tutorialCheckInfomation.setVisibility(View.GONE);
            imageCheckInfomation.setVisibility(View.GONE);
        }
        layoutInfomation.setVisibility(View.GONE);
        animationIntro(layoutMain, 0, 1, 1, 500);
        new Handler().postDelayed(() -> layoutInfomation.setVisibility(View.VISIBLE), 1000);
        animationIntro(btnSkip, -500f, 0, 0, 1000);
        animationIntro(btnLike, 500f, 0, 0, 1000);
        if (UserDAO.imageUserFound.size() == 0) {
            btnLike.setEnabled(false);
            btnSkip.setEnabled(false);
            btnLike.setAlpha(0.2f);
            btnSkip.setAlpha(0.2f);
        }
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing, int time) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(time);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing == 0) {
                view.setTranslationX(progress);
            } else if (thing == 1) {
                view.setAlpha(progress);
            } else if (thing == 2) {
                view.setScaleX(progress);
            } else if (thing == 3) {
                view.setScaleY(progress);
            } else if (thing == 4) {
                view.setTranslationY(progress);
            } else if (thing == 5) {
                view.setRotation(progress);
            }
        });
        move.start();
    }
}