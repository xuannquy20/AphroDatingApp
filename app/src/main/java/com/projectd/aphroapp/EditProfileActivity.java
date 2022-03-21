package com.projectd.aphroapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.language.AllWord;
import com.projectd.aphroapp.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView imageEdit;
    private TextView titleName, titleGender, titleDescription, titleCity, titleDistrict, titleWard;
    private EditText editName, editDescription;
    private Spinner spinnerGender, spinnerCity, spinnerDistrict, spinnerWard;
    private Button btnSaveEdit, btnBack;

    private Uri uri;

    private List<String> listCity = new ArrayList<>();
    private List<String> listDistrict = new ArrayList<>();
    private List<String> listWard = new ArrayList<>();

    private void bindingView() {
        imageEdit = findViewById(R.id.image_edit);
        titleName = findViewById(R.id.title_name_edit);
        titleGender = findViewById(R.id.title_gender_edit);
        titleDescription = findViewById(R.id.title_description_edit);
        titleCity = findViewById(R.id.title_city_edit);
        titleDistrict = findViewById(R.id.title_district_edit);
        titleWard = findViewById(R.id.title_ward_edit);

        imageEdit = findViewById(R.id.image_edit);

        btnSaveEdit = findViewById(R.id.button_save_edit);
        btnBack = findViewById(R.id.button_back_edit);

        editName = findViewById(R.id.edit_name);
        editDescription = findViewById(R.id.edit_description);

        spinnerGender = findViewById(R.id.spinner_gender_edit);
        spinnerCity = findViewById(R.id.spinner_city_edit);
        spinnerDistrict = findViewById(R.id.spinner_district_edit);
        spinnerWard = findViewById(R.id.spinner_ward_edit);
    }

    private void bindingAction() {
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listDistrict.clear();
                String idCity = spinnerCity.getSelectedItem().toString().substring(0, 2);
                getDataJson("quan-huyen/" + idCity + ".json", "huyen", listDistrict, spinnerDistrict, false);
                spinnerDistrict.post(() -> spinnerDistrict.setSelection(0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listWard.clear();
                String idDistrict = spinnerDistrict.getSelectedItem().toString().substring(0, 3);
                getDataJson("xa-phuong/" + idDistrict + ".json", "xa", listWard, spinnerWard, false);
                spinnerWard.post(() -> spinnerWard.setSelection(0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imageEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, 1);
        });

        btnSaveEdit.setOnClickListener(v -> {
            if (editName.getText().toString().length() > 0) {
                new SweetAlertDialog(EditProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(AllWord.titleEdit)
                        .setContentText(AllWord.messageEdit)
                        .setConfirmText(AllWord.okButtonDialog)
                        .setConfirmClickListener(sweetAlertDialog -> {
                            setDataEdit();
                            LoadingDialog loadingDialog = new LoadingDialog(EditProfileActivity.this);
                            loadingDialog.show();

                            try {
                                File dir = new File(EditProfileActivity.this.getFilesDir(), "image");
                                if (!dir.exists()) {
                                    dir.mkdir();
                                }
                                File file = new File(dir, UserDAO.CURRENT_USER_ID + ".png");
                                if (file.exists()) {
                                    file.delete();
                                }
                            } catch (Exception e) {
                            }

                            if (uri != null) {
                                StorageReference upImage = InternetDAO.storage.child(UserDAO.CURRENT_USER_ID);
                                upImage.putFile(uri).addOnSuccessListener(taskSnapshot -> UserDAO.refUser.child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").setValue(UserDAO.CURRENT_USER).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        NotificationManagerCompat.from(EditProfileActivity.this).cancelAll();
                                        loadingDialog.cancel();
                                        new SweetAlertDialog(EditProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText(AllWord.titleDialog)
                                                .setContentText(AllWord.messageDialog)
                                                .setConfirmText(AllWord.okButtonDialog)
                                                .setConfirmClickListener(sweetAlertDialog1 -> System.exit(0)).show();
                                    }
                                }));
                            } else {
                                UserDAO.refUser.child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").setValue(UserDAO.CURRENT_USER).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        NotificationManagerCompat.from(EditProfileActivity.this).cancelAll();
                                        loadingDialog.cancel();
                                        new SweetAlertDialog(EditProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText(AllWord.titleDialog)
                                                .setContentText(AllWord.messageDialog)
                                                .setConfirmText(AllWord.okButtonDialog)
                                                .setConfirmClickListener(sweetAlertDialog1 -> System.exit(0)).show();
                                    }
                                });
                            }
                        }).setCancelButton(AllWord.cancelButtonDialog, null)
                        .show();
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    uri = data.getData();
                    UserDAO.imageUser = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageEdit.setImageURI(uri);
                    imageEdit.buildDrawingCache();
                } catch (Exception e) {
                }
            }
        }
    }

    private void setDataEdit() {
        UserDAO.CURRENT_USER.setName(editName.getText().toString());
        UserDAO.CURRENT_USER.setDescription(editDescription.getText().toString());
        if (spinnerGender.getSelectedItemPosition() == 0) {
            UserDAO.CURRENT_USER.setGenderFinding(true);
        } else {
            UserDAO.CURRENT_USER.setGenderFinding(false);
        }
        UserDAO.CURRENT_USER.setCity(spinnerCity.getSelectedItem().toString());
        UserDAO.CURRENT_USER.setDistrict(spinnerDistrict.getSelectedItem().toString());
        UserDAO.CURRENT_USER.setWard(spinnerWard.getSelectedItem().toString());
    }

    private void setTitle() {
        titleName.setText(AllWord.yourName);
        editName.setHint(AllWord.yourName);

        titleGender.setText(AllWord.yourGenderFinding);
        titleDescription.setText(AllWord.yourDescription);

        editDescription.setHint(AllWord.hintDescription);

        titleCity.setText(AllWord.city);
        titleDistrict.setText(AllWord.district);
        titleWard.setText(AllWord.wards);
    }

    private void setData() {
        imageEdit.setImageBitmap(UserDAO.imageUser);

        editName.setText(UserDAO.CURRENT_USER.getName());
        editDescription.setText(UserDAO.CURRENT_USER.getDescription());

        List<String> listGender = new ArrayList<>();
        listGender.add(AllWord.male.toUpperCase(Locale.ROOT));
        listGender.add(AllWord.female.toUpperCase(Locale.ROOT));
        ArrayAdapter<String> listGenderAdd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listGender);
        listGenderAdd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(listGenderAdd);
        if (UserDAO.CURRENT_USER.isGenderFinding()) {
            spinnerGender.setSelection(0, true);
        } else {
            spinnerGender.setSelection(1, true);
        }

        listCity = new ArrayList<>();
        listDistrict = new ArrayList<>();
        listWard = new ArrayList<>();
        getDataAdapter("tinh-thanhpho/tinh_tp.json", "city", listCity, spinnerCity, true);
        for (int i = 0; i < listCity.size(); i++) {
            if (listCity.get(i).contains(UserDAO.CURRENT_USER.getCity())) {
                spinnerCity.setSelection(i, true);
                String idCity = spinnerCity.getSelectedItem().toString().substring(0, 2);
                getDataAdapter("quan-huyen/" + idCity + ".json", "huyen", listDistrict, spinnerDistrict, false);
                break;
            }
        }

        for (int i = 0; i < listDistrict.size(); i++) {
            if (listDistrict.get(i).contains(UserDAO.CURRENT_USER.getDistrict())) {
                spinnerDistrict.setSelection(i, true);
                String idDistrict = spinnerDistrict.getSelectedItem().toString().substring(0, 3);
                getDataAdapter("xa-phuong/" + idDistrict + ".json", "xa", listWard, spinnerWard, false);
                break;
            }
        }

        for (int i = 0; i < listWard.size(); i++) {
            if (listWard.get(i).contains(UserDAO.CURRENT_USER.getWard())) {
                spinnerWard.setSelection(i, true);
                break;
            }
        }
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
        } catch (Exception e) {
            return null;
        }
        return json;
    }

    protected void getDataJson(String path, String name, List<String> list, Spinner spinner, boolean city) {
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset(path));
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                if (city) {
                    list.add(data.getString("code") + "-" + data.getString("name"));
                } else {
                    if (!data.getString("type").equals("thanh-pho") && !data.getString("type").equals("tinh")) {
                        list.add(data.getString("code") + "-" + data.getString("name"));
                    }
                }
            }
            spinner.setSelection(1);
        } catch (Exception e) {
        }
    }

    protected void getDataAdapter(String path, String name, List<String> list, Spinner spinner, boolean city) {
        getDataJson(path, name, list, spinner, city);
        ArrayAdapter<String> listAdd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        listAdd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(listAdd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        bindingView();
        setTitle();
        setData();
        bindingAction();
    }
}