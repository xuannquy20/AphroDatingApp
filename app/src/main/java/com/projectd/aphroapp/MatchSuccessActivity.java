package com.projectd.aphroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.Messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MatchSuccessActivity extends AppCompatActivity {
    private ImageView ortherAvt, userAvt;
    private TextView txtConnect;
    private Button btnChat, btnSkip;
    private LottieAnimationView imageHeart;

    private void bindingView(){
        userAvt = findViewById(R.id.image_user_left);
        ortherAvt = findViewById(R.id.image_user_right);
        txtConnect = findViewById(R.id.text_connect);
        btnChat = findViewById(R.id.button_to_chat);
        btnSkip = findViewById(R.id.button_skip_chat);
        imageHeart = findViewById(R.id.heart_animation);
    }

    private void bindingAction() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_success);
        bindingView();
        bindingAction();

        Intent i = getIntent();
        String idRoom = i.getStringExtra("idRoom");
        String idUser = i.getStringExtra("idUser");
        String nameUser = i.getStringExtra("nameUser");

        int position = -1;
        for(int j = 0; j<UserDAO.listChat.size(); j++){
            if(idRoom.equals(UserDAO.listChat.get(j).getIdRoom())){
                position = j;
            }
        }
        ArrayList<Messenger> messengerList = UserDAO.listChat.get(position).getMessengers();
        Log.i("checkdatachat", UserDAO.listChat.get(position).getIdRoom());

        try {
            File dir = new File(getFilesDir(), "image");
            File file = new File(dir, idUser + ".png");
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            byte[] byteArray = (byte[]) objectIn.readObject();
            Bitmap avtUser = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ortherAvt.setImageBitmap(avtUser);
            objectIn.close();

            File file2 = new File(dir, UserDAO.CURRENT_USER_ID + ".png");
            FileInputStream fileIn2 = new FileInputStream(file2);
            ObjectInputStream objectIn2 = new ObjectInputStream(fileIn2);
            byte[] byteArray2 = (byte[]) objectIn2.readObject();
            Bitmap avtUser1 = BitmapFactory.decodeByteArray(byteArray2, 0, byteArray2.length);
            userAvt.setImageBitmap(avtUser1);
            objectIn.close();
        }
        catch (Exception e){}

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MatchSuccessActivity.this, ChatActivity.class);

                for(int j = 0; j<UserDAO.listChat.size(); j++){
                    if(UserDAO.listChat.get(j).getIdRoom().equals(idRoom)){
                        UserDAO.listChat.get(j).setReaded(true);
                        UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + j + "/readed").setValue(true);
                        break;
                    }
                }

                i.putExtra("idRoom", idRoom);
                i.putExtra("idUser", idUser);
                i.putExtra("nameUser", nameUser);

                i.putExtra("first", true + "");
                i.putExtra("list", messengerList);
                startActivity(i);
                finish();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageHeart.playAnimation();
        animationIntro(txtConnect, 0f, 1f, 0);
        animationIntro(txtConnect, 0f, 1f, 1);
        animationIntro(txtConnect, 0f, 1f, 2);

        animationIntro(btnChat, -800f, 0f, 3);
        animationIntro(btnChat, 0f, 1f, 2);
        animationIntro(btnSkip, 800f, 0f, 3);
        animationIntro(btnSkip, 0f, 1f, 2);
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
            } else if(thing == 2) {
                view.setAlpha(progress);
            }
            else{
                view.setTranslationX(progress);
            }
        });
        move.start();
    }
}