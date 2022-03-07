package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout layoutMain;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogleSignIn;
    private Button btnFacebookSignIn;
    private CallbackManager callbackManager;
    private TextView ruleLink;
    private LinearLayout layoutLogo;
    DatabaseReference refCheck = FirebaseDatabase.getInstance().getReference().child("data_user");
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
    DatabaseReference refTotalUser = FirebaseDatabase.getInstance().getReference().child("total_user");

    public void bindingView() {
        layoutMain = findViewById(R.id.LayoutLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);
        ruleLink = findViewById(R.id.textView3);
        layoutLogo = findViewById(R.id.layoutLogo);
        SpannableString content = new SpannableString("Điều khoản và dịch vụ");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        ruleLink.setText(content);
    }

    public void bindingActionListener() {
        btnFacebookSignIn.setOnClickListener(view -> LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile")));
        ruleLink.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterSuccessActivity.class)));
        btnGoogleSignIn.setOnClickListener(view -> signInGoogle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindingView();
        GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignIn);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                        UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                        UserDAO.getDataUser();
                        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.show();
                        new Thread(() -> {
                            while (true) {
                                try {
                                    if (UserDAO.getDataComplete) {
                                        loadingDialog.cancel();
                                        nextActivity();
                                        break;
                                    } else {
                                        Thread.sleep(100);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Huỷ đăng nhập", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                    }
                });
        bindingActionListener();
    }

    private void nextActivity() {
        if (UserDAO.ORDER_NUMBER == -1) {
            Intent i = new Intent(this, RegisterNameActivity.class);
            startActivity(i);
            finish();
        } else if(UserDAO.CURRENT_USER.getAge() >= 18){
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(this, AccountUnder18Activity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
            UserDAO.CURRENT_USER_ID = account.getId();
            UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
            UserDAO.getDataUser();
            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.show();
            new Thread(() -> {
                while (true) {
                    try {
                        if (UserDAO.getDataComplete) {
                            loadingDialog.cancel();
                            nextActivity();
                            break;
                        } else {
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                    }
                }
            }).start();

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(layoutMain, 1000, 0);
        animationIntro(layoutLogo, -1000, 0);

    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    private void animationIntro(View view, int positionFirst, int positionLast) {
        ValueAnimator move = ValueAnimator.ofFloat(positionFirst, positionLast);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(1000);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            view.setTranslationY(progress);
        });
        move.start();
    }
}