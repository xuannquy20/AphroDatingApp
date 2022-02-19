package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectd.aphroapp.dao.UserDAO;

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
        txtInfo = findViewById(R.id.txtInfo);
        selectBirthDate = findViewById(R.id.select_birthdate);
        selectDay = findViewById(R.id.day);
        selectMonth = findViewById(R.id.month);
        selectYear = findViewById(R.id.year);
        btnNext = findViewById(R.id.btnNext);

        listYear = addListData(1950, 2022);
        listMonth = addListData(1, 12);
        listDay = addListData(1, 31);
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
            selectDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Integer.parseInt(selectDay.getSelectedItem().toString()) == 30){
                        listMonth.clear();
                        for(int i = 0; i<12; i++){
                            if(i+1 != 2) {
                                listMonth.add(Integer.toString(i + 1));
                            }
                        }
                        listYear.clear();
                        for(int i = 1950; i<=Calendar.getInstance().get(Calendar.YEAR); i++){
                            listYear.add(Integer.toString(i));
                        }
                    }
                    else if(Integer.parseInt(selectDay.getSelectedItem().toString()) == 31){
                        listMonth.clear();
                        listMonth.add("1");
                        listMonth.add("3");
                        listMonth.add("5");
                        listMonth.add("7");
                        listMonth.add("8");
                        listMonth.add("10");
                        listMonth.add("12");
                    }
                    else{
                        listMonth.clear();
                        for(int i = 0; i<12; i++){
                            listMonth.add(Integer.toString(i + 1));
                        }
                        listYear.clear();
                        for(int i = 1950; i<=Calendar.getInstance().get(Calendar.YEAR); i++){
                            listYear.add(Integer.toString(i));
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            selectMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Integer.parseInt(selectMonth.getSelectedItem().toString()) == 4 ||
                            Integer.parseInt(selectMonth.getSelectedItem().toString()) == 6 ||
                            Integer.parseInt(selectMonth.getSelectedItem().toString()) == 9 ||
                            Integer.parseInt(selectMonth.getSelectedItem().toString()) == 11){
                        listDay.clear();
                        for(int i = 0; i < 30; i++){
                            listDay.add(Integer.toString(i+1));
                        }
                    }
                    else if(Integer.parseInt(selectMonth.getSelectedItem().toString()) == 2){
                        if(Integer.parseInt(selectYear.getSelectedItem().toString()) % 4 == 0){
                            listDay.clear();
                            if((Integer.parseInt(selectYear.getSelectedItem().toString()) % 100 == 0
                                    && Integer.parseInt(selectYear.getSelectedItem().toString()) % 400 == 0)
                                    || (Integer.parseInt(selectYear.getSelectedItem().toString()) % 100 != 0)){
                                for(int i = 0; i < 29; i++){
                                    listDay.add(Integer.toString(i+1));
                                }
                            }
                            else{
                                for (int i = 0; i < 28; i++){
                                    listDay.add(Integer.toString(i+1));
                                }
                            }
                        }
                        else if(Integer.parseInt(selectDay.getSelectedItem().toString()) == 29){
                            listYear.clear();
                            for(int i = 1950; i<=Calendar.getInstance().get(Calendar.YEAR); i++){
                                if(i % 4 == 0){
                                    if(i%100!=0 || (i%100==0 && i%400==0)){
                                        listYear.add(Integer.toString(i));
                                    }
                                }
                            }
                            selectYear.setSelection(1, true);
                        }
                        else{
                            listDay.clear();
                            for (int i = 0; i < 28; i++){
                                listDay.add(Integer.toString(i+1));
                            }
                        }
                    }
                    else{
                        listDay.clear();
                        for(int i = 0; i < 31; i++){
                            listDay.add(Integer.toString(i+1));
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            selectYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Integer.parseInt(selectYear.getSelectedItem().toString()) % 4 == 0 &&
                            Integer.parseInt(selectMonth.getSelectedItem().toString()) == 2){
                        listDay.clear();
                        if((Integer.parseInt(selectYear.getSelectedItem().toString()) % 100 == 0
                                && Integer.parseInt(selectYear.getSelectedItem().toString()) % 400 == 0)
                                || (Integer.parseInt(selectYear.getSelectedItem().toString()) % 100 != 0)){
                            for(int i = 0; i < 29; i++){
                                listDay.add(Integer.toString(i+1));
                            }
                        }
                        else{
                            for (int i = 0; i < 28; i++){
                                listDay.add(Integer.toString(i+1));
                            }
                        }
                    }
                    else{
                        listDay.clear();
                        for (int i = 0; i < 31; i++){
                            listDay.add(Integer.toString(i+1));
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            btnNext.setOnClickListener(v -> {
                Intent date = new Intent(this, RegisterAddressActivity.class);
                UserDAO.CURRENT_USER.setDay(Integer.parseInt(selectDay.getSelectedItem().toString()));
                UserDAO.CURRENT_USER.setMonth(Integer.parseInt(selectMonth.getSelectedItem().toString()));
                UserDAO.CURRENT_USER.setYear(Integer.parseInt(selectYear.getSelectedItem().toString()));
                startActivity(date);
            });
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