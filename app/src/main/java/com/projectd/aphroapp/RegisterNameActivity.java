package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

public class RegisterNameActivity extends AppCompatActivity {
    private TextView txtInfo, bannerName;
    private EditText inputName;
    private Button btnNext;


    protected void bindingView(){
        txtInfo = findViewById(R.id.txtInfo);
        bannerName = findViewById(R.id.bannerName);
        inputName = findViewById(R.id.inputName);
        btnNext = findViewById(R.id.btnNext);
    }

    protected void bindingAction(){
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputName.getText().length() > 0){
                    btnNext.setEnabled(true);
                }
                else{
                    btnNext.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnNext.setOnClickListener(v -> {
            Intent name = new Intent(this, RegisterGenderActivity.class);
            UserDAO.CURRENT_USER.setName(inputName.getText().toString());
            startActivity(name);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);
        bindingView();
        bindingAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(bannerName, 1200, 800, false);
        animationIntro(inputName, 1200, 800, false);
        animationIntro(btnNext, -1200, 1000, false);
    }

    private void animationIntro(View view, float infoNum1, int timeMove, boolean thing){
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, 0);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(timeMove);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if(thing){
                view.setTranslationY(progress);
            }
            else {
                view.setTranslationX(progress);
            }
        });
        move.start();
    }
}