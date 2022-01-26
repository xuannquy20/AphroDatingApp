package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtInfo, bannerName, bannerDate, bannerGender, bannerFind, bannerDesc;
    private EditText inputName, inputDate,  inputDesc;
    private RadioGroup radioGender, radioFind;
    private Button btnNext;


    protected void bindingView(){
        txtInfo = findViewById(R.id.txtInfo);
        bannerName = findViewById(R.id.bannerName);
        bannerDate = findViewById(R.id.bannerDate);
        bannerGender = findViewById(R.id.bannerGender);
        bannerFind = findViewById(R.id.bannerFind);
        bannerDesc = findViewById(R.id.bannerDesc);

        inputName = findViewById(R.id.inputName);
        inputDate = findViewById(R.id.inputDate);
        inputDesc = findViewById(R.id.inputDesc);

        radioGender = findViewById(R.id.radioGender);
        radioFind = findViewById(R.id.radioFind);

        btnNext = findViewById(R.id.btnNext);
    }

    protected void bindingActionLister(){
        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindingView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(bannerName, 1200, 800, false);
        animationIntro(inputName, 1200, 800, false);
        animationIntro(bannerDate, 1200, 900, false);
        animationIntro(inputDate, 1200, 900, false);
        animationIntro(bannerGender, 1200, 1000, false);
        animationIntro(radioGender, 1200, 1000, false);
        animationIntro(bannerFind, 1200, 1100, false);
        animationIntro(radioFind, 1200, 1100, false);
        animationIntro(bannerDesc, 1200, 1200, false);
        animationIntro(inputDesc, 1200, 1200, false);
        animationIntro(btnNext, -1200, 1000, false);
    }

    private void animationIntro(View view, float infoNum1, int timeMove, boolean thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, 0);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(timeMove);
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if(thing){
                    view.setTranslationY(progress);
                }
                else {
                    view.setTranslationX(progress);
                }
            }
        });
        move.start();
    }
}