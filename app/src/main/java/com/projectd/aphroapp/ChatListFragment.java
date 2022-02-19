package com.projectd.aphroapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.model.User;

import java.util.LinkedList;

public class ChatListFragment extends Fragment {
    RecyclerView recyclerView;
    LinkedList words = new LinkedList();
    DatabaseReference ref = InternetDAO.database.child("user/male");

    protected void getData(Context context){
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren()){
                    User u = ds.child("profile").getValue(User.class);
                    words.add(u);
                }
                ChatListAdapter adapter = new ChatListAdapter(context, words);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = getView().findViewById(R.id.chat_list);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getData(context);
    }
}