package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.User;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout layoutMain;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogleSignIn;
    private Button btnFacebookSignIn;
    private CallbackManager callbackManager;
    private TextView ruleLink;
    private LinearLayout layoutLogo;
    private boolean checkLogin = false;
    DatabaseReference ref = InternetDAO.database.child("data_user");

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
        ruleLink.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, HomeActivity.class)));
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
                        UserDAO.CURRENT_USER = new User();
                        UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);

                        ref.get().addOnCompleteListener(task -> {
                            if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("user");
                                UserDAO.ORDER_NUMBER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                                UserDAO.GENDER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/gender").getValue(String.class);
                                refUser.get().addOnCompleteListener(task1 -> UserDAO.CURRENT_USER = task1.getResult().child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").getValue(User.class)).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                            else{
                                Intent i = new Intent(LoginActivity.this, RegisterNameActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
            UserDAO.CURRENT_USER_ID = account.getId();
            UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
            ref.get().addOnCompleteListener(task -> {
                if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                    checkLogin = true;
                } else {
                    checkLogin = false;
                }
            });
            if (checkLogin) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(LoginActivity.this, RegisterNameActivity.class);
                startActivity(i);
            }
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