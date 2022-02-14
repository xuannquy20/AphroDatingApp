package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectd.aphroapp.dao.UserDAO;

public class RegisterDescriptionActivity extends AppCompatActivity {
    private TextView txtInfo;
    private CardView layoutDescription;
    private Button btnNext;
    private EditText description;

    protected void bindingView(){
        txtInfo = findViewById(R.id.txtInfo);
        layoutDescription = findViewById(R.id.layout_description);
        btnNext = findViewById(R.id.btnNext);
        description = findViewById(R.id.description);
    }

    protected void bindingAction(){
        btnNext.setOnClickListener(v -> {
            Intent nextGenderFinding = new Intent(RegisterDescriptionActivity.this, RegisterGenderActivity.class);
            UserDAO.CURRENT_USER.setDescription(description.getText().toString());
            nextGenderFinding.putExtra("genderFinding", true);
            startActivity(nextGenderFinding);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_description);
        bindingView();
        bindingAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(layoutDescription, 1200, 800, false);
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