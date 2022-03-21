package com.projectd.aphroapp;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.language.AllWord;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {
    private Button signOut, editProfile;
    private ImageView imageShow, imageHide;
    private LinearLayout layoutMain;
    private TextView nameUser, nameUserHide, cityUser, addressUserHide, descriptionUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void bindingView() {
        signOut = getView().findViewById(R.id.button_sign_out);
        editProfile = getView().findViewById(R.id.button_edit_profile);
        imageShow = getView().findViewById(R.id.image_show);
        layoutMain = getView().findViewById(R.id.layout_main_match);
        imageHide = getView().findViewById(R.id.image_hide_infomation);
        nameUser = getView().findViewById(R.id.user_name_found);
        nameUserHide = getView().findViewById(R.id.user_name_found_hide);
        cityUser = getView().findViewById(R.id.user_city_found);
        addressUserHide = getView().findViewById(R.id.user_address_found_hide);
        descriptionUser = getView().findViewById(R.id.user_description_found_hide);
    }

    private void bindingAction() {
        signOut.setOnClickListener(v -> {
            exits();
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutMain.getAlpha() == 1) {
                    animationIntro(layoutMain, 1f, 0f, 300);
                }
                else{
                    animationIntro(layoutMain, 0f, 1f, 300);
                }
            }
        });
    }

    private void setData(){
        editProfile.setText(AllWord.editProfile);
        signOut.setText(AllWord.logOut);
        nameUser.setText(UserDAO.CURRENT_USER.getName() + ", " + UserDAO.CURRENT_USER.getAge());
        cityUser.setText(UserDAO.CURRENT_USER.getCity().substring(3));

        String address = UserDAO.CURRENT_USER.getWard().substring(6) + ", " + UserDAO.CURRENT_USER.getDistrict().substring(4) + ", " + cityUser.getText().toString();
        addressUserHide.setText(address);
        descriptionUser.setText(UserDAO.CURRENT_USER.getDescription());

        nameUserHide.setText(nameUser.getText().toString());

        try {
            File dir = new File(getActivity().getFilesDir(), "image");
            File file = new File(dir, UserDAO.CURRENT_USER_ID + ".png");
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            byte[] byteArray = (byte[]) objectIn.readObject();
            Bitmap avtUser1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageHide.setImageBitmap(avtUser1);
            imageShow.setImageBitmap(avtUser1);
            objectIn.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingView();
        bindingAction();
        setData();
    }

    private void exits() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(AllWord.titleDialog)
                .setContentText(AllWord.messageDialog)
                .setConfirmText(AllWord.okButtonDialog)
                .setConfirmClickListener(sweetAlertDialog -> {
                    if (UserDAO.isGoogle) {
                        IntroActivity.mGoogleSignInClient.signOut();
                    } else {
                        LoginManager.getInstance().logOut();
                    }
                    LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.show();
                    new Handler().postDelayed(() -> {
                        NotificationManagerCompat.from(getActivity()).cancelAll();
                        loadingDialog.cancel();
                        System.exit(0);
                    }, 1000);
                })
                .setCancelButton(AllWord.cancelButtonDialog, null)
                .show();
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int time) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(time);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            view.setAlpha(progress);
        });
        move.start();
    }
}