package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectd.aphroapp.model.User;

import java.util.LinkedList;

public class ChatListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinkedList words = new LinkedList();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference().child("user");

    protected void getData(){
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()) {
//                    User u = new User(ds.getKey(), ds.child("profile/name").getValue(String.class), ds.child("profile/age").getValue(Integer.class));
//                    words.add(u);
                }
                ChatListAdapter adapter = new ChatListAdapter(ChatListActivity.this, words);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        recyclerView = findViewById(R.id.chatList);
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

