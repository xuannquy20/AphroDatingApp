package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.Messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static MessengerAdapter adapter;
    public static String idUser, idRoom, nameUser;
    private ImageView avtUser;
    private TextView nameTextUser;
    private Button btnBack, btnSend;
    private EditText boxTyping;
    private int position;
    private DatabaseReference chatbox = FirebaseDatabase.getInstance().getReference().child("chat_box");

    private void bindingView() {
        recyclerView = findViewById(R.id.list_messenger);
        avtUser = findViewById(R.id.avatar_user);
        nameTextUser = findViewById(R.id.name_user);
        btnBack = findViewById(R.id.button_back);
        btnSend = findViewById(R.id.button_send);
        boxTyping = findViewById(R.id.box_typing);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idRoom = "";
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(v -> {
            String type = boxTyping.getText().toString().trim();
            if (!type.equals("")) {
                addNewMess(type);
            }
        });
    }

    private void addNewMess(String text) {
        Messenger messenger = new Messenger(UserDAO.CURRENT_USER_ID, text, Calendar.getInstance().getTime());
        chatbox.child(idRoom).get().addOnCompleteListener(task -> {
            long size = task.getResult().getChildrenCount();
            chatbox.child(idRoom + "/" + size).setValue(messenger);
        });
        UserDAO.listChat.get(position).getMessengers().add(0, messenger);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
        boxTyping.setText("");
        ChatListFragment.swapItems(position);
        position = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindingView();
        bindingAction();
        try {
            Intent i = getIntent();
            idRoom = i.getStringExtra("idRoom");
            idUser = i.getStringExtra("idUser");
            nameUser = i.getStringExtra("nameUser");
            position = i.getIntExtra("position", -1);
            nameTextUser.setText(nameUser);
            try {
                File dir = new File(this.getFilesDir(), "image");
                File file = new File(dir, idUser + ".png");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                byte[] byteArray = (byte[]) objectIn.readObject();
                Bitmap avtUser1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                avtUser.setImageBitmap(avtUser1);
                objectIn.close();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new MessengerAdapter(ChatActivity.this, UserDAO.listChat.get(position).getMessengers());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        new Handler().postDelayed(() -> recyclerView.scrollToPosition(0), 200);

    }

    @Override
    protected void onDestroy() {
        idRoom = "";
        super.onDestroy();
    }
}