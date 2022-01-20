package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgLogo = findViewById(R.id.logoView);
        animationIntro(imgLogo, 0f, 1f, false);
        animationIntro(imgLogo, 90f, 0f, true);
    }

    @SuppressLint("WrongConstant")
    private void animationIntro(View view, float infoNum1, float infoNum2, boolean thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(1000);
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if(thing){
                    view.setRotation(progress);
                }
                else{
                    view.setScaleX(progress);
                }
            }
        });
        move.start();
    }
}