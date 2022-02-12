package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterBirthDateActivity extends AppCompatActivity {
    private TextView txtInfo;
    private LinearLayout selectBirthDate;
    private Spinner selectDay, selectMonth, selectYear;
    private Button btnNext;
    private List<String> listYear, listMonth, listDay;

    protected void bindingView(){
        try {
            txtInfo = findViewById(R.id.txtInfo);
            selectBirthDate = findViewById(R.id.select_birthdate);
            selectDay = findViewById(R.id.day);
            selectMonth = findViewById(R.id.month);
            selectYear = findViewById(R.id.year);
            btnNext = findViewById(R.id.btnNext);

            listYear = addListData(1900, 2022);
            listMonth = addListData(1, 12);
            listDay = addListData(1, 31);
        }
        catch (Exception e){
            Toast.makeText(this, "Loi view", Toast.LENGTH_LONG).show();
        }
    }

    protected void addDataSpinner(Spinner v, List<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        v.setAdapter(adapter);
    }

    protected List<String> addListData(int start, int end){
        List<String> list = new ArrayList<>();
        for(int i = start; i <= end; i++){
            list.add(Integer.toString(i));
        }
        return list;
    }

    protected void bindingAction(){
        try {
            selectDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Integer.parseInt(selectDay.getSelectedItem().toString()) == 30){
                        listMonth.clear();
                        listMonth.add("4");
                        listMonth.add("6");
                        listMonth.add("9");
                        listMonth.add("11");
                        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(RegisterBirthDateActivity.this, android.R.layout.simple_spinner_item, listMonth);
                        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectMonth.setAdapter(adapterMonth);
                        listMonth.clear();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "Loi action", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_birth_date);
        bindingView();
        bindingAction();
        addDataSpinner(selectDay, listDay);
        addDataSpinner(selectMonth, listMonth);
        addDataSpinner(selectYear, listYear);
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectBirthDate, 1200, 800, false);
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