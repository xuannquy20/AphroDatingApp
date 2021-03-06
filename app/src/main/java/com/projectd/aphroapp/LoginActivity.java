package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.projectd.aphroapp.language.AllWord;
import com.projectd.aphroapp.dao.UserDAO;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {
    private LinearLayout layoutMain;
    private Button btnGoogleSignIn;
    private Button btnFacebookSignIn;
    private CallbackManager callbackManager;
    private TextView ruleLink;
    private LinearLayout layoutLogo;

    public void bindingView() {
        layoutMain = findViewById(R.id.LayoutLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);
        ruleLink = findViewById(R.id.textView3);
        layoutLogo = findViewById(R.id.layoutLogo);
    }

    public void bindingActionListener() {
        btnFacebookSignIn.setOnClickListener(view -> LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile")));
        btnGoogleSignIn.setOnClickListener(view -> signInGoogle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindingView();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                        UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                        UserDAO.getDataUser(LoginActivity.this);

                        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                        loadingDialog.show();

                        new Thread(() -> {
                            while (true) {
                                try {
                                    if (UserDAO.getDataComplete) {
                                        UserDAO.isGoogle = false;
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
                        Toast.makeText(LoginActivity.this, "Hu??? ????ng nh???p", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "L???i h??? th???ng", Toast.LENGTH_SHORT).show();
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
            UserDAO.getDataUser(this);

            LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
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

        if(IntroActivity.notVn){
            btnGoogleSignIn.setText(AllWord.loginByGoogle);
            btnFacebookSignIn.setText(AllWord.loginByFaceBook);
            ruleLink.setText(AllWord.rule);
        }
    }

    private void signInGoogle() {
        Intent signInIntent = IntroActivity.mGoogleSignInClient.getSignInIntent();
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