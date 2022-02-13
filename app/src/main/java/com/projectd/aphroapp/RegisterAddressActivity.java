package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterAddressActivity extends AppCompatActivity {
    private TextView txtInfo;
    private Spinner city, district, ward;
    private LinearLayout selectAddress;
    private Button btnNext;

    protected void bindingView(){
        txtInfo = findViewById(R.id.txtInfo);
        selectAddress = findViewById(R.id.selectAddress);
        btnNext = findViewById(R.id.btnNext);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        ward = findViewById(R.id.ward);
    }

    protected void getData(){
        try {
            List<String> listCity = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset());
            JSONArray jsonArray = jsonObject.getJSONArray("city");
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject city = jsonArray.getJSONObject(i);
                listCity.add(city.getString("code") + "-" + city.getString("name"));
            }
            ArrayAdapter<String> listAddCity = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCity);
            listAddCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            city.setAdapter(listAddCity);
        }
        catch (Exception e){}
    }

    private String JsonDataFromAsset() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("tinh-thanhpho/tinh_tp.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        }
        catch (Exception e){
            return null;
        }
        return json;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_address);
        bindingView();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(txtInfo, -500, 800, true);
        animationIntro(selectAddress, 1200, 800, false);
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