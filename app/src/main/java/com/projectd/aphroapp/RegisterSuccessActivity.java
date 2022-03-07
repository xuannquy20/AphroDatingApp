package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterSuccessActivity extends AppCompatActivity {
    private TextView titleSuccess, textSuccess;
    private ImageView imageSuccess;
    private CardView btnNext;
    private Button buttonNext;

    private void bindingView(){
        titleSuccess = findViewById(R.id.title_success);
        textSuccess = findViewById(R.id.text_success);
        imageSuccess = findViewById(R.id.image_success);
        btnNext = findViewById(R.id.btn_next_success);
        buttonNext = findViewById(R.id.button_next_success);

        buttonNext.setOnClickListener(v -> {
            Intent i = new Intent(RegisterSuccessActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        bindingView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(titleSuccess, -1000f, 0f, 800, 0);
        animationIntro(imageSuccess, -1000f, 0f, 800, 1);
        animationIntro(textSuccess, 1000f, 0f, 800, 1);
        animationIntro(btnNext, -1000f, 0f, 800, 1);
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int timeMove, int thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(timeMove);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if(thing == 0){
                view.setTranslationY(progress);
            }
            else if(thing == 1) {
                view.setTranslationX(progress);
            }
        });
        move.start();
    }
}