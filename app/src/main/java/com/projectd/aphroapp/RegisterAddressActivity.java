package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterAddressActivity extends AppCompatActivity {
    private TextView txtInfo;
    private Spinner city, district, ward;
    private CardView selectAddress;
    private Button btnNext;
    private List<String> listCity = new ArrayList<>();
    private List<String> listDistrict = new ArrayList<>();
    private List<String> listWard = new ArrayList<>();

    protected void bindingView(){
        txtInfo = findViewById(R.id.txtInfo);
        selectAddress = findViewById(R.id.layout_description);
        btnNext = findViewById(R.id.btnNext);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        ward = findViewById(R.id.ward);
    }

    protected void getDataAdapter(String path, String name, List<String> list, Spinner spinner, boolean city){
        getDataJson(path, name, list,spinner, city);
        ArrayAdapter<String> listAdd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        listAdd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(listAdd);
    }

    protected void getDataJson(String path, String name, List<String> list, Spinner spinner, boolean city){
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset(path));
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                if(city) {
                    list.add(data.getString("code") + "-" + data.getString("name"));
                }
                else{
                    if(!data.getString("type").equals("thanh-pho") && !data.getString("type").equals("tinh")){
                        list.add(data.getString("code") + "-" + data.getString("name"));
                    }
                }
            }
            spinner.setSelection(1);
        }
        catch (Exception e){}
    }

    private String JsonDataFromAsset(String path) {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(path);
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

    protected void bindingAction(){
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listDistrict.clear();
                String idCity = city.getSelectedItem().toString().substring(0, 2);
                getDataJson("quan-huyen/" + idCity +".json", "huyen", listDistrict, district, false);
                district.post(() -> district.setSelection(0));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listWard.clear();
                String idDistrict = district.getSelectedItem().toString().substring(0, 3);
                getDataJson("xa-phuong/" + idDistrict +".json", "xa", listWard, ward, false);
                ward.post(() -> ward.setSelection(0));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnNext.setOnClickListener(v -> {
            Intent address = new Intent(RegisterAddressActivity.this, RegisterDescriptionActivity.class);
            UserDAO.CURRENT_USER.setCity(city.getSelectedItem().toString());
            UserDAO.CURRENT_USER.setDistrict(district.getSelectedItem().toString());
            UserDAO.CURRENT_USER.setWard(ward.getSelectedItem().toString());
            startActivity(address);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_address);
        bindingView();
        getDataAdapter("tinh-thanhpho/tinh_tp.json", "city", listCity, city, true);
        getDataAdapter("quan-huyen/01.json", "huyen", listDistrict, district, false);
        getDataAdapter("xa-phuong/001.json", "xa", listWard, ward, false);
        bindingAction();
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