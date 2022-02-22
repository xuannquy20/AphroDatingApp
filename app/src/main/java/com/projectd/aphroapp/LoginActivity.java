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
                        UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                        getData();
                        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.show();
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

    private void getData() {
        refCheck.get().addOnCompleteListener(task -> {
            if (task.getResult().hasChild(UserDAO.CURRENT_USER_ID)) {
                UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                UserDAO.ORDER_NUMBER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/order_number").getValue(Integer.class);
                UserDAO.GENDER = task.getResult().child(UserDAO.CURRENT_USER_ID + "/gender").getValue(String.class);
                refUser.get().addOnCompleteListener(task1 -> UserDAO.CURRENT_USER = task1.getResult().child(UserDAO.GENDER + "/" + UserDAO.ORDER_NUMBER + "/profile").getValue(User.class))
                        .addOnSuccessListener(dataSnapshot -> {
                            StorageReference storeRef = InternetDAO.storage.child(UserDAO.CURRENT_USER.getImage());
                            try {
                                File localFile = File.createTempFile(UserDAO.CURRENT_USER.getImage(), "png");
                                storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                    UserDAO.imageBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                });
                            } catch (Exception e) {
                            }
                        }).addOnSuccessListener(dataSnapshot -> refTotalUser.get().addOnCompleteListener(task1 -> {
                    String gender = "";
                    if (UserDAO.CURRENT_USER.isGenderFinding()) {
                        gender += "male";
                    } else {
                        gender += "female";
                    }
                    UserDAO.maxGenderFinding = task1.getResult().child(gender).getValue(Integer.class);
                })).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        refCheck.child(UserDAO.CURRENT_USER_ID + "/collection/take").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                    UserDAO.takedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                refCheck.child(UserDAO.CURRENT_USER_ID + "/collection/give").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        for (DataSnapshot ds : task.getResult().getChildren()) {
                                            UserDAO.givedLike.add(ds.child(ds.getKey()).getValue(ReactUser.class));
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        String gender = "";
                                        if (UserDAO.CURRENT_USER.isGenderFinding()) {
                                            gender += "male";
                                        } else {
                                            gender += "female";
                                        }
                                        boolean loop = true;
                                        while (loop) {
                                            Random rd = new Random();
                                            int orderNumberFound = rd.nextInt(UserDAO.maxGenderFinding);
                                            if ((UserDAO.CURRENT_USER.isGenderFinding() == UserDAO.CURRENT_USER.isGender()) && orderNumberFound == UserDAO.ORDER_NUMBER) {
                                                continue;
                                            } else if (UserDAO.givedLike.size() == 0) {
                                                loop = false;
                                            } else {
                                                for (int i = 0; i < UserDAO.givedLike.size(); i++) {
                                                    if (orderNumberFound == UserDAO.givedLike.get(i).getOrderNumber()) {
                                                        break;
                                                    } else if (i == UserDAO.givedLike.size() - 1) {
                                                        loop = false;
                                                    }
                                                }
                                            }
                                            if (loop == false) {
                                                String finalGender = gender;
                                                refUser.child(gender + "/" + orderNumberFound).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if (UserDAO.userFound.size() < 2) {
                                                            UserDAO.userFound.add(task.getResult().child("profile").getValue(User.class));
                                                        }
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                                        refUser.child(finalGender + "/" + orderNumberFound + "/profile/image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                if (UserDAO.imageUserFound.size() < 2) {
                                                                    String image = task.getResult().getValue(String.class);
                                                                    StorageReference storeRef = InternetDAO.storage.child(image);
                                                                    try {
                                                                        File localFile = File.createTempFile(image, "png");
                                                                        storeRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                                                            UserDAO.imageUserFound.add(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                                                                        });
                                                                    } catch (Exception e) {
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void nextActivity() {
        if (UserDAO.ORDER_NUMBER == -1) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (UserDAO.ORDER_NUMBER == -1) {
            Intent i = new Intent(this, RegisterNameActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(this, HomeActivity.class);
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
            getData();
            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (UserDAO.CURRENT_USER.getId() != null && (UserDAO.imageBitmap == null || UserDAO.imageUserFound.size() < 1)) {
                        if (UserDAO.imageUserFound.size() < 1) {
                            Toast.makeText(LoginActivity.this, "Mạng yếu, thử lại sau", Toast.LENGTH_LONG).show();
                            Handler handler1 = new Handler();
                            handler1.postDelayed(() -> System.exit(0), 1800);
                        } else {
                            loadingDialog.cancel();
                            nextActivity();
                        }
                    } else {
                        nextActivity();
                    }
                }
            }, 10000);

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