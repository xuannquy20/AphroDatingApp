package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout layoutMain;

    public void bindingView(){
        layoutMain = findViewById(R.id.LayoutLogin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(layoutMain);
    }

    private void animationIntro(View view){
        ValueAnimator move = ValueAnimator.ofFloat(1500f, 0f);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(1000);
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                view.setTranslationY(progress);
            }
        });
        move.start();
    }
}