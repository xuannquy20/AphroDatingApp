package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterGenderActivity extends AppCompatActivity {
    private TextView txtInfo;
    private RadioGroup selectGender;
    private Button btnNext;
    private String selectGenderId;

    protected void bindingView(){
        try {
            txtInfo = findViewById(R.id.txtInfo);
            selectGender = findViewById(R.id.select_gender);
            btnNext = findViewById(R.id.btnNext);
        }
        catch (Exception e){
            Toast.makeText(this, "Lỗi view", Toast.LENGTH_LONG).show();
        }
    }

    protected void bindingAction(){
        try{
            selectGender.setOnCheckedChangeListener((group, checkedId) -> {
                if(selectGender.getCheckedRadioButtonId() == R.id.male){
                    selectGenderId = "nam";
                }
                else {
                    selectGenderId = "nu";
                }
                btnNext.setEnabled(true);
            });

            btnNext.setOnClickListener(v -> {
                try{
                    Intent name = getIntent();
                    Intent gender = new Intent(this, RegisterBirthDateActivity.class);
                    gender.putExtra("name", name.getStringExtra("name"));
                    gender.putExtra("gender", selectGenderId);
                    startActivity(gender);
                }
                catch (Exception e){
                    Toast.makeText(RegisterGenderActivity.this, "Loi next", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "Lỗi action", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);
        bindingView();
        bindingAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectGender, 1200, 800, false);
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