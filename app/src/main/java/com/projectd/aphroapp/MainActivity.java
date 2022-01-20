package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
        animationIntro(imgLogo, 0f, 0.8f, 0);
        animationIntro(imgLogo, 0f, 0.8f, 1);

        Handler handler = new Handler();
        Intent i = new Intent(this, LoginActivity.class);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
                animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
                animationIntro(imgLogo, 1f, 0f, 2);
                Handler newHandler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(i);
                    }
                }, 800);
            }
        }, 2000);

    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(800);
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if(thing==0){
                    view.setScaleX(progress);
                }
                else if(thing == 1){
                    view.setScaleY(progress);
                }
                else{
                    view.setAlpha(progress);
                }
            }
        });
        move.start();
    }
}