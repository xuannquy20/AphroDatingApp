package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.projectd.aphroapp.language.AllWord;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;

public class IntroActivity extends AppCompatActivity {
    private TextView imgLogo;
    private boolean login = false;
    public static GoogleSignInClient mGoogleSignInClient;
    boolean downloadLang = false;
    public static boolean notVn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (!UserDAO.nowLangFirst.equals("vi")) {
            notVn = true;
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                            .setTargetLanguage(UserDAO.nowLangFirst)
                            .build();
            Translator translator = Translation.getClient(options);
            DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    AllWord.translate();
                    downloadLang = true;
                }
            });
        } else {
            downloadLang = true;
        }

        imgLogo = findViewById(R.id.logoView);
        GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignIn);
        checkAccount();
    }

    public void checkAccount() {
        try {
            if (InternetDAO.isNetworkAvailable(IntroActivity.this)) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(IntroActivity.this);
                if (account != null || isFacebookLoggedIn()) {
                    login = true;
                    if (account != null) {
                        UserDAO.CURRENT_USER_ID = account.getId();
                    } else {
                        UserDAO.isGoogle = false;
                        UserDAO.CURRENT_USER_ID = AccessToken.getCurrentAccessToken().getUserId();
                    }
                    UserDAO.CURRENT_USER.setId(UserDAO.CURRENT_USER_ID);
                    UserDAO.getDataUser(this);
                }
            } else {
                Toast.makeText(IntroActivity.this, "Không có kết nối mạng, thử lại sau", Toast.LENGTH_LONG).show();
                Handler handler = new Handler();
                handler.postDelayed(() -> System.exit(0), 1800);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Mạng yếu", Toast.LENGTH_LONG).show();
        }
    }

    private void nextActivity() {
        if (UserDAO.ORDER_NUMBER == -1 && !login) {
            Intent i = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (UserDAO.ORDER_NUMBER == -1) {
            Intent i = new Intent(IntroActivity.this, RegisterNameActivity.class);
            startActivity(i);
            finish();
        } else if (UserDAO.CURRENT_USER.getAge() >= 18) {
            Intent i = new Intent(IntroActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(IntroActivity.this, AccountUnder18Activity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationIntro(imgLogo, 0f, 1f, 0);
        animationIntro(imgLogo, 0f, 1f, 1);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            animationIntro(imgLogo, imgLogo.getScaleX(), 10f, 0);
            animationIntro(imgLogo, imgLogo.getScaleY(), 10f, 1);
            animationIntro(imgLogo, 1f, 0f, 2);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog loadingDialog = new LoadingDialog(IntroActivity.this);
                    loadingDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    if ((UserDAO.CURRENT_USER.getId() != null && !UserDAO.getDataComplete) || (notVn && !AllWord.complete)) {
                                        Thread.sleep(100);
                                    } else {
                                        loadingDialog.cancel();
                                        nextActivity();
                                        break;
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }).start();
                }
            }, 1000);
        }, 2000);
    }

    public boolean isFacebookLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void animationIntro(View view, float infoNum1, float infoNum2, int thing) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(1000);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (thing == 0) {
                view.setScaleX(progress);
            } else if (thing == 1) {
                view.setScaleY(progress);
            } else {
                view.setAlpha(progress);
            }
        });
        move.start();
    }
}