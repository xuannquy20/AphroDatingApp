package com.projectd.aphroapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.User;


public class ChatListFragment extends Fragment {
    public static RecyclerView recyclerView;
    public static ChatListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.chat_list);
        adapter = new ChatListAdapter(getActivity(), UserDAO.listChat);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public static void swapItems(int itemAIndex) {
        ChatBox item = UserDAO.listChat.get(itemAIndex);
        UserDAO.listChat.remove(itemAIndex);
        UserDAO.listChat.push(item);

        for (int i = 0; i < UserDAO.listChat.size(); i++) {
            ChatListFragment.adapter.notifyItemChanged(i);
        }

        for (int i = 0; i < UserDAO.listChat.size(); i++) {
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + i + "/idRoom").setValue(UserDAO.listChat.get(i).getIdRoom());
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + i + "/idUser").setValue(UserDAO.listChat.get(i).getIdUser());
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + i + "/nameUser").setValue(UserDAO.listChat.get(i).getNameUser());
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + i + "/readed").setValue(UserDAO.listChat.get(i).isReaded());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}