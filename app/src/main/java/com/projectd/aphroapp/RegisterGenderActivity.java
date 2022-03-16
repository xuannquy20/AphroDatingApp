package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.projectd.aphroapp.language.AllWord;
import com.projectd.aphroapp.dao.UserDAO;

public class RegisterGenderActivity extends AppCompatActivity {
    private TextView txtInfo;
    private RadioGroup selectGender;
    private RadioButton male, female;
    private Button btnNext;
    private boolean selectGenderId;
    private boolean genderFinding = false;

    protected void bindingView() {
        txtInfo = findViewById(R.id.txtInfo);
        selectGender = findViewById(R.id.select_gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        btnNext = findViewById(R.id.btnNext);

        txtInfo.setText(AllWord.yourGender);
        male.setText(AllWord.male);
        female.setText(AllWord.female);
        btnNext.setText(AllWord.next);
    }

    protected void bindingAction() {
        selectGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (selectGender.getCheckedRadioButtonId() == R.id.male) {
                selectGenderId = true;
            } else {
                selectGenderId = false;
            }
            btnNext.setEnabled(true);
        });

        btnNext.setOnClickListener(v -> {
            if (!genderFinding) {
                Intent gender = new Intent(this, RegisterBirthDateActivity.class);
                UserDAO.CURRENT_USER.setGender(selectGenderId);
                startActivity(gender);
            } else {
                Intent nextImage = new Intent(this, RegisterImageActivity.class);
                UserDAO.CURRENT_USER.setGenderFinding(selectGenderId);
                startActivity(nextImage);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);

        bindingView();
        bindingAction();

        Intent genderCheck = getIntent();
        if (genderCheck.getBooleanExtra("genderFinding", false) == true) {
            txtInfo.setText("Giới tính bạn quan tâm");
            genderFinding = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectGender, 1200, 800, false);
        animationIntro(btnNext, -1200, 1000, false);
    }

    private void animationIntro(View view, float infoNum1, int timeMove, boolean thing) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, 0);
        move.setInterpolator(new AccelerateDecelerateInterpolator());

        move.setDuration(timeMove);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing) {
                view.setTranslationY(progress);
            } else {
                view.setTranslationX(progress);
            }
        });
        move.start();
    }
}