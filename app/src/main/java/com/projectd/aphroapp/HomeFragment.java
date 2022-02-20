package com.projectd.aphroapp;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;

import java.io.File;

public class HomeFragment extends Fragment {
    private ImageView imageShow, btnSkip, btnLike;
    private LinearLayout layoutMain, layoutInfomation;
    private boolean checkInformation = false;

    private void bindingView() {
        imageShow = getView().findViewById(R.id.image_show);
        btnLike = getView().findViewById(R.id.button_like);
        btnSkip = getView().findViewById(R.id.button_skip);
        layoutMain = getView().findViewById(R.id.layout_main_match);
        layoutInfomation = getView().findViewById(R.id.layout_infomation);
    }

    private void bindingAction(){
        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkInformation){
                    animationIntro(layoutMain, 1, 0, 1, 500);
                    checkInformation = true;
                }
                else{
                    animationIntro(layoutMain, 0, 1, 1, 500);
                    checkInformation = false;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindingView();
        bindingAction();
        imageShow.setImageBitmap(UserDAO.imageBitmap);
        layoutInfomation.setVisibility(View.GONE);
        animationIntro(layoutMain, 0, 1, 1, 1000);
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
            }else {
                view.setAlpha(progress);
            }
        });
        move.start();
    }
}